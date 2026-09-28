package com.tzn.shop.service.impl;

import com.tzn.shop.dto.ShopOrderCreateRequest;
import com.tzn.shop.enums.ShopItemStatus;
import com.tzn.shop.enums.ShopOrderStatus;
import com.tzn.shop.service.CurrentUserService;
import com.tzn.shop.service.ShopOrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopOrderServiceImpl implements ShopOrderService {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final JdbcTemplate jdbcTemplate;
    private final CurrentUserService currentUserService;

    public ShopOrderServiceImpl(JdbcTemplate jdbcTemplate, CurrentUserService currentUserService) {
        this.jdbcTemplate = jdbcTemplate;
        this.currentUserService = currentUserService;
    }

    @Override
    public Map<String, Object> account(String authorization) {
        long userId = currentUserService.requireUserId(authorization);
        ensureAccount(userId);
        return jdbcTemplate.queryForMap("SELECT balance, frozen_amount AS frozenAmount FROM shop_user_account WHERE user_id = ? AND del_flag = 0", userId);
    }

    @Override
    @Transactional
    public Map<String, Object> create(String authorization, ShopOrderCreateRequest request) {
        long buyerId = currentUserService.requireUserId(authorization);
        validateRequest(request);
        Map<String, Object> item = loadPurchasableItem(request.itemId(), buyerId);
        int quantity = request.quantity();
        BigDecimal unitPrice = (BigDecimal) item.get("price");
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));
        ensureAccount(buyerId);
        int deducted = jdbcTemplate.update("UPDATE shop_user_account SET balance = balance - ?, update_time = NOW(), version = version + 1 WHERE user_id = ? AND balance >= ? AND del_flag = 0", total, buyerId, total);
        if (deducted == 0) { throw new IllegalArgumentException("余额不足"); }
        BigDecimal before = jdbcTemplate.queryForObject("SELECT balance + ? FROM shop_user_account WHERE user_id = ?", BigDecimal.class, total, buyerId);
        BigDecimal after = jdbcTemplate.queryForObject("SELECT balance FROM shop_user_account WHERE user_id = ?", BigDecimal.class, buyerId);
        int stockUpdated = jdbcTemplate.update("UPDATE shop_item SET stock = stock - ?, update_time = NOW(), version = version + 1 WHERE id = ? AND stock >= ? AND status = ? AND del_flag = 0", quantity, request.itemId(), quantity, ShopItemStatus.ON_SHELF.getCode());
        if (stockUpdated == 0) { throw new IllegalArgumentException("库存不足"); }
        String orderNo = "SO" + System.currentTimeMillis() + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        Long orderId = insertOrder(orderNo, buyerId, ((Number) item.get("sellerUserId")).longValue(), total, request);
        jdbcTemplate.update("INSERT INTO shop_order_item (order_id, item_id, item_title, item_type, unit_price, quantity, subtotal, cover_url, service_duration_min, service_rule, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())", orderId, request.itemId(), item.get("title"), item.get("itemType"), unitPrice, quantity, total, item.get("coverUrl"), item.get("serviceDurationMin"), item.get("serviceRule"));
        jdbcTemplate.update("INSERT INTO shop_account_flow (user_id, order_id, order_no, flow_type, amount, balance_before, balance_after, remark, create_user, update_user) VALUES (?, ?, ?, 1, ?, ?, ?, ?, ?, ?)", buyerId, orderId, orderNo, total, before, after, "购买商品", buyerId, buyerId);
        addTrack(orderId, ShopOrderStatus.WAIT_ACCEPT, "已下单", "订单已支付，等待对方接单", buyerId);
        Map<String, Object> result = new LinkedHashMap<>(); result.put("id", orderId); result.put("orderNo", orderNo); result.put("status", ShopOrderStatus.WAIT_ACCEPT.getCode()); result.put("totalAmount", total); result.put("remainingBalance", after); return result;
    }

    @Override public Map<String, Object> buyerList(String authorization, Integer status, int pageNum, int pageSize) { return participantPageQuery(currentUserService.requireUserId(authorization), status, pageNum, pageSize); }
    @Override public Map<String, Object> sellerList(String authorization, Integer status, int pageNum, int pageSize) { return pageQuery(currentUserService.requireUserId(authorization), false, status, pageNum, pageSize); }

    private Map<String, Object> participantPageQuery(long userId, Integer status, int pageNum, int pageSize) {
        if (pageNum < 1 || pageSize < 1 || pageSize > 100) { throw new IllegalArgumentException("分页参数不合法"); }
        if (status != null && status == 0) { status = null; }
        if (status != null) { ShopOrderStatus.fromCode(status); }
        StringBuilder sql = new StringBuilder("SELECT o.id, o.order_no AS orderNo, o.total_amount AS totalAmount, o.status, o.is_reviewed AS isReviewed, o.is_reviewed AS reviewed, CASE WHEN o.buyer_user_id = ? AND o.status = 3 AND o.is_reviewed = 0 THEN 1 ELSE 0 END AS canReview, o.create_time AS createTime, oi.item_title AS itemTitle, oi.item_type AS itemType, oi.quantity, oi.cover_url AS coverUrl, b.nickname AS buyerName, b.avatar_url AS buyerAvatarUrl FROM shop_order o JOIN sys_user b ON b.id = o.buyer_user_id JOIN shop_order_item oi ON oi.order_id = o.id WHERE (o.buyer_user_id = ? OR o.seller_user_id = ?) AND o.del_flag = 0");
        List<Object> args = new ArrayList<>(); args.add(userId); args.add(userId); args.add(userId);
        if (status != null) { sql.append(" AND o.status = ?"); args.add(status); }
        sql.append(" ORDER BY o.create_time DESC, o.id DESC LIMIT ? OFFSET ?"); args.add(pageSize); args.add((pageNum - 1) * pageSize);
        String countSql = "SELECT COUNT(*) FROM shop_order WHERE (buyer_user_id = ? OR seller_user_id = ?) AND del_flag = 0" + (status == null ? "" : " AND status = ?");
        Integer total = status == null ? jdbcTemplate.queryForObject(countSql, Integer.class, userId, userId) : jdbcTemplate.queryForObject(countSql, Integer.class, userId, userId, status);
        return Map.of("items", jdbcTemplate.queryForList(sql.toString(), args.toArray()), "total", total, "pageNum", pageNum, "pageSize", pageSize);
    }
    private Map<String, Object> pageQuery(long userId, boolean buyer, Integer status, int pageNum, int pageSize) {
        if (pageNum < 1 || pageSize < 1 || pageSize > 100) { throw new IllegalArgumentException("分页参数不合法"); }
        if (status != null && status == 0) { status = null; }
        if (status != null) { ShopOrderStatus.fromCode(status); }
        String owner = buyer ? "buyer_user_id" : "seller_user_id";
        StringBuilder sql = new StringBuilder("SELECT o.id, o.order_no AS orderNo, o.total_amount AS totalAmount, o.status, o.is_reviewed AS isReviewed, o.is_reviewed AS reviewed, CASE WHEN o.buyer_user_id = ? AND o.status = 3 AND o.is_reviewed = 0 THEN 1 ELSE 0 END AS canReview, o.create_time AS createTime, oi.item_title AS itemTitle, oi.item_type AS itemType, oi.quantity, oi.cover_url AS coverUrl, b.nickname AS buyerName, b.avatar_url AS buyerAvatarUrl FROM shop_order o JOIN sys_user b ON b.id = o.buyer_user_id JOIN shop_order_item oi ON oi.order_id = o.id WHERE o.").append(owner).append(" = ? AND o.del_flag = 0");
        List<Object> args = new ArrayList<>(); args.add(userId); args.add(userId);
        if (status != null) { sql.append(" AND o.status = ?"); args.add(status); }
        sql.append(" ORDER BY o.create_time DESC, o.id DESC LIMIT ? OFFSET ?"); args.add(pageSize); args.add((pageNum - 1) * pageSize);
        String countSql = "SELECT COUNT(*) FROM shop_order WHERE " + owner + " = ? AND del_flag = 0" + (status == null ? "" : " AND status = ?");
        Integer total = status == null ? jdbcTemplate.queryForObject(countSql, Integer.class, userId) : jdbcTemplate.queryForObject(countSql, Integer.class, userId, status);
        return Map.of("items", jdbcTemplate.queryForList(sql.toString(), args.toArray()), "total", total, "pageNum", pageNum, "pageSize", pageSize);
    }

    @Override public Map<String, Object> detail(String authorization, long orderId) {
        long userId = currentUserService.requireUserId(authorization);
        List<Map<String, Object>> orders = jdbcTemplate.queryForList("SELECT o.id, o.order_no AS orderNo, o.buyer_user_id AS buyerUserId, o.seller_user_id AS sellerUserId, o.total_amount AS totalAmount, o.status, o.is_reviewed AS isReviewed, o.payment_status AS paymentStatus, o.payment_method AS paymentMethod, o.paid_amount AS paidAmount, o.paid_time AS paidTime, o.appointment_time AS appointmentTime, o.remark, o.create_time AS createTime, b.nickname AS buyerName, s.nickname AS sellerName FROM shop_order o JOIN sys_user b ON b.id = o.buyer_user_id JOIN sys_user s ON s.id = o.seller_user_id WHERE o.id = ? AND (o.buyer_user_id = ? OR o.seller_user_id = ?) AND o.del_flag = 0", orderId, userId, userId);
        if (orders.isEmpty()) { throw new IllegalArgumentException("订单不存在或无权查看"); }
        Map<String, Object> result = new LinkedHashMap<>(); result.put("order", orders.get(0)); result.put("canAccept", userId == ((Number) orders.get(0).get("sellerUserId")).longValue() && ((Number) orders.get(0).get("status")).intValue() == ShopOrderStatus.WAIT_ACCEPT.getCode()); result.put("canComplete", userId == ((Number) orders.get(0).get("buyerUserId")).longValue() && ((Number) orders.get(0).get("status")).intValue() == ShopOrderStatus.ACCEPTED.getCode()); result.put("reviewed", ((Number) orders.get(0).get("isReviewed")).intValue() == 1); result.put("canReview", userId == ((Number) orders.get(0).get("buyerUserId")).longValue() && ((Number) orders.get(0).get("status")).intValue() == ShopOrderStatus.COMPLETED.getCode() && !((Boolean) result.get("reviewed"))); result.put("review", jdbcTemplate.queryForList("SELECT r.id, r.rating, r.content, r.create_time AS createTime, u.nickname AS reviewerName, u.avatar_url AS reviewerAvatarUrl FROM shop_item_review r JOIN sys_user u ON u.id = r.reviewer_user_id WHERE r.order_id = ? AND r.del_flag = 0", orderId)); result.put("items", jdbcTemplate.queryForList("SELECT id, item_id AS itemId, item_title AS itemTitle, item_type AS itemType, unit_price AS unitPrice, quantity, subtotal, cover_url AS coverUrl, service_duration_min AS serviceDurationMin, service_rule AS serviceRule FROM shop_order_item WHERE order_id = ?", orderId)); result.put("tracks", jdbcTemplate.queryForList("SELECT status, title, description, create_time AS operateTime FROM shop_order_track WHERE order_id = ? ORDER BY create_time ASC, id ASC", orderId)); return result;
    }

    @Override @Transactional public void accept(String authorization, long orderId) { long sellerId = currentUserService.requireUserId(authorization); int updated = jdbcTemplate.update("UPDATE shop_order SET status = ?, seller_confirm_time = NOW(), update_user = ?, update_time = NOW(), version = version + 1 WHERE id = ? AND seller_user_id = ? AND status = ? AND del_flag = 0", ShopOrderStatus.ACCEPTED.getCode(), sellerId, orderId, sellerId, ShopOrderStatus.WAIT_ACCEPT.getCode()); if (updated == 0) { throw new IllegalArgumentException("订单不存在、已处理或无权接单"); } addTrack(orderId, ShopOrderStatus.ACCEPTED, "已接待", "对方已确认接单", sellerId); }
    @Override @Transactional public void complete(String authorization, long orderId) { long buyerId = currentUserService.requireUserId(authorization); int updated = jdbcTemplate.update("UPDATE shop_order SET status = ?, buyer_confirm_time = NOW(), completed_time = NOW(), update_user = ?, update_time = NOW(), version = version + 1 WHERE id = ? AND buyer_user_id = ? AND status = ? AND del_flag = 0", ShopOrderStatus.COMPLETED.getCode(), buyerId, orderId, buyerId, ShopOrderStatus.ACCEPTED.getCode()); if (updated == 0) { throw new IllegalArgumentException("订单不存在、状态不正确或无权确认"); } addTrack(orderId, ShopOrderStatus.COMPLETED, "已完成", "买家已确认完成", buyerId); }

    private Map<String, Object> loadPurchasableItem(long itemId, long buyerId) { List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT i.id, i.item_type AS itemType, i.title, i.price, i.stock, i.cover_url AS coverUrl, i.seller_user_id AS sellerUserId, i.service_duration_min AS serviceDurationMin, i.service_rule AS serviceRule FROM shop_item i WHERE i.id = ? AND i.status = ? AND i.del_flag = 0 AND i.seller_user_id = (SELECT CASE WHEN requester_user_id = ? THEN target_user_id ELSE requester_user_id END FROM couple_binding WHERE (requester_user_id = ? OR target_user_id = ?) AND status = 'ACCEPTED' LIMIT 1)", itemId, ShopItemStatus.ON_SHELF.getCode(), buyerId, buyerId, buyerId); if (rows.isEmpty()) { throw new IllegalArgumentException("商品不存在、已下架或不属于绑定对象"); } return rows.get(0); }
    private void validateRequest(ShopOrderCreateRequest request) { if (request == null || request.itemId() == null || request.quantity() == null || request.quantity() < 1) { throw new IllegalArgumentException("商品和购买数量不能为空"); } }
    private Long insertOrder(String orderNo, long buyerId, long sellerId, BigDecimal total, ShopOrderCreateRequest request) { LocalDateTime appointment = null; if (request.appointmentTime() != null && !request.appointmentTime().isBlank()) { appointment = LocalDateTime.parse(request.appointmentTime(), DATE_TIME); } jdbcTemplate.update("INSERT INTO shop_order (order_no, buyer_user_id, seller_user_id, total_amount, status, payment_status, payment_method, paid_amount, paid_time, remark, appointment_time, create_user, update_user) VALUES (?, ?, ?, ?, ?, 1, 1, ?, NOW(), ?, ?, ?, ?)", orderNo, buyerId, sellerId, total, ShopOrderStatus.WAIT_ACCEPT.getCode(), total, request.remark(), appointment == null ? null : java.sql.Timestamp.valueOf(appointment), buyerId, buyerId); return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class); }
    private void ensureAccount(long userId) { jdbcTemplate.update("INSERT IGNORE INTO shop_user_account (user_id, create_user, update_user) VALUES (?, ?, ?)", userId, userId, userId); }
    private void addTrack(long orderId, ShopOrderStatus status, String title, String description, long userId) { jdbcTemplate.update("INSERT INTO shop_order_track (order_id, status, title, description, operate_user_id, create_user, update_user) VALUES (?, ?, ?, ?, ?, ?, ?)", orderId, status.getCode(), title, description, userId, userId, userId); }
}




package com.tzn.shop.service.impl;

import com.tzn.shop.dto.ShopReviewCreateRequest;
import com.tzn.shop.enums.ShopOrderStatus;
import com.tzn.shop.service.CurrentUserService;
import com.tzn.shop.service.ShopReviewService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopReviewServiceImpl implements ShopReviewService {
    private final JdbcTemplate jdbcTemplate;
    private final CurrentUserService currentUserService;

    public ShopReviewServiceImpl(JdbcTemplate jdbcTemplate, CurrentUserService currentUserService) {
        this.jdbcTemplate = jdbcTemplate;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional
    public Map<String, Object> create(String authorization, long orderId, ShopReviewCreateRequest request) {
        long reviewerId = currentUserService.requireUserId(authorization);
        validate(request);
        List<Map<String, Object>> orders = jdbcTemplate.queryForList("SELECT o.id, o.buyer_user_id AS buyerUserId, o.seller_user_id AS sellerUserId, o.status, oi.item_id AS itemId FROM shop_order o JOIN shop_order_item oi ON oi.order_id = o.id WHERE o.id = ? AND o.del_flag = 0", orderId);
        if (orders.isEmpty()) { throw new IllegalArgumentException("订单不存在"); }
        Map<String, Object> order = orders.get(0);
        if (((Number) order.get("buyerUserId")).longValue() != reviewerId) { throw new IllegalArgumentException("只有下单方可以评价"); }
        if (((Number) order.get("status")).intValue() != ShopOrderStatus.COMPLETED.getCode()) { throw new IllegalArgumentException("订单完成后才可以评价"); }
        Integer exists = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM shop_item_review WHERE order_id = ? AND del_flag = 0", Integer.class, orderId);
        if (exists != null && exists > 0) { throw new IllegalArgumentException("该订单已经评价过了"); }
        jdbcTemplate.update("INSERT INTO shop_item_review (order_id, item_id, reviewer_user_id, seller_user_id, rating, content, create_user, update_user) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", orderId, order.get("itemId"), reviewerId, order.get("sellerUserId"), request.rating(), trim(request.content()), reviewerId, reviewerId);
        jdbcTemplate.update("UPDATE shop_order SET is_reviewed = 1, update_user = ?, update_time = NOW(), version = version + 1 WHERE id = ? AND buyer_user_id = ? AND del_flag = 0", reviewerId, orderId, reviewerId);
        Long reviewId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        Map<String, Object> result = new LinkedHashMap<>(); result.put("id", reviewId); result.put("orderId", orderId); result.put("rating", request.rating()); result.put("content", trim(request.content())); return result;
    }

    private void validate(ShopReviewCreateRequest request) {
        if (request == null || request.rating() == null || request.rating() < 1 || request.rating() > 5) { throw new IllegalArgumentException("评分必须是1至5星"); }
        if (request.content() != null && request.content().length() > 1000) { throw new IllegalArgumentException("评价内容不能超过1000字"); }
    }

    private String trim(String content) { return content == null ? null : content.trim(); }
}
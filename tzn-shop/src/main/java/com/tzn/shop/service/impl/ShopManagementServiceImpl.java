package com.tzn.shop.service.impl;

import com.tzn.shop.dto.ShopItemRequest;
import com.tzn.shop.enums.ShopItemStatus;
import com.tzn.shop.enums.ShopItemType;
import com.tzn.shop.service.CurrentUserService;
import com.tzn.shop.service.ShopManagementService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopManagementServiceImpl implements ShopManagementService {

    private static final String ITEM_COLUMNS = "i.id, i.category_id AS categoryId, i.item_type AS itemType, i.title, i.subtitle, i.description, i.price, i.stock, i.cover_url AS coverUrl, i.service_duration_min AS serviceDurationMin, i.service_rule AS serviceRule, i.is_recommended AS isRecommended, i.status, i.publish_time AS publishTime, i.off_shelf_time AS offShelfTime, i.off_shelf_reason AS offShelfReason";
    private final JdbcTemplate jdbcTemplate;
    private final CurrentUserService currentUserService;

    public ShopManagementServiceImpl(JdbcTemplate jdbcTemplate, CurrentUserService currentUserService) {
        this.jdbcTemplate = jdbcTemplate;
        this.currentUserService = currentUserService;
    }

    @Override
    public Map<String, Object> overview(String authorization, Integer itemType, Integer status) {
        long userId = currentUserService.requireUserId(authorization);
        StringBuilder sql = new StringBuilder("SELECT ").append(ITEM_COLUMNS)
                .append(" FROM shop_item i WHERE i.seller_user_id = ? AND i.del_flag = 0");
        if (itemType != null) { ShopItemType.fromCode(itemType); sql.append(" AND i.item_type = ?"); }
        if (status != null) { ShopItemStatus.fromCode(status); sql.append(" AND i.status = ?"); }
        sql.append(" ORDER BY i.update_time DESC, i.id DESC");
        List<Object> args = new java.util.ArrayList<>(); args.add(userId); if (itemType != null) args.add(itemType); if (status != null) args.add(status);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", jdbcTemplate.queryForList(sql.toString(), args.toArray()));
        result.put("publishedCount", count(userId, ShopItemStatus.ON_SHELF.getCode()));
        result.put("pendingOrderCount", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM shop_order WHERE seller_user_id = ? AND status IN (1, 2) AND del_flag = 0", Integer.class, userId));
        result.put("todayIncome", jdbcTemplate.queryForObject("SELECT COALESCE(SUM(total_amount), 0) FROM shop_order WHERE seller_user_id = ? AND status = 3 AND create_time >= CURDATE() AND del_flag = 0", java.math.BigDecimal.class, userId));
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> create(String authorization, ShopItemRequest request) {
        long userId = currentUserService.requireUserId(authorization); validate(request);
        int status = request.status() == null ? ShopItemStatus.DRAFT.getCode() : request.status();
        ShopItemStatus.fromCode(status);
        jdbcTemplate.update("INSERT INTO shop_item (category_id, item_type, title, subtitle, description, price, stock, cover_url, seller_user_id, service_duration_min, service_rule, status, publish_time, create_user, update_user) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", request.categoryId(), request.itemType(), request.title().trim(), request.subtitle(), request.description(), request.price(), request.stock(), request.coverUrl(), userId, request.serviceDurationMin(), request.serviceRule(), status, status == 1 ? new java.util.Date() : null, userId, userId);
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        saveImages(id, userId, request.imageUrls());
        return Map.of("id", id);
    }

    @Override
    @Transactional
    public Map<String, Object> update(String authorization, long itemId, ShopItemRequest request) {
        long userId = currentUserService.requireUserId(authorization); validate(request);
        int status = request.status() == null ? ShopItemStatus.DRAFT.getCode() : request.status(); ShopItemStatus.fromCode(status);
        int updated = jdbcTemplate.update("UPDATE shop_item SET category_id = ?, item_type = ?, title = ?, subtitle = ?, description = ?, price = ?, stock = ?, cover_url = ?, service_duration_min = ?, service_rule = ?, status = ?, publish_time = CASE WHEN ? = 1 AND publish_time IS NULL THEN NOW() ELSE publish_time END, update_user = ?, update_time = NOW(), version = version + 1 WHERE id = ? AND seller_user_id = ? AND del_flag = 0", request.categoryId(), request.itemType(), request.title().trim(), request.subtitle(), request.description(), request.price(), request.stock(), request.coverUrl(), request.serviceDurationMin(), request.serviceRule(), status, status, userId, itemId, userId);
        if (updated == 0) throw new IllegalArgumentException("商品不存在或无权修改");
        saveImages(itemId, userId, request.imageUrls()); return Map.of("id", itemId);
    }

    @Override
    public void changeStatus(String authorization, long itemId, Integer status) {
        long userId = currentUserService.requireUserId(authorization); ShopItemStatus.fromCode(status);
        int updated = jdbcTemplate.update("UPDATE shop_item SET status = ?, publish_time = CASE WHEN ? = 1 THEN NOW() ELSE publish_time END, off_shelf_time = CASE WHEN ? = 2 THEN NOW() ELSE off_shelf_time END, update_user = ?, update_time = NOW(), version = version + 1 WHERE id = ? AND seller_user_id = ? AND del_flag = 0", status, status, status, userId, itemId, userId);
        if (updated == 0) throw new IllegalArgumentException("商品不存在或无权操作");
    }

    @Override
    public void delete(String authorization, long itemId) {
        long userId = currentUserService.requireUserId(authorization);
        if (jdbcTemplate.update("UPDATE shop_item SET del_flag = 1, update_user = ?, update_time = NOW(), version = version + 1 WHERE id = ? AND seller_user_id = ? AND del_flag = 0", userId, itemId, userId) == 0) throw new IllegalArgumentException("商品不存在或无权删除");
    }

    private int count(long userId, int status) { return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM shop_item WHERE seller_user_id = ? AND status = ? AND del_flag = 0", Integer.class, userId, status); }
    private void validate(ShopItemRequest request) { if (request == null || request.itemType() == null || request.title() == null || request.title().isBlank() || request.price() == null || request.stock() == null) throw new IllegalArgumentException("商品类型、名称、价格和库存不能为空"); ShopItemType.fromCode(request.itemType()); if (request.price().signum() < 0 || request.stock() < 0) throw new IllegalArgumentException("价格和库存不能为负数"); }
    private void saveImages(Long itemId, long userId, List<String> imageUrls) { if (imageUrls == null) return; jdbcTemplate.update("UPDATE shop_item_image SET del_flag = 1, update_user = ? WHERE item_id = ? AND del_flag = 0", userId, itemId); for (int i = 0; i < imageUrls.size(); i++) { if (imageUrls.get(i) != null && !imageUrls.get(i).isBlank()) jdbcTemplate.update("INSERT INTO shop_item_image (item_id, image_url, sort_no, create_user, update_user) VALUES (?, ?, ?, ?, ?)", itemId, imageUrls.get(i), i, userId, userId); } }
    @Override
    public void changeRecommendation(String authorization, long itemId, Integer recommended) {
        long userId = currentUserService.requireUserId(authorization);
        if (recommended == null || (recommended != 0 && recommended != 1)) {
            throw new IllegalArgumentException("推荐状态只能是0或1");
        }
        int updated = jdbcTemplate.update("UPDATE shop_item SET is_recommended = ?, update_user = ?, update_time = NOW(), version = version + 1 WHERE id = ? AND seller_user_id = ? AND item_type IN (1, 2) AND del_flag = 0", recommended, userId, itemId, userId);
        if (updated == 0) {
            throw new IllegalArgumentException("商品不存在或无权操作");
        }
    }}

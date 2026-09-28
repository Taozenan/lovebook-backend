package com.tzn.shop.service.impl;

import com.tzn.shop.enums.ShopEnabledStatus;
import com.tzn.shop.enums.ShopItemStatus;
import com.tzn.shop.enums.ShopItemType;
import com.tzn.shop.service.CurrentUserService;
import com.tzn.shop.service.ShopService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {

    private static final String ITEM_COLUMNS = """
            i.id, i.item_type AS itemType, i.title, i.subtitle, i.description, i.price, i.stock,
            i.cover_url AS coverUrl, i.service_duration_min AS serviceDurationMin,
            u.id AS sellerId, u.username AS sellerPhone, u.nickname AS sellerName
            """;

    private final JdbcTemplate jdbcTemplate;
    private final CurrentUserService currentUserService;

    public ShopServiceImpl(JdbcTemplate jdbcTemplate, CurrentUserService currentUserService) {
        this.jdbcTemplate = jdbcTemplate;
        this.currentUserService = currentUserService;
    }

    @Override
    public Map<String, Object> home(String authorization) {
        long partnerId = partnerId(authorization);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("banners", jdbcTemplate.queryForList("""
                SELECT id, title, image_url AS imageUrl, link_item_id AS linkItemId
                FROM shop_banner
                WHERE status = ? AND del_flag = 0
                  AND (start_time IS NULL OR start_time <= NOW())
                  AND (end_time IS NULL OR end_time >= NOW())
                ORDER BY sort_no DESC, id DESC
                """, ShopEnabledStatus.ENABLED.getCode()));
        result.put("categories", jdbcTemplate.queryForList("""
                SELECT id, category_type AS categoryType, name, icon_url AS iconUrl
                FROM shop_category WHERE status = ? AND del_flag = 0
                ORDER BY sort_no ASC, id ASC
                """, ShopEnabledStatus.ENABLED.getCode()));
        result.put("partner", partnerProfile(partnerId));
        result.put("recommendedFoods", queryItems(ShopItemType.FOOD, partnerId, true, 6));
        result.put("hotServices", queryItems(ShopItemType.SERVICE, partnerId, true, 6));
        return result;
    }

    @Override
    public Map<String, Object> list(String authorization, Integer itemType, String keyword, Long categoryId, Boolean recommended) {
        long sellerId = partnerId(authorization);
        StringBuilder sql = new StringBuilder("SELECT ").append(ITEM_COLUMNS)
                .append(" FROM shop_item i JOIN sys_user u ON u.id = i.seller_user_id")
                .append(" WHERE i.seller_user_id = ? AND i.status = ? AND i.del_flag = 0");
        if (itemType != null) {
            sql.append(" AND i.item_type = ?");
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND i.title LIKE ?");
        }
        if (categoryId != null) {
            sql.append(" AND i.category_id = ?");
        }
        if (Boolean.TRUE.equals(recommended)) {
            sql.append(" AND i.is_recommended = 1");
        }
        sql.append(" ORDER BY i.publish_time DESC, i.id DESC");
        List<Object> args = new java.util.ArrayList<>();
        args.add(sellerId); args.add(ShopItemStatus.ON_SHELF.getCode());
        if (itemType != null) { args.add(itemType); }
        if (keyword != null && !keyword.isBlank()) { args.add("%" + keyword.trim() + "%"); }
        if (categoryId != null) { args.add(categoryId); }
        List<Map<String, Object>> items = jdbcTemplate.queryForList(sql.toString(), args.toArray());
        return Map.of("items", items);
    }

    @Override
    public Map<String, Object> detail(long itemId) {
        List<Map<String, Object>> items = jdbcTemplate.queryForList("SELECT " + ITEM_COLUMNS + " FROM shop_item i JOIN sys_user u ON u.id = i.seller_user_id WHERE i.id = ? AND i.status = ? AND i.del_flag = 0", itemId, ShopItemStatus.ON_SHELF.getCode());
        if (items.isEmpty()) {
            throw new IllegalArgumentException("商品不存在或已下架");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("item", items.get(0));
        result.put("images", jdbcTemplate.queryForList("SELECT id, image_url AS imageUrl, sort_no AS sortNo FROM shop_item_image WHERE item_id = ? AND del_flag = 0 ORDER BY sort_no ASC, id ASC", itemId));
        result.put("reviews", jdbcTemplate.queryForList("SELECT r.id, r.rating, r.content, r.create_time AS createTime, u.nickname AS reviewerName, u.avatar_url AS reviewerAvatarUrl FROM shop_item_review r JOIN sys_user u ON u.id = r.reviewer_user_id WHERE r.item_id = ? AND r.del_flag = 0 ORDER BY r.create_time DESC, r.id DESC", itemId));
        result.put("reviewSummary", jdbcTemplate.queryForMap("SELECT COALESCE(AVG(rating), 0) AS averageRating, COUNT(*) AS reviewCount FROM shop_item_review WHERE item_id = ? AND del_flag = 0", itemId));
        return result;
    }

    private List<Map<String, Object>> queryItems(ShopItemType itemType, long sellerId, boolean recommendedOnly, int limit) {
        String recommended = recommendedOnly ? " AND i.is_recommended = 1" : "";
        return jdbcTemplate.queryForList("SELECT " + ITEM_COLUMNS + " FROM shop_item i JOIN sys_user u ON u.id = i.seller_user_id WHERE i.item_type = ? AND i.seller_user_id = ? AND i.status = ? AND i.del_flag = 0" + recommended + " ORDER BY i.sort_no DESC, i.id DESC LIMIT ?", itemType.getCode(), sellerId, ShopItemStatus.ON_SHELF.getCode(), limit);
    }

    private Map<String, Object> partnerProfile(long partnerId) {
        if (partnerId < 0) {
            return Map.of("bound", false, "nickname", "");
        }
        List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT nickname, username AS phone FROM sys_user WHERE id = ?", partnerId);
        if (users.isEmpty()) {
            return Map.of("bound", false, "nickname", "");
        }
        Map<String, Object> profile = new LinkedHashMap<>(users.get(0));
        profile.put("bound", true);
        return profile;
    }

    private long partnerId(String authorization) {
        long currentId = currentUserService.requireUserId(authorization);
        List<Long> partnerIds = jdbcTemplate.queryForList(
                "SELECT CASE WHEN requester_user_id = ? THEN target_user_id ELSE requester_user_id END "
                        + "FROM couple_binding WHERE (requester_user_id = ? OR target_user_id = ?) "
                        + "AND status = 'ACCEPTED' LIMIT 1",
                Long.class, currentId, currentId, currentId);
        return partnerIds.isEmpty() ? -1L : partnerIds.get(0);
    }
}

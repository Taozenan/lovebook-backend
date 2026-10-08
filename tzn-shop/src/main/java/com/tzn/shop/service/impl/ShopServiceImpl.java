package com.tzn.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tzn.common.entity.CoupleBinding;
import com.tzn.common.entity.ShopBanner;
import com.tzn.common.entity.ShopCategory;
import com.tzn.common.entity.ShopItem;
import com.tzn.common.entity.ShopItemImage;
import com.tzn.common.entity.ShopItemReview;
import com.tzn.common.entity.SysUser;
import com.tzn.common.mapper.CoupleBindingMapper;
import com.tzn.common.mapper.ShopBannerMapper;
import com.tzn.common.mapper.ShopCategoryMapper;
import com.tzn.common.mapper.ShopItemImageMapper;
import com.tzn.common.mapper.ShopItemMapper;
import com.tzn.common.mapper.ShopItemReviewMapper;
import com.tzn.common.mapper.SysUserMapper;
import com.tzn.shop.enums.ShopEnabledStatus;
import com.tzn.shop.enums.ShopItemStatus;
import com.tzn.shop.enums.ShopItemType;
import com.tzn.shop.service.CurrentUserService;
import com.tzn.shop.service.ShopService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {
    private final ShopItemMapper itemMapper;
    private final ShopItemImageMapper imageMapper;
    private final ShopCategoryMapper categoryMapper;
    private final ShopBannerMapper bannerMapper;
    private final ShopItemReviewMapper reviewMapper;
    private final SysUserMapper userMapper;
    private final CoupleBindingMapper bindingMapper;
    private final CurrentUserService currentUserService;

    public ShopServiceImpl(ShopItemMapper itemMapper, ShopItemImageMapper imageMapper, ShopCategoryMapper categoryMapper, ShopBannerMapper bannerMapper, ShopItemReviewMapper reviewMapper, SysUserMapper userMapper, CoupleBindingMapper bindingMapper, CurrentUserService currentUserService) {
        this.itemMapper = itemMapper;
        this.imageMapper = imageMapper;
        this.categoryMapper = categoryMapper;
        this.bannerMapper = bannerMapper;
        this.reviewMapper = reviewMapper;
        this.userMapper = userMapper;
        this.bindingMapper = bindingMapper;
        this.currentUserService = currentUserService;
    }

    @Override
    public Map<String, Object> home(String authorization) {
        long pid = partnerId(authorization);
        Map<String, Object> r = new LinkedHashMap<>();
        List<Map<String, Object>> banners = new ArrayList<>();
        bannerMapper.selectList(new LambdaQueryWrapper<ShopBanner>().eq(ShopBanner::getStatus, 1).eq(ShopBanner::getDelFlag, 0).orderByDesc(ShopBanner::getSortNo).orderByDesc(ShopBanner::getId)).forEach(b -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", b.getId());
            m.put("title", b.getTitle());
            m.put("imageUrl", b.getImageUrl());
            m.put("linkItemId", b.getLinkItemId());
            banners.add(m);
        });
        r.put("banners", banners);
        List<Map<String, Object>> cats = new ArrayList<>();
        categoryMapper.selectList(new LambdaQueryWrapper<ShopCategory>().eq(ShopCategory::getStatus, 1).eq(ShopCategory::getDelFlag, 0).orderByAsc(ShopCategory::getSortNo).orderByAsc(ShopCategory::getId)).forEach(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("categoryType", c.getCategoryType());
            m.put("name", c.getName());
            m.put("iconUrl", c.getIconUrl());
            cats.add(m);
        });
        r.put("categories", cats);
        r.put("partner", partnerProfile(pid));
        r.put("recommendedFoods", queryItems(ShopItemType.FOOD, pid, true, 6));
        r.put("hotServices", queryItems(ShopItemType.SERVICE, pid, true, 6));
        return r;
    }

    @Override
    public Map<String, Object> list(String authorization, Integer itemType, String keyword, Long categoryId, Boolean recommended) {
        long seller = partnerId(authorization);
        LambdaQueryWrapper<ShopItem> w = new LambdaQueryWrapper<ShopItem>().eq(ShopItem::getSellerUserId, seller).eq(ShopItem::getStatus, 1).eq(ShopItem::getDelFlag, 0).orderByDesc(ShopItem::getPublishTime).orderByDesc(ShopItem::getId);
        if (itemType != null) w.eq(ShopItem::getItemType, itemType);
        if (keyword != null && !keyword.isBlank()) w.like(ShopItem::getTitle, keyword.trim());
        if (categoryId != null) w.eq(ShopItem::getCategoryId, categoryId);
        if (Boolean.TRUE.equals(recommended)) w.eq(ShopItem::getIsRecommended, 1);
        List<Map<String, Object>> items = new ArrayList<>();
        itemMapper.selectList(w).forEach(i -> items.add(itemMap(i, userMapper.selectById(i.getSellerUserId()))));
        return Map.of("items", items);
    }

    @Override
    public Map<String, Object> detail(long itemId) {
        ShopItem i = itemMapper.selectOne(new LambdaQueryWrapper<ShopItem>().eq(ShopItem::getId, itemId).eq(ShopItem::getStatus, 1).eq(ShopItem::getDelFlag, 0));
        if (i == null) throw new IllegalArgumentException("商品不存在或已下架");
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("item", itemMap(i, userMapper.selectById(i.getSellerUserId())));
        List<Map<String, Object>> images = new ArrayList<>();
        imageMapper.selectList(new LambdaQueryWrapper<ShopItemImage>().eq(ShopItemImage::getItemId, itemId).eq(ShopItemImage::getDelFlag, 0).orderByAsc(ShopItemImage::getSortNo).orderByAsc(ShopItemImage::getId)).forEach(x -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", x.getId());
            m.put("imageUrl", x.getImageUrl());
            m.put("sortNo", x.getSortNo());
            images.add(m);
        });
        r.put("images", images);
        List<Map<String, Object>> reviews = new ArrayList<>();
        List<ShopItemReview> rs = reviewMapper.selectList(new LambdaQueryWrapper<ShopItemReview>().eq(ShopItemReview::getItemId, itemId).eq(ShopItemReview::getDelFlag, 0).orderByDesc(ShopItemReview::getCreateTime).orderByDesc(ShopItemReview::getId));
        for (ShopItemReview x : rs) {
            SysUser u = userMapper.selectById(x.getReviewerUserId());
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", x.getId());
            m.put("rating", x.getRating());
            m.put("content", x.getContent());
            m.put("createTime", x.getCreateTime());
            m.put("reviewerName", u == null ? "" : u.getNickname());
            m.put("reviewerAvatarUrl", u == null ? null : u.getAvatarUrl());
            reviews.add(m);
        }
        r.put("reviews", reviews);
        double avg = rs.stream().mapToInt(ShopItemReview::getRating).average().orElse(0);
        r.put("reviewSummary", Map.of("averageRating", BigDecimal.valueOf(avg), "reviewCount", rs.size()));
        return r;
    }

    private List<Map<String, Object>> queryItems(ShopItemType type, long seller, boolean rec, int limit) {
        if (seller < 0) return List.of();
        LambdaQueryWrapper<ShopItem> w = new LambdaQueryWrapper<ShopItem>().eq(ShopItem::getItemType, type.getCode()).eq(ShopItem::getSellerUserId, seller).eq(ShopItem::getStatus, 1).eq(ShopItem::getDelFlag, 0).orderByDesc(ShopItem::getSortNo).orderByDesc(ShopItem::getId);
        if (rec) w.eq(ShopItem::getIsRecommended, 1);
        List<Map<String, Object>> r = new ArrayList<>();
        itemMapper.selectList(w).stream().limit(limit).forEach(i -> r.add(itemMap(i, userMapper.selectById(i.getSellerUserId()))));
        return r;
    }

    private Map<String, Object> partnerProfile(long id) {
        if (id < 0) return Map.of("bound", false, "nickname", "");
        SysUser u = userMapper.selectById(id);
        if (u == null) return Map.of("bound", false, "nickname", "");
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("bound", true);
        m.put("nickname", u.getNickname());
        m.put("phone", u.getUsername());
        return m;
    }

    private long partnerId(String authorization) {
        long uid = currentUserService.requireUserId(authorization);
        CoupleBinding b = bindingMapper.selectOne(new LambdaQueryWrapper<CoupleBinding>().and(w -> w.eq(CoupleBinding::getRequesterUserId, uid).or().eq(CoupleBinding::getTargetUserId, uid)).eq(CoupleBinding::getStatus, "ACCEPTED").last("LIMIT 1"));
        if (b == null) return -1;
        return uid == b.getRequesterUserId() ? b.getTargetUserId() : b.getRequesterUserId();
    }

    private Map<String, Object> itemMap(ShopItem i, SysUser u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", i.getId());
        m.put("itemType", i.getItemType());
        m.put("title", i.getTitle());
        m.put("subtitle", i.getSubtitle());
        m.put("description", i.getDescription());
        m.put("price", i.getPrice());
        m.put("stock", i.getStock());
        m.put("coverUrl", i.getCoverUrl());
        m.put("serviceDurationMin", i.getServiceDurationMin());
        m.put("sellerId", i.getSellerUserId());
        m.put("sellerPhone", u == null ? null : u.getUsername());
        m.put("sellerName", u == null ? null : u.getNickname());
        return m;
    }
}
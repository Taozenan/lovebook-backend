package com.tzn.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tzn.common.entity.*;
import com.tzn.common.mapper.*;
import com.tzn.shop.enums.ShopItemType;
import com.tzn.shop.service.CurrentUserService;
import com.tzn.shop.service.ShopService;
import com.tzn.shop.vo.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

  public ShopServiceImpl(
          ShopItemMapper itemMapper,
          ShopItemImageMapper imageMapper,
          ShopCategoryMapper categoryMapper,
          ShopBannerMapper bannerMapper,
          ShopItemReviewMapper reviewMapper,
          SysUserMapper userMapper,
          CoupleBindingMapper bindingMapper,
          CurrentUserService currentUserService) {
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
  public ShopHomeVo home(String authorization) {
    long pid = partnerId(authorization);
    List<ShopBannerVo> banners = new ArrayList<>();
    bannerMapper
            .selectList(
                    new LambdaQueryWrapper<ShopBanner>()
                            .eq(ShopBanner::getStatus, 1)
                            .eq(ShopBanner::getDelFlag, 0)
                            .orderByDesc(ShopBanner::getSortNo)
                            .orderByDesc(ShopBanner::getId))
            .forEach(
                    b -> {
                      banners.add(
                              new ShopBannerVo(b.getId(), b.getTitle(), b.getImageUrl(), b.getLinkItemId()));
                    });
    List<ShopCategoryVo> cats = new ArrayList<>();
    categoryMapper
            .selectList(
                    new LambdaQueryWrapper<ShopCategory>()
                            .eq(ShopCategory::getStatus, 1)
                            .eq(ShopCategory::getDelFlag, 0)
                            .orderByAsc(ShopCategory::getSortNo)
                            .orderByAsc(ShopCategory::getId))
            .forEach(
                    c -> {
                      cats.add(
                              new ShopCategoryVo(c.getId(), c.getCategoryType(), c.getName(), c.getIconUrl()));
                    });
    return new ShopHomeVo(
            banners,
            cats,
            partnerProfile(pid),
            queryItems(ShopItemType.FOOD, pid, true, 6),
            queryItems(ShopItemType.SERVICE, pid, true, 6));
  }

  @Override
  public ShopItemVoList list(
          String authorization,
          Integer itemType,
          String keyword,
          Long categoryId,
          Boolean recommended) {
    long seller = partnerId(authorization);
    LambdaQueryWrapper<ShopItem> w =
            new LambdaQueryWrapper<ShopItem>()
                    .eq(ShopItem::getSellerUserId, seller)
                    .eq(ShopItem::getStatus, 1)
                    .eq(ShopItem::getDelFlag, 0)
                    .orderByDesc(ShopItem::getPublishTime)
                    .orderByDesc(ShopItem::getId);
    if (itemType != null) w.eq(ShopItem::getItemType, itemType);
    if (keyword != null && !keyword.isBlank()) w.like(ShopItem::getTitle, keyword.trim());
    if (categoryId != null) w.eq(ShopItem::getCategoryId, categoryId);
    if (Boolean.TRUE.equals(recommended)) w.eq(ShopItem::getIsRecommended, 1);
    List<ShopItemVo> items = new ArrayList<>();
    itemMapper
            .selectList(w)
            .forEach(i -> items.add(itemMap(i, userMapper.selectById(i.getSellerUserId()))));
    return new ShopItemVoList(items);
  }

  @Override
  public ShopItemVoDetail detail(long itemId) {
    ShopItem i =
            itemMapper.selectOne(
                    new LambdaQueryWrapper<ShopItem>()
                            .eq(ShopItem::getId, itemId)
                            .eq(ShopItem::getStatus, 1)
                            .eq(ShopItem::getDelFlag, 0));
    if (i == null) throw new IllegalArgumentException("商品不存在或已下架");
    List<ShopItemVoImage> images = new ArrayList<>();
    imageMapper
            .selectList(
                    new LambdaQueryWrapper<ShopItemImage>()
                            .eq(ShopItemImage::getItemId, itemId)
                            .eq(ShopItemImage::getDelFlag, 0)
                            .orderByAsc(ShopItemImage::getSortNo)
                            .orderByAsc(ShopItemImage::getId))
            .forEach(
                    x -> {
                      images.add(new ShopItemVoImage(x.getId(), x.getImageUrl(), x.getSortNo()));
                    });
    List<ShopItemReviewVo> reviews = new ArrayList<>();
    List<ShopItemReview> rs =
            reviewMapper.selectList(
                    new LambdaQueryWrapper<ShopItemReview>()
                            .eq(ShopItemReview::getItemId, itemId)
                            .eq(ShopItemReview::getDelFlag, 0)
                            .orderByDesc(ShopItemReview::getCreateTime)
                            .orderByDesc(ShopItemReview::getId));
    for (ShopItemReview x : rs) {
      SysUser u = userMapper.selectById(x.getReviewerUserId());
      reviews.add(
              new ShopItemReviewVo(
                      x.getId(),
                      x.getRating(),
                      x.getContent(),
                      x.getCreateTime(),
                      u == null ? "" : u.getNickname(),
                      u == null ? null : u.getAvatarUrl()));
    }
    double avg = rs.stream().mapToInt(ShopItemReview::getRating).average().orElse(0);
    return new ShopItemVoDetail(
            itemMap(i, userMapper.selectById(i.getSellerUserId())),
            images,
            reviews,
            new ShopReviewSummaryVo(BigDecimal.valueOf(avg), rs.size()));
  }

  private List<ShopItemVo> queryItems(ShopItemType type, long seller, boolean rec, int limit) {
    if (seller < 0) return List.of();
    LambdaQueryWrapper<ShopItem> w =
            new LambdaQueryWrapper<ShopItem>()
                    .eq(ShopItem::getItemType, type.getCode())
                    .eq(ShopItem::getSellerUserId, seller)
                    .eq(ShopItem::getStatus, 1)
                    .eq(ShopItem::getDelFlag, 0)
                    .orderByDesc(ShopItem::getSortNo)
                    .orderByDesc(ShopItem::getId);
    if (rec) w.eq(ShopItem::getIsRecommended, 1);
    List<ShopItemVo> r = new ArrayList<>();
    itemMapper.selectList(w).stream()
            .limit(limit)
            .forEach(i -> r.add(itemMap(i, userMapper.selectById(i.getSellerUserId()))));
    return r;
  }

  private ShopPartnerVo partnerProfile(long id) {
    if (id < 0) return new ShopPartnerVo(false, "", null);
    SysUser u = userMapper.selectById(id);
    if (u == null) return new ShopPartnerVo(false, "", null);
    return new ShopPartnerVo(true, u.getNickname(), u.getUsername());
  }

  private long partnerId(String authorization) {
    long uid = currentUserService.requireUserId(authorization);
    CoupleBinding b =
            bindingMapper.selectOne(
                    new LambdaQueryWrapper<CoupleBinding>()
                            .and(
                                    w ->
                                            w.eq(CoupleBinding::getRequesterUserId, uid)
                                                    .or()
                                                    .eq(CoupleBinding::getTargetUserId, uid))
                            .eq(CoupleBinding::getStatus, "ACCEPTED")
                            .last("LIMIT 1"));
    if (b == null) return -1;
    return uid == b.getRequesterUserId() ? b.getTargetUserId() : b.getRequesterUserId();
  }

  private ShopItemVo itemMap(ShopItem i, SysUser u) {
    return new ShopItemVo(
            i.getId(),
            i.getItemType(),
            i.getTitle(),
            i.getSubtitle(),
            i.getDescription(),
            i.getPrice(),
            i.getStock(),
            i.getCoverUrl(),
            i.getServiceDurationMin(),
            i.getSellerUserId(),
            u == null ? null : u.getUsername(),
            u == null ? null : u.getNickname());
  }
}

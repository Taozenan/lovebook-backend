package com.tzn.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tzn.common.entity.ShopItem;
import com.tzn.common.entity.ShopItemImage;
import com.tzn.common.entity.ShopOrder;
import com.tzn.common.enums.DeletedStatus;
import com.tzn.common.mapper.ShopItemImageMapper;
import com.tzn.common.mapper.ShopItemMapper;
import com.tzn.common.mapper.ShopOrderMapper;
import com.tzn.shop.dto.ShopItemRequest;
import com.tzn.shop.enums.ShopItemStatus;
import com.tzn.shop.enums.ShopItemType;
import com.tzn.shop.enums.ShopOrderStatus;
import com.tzn.shop.service.CurrentUserService;
import com.tzn.shop.service.ShopManagementService;
import com.tzn.shop.vo.ShopIdVo;
import com.tzn.shop.vo.ShopManagementItemVo;
import com.tzn.shop.vo.ShopManagementOverviewVo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopManagementServiceImpl implements ShopManagementService {
  private final ShopItemMapper itemMapper;
  private final ShopItemImageMapper imageMapper;
  private final ShopOrderMapper orderMapper;
  private final CurrentUserService currentUserService;

  public ShopManagementServiceImpl(
      ShopItemMapper itemMapper,
      ShopItemImageMapper imageMapper,
      ShopOrderMapper orderMapper,
      CurrentUserService currentUserService) {
    this.itemMapper = itemMapper;
    this.imageMapper = imageMapper;
    this.orderMapper = orderMapper;
    this.currentUserService = currentUserService;
  }

  @Override
  public ShopManagementOverviewVo overview(String authorization, Integer itemType, Integer status) {
    long uid = currentUserService.requireUserId(authorization);
    LambdaQueryWrapper<ShopItem> w =
        new LambdaQueryWrapper<ShopItem>()
            .eq(ShopItem::getSellerUserId, uid)
            .eq(ShopItem::getDelFlag, DeletedStatus.NOT_DELETED.getCode())
            .orderByDesc(ShopItem::getUpdateTime)
            .orderByDesc(ShopItem::getId);
    if (itemType != null) {
      ShopItemType.fromCode(itemType);
      w.eq(ShopItem::getItemType, itemType);
    }
    if (status != null) {
      ShopItemStatus.fromCode(status);
      w.eq(ShopItem::getStatus, status);
    }
    List<ShopManagementItemVo> items = new ArrayList<>();
    itemMapper.selectList(w).forEach(i -> items.add(itemMap(i)));
    long pendingOrderCount =
        orderMapper.selectCount(
            new LambdaQueryWrapper<ShopOrder>()
                .eq(ShopOrder::getSellerUserId, uid)
                .in(
                    ShopOrder::getStatus,
                    List.of(
                        ShopOrderStatus.WAIT_ACCEPT.getCode(), ShopOrderStatus.ACCEPTED.getCode()))
                .eq(ShopOrder::getDelFlag, DeletedStatus.NOT_DELETED.getCode()));
    BigDecimal income =
        orderMapper
            .selectList(
                new LambdaQueryWrapper<ShopOrder>()
                    .eq(ShopOrder::getSellerUserId, uid)
                    .eq(ShopOrder::getStatus, ShopOrderStatus.COMPLETED.getCode())
                    .eq(ShopOrder::getDelFlag, DeletedStatus.NOT_DELETED.getCode())
                    .ge(ShopOrder::getCreateTime, LocalDate.now().atStartOfDay()))
            .stream()
            .map(ShopOrder::getTotalAmount)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return new ShopManagementOverviewVo(
        items, count(uid, ShopItemStatus.ON_SHELF.getCode()), pendingOrderCount, income);
  }

  @Override
  @Transactional
  public ShopIdVo create(String authorization, ShopItemRequest request) {
    long uid = currentUserService.requireUserId(authorization);
    validate(request);
    int st = request.status() == null ? ShopItemStatus.DRAFT.getCode() : request.status();
    ShopItemStatus.fromCode(st);
    ShopItem i = new ShopItem();
    fill(i, request, uid, st);
    itemMapper.insert(i);
    saveImages(i.getId(), uid, request.imageUrls());
    return new ShopIdVo(i.getId());
  }

  @Override
  @Transactional
  public ShopIdVo update(String authorization, long itemId, ShopItemRequest request) {
    long uid = currentUserService.requireUserId(authorization);
    validate(request);
    ShopItem i =
        itemMapper.selectOne(
            new LambdaQueryWrapper<ShopItem>()
                .eq(ShopItem::getId, itemId)
                .eq(ShopItem::getSellerUserId, uid)
                .eq(ShopItem::getDelFlag, DeletedStatus.NOT_DELETED.getCode()));
    if (i == null) throw new IllegalArgumentException("商品不存在或无权修改");
    int st = request.status() == null ? ShopItemStatus.DRAFT.getCode() : request.status();
    ShopItemStatus.fromCode(st);
    fill(i, request, uid, st);
    itemMapper.updateById(i);
    saveImages(itemId, uid, request.imageUrls());
    return new ShopIdVo(itemId);
  }

  @Override
  public void changeStatus(String authorization, long itemId, Integer status) {
    long uid = currentUserService.requireUserId(authorization);
    ShopItemStatus.fromCode(status);
    ShopItem i =
        itemMapper.selectOne(
            new LambdaQueryWrapper<ShopItem>()
                .eq(ShopItem::getId, itemId)
                .eq(ShopItem::getSellerUserId, uid)
                .eq(ShopItem::getDelFlag, DeletedStatus.NOT_DELETED.getCode()));
    if (i == null) throw new IllegalArgumentException("商品不存在或无权操作");
    i.setStatus(status);
    i.setUpdateUser(uid);
    i.setUpdateTime(LocalDateTime.now());
    if (status == 1 && i.getPublishTime() == null) i.setPublishTime(LocalDateTime.now());
    if (status == 2) i.setOffShelfTime(LocalDateTime.now());
    itemMapper.updateById(i);
  }

  @Override
  public void delete(String authorization, long itemId) {
    long uid = currentUserService.requireUserId(authorization);
    ShopItem i =
        itemMapper.selectOne(
            new LambdaQueryWrapper<ShopItem>()
                .eq(ShopItem::getId, itemId)
                .eq(ShopItem::getSellerUserId, uid)
                .eq(ShopItem::getDelFlag, DeletedStatus.NOT_DELETED.getCode()));
    if (i == null) throw new IllegalArgumentException("商品不存在或无权删除");
    i.setDelFlag(DeletedStatus.DELETED.getCode());
    i.setUpdateUser(uid);
    i.setUpdateTime(LocalDateTime.now());
    itemMapper.updateById(i);
  }

  @Override
  public void changeRecommendation(String authorization, long itemId, Integer recommended) {
    long uid = currentUserService.requireUserId(authorization);
    if (recommended == null || (recommended != 0 && recommended != 1))
      throw new IllegalArgumentException("推荐状态只能是0或1");
    ShopItem i =
        itemMapper.selectOne(
            new LambdaQueryWrapper<ShopItem>()
                .eq(ShopItem::getId, itemId)
                .eq(ShopItem::getSellerUserId, uid)
                .in(
                    ShopItem::getItemType,
                    List.of(ShopItemType.FOOD.getCode(), ShopItemType.SERVICE.getCode()))
                .eq(ShopItem::getDelFlag, DeletedStatus.NOT_DELETED.getCode()));
    if (i == null) throw new IllegalArgumentException("商品不存在或无权操作");
    i.setIsRecommended(recommended);
    i.setUpdateUser(uid);
    i.setUpdateTime(LocalDateTime.now());
    itemMapper.updateById(i);
  }

  private void fill(ShopItem i, ShopItemRequest r, long uid, int st) {
    i.setCategoryId(r.categoryId());
    i.setItemType(r.itemType());
    i.setTitle(r.title().trim());
    i.setSubtitle(r.subtitle());
    i.setDescription(r.description());
    i.setPrice(r.price());
    i.setStock(r.stock());
    i.setCoverUrl(r.coverUrl());
    i.setSellerUserId(uid);
    i.setServiceDurationMin(r.serviceDurationMin());
    i.setServiceRule(r.serviceRule());
    i.setStatus(st);
    i.setUpdateUser(uid);
    i.setUpdateTime(LocalDateTime.now());
    if (i.getCreateUser() == null) i.setCreateUser(uid);
    if (i.getCreateTime() == null) i.setCreateTime(LocalDateTime.now());
    if (st == 1 && i.getPublishTime() == null) i.setPublishTime(LocalDateTime.now());
  }

  private void saveImages(Long id, long uid, List<String> urls) {
    if (urls == null) return;
    List<ShopItemImage> old =
        imageMapper.selectList(
            new LambdaQueryWrapper<ShopItemImage>()
                .eq(ShopItemImage::getItemId, id)
                .eq(ShopItemImage::getDelFlag, DeletedStatus.NOT_DELETED.getCode()));
    old.forEach(
        x -> {
          x.setDelFlag(DeletedStatus.DELETED.getCode());
          x.setUpdateUser(uid);
          x.setUpdateTime(LocalDateTime.now());
          imageMapper.updateById(x);
        });
    for (int n = 0; n < urls.size(); n++) {
      String u = urls.get(n);
      if (u != null && !u.isBlank()) {
        ShopItemImage x = new ShopItemImage();
        x.setItemId(id);
        x.setImageUrl(u);
        x.setSortNo(n);
        x.setDelFlag(DeletedStatus.NOT_DELETED.getCode());
        x.setCreateUser(uid);
        x.setUpdateUser(uid);
        x.setCreateTime(LocalDateTime.now());
        x.setUpdateTime(LocalDateTime.now());
        imageMapper.insert(x);
      }
    }
  }

  private int count(long uid, int st) {
    return itemMapper
        .selectCount(
            new LambdaQueryWrapper<ShopItem>()
                .eq(ShopItem::getSellerUserId, uid)
                .eq(ShopItem::getStatus, st)
                .eq(ShopItem::getDelFlag, DeletedStatus.NOT_DELETED.getCode()))
        .intValue();
  }

  private void validate(ShopItemRequest r) {
    if (r == null
        || r.itemType() == null
        || r.title() == null
        || r.title().isBlank()
        || r.price() == null
        || r.stock() == null) throw new IllegalArgumentException("商品类型、名称、价格和库存不能为空");
    ShopItemType.fromCode(r.itemType());
    if (r.price().signum() < 0 || r.stock() < 0) throw new IllegalArgumentException("价格和库存不能为负数");
  }

  private ShopManagementItemVo itemMap(ShopItem i) {
    return new ShopManagementItemVo(
        i.getId(),
        i.getCategoryId(),
        i.getItemType(),
        i.getTitle(),
        i.getSubtitle(),
        i.getDescription(),
        i.getPrice(),
        i.getStock(),
        i.getCoverUrl(),
        i.getServiceDurationMin(),
        i.getServiceRule(),
        i.getIsRecommended(),
        i.getStatus(),
        i.getPublishTime(),
        i.getOffShelfTime(),
        i.getOffShelfReason());
  }
}

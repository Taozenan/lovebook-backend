package com.tzn.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tzn.common.entity.*;
import com.tzn.common.mapper.*;
import com.tzn.shop.dto.ShopOrderCreateRequest;
import com.tzn.shop.enums.ShopOrderStatus;
import com.tzn.shop.service.CurrentUserService;
import com.tzn.shop.service.ShopOrderService;
import com.tzn.shop.vo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ShopOrderServiceImpl implements ShopOrderService {
  private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private final ShopOrderMapper orderMapper;
  private final ShopOrderItemMapper orderItemMapper;
  private final ShopItemMapper itemMapper;
  private final ShopUserAccountMapper accountMapper;
  private final ShopAccountFlowMapper flowMapper;
  private final ShopOrderTrackMapper trackMapper;
  private final ShopItemReviewMapper reviewMapper;
  private final SysUserMapper userMapper;
  private final CoupleBindingMapper bindingMapper;
  private final CurrentUserService current;

  public ShopOrderServiceImpl(
          ShopOrderMapper o,
          ShopOrderItemMapper oi,
          ShopItemMapper i,
          ShopUserAccountMapper a,
          ShopAccountFlowMapper f,
          ShopOrderTrackMapper t,
          ShopItemReviewMapper r,
          SysUserMapper u,
          CoupleBindingMapper b,
          CurrentUserService c) {
    orderMapper = o;
    orderItemMapper = oi;
    itemMapper = i;
    accountMapper = a;
    flowMapper = f;
    trackMapper = t;
    reviewMapper = r;
    userMapper = u;
    bindingMapper = b;
    current = c;
  }

  @Override
  public ShopAccountVo account(String auth) {
    long uid = current.requireUserId(auth);
    ensureAccount(uid);
    ShopUserAccount a =
            accountMapper.selectOne(
                    new LambdaQueryWrapper<ShopUserAccount>()
                            .eq(ShopUserAccount::getUserId, uid)
                            .eq(ShopUserAccount::getDelFlag, 0));
      return new ShopAccountVo(a.getBalance(), a.getFrozenAmount());
  }

  @Override
  @Transactional
  public ShopOrderCreatedVo create(String auth, ShopOrderCreateRequest req) {
    long buyer = current.requireUserId(auth);
    if (req == null || req.itemId() == null || req.quantity() == null || req.quantity() < 1)
      throw new IllegalArgumentException("商品和购买数量不能为空");
    ShopItem item = purchasable(req.itemId(), buyer);
    BigDecimal total = item.getPrice().multiply(BigDecimal.valueOf(req.quantity()));
    ensureAccount(buyer);
    ShopUserAccount a =
            accountMapper.selectOne(
                    new LambdaQueryWrapper<ShopUserAccount>()
                            .eq(ShopUserAccount::getUserId, buyer)
                            .eq(ShopUserAccount::getDelFlag, 0));
    BigDecimal before = a.getBalance();
    if (before.compareTo(total) < 0) throw new IllegalArgumentException("余额不足");
    a.setBalance(before.subtract(total));
    a.setUpdateUser(buyer);
    a.setUpdateTime(LocalDateTime.now());
    accountMapper.updateById(a);
    if (itemMapper.update(
            null,
            new LambdaUpdateWrapper<ShopItem>()
                    .eq(ShopItem::getId, item.getId())
                    .ge(ShopItem::getStock, req.quantity())
                    .eq(ShopItem::getStatus, 1)
                    .eq(ShopItem::getDelFlag, 0)
                    .setSql("stock = stock - " + req.quantity())
                    .set(ShopItem::getUpdateUser, buyer)
                    .set(ShopItem::getUpdateTime, LocalDateTime.now()))
            == 0) throw new IllegalArgumentException("库存不足");
    String no =
            "SO"
                    + System.currentTimeMillis()
                    + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    ShopOrder o = new ShopOrder();
    o.setOrderNo(no);
    o.setBuyerUserId(buyer);
    o.setSellerUserId(item.getSellerUserId());
    o.setTotalAmount(total);
    o.setStatus(1);
    o.setIsReviewed(0);
    o.setPaymentStatus(1);
    o.setPaymentMethod(1);
    o.setPaidAmount(total);
    o.setPaidTime(LocalDateTime.now());
    o.setRemark(req.remark());
    o.setAppointmentTime(
            req.appointmentTime() == null || req.appointmentTime().isBlank()
                    ? null
                    : LocalDateTime.parse(req.appointmentTime(), F));
    o.setDelFlag(0);
    o.setCreateUser(buyer);
    o.setUpdateUser(buyer);
    o.setCreateTime(LocalDateTime.now());
    o.setUpdateTime(LocalDateTime.now());
    o.setVersion(0);
    orderMapper.insert(o);
    ShopOrderItem oi = new ShopOrderItem();
    oi.setOrderId(o.getId());
    oi.setItemId(item.getId());
    oi.setItemTitle(item.getTitle());
    oi.setItemType(item.getItemType());
    oi.setUnitPrice(item.getPrice());
    oi.setQuantity(req.quantity());
    oi.setSubtotal(total);
    oi.setCoverUrl(item.getCoverUrl());
    oi.setServiceDurationMin(item.getServiceDurationMin());
    oi.setServiceRule(item.getServiceRule());
    oi.setCreateTime(LocalDateTime.now());
    orderItemMapper.insert(oi);
    ShopAccountFlow flow = new ShopAccountFlow();
    flow.setUserId(buyer);
    flow.setOrderId(o.getId());
    flow.setOrderNo(no);
    flow.setFlowType(1);
    flow.setAmount(total);
    flow.setBalanceBefore(before);
    flow.setBalanceAfter(a.getBalance());
    flow.setRemark("购买商品");
    flow.setCreateUser(buyer);
    flow.setUpdateUser(buyer);
    flow.setCreateTime(LocalDateTime.now());
    flowMapper.insert(flow);
    addTrack(o.getId(), 1, "已下单", "订单已支付，等待对方接单", buyer);
      return new ShopOrderCreatedVo(o.getId(), no, 1, total, a.getBalance());
  }

  @Override
  public ShopOrderVoPage buyerList(String auth, Integer status, int pn, int ps) {
    return page(current.requireUserId(auth), true, status, pn, ps);
  }

  @Override
  public ShopOrderVoPage sellerList(String auth, Integer status, int pn, int ps) {
    return page(current.requireUserId(auth), false, status, pn, ps);
  }

    private ShopOrderVoPage page(long uid, boolean buyer, Integer status, int pn, int ps) {
    if (pn < 1 || ps < 1 || ps > 100) throw new IllegalArgumentException("分页参数不合法");
    LambdaQueryWrapper<ShopOrder> w =
            new LambdaQueryWrapper<ShopOrder>()
                    .eq(ShopOrder::getDelFlag, 0)
                    .orderByDesc(ShopOrder::getCreateTime)
                    .orderByDesc(ShopOrder::getId);
    if (buyer) {
      w.eq(ShopOrder::getBuyerUserId, uid);
    } else {
      w.eq(ShopOrder::getSellerUserId, uid);
    }
    if (status != null && status != 0) {
      ShopOrderStatus.fromCode(status);
      w.eq(ShopOrder::getStatus, status);
    }
    List<ShopOrder> all = orderMapper.selectList(w);
    int from = Math.min((pn - 1) * ps, all.size());
    int to = Math.min(from + ps, all.size());
    List<ShopOrder> os = all.subList(from, to);
        List<ShopOrderSummaryVo> rows = new ArrayList<>();
    for (ShopOrder o : os) {
      ShopOrderItem i =
              orderItemMapper.selectOne(
                      new LambdaQueryWrapper<ShopOrderItem>()
                              .eq(ShopOrderItem::getOrderId, o.getId())
                              .last("LIMIT 1"));
      SysUser b = userMapper.selectById(o.getBuyerUserId());
        rows.add(
                new ShopOrderSummaryVo(
                        o.getId(),
                        o.getOrderNo(),
                        o.getTotalAmount(),
                        o.getStatus(),
                        o.getIsReviewed(),
                        o.getIsReviewed(),
                        buyer && o.getStatus() == 3 && o.getIsReviewed() == 0,
                        o.getCreateTime(),
                        i == null ? null : i.getItemTitle(),
                        i == null ? null : i.getItemType(),
                        i == null ? null : i.getQuantity(),
                        i == null ? null : i.getCoverUrl(),
                        b == null ? null : b.getNickname(),
                        b == null ? null : b.getAvatarUrl()));
    }
        return new ShopOrderVoPage(rows, orderMapper.selectCount(w), pn, ps);
  }

  @Override
  public ShopOrderDetailVo detail(String auth, long id) {
    long uid = current.requireUserId(auth);
    ShopOrder o =
            orderMapper.selectOne(
                    new LambdaQueryWrapper<ShopOrder>()
                            .eq(ShopOrder::getId, id)
                            .eq(ShopOrder::getDelFlag, 0)
                            .and(
                                    w ->
                                            w.eq(ShopOrder::getBuyerUserId, uid)
                                                    .or()
                                                    .eq(ShopOrder::getSellerUserId, uid)));
    if (o == null) throw new IllegalArgumentException("订单不存在或无权查看");
    SysUser bu = userMapper.selectById(o.getBuyerUserId()),
            su = userMapper.selectById(o.getSellerUserId());
      ShopOrderVo order =
              new ShopOrderVo(
                      o.getId(),
                      o.getOrderNo(),
                      o.getBuyerUserId(),
                      o.getSellerUserId(),
                      o.getTotalAmount(),
                      o.getStatus(),
                      o.getIsReviewed(),
                      o.getPaymentStatus(),
                      o.getPaymentMethod(),
                      o.getPaidAmount(),
                      o.getPaidTime(),
                      o.getAppointmentTime(),
                      o.getRemark(),
                      o.getCreateTime(),
                      bu == null ? null : bu.getNickname(),
                      su == null ? null : su.getNickname());
    ShopOrderItem i =
            orderItemMapper.selectOne(
                    new LambdaQueryWrapper<ShopOrderItem>()
                            .eq(ShopOrderItem::getOrderId, id)
                            .last("LIMIT 1"));
      List<ShopItemReview> review =
              reviewMapper.selectList(
                      new LambdaQueryWrapper<ShopItemReview>()
                              .eq(ShopItemReview::getOrderId, id)
                              .eq(ShopItemReview::getDelFlag, 0));
      List<ShopOrderTrack> tracks =
              trackMapper.selectList(
                      new LambdaQueryWrapper<ShopOrderTrack>()
                              .eq(ShopOrderTrack::getOrderId, id)
                              .orderByAsc(ShopOrderTrack::getCreateTime)
                              .orderByAsc(ShopOrderTrack::getId));
      return new ShopOrderDetailVo(
              order,
              uid == o.getSellerUserId() && o.getStatus() == 1,
              uid == o.getBuyerUserId() && o.getStatus() == 2,
              o.getIsReviewed() == 1,
              uid == o.getBuyerUserId() && o.getStatus() == 3 && o.getIsReviewed() == 0,
              i == null ? List.of() : List.of(i),
              review,
              tracks);
  }

  @Override
  @Transactional
  public void accept(String auth, long id) {
    long uid = current.requireUserId(auth);
    ShopOrder o = owned(id, uid, false);
    if (o.getStatus() != 1) throw new IllegalArgumentException("订单不存在、已处理或无权接单");
    o.setStatus(2);
    o.setSellerConfirmTime(LocalDateTime.now());
    o.setUpdateUser(uid);
    o.setUpdateTime(LocalDateTime.now());
    orderMapper.updateById(o);
    addTrack(id, 2, "已接待", "对方已确认接单", uid);
  }

  @Override
  @Transactional
  public void complete(String auth, long id) {
    long uid = current.requireUserId(auth);
    ShopOrder o = owned(id, uid, true);
    if (o.getStatus() != 2) throw new IllegalArgumentException("订单不存在、状态不正确或无权确认");
    o.setStatus(3);
    o.setBuyerConfirmTime(LocalDateTime.now());
    o.setCompletedTime(LocalDateTime.now());
    o.setUpdateUser(uid);
    o.setUpdateTime(LocalDateTime.now());
    orderMapper.updateById(o);
    addTrack(id, 3, "已完成", "买家已确认完成", uid);
  }

  private ShopOrder owned(long id, long uid, boolean buyer) {
    LambdaQueryWrapper<ShopOrder> w =
            new LambdaQueryWrapper<ShopOrder>().eq(ShopOrder::getId, id).eq(ShopOrder::getDelFlag, 0);
    if (buyer) {
      w.eq(ShopOrder::getBuyerUserId, uid);
    } else {
      w.eq(ShopOrder::getSellerUserId, uid);
    }
    return orderMapper.selectOne(w);
  }

  private ShopItem purchasable(long id, long buyer) {
    CoupleBinding b =
            bindingMapper.selectOne(
                    new LambdaQueryWrapper<CoupleBinding>()
                            .and(
                                    w ->
                                            w.eq(CoupleBinding::getRequesterUserId, buyer)
                                                    .or()
                                                    .eq(CoupleBinding::getTargetUserId, buyer))
                            .eq(CoupleBinding::getStatus, "ACCEPTED")
                            .last("LIMIT 1"));
    if (b == null) throw new IllegalArgumentException("请先绑定对象");
    long seller = b.getRequesterUserId() == buyer ? b.getTargetUserId() : b.getRequesterUserId();
    ShopItem i =
            itemMapper.selectOne(
                    new LambdaQueryWrapper<ShopItem>()
                            .eq(ShopItem::getId, id)
                            .eq(ShopItem::getSellerUserId, seller)
                            .eq(ShopItem::getStatus, 1)
                            .eq(ShopItem::getDelFlag, 0));
    if (i == null) throw new IllegalArgumentException("商品不存在、已下架或不属于绑定对象");
    return i;
  }

  private void ensureAccount(long uid) {
    if (accountMapper.selectCount(
            new LambdaQueryWrapper<ShopUserAccount>().eq(ShopUserAccount::getUserId, uid))
            == 0) {
      ShopUserAccount a = new ShopUserAccount();
      a.setUserId(uid);
      a.setBalance(BigDecimal.ZERO);
      a.setFrozenAmount(BigDecimal.ZERO);
      a.setDelFlag(0);
      a.setCreateUser(uid);
      a.setUpdateUser(uid);
      a.setCreateTime(LocalDateTime.now());
      a.setUpdateTime(LocalDateTime.now());
      accountMapper.insert(a);
    }
  }

  private void addTrack(long id, int status, String title, String desc, long uid) {
    ShopOrderTrack t = new ShopOrderTrack();
    t.setOrderId(id);
    t.setStatus(status);
    t.setTitle(title);
    t.setDescription(desc);
    t.setOperateUserId(uid);
    t.setCreateUser(uid);
    t.setUpdateUser(uid);
    t.setCreateTime(LocalDateTime.now());
    trackMapper.insert(t);
  }
}

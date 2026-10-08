package com.tzn.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tzn.common.entity.ShopItemReview;
import com.tzn.common.entity.ShopOrder;
import com.tzn.common.entity.ShopOrderItem;
import com.tzn.common.mapper.ShopItemReviewMapper;
import com.tzn.common.mapper.ShopOrderItemMapper;
import com.tzn.common.mapper.ShopOrderMapper;
import com.tzn.shop.dto.ShopReviewCreateRequest;
import com.tzn.shop.enums.ShopOrderStatus;
import com.tzn.shop.service.CurrentUserService;
import com.tzn.shop.service.ShopReviewService;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopReviewServiceImpl implements ShopReviewService {
    private final ShopOrderMapper orderMapper;
    private final ShopOrderItemMapper orderItemMapper;
    private final ShopItemReviewMapper reviewMapper;
    private final CurrentUserService currentUserService;

    public ShopReviewServiceImpl(ShopOrderMapper o, ShopOrderItemMapper oi, ShopItemReviewMapper r, CurrentUserService c) {
        orderMapper = o;
        orderItemMapper = oi;
        reviewMapper = r;
        currentUserService = c;
    }

    @Override
    @Transactional
    public Map<String, Object> create(String authorization, long orderId, ShopReviewCreateRequest request) {
        long reviewer = currentUserService.requireUserId(authorization);
        validate(request);
        ShopOrder order = orderMapper.selectOne(new LambdaQueryWrapper<ShopOrder>().eq(ShopOrder::getId, orderId).eq(ShopOrder::getDelFlag, 0));
        if (order == null) throw new IllegalArgumentException("订单不存在");
        if (!Long.valueOf(reviewer).equals(order.getBuyerUserId()))
            throw new IllegalArgumentException("只有下单方可以评价");
        if (!Integer.valueOf(ShopOrderStatus.COMPLETED.getCode()).equals(order.getStatus()))
            throw new IllegalArgumentException("订单完成后才可以评价");
        if (reviewMapper.selectCount(new LambdaQueryWrapper<ShopItemReview>().eq(ShopItemReview::getOrderId, orderId).eq(ShopItemReview::getDelFlag, 0)) > 0)
            throw new IllegalArgumentException("该订单已经评价过了");
        ShopOrderItem item = orderItemMapper.selectOne(new LambdaQueryWrapper<ShopOrderItem>().eq(ShopOrderItem::getOrderId, orderId).last("LIMIT 1"));
        if (item == null) throw new IllegalArgumentException("订单明细不存在");
        ShopItemReview review = new ShopItemReview();
        review.setOrderId(orderId);
        review.setItemId(item.getItemId());
        review.setReviewerUserId(reviewer);
        review.setSellerUserId(order.getSellerUserId());
        review.setRating(request.rating());
        review.setContent(trim(request.content()));
        review.setDelFlag(0);
        review.setCreateUser(reviewer);
        review.setUpdateUser(reviewer);
        review.setCreateTime(LocalDateTime.now());
        review.setUpdateTime(LocalDateTime.now());
        reviewMapper.insert(review);
        order.setIsReviewed(1);
        order.setUpdateUser(reviewer);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("id", review.getId());
        r.put("orderId", orderId);
        r.put("rating", review.getRating());
        r.put("content", review.getContent());
        return r;
    }

    private void validate(ShopReviewCreateRequest r) {
        if (r == null || r.rating() == null || r.rating() < 1 || r.rating() > 5)
            throw new IllegalArgumentException("评分必须是1至5星");
        if (r.content() != null && r.content().length() > 1000)
            throw new IllegalArgumentException("评价内容不能超过1000字");
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}
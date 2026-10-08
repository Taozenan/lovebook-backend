package com.tzn.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tzn.auth.dto.BindingInviteRequest;
import com.tzn.auth.service.AuthService;
import com.tzn.auth.service.BindingService;
import com.tzn.common.entity.CoupleBinding;
import com.tzn.common.entity.SysUser;
import com.tzn.common.mapper.CoupleBindingMapper;
import com.tzn.common.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BindingServiceImpl implements BindingService {
    private final CoupleBindingMapper bindingMapper;
    private final SysUserMapper userMapper;
    private final AuthService authService;

    public BindingServiceImpl(
            CoupleBindingMapper bindingMapper, SysUserMapper userMapper, AuthService authService) {
        this.bindingMapper = bindingMapper;
        this.userMapper = userMapper;
        this.authService = authService;
    }

    @Override
    @Transactional
    public Map<String, Object> sendInvite(String authorization, BindingInviteRequest request) {
        SysUser requester = currentUser(authorization);
        SysUser target = userByAccount(request.targetPhone().trim());
        if (requester.getId().equals(target.getId())) {
            throw new IllegalArgumentException("不能邀请自己绑定");
        }
        CoupleBinding existing =
                bindingMapper.selectOne(
                        new LambdaQueryWrapper<CoupleBinding>()
                                .and(
                                        w ->
                                                w.eq(CoupleBinding::getRequesterUserId, requester.getId())
                                                        .eq(CoupleBinding::getTargetUserId, target.getId())
                                                        .or()
                                                        .eq(CoupleBinding::getRequesterUserId, target.getId())
                                                        .eq(CoupleBinding::getTargetUserId, requester.getId()))
                .in(CoupleBinding::getStatus, List.of("PENDING", "ACCEPTED")));
        if (existing != null) {
            throw new IllegalArgumentException(
                    "ACCEPTED".equals(existing.getStatus()) ? "你们已经绑定" : "绑定邀请正在等待对方确认");
        }
        CoupleBinding binding = new CoupleBinding();
        binding.setRequesterUserId(requester.getId());
        binding.setTargetUserId(target.getId());
        binding.setStatus("PENDING");
        binding.setCreateTime(LocalDateTime.now());
        binding.setUpdateTime(LocalDateTime.now());
        bindingMapper.insert(binding);
        return Map.of("status", "PENDING", "targetPhone", target.getUsername());
    }

    @Override
    public Map<String, Object> status(String authorization) {
        SysUser current = currentUser(authorization);
        CoupleBinding active =
                bindingMapper.selectOne(
                        new LambdaQueryWrapper<CoupleBinding>()
                                .and(
                                        w ->
                                                w.eq(CoupleBinding::getRequesterUserId, current.getId())
                                                        .or()
                                                        .eq(CoupleBinding::getTargetUserId, current.getId()))
                                .eq(CoupleBinding::getStatus, "ACCEPTED")
                                .last("LIMIT 1"));
        Map<String, Object> result = new LinkedHashMap<>();
        if (active != null) {
            Long partnerId =
                    current.getId().equals(active.getRequesterUserId())
                            ? active.getTargetUserId()
                            : active.getRequesterUserId();
            SysUser partner = userMapper.selectById(partnerId);
            Map<String, Object> binding = new LinkedHashMap<>();
            binding.put("id", active.getId());
            binding.put("partnerPhone", partner.getUsername());
            binding.put("partnerName", partner.getNickname());
            binding.put("bindingTime", active.getUpdateTime());
            result.put("state", "BOUND");
            result.put("binding", binding);
            return result;
        }
        result.put("state", "UNBOUND");
        result.put("sent", pendingSent(current.getId()));
        result.put("received", pendingReceived(current.getId()));
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> accept(String authorization, long invitationId) {
        SysUser current = currentUser(authorization);
        CoupleBinding invitation =
                bindingMapper.selectOne(
                        new LambdaQueryWrapper<CoupleBinding>()
                                .eq(CoupleBinding::getId, invitationId)
                                .eq(CoupleBinding::getTargetUserId, current.getId())
                                .eq(CoupleBinding::getStatus, "PENDING"));
        if (invitation == null) {
            throw new IllegalArgumentException("绑定邀请不存在或已处理");
        }
        if (hasActiveBinding(current.getId()) || hasActiveBinding(invitation.getRequesterUserId())) {
            throw new IllegalArgumentException("其中一方已经绑定对象");
        }
        invitation.setStatus("ACCEPTED");
        invitation.setUpdateTime(LocalDateTime.now());
        bindingMapper.updateById(invitation);
        return status(authorization);
    }

    @Override
    public void reject(String authorization, long invitationId) {
        SysUser current = currentUser(authorization);
        CoupleBinding invitation =
                bindingMapper.selectOne(
                        new LambdaQueryWrapper<CoupleBinding>()
                                .eq(CoupleBinding::getId, invitationId)
                                .eq(CoupleBinding::getTargetUserId, current.getId())
                                .eq(CoupleBinding::getStatus, "PENDING"));
        if (invitation == null) {
            throw new IllegalArgumentException("绑定邀请不存在或已处理");
        }
        invitation.setStatus("REJECTED");
        invitation.setUpdateTime(LocalDateTime.now());
        bindingMapper.updateById(invitation);
    }

    @Override
    public void unbind(String authorization) {
        SysUser current = currentUser(authorization);
        int affected =
                bindingMapper.delete(
                        new LambdaQueryWrapper<CoupleBinding>()
                                .and(
                                        w ->
                                                w.eq(CoupleBinding::getRequesterUserId, current.getId())
                                                        .or()
                                                        .eq(CoupleBinding::getTargetUserId, current.getId()))
                .eq(CoupleBinding::getStatus, "ACCEPTED"));
        if (affected == 0) {
            throw new IllegalArgumentException("当前没有已绑定对象");
        }
    }

    private List<Map<String, Object>> pendingSent(Long userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        bindingMapper
                .selectList(
                        new LambdaQueryWrapper<CoupleBinding>()
                                .eq(CoupleBinding::getRequesterUserId, userId)
                                .eq(CoupleBinding::getStatus, "PENDING")
                                .orderByDesc(CoupleBinding::getId))
                .forEach(
                        binding -> {
                            SysUser user = userMapper.selectById(binding.getTargetUserId());
                            Map<String, Object> row = new LinkedHashMap<>();
                            row.put("id", binding.getId());
                            row.put("targetPhone", user.getUsername());
                            row.put("targetName", user.getNickname());
                            row.put("createTime", binding.getCreateTime());
                            result.add(row);
                        });
        return result;
    }

    private List<Map<String, Object>> pendingReceived(Long userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        bindingMapper
                .selectList(
                        new LambdaQueryWrapper<CoupleBinding>()
                                .eq(CoupleBinding::getTargetUserId, userId)
                                .eq(CoupleBinding::getStatus, "PENDING")
                                .orderByDesc(CoupleBinding::getId))
                .forEach(
                        binding -> {
                            SysUser user = userMapper.selectById(binding.getRequesterUserId());
                            Map<String, Object> row = new LinkedHashMap<>();
                            row.put("id", binding.getId());
                            row.put("requesterPhone", user.getUsername());
                            row.put("requesterName", user.getNickname());
                            row.put("createTime", binding.getCreateTime());
                            result.add(row);
                        });
        return result;
    }

    private SysUser currentUser(String authorization) {
        return userByAccount(authService.currentUsername(authorization));
    }

    private SysUser userByAccount(String account) {
        SysUser user =
                userMapper.selectOne(
                        new LambdaQueryWrapper<SysUser>()
                                .eq(SysUser::getUsername, account)
                                .eq(SysUser::getEnabled, 1));
        if (user == null) {
            throw new IllegalArgumentException("该手机号尚未注册");
        }
        return user;
    }

    private boolean hasActiveBinding(Long userId) {
        return bindingMapper.selectCount(
                new LambdaQueryWrapper<CoupleBinding>()
                        .and(
                                w ->
                                        w.eq(CoupleBinding::getRequesterUserId, userId)
                                                .or()
                                                .eq(CoupleBinding::getTargetUserId, userId))
                        .eq(CoupleBinding::getStatus, "ACCEPTED"))
                > 0;
    }
}

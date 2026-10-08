package com.tzn.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tzn.auth.dto.BindingInviteRequest;
import com.tzn.auth.service.AuthService;
import com.tzn.auth.service.BindingService;
import com.tzn.auth.vo.BindingInvitationVo;
import com.tzn.auth.vo.BindingStatusVo;
import com.tzn.common.entity.CoupleBinding;
import com.tzn.common.entity.SysUser;
import com.tzn.common.mapper.CoupleBindingMapper;
import com.tzn.common.mapper.SysUserMapper;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  public BindingInvitationVo sendInvite(String authorization, BindingInviteRequest request) {
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
    return new BindingInvitationVo("PENDING", target.getUsername());
  }

  @Override
  public BindingStatusVo status(String authorization) {
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
    if (active != null) {
      Long partnerId =
              current.getId().equals(active.getRequesterUserId())
                      ? active.getTargetUserId()
                      : active.getRequesterUserId();
      SysUser partner = userMapper.selectById(partnerId);
      return new BindingStatusVo(
              "BOUND",
              new BindingStatusVo.BindingVo(
                      active.getId(), partner.getUsername(), partner.getNickname(), active.getUpdateTime()),
              null,
              null);
    }
    return new BindingStatusVo(
            "UNBOUND", null, pendingSent(current.getId()), pendingReceived(current.getId()));
  }

  @Override
  @Transactional
  public BindingStatusVo accept(String authorization, long invitationId) {
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

  private List<BindingStatusVo.SentInvitationVo> pendingSent(Long userId) {
    return bindingMapper
            .selectList(
                    new LambdaQueryWrapper<CoupleBinding>()
                            .eq(CoupleBinding::getRequesterUserId, userId)
                            .eq(CoupleBinding::getStatus, "PENDING")
                            .orderByDesc(CoupleBinding::getId))
            .stream()
            .map(
                    binding -> {
                      SysUser user = userMapper.selectById(binding.getTargetUserId());
                      return new BindingStatusVo.SentInvitationVo(
                              binding.getId(), user.getUsername(), user.getNickname(), binding.getCreateTime());
                    })
            .toList();
  }

  private List<BindingStatusVo.ReceivedInvitationVo> pendingReceived(Long userId) {
    return bindingMapper
            .selectList(
                    new LambdaQueryWrapper<CoupleBinding>()
                            .eq(CoupleBinding::getTargetUserId, userId)
                            .eq(CoupleBinding::getStatus, "PENDING")
                            .orderByDesc(CoupleBinding::getId))
            .stream()
            .map(
                    binding -> {
                      SysUser user = userMapper.selectById(binding.getRequesterUserId());
                      return new BindingStatusVo.ReceivedInvitationVo(
                              binding.getId(), user.getUsername(), user.getNickname(), binding.getCreateTime());
                    })
            .toList();
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

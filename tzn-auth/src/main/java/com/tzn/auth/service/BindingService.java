package com.tzn.auth.service;

import com.tzn.auth.dto.BindingInviteRequest;
import com.tzn.auth.vo.BindingInvitationVo;
import com.tzn.auth.vo.BindingStatusVo;

public interface BindingService {

  BindingInvitationVo sendInvite(String authorization, BindingInviteRequest request);

  BindingStatusVo status(String authorization);

  BindingStatusVo accept(String authorization, long invitationId);

  void reject(String authorization, long invitationId);

  void unbind(String authorization);
}

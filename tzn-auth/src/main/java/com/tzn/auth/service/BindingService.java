package com.tzn.auth.service;

import com.tzn.auth.dto.BindingInviteRequest;

import java.util.Map;

public interface BindingService {

    Map<String, Object> sendInvite(String authorization, BindingInviteRequest request);

    Map<String, Object> status(String authorization);

    Map<String, Object> accept(String authorization, long invitationId);

    void reject(String authorization, long invitationId);

    void unbind(String authorization);
}

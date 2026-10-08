package com.tzn.auth.service;

import com.tzn.auth.dto.LoginRequest;
import com.tzn.auth.dto.AvatarUpdateRequest;

import java.util.Map;

public interface AuthService {

    Map<String, Object> login(LoginRequest request);

    void register(String phone, String nickname, String password);

    String currentUsername(String authorization);

    void updateAvatar(String authorization, AvatarUpdateRequest request);
}



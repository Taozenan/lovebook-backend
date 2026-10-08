package com.tzn.auth.service;

import com.tzn.auth.dto.AvatarUpdateRequest;
import com.tzn.auth.dto.LoginRequest;
import com.tzn.auth.vo.LoginVo;

public interface AuthService {

  LoginVo login(LoginRequest request);

  void register(String phone, String nickname, String password);

  String currentUsername(String authorization);

  void updateAvatar(String authorization, AvatarUpdateRequest request);
}

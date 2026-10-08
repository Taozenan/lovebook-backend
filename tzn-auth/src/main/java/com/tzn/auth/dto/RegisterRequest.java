package com.tzn.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "请输入手机号") @Pattern(regexp = "^1\\d{10}$", message = "请输入正确的手机号")
        String phone,
        @NotBlank(message = "请输入昵称") @Size(max = 32, message = "昵称不能超过 32 个字符") String nickname,
        @NotBlank(message = "请输入密码") @Size(min = 6, max = 64, message = "密码长度需为 6 到 64 位")
        String password) {
}

package com.tzn.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record BindingInviteRequest(@NotBlank(message = "请输入对方手机号") String targetPhone) {
}

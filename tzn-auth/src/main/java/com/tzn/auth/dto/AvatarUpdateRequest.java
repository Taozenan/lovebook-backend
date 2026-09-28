package com.tzn.auth.dto;

import jakarta.validation.constraints.Size;

public record AvatarUpdateRequest(@Size(max = 500, message = "头像地址不能超过500个字符") String avatarUrl) {
}

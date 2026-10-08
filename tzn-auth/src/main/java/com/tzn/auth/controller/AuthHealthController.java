package com.tzn.auth.controller;

import com.tzn.common.api.ApiResponse;
import com.tzn.auth.dto.BindingInviteRequest;
import com.tzn.auth.dto.AvatarUpdateRequest;
import com.tzn.auth.dto.LoginRequest;
import com.tzn.auth.dto.RegisterRequest;
import com.tzn.auth.service.AuthService;
import com.tzn.auth.service.BindingService;
import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthHealthController {

    private final AuthService authService;
    private final BindingService bindingService;

    public AuthHealthController(AuthService authService, BindingService bindingService) {
        this.authService = authService;
        this.bindingService = bindingService;
    }

    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success(Map.of("service", "tzn-auth", "status", "UP"));
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request.phone(), request.nickname(), request.password());
        return ApiResponse.success(null);
    }

    @PutMapping("/profile/avatar")
    public ApiResponse<Void> updateAvatar(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                          @Valid @RequestBody AvatarUpdateRequest request) {
        authService.updateAvatar(authorization, request);
        return ApiResponse.success(null);
    }

    @PostMapping("/bindings/invitations")
    public ApiResponse<Map<String, Object>> sendBindingInvite(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                              @Valid @RequestBody BindingInviteRequest request) {
        return ApiResponse.success(bindingService.sendInvite(authorization, request));
    }

    @GetMapping("/bindings/status")
    public ApiResponse<Map<String, Object>> bindingStatus(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ApiResponse.success(bindingService.status(authorization));
    }

    @PutMapping("/bindings/invitations/{id}/accept")
    public ApiResponse<Map<String, Object>> acceptBindingInvite(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                                @PathVariable("id") long id) {
        return ApiResponse.success(bindingService.accept(authorization, id));
    }

    @PutMapping("/bindings/invitations/{id}/reject")
    public ApiResponse<Void> rejectBindingInvite(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                 @PathVariable("id") long id) {
        bindingService.reject(authorization, id);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/bindings")
    public ApiResponse<Void> unbind(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        bindingService.unbind(authorization);
        return ApiResponse.success(null);
    }
}


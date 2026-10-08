package com.tzn.system.controller;

import com.tzn.common.api.ApiResponse;
import com.tzn.common.api.ServiceHealthVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemHealthController {

    @GetMapping("/health")
    public ApiResponse<ServiceHealthVo> health() {
        return ApiResponse.success(new ServiceHealthVo("tzn-system", "UP"));
    }
}

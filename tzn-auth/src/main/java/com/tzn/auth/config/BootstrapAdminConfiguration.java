package com.tzn.auth.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tzn.common.entity.SysRole;
import com.tzn.common.entity.SysUser;
import com.tzn.common.entity.SysUserRole;
import com.tzn.common.mapper.SysRoleMapper;
import com.tzn.common.mapper.SysUserMapper;
import com.tzn.common.mapper.SysUserRoleMapper;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BootstrapAdminConfiguration {
    @Bean
    CommandLineRunner bootstrapAdmin(SysUserMapper userMapper, SysRoleMapper roleMapper, SysUserRoleMapper userRoleMapper,
                                     @Value("${auth.bootstrap.admin-password}") String adminPassword) {
        return args -> {
            SysUser admin = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "admin"));
            if (admin != null) {
                return;
            }
            admin = new SysUser();
            admin.setUsername("admin");
            admin.setNickname("管理员");
            admin.setPassword(new BCryptPasswordEncoder().encode(adminPassword));
            admin.setEnabled(1);
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());
            userMapper.insert(admin);
            SysRole role = roleMapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, "ADMIN"));
            if (role == null) {
                throw new IllegalStateException("ADMIN role not found");
            }
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(admin.getId());
            userRole.setRoleId(role.getId());
            userRoleMapper.insert(userRole);
        };
    }
}
package com.tzn.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BootstrapAdminConfiguration {

    @Bean
    CommandLineRunner bootstrapAdmin(JdbcTemplate jdbcTemplate,
                                     @Value("${auth.bootstrap.admin-password}") String adminPassword) {
        return args -> {
            Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sys_user WHERE username = 'admin'", Integer.class);
            if (userCount != null && userCount > 0) {
                return;
            }
            jdbcTemplate.update("INSERT INTO sys_user (username, nickname, password, enabled) VALUES (?, ?, ?, ?)",
                    "admin", "\u7BA1\u7406\u5458", new BCryptPasswordEncoder().encode(adminPassword), true);
            Long userId = jdbcTemplate.queryForObject("SELECT id FROM sys_user WHERE username = 'admin'", Long.class);
            Long roleId = jdbcTemplate.queryForObject("SELECT id FROM sys_role WHERE role_code = 'ADMIN'", Long.class);
            jdbcTemplate.update("INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES (?, ?)", userId, roleId);
        };
    }
}

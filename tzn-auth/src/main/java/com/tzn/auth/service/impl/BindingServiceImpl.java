package com.tzn.auth.service.impl;

import com.tzn.auth.dto.BindingInviteRequest;
import com.tzn.auth.service.AuthService;
import com.tzn.auth.service.BindingService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class BindingServiceImpl implements BindingService {

    private final JdbcTemplate jdbcTemplate;
    private final AuthService authService;

    public BindingServiceImpl(JdbcTemplate jdbcTemplate, AuthService authService) {
        this.jdbcTemplate = jdbcTemplate;
        this.authService = authService;
    }

    @Override
    public Map<String, Object> sendInvite(String authorization, BindingInviteRequest request) {
        Map<String, Object> requester = currentUser(authorization);
        Map<String, Object> target = userByAccount(request.targetPhone().trim());
        long requesterId = userId(requester);
        long targetId = userId(target);
        if (requesterId == targetId) {
            throw new IllegalArgumentException("不能邀请自己绑定");
        }
        List<Map<String, Object>> existing = jdbcTemplate.queryForList("""
                SELECT id, status FROM couple_binding
                WHERE ((requester_user_id = ? AND target_user_id = ?) OR (requester_user_id = ? AND target_user_id = ?))
                  AND status IN ('PENDING', 'ACCEPTED')
                """, requesterId, targetId, targetId, requesterId);
        if (!existing.isEmpty()) {
            String status = String.valueOf(existing.get(0).get("status"));
            throw new IllegalArgumentException("ACCEPTED".equals(status) ? "你们已经绑定" : "绑定邀请正在等待对方确认");
        }
        jdbcTemplate.update("INSERT INTO couple_binding (requester_user_id, target_user_id, status) VALUES (?, ?, 'PENDING')",
                requesterId, targetId);
        return Map.of("status", "PENDING", "targetPhone", target.get("username"));
    }

    @Override
    public Map<String, Object> status(String authorization) {
        Map<String, Object> current = currentUser(authorization);
        long currentId = userId(current);
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> active = jdbcTemplate.queryForList("""
                SELECT cb.id, u.username AS partnerPhone, u.nickname AS partnerName, cb.update_time AS bindingTime
                FROM couple_binding cb
                JOIN sys_user u ON u.id = CASE WHEN cb.requester_user_id = ? THEN cb.target_user_id ELSE cb.requester_user_id END
                WHERE (cb.requester_user_id = ? OR cb.target_user_id = ?) AND cb.status = 'ACCEPTED'
                LIMIT 1
                """, currentId, currentId, currentId);
        if (!active.isEmpty()) {
            result.put("state", "BOUND");
            result.put("binding", active.get(0));
            return result;
        }
        result.put("state", "UNBOUND");
        result.put("sent", jdbcTemplate.queryForList("""
                SELECT cb.id, u.username AS targetPhone, u.nickname AS targetName, cb.create_time AS createTime
                FROM couple_binding cb JOIN sys_user u ON u.id = cb.target_user_id
                WHERE cb.requester_user_id = ? AND cb.status = 'PENDING' ORDER BY cb.id DESC
                """, currentId));
        result.put("received", jdbcTemplate.queryForList("""
                SELECT cb.id, u.username AS requesterPhone, u.nickname AS requesterName, cb.create_time AS createTime
                FROM couple_binding cb JOIN sys_user u ON u.id = cb.requester_user_id
                WHERE cb.target_user_id = ? AND cb.status = 'PENDING' ORDER BY cb.id DESC
                """, currentId));
        return result;
    }

    @Override
    public Map<String, Object> accept(String authorization, long invitationId) {
        Map<String, Object> current = currentUser(authorization);
        long currentId = userId(current);
        List<Map<String, Object>> invitations = jdbcTemplate.queryForList("""
                SELECT requester_user_id FROM couple_binding WHERE id = ? AND target_user_id = ? AND status = 'PENDING'
                """, invitationId, currentId);
        if (invitations.isEmpty()) {
            throw new IllegalArgumentException("绑定邀请不存在或已处理");
        }
        long requesterId = ((Number) invitations.get(0).get("requester_user_id")).longValue();
        if (hasActiveBinding(currentId) || hasActiveBinding(requesterId)) {
            throw new IllegalArgumentException("其中一方已经绑定对象");
        }
        jdbcTemplate.update("UPDATE couple_binding SET status = 'ACCEPTED' WHERE id = ?", invitationId);
        return status(authorization);
    }

    @Override
    public void reject(String authorization, long invitationId) {
        long currentId = userId(currentUser(authorization));
        int affected = jdbcTemplate.update("UPDATE couple_binding SET status = 'REJECTED' WHERE id = ? AND target_user_id = ? AND status = 'PENDING'",
                invitationId, currentId);
        if (affected == 0) {
            throw new IllegalArgumentException("绑定邀请不存在或已处理");
        }
    }

    @Override
    public void unbind(String authorization) {
        long currentId = userId(currentUser(authorization));
        int affected = jdbcTemplate.update("DELETE FROM couple_binding WHERE (requester_user_id = ? OR target_user_id = ?) AND status = 'ACCEPTED'",
                currentId, currentId);
        if (affected == 0) {
            throw new IllegalArgumentException("当前没有已绑定对象");
        }
    }

    private Map<String, Object> currentUser(String authorization) {
        return userByAccount(authService.currentUsername(authorization));
    }

    private Map<String, Object> userByAccount(String account) {
        List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT id, username FROM sys_user WHERE username = ? AND enabled = 1", account);
        if (users.isEmpty()) {
            throw new IllegalArgumentException("该手机号尚未注册");
        }
        return users.get(0);
    }

    private long userId(Map<String, Object> user) {
        return ((Number) user.get("id")).longValue();
    }

    private boolean hasActiveBinding(long userId) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM couple_binding WHERE (requester_user_id = ? OR target_user_id = ?) AND status = 'ACCEPTED'",
                Integer.class, userId, userId);
        return count != null && count > 0;
    }
}

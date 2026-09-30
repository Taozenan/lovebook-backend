package com.tzn.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("sys_role")
public class SysRole {
    @TableId(type = IdType.AUTO) private Long id;
    private String roleCode;
    private String roleName;
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getRoleCode(){return roleCode;} public void setRoleCode(String v){roleCode=v;}
    public String getRoleName(){return roleName;} public void setRoleName(String v){roleName=v;}
}
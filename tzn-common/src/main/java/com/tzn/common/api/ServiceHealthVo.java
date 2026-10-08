package com.tzn.common.api;

import lombok.*;

/** 服务健康检查响应。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceHealthVo {
  /** 服务名称。 */
  private String service;

  /** 服务状态。 */
  private String status;

  public String service() {
    return service;
  }

  public String status() {
    return status;
  }
}

package com.tzn.auth.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 当前用户的绑定状态与待处理邀请。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BindingStatusVo {
  /** 绑定状态。 */
  private String state;

  /** 已绑定对象信息。 */
  private BindingVo binding;

  /** 已发出的待处理邀请。 */
  private List<SentInvitationVo> sent;

  /** 已收到的待处理邀请。 */
  private List<ReceivedInvitationVo> received;

  public String state() {
    return state;
  }

  public BindingVo binding() {
    return binding;
  }

  public List<SentInvitationVo> sent() {
    return sent;
  }

  public List<ReceivedInvitationVo> received() {
    return received;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class BindingVo {
    /** 绑定标识。 */
    private Long id;

    /** 对方手机号。 */
    private String partnerPhone;

    /** 对方昵称。 */
    private String partnerName;

    /** 绑定时间。 */
    private LocalDateTime bindingTime;

    public Long id() {
      return id;
    }

    public String partnerPhone() {
      return partnerPhone;
    }

    public String partnerName() {
      return partnerName;
    }

    public LocalDateTime bindingTime() {
      return bindingTime;
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SentInvitationVo {
    /** 邀请标识。 */
    private Long id;

    /** 被邀请人手机号。 */
    private String targetPhone;

    /** 被邀请人昵称。 */
    private String targetName;

    /** 创建时间。 */
    private LocalDateTime createTime;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ReceivedInvitationVo {
    /** 邀请标识。 */
    private Long id;

    /** 邀请人手机号。 */
    private String requesterPhone;

    /** 邀请人昵称。 */
    private String requesterName;

    /** 创建时间。 */
    private LocalDateTime createTime;
  }
}

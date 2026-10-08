package com.tzn.shop.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopItemReviewVo {
  /** 标识。 */
  private Long id;

  /** 评分。 */
  private Integer rating;

  /** 内容。 */
  private String content;

  /** 创建时间。 */
  private LocalDateTime createTime;

  /** 评价人昵称。 */
  private String reviewerName;

  /** 评价人头像地址。 */
  private String reviewerAvatarUrl;

  public Long id() {
    return id;
  }

  public Integer rating() {
    return rating;
  }

  public String content() {
    return content;
  }

  public LocalDateTime createTime() {
    return createTime;
  }

  public String reviewerName() {
    return reviewerName;
  }

  public String reviewerAvatarUrl() {
    return reviewerAvatarUrl;
  }
}

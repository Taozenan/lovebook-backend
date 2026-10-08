package com.tzn.shop.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopUploadVo {
  /** 文件访问地址。 */
  private String url;

  /** 文件名。 */
  private String filename;

  public String url() {
    return url;
  }

  public String filename() {
    return filename;
  }
}

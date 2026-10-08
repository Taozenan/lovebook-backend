package com.tzn.shop.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopItemListVo {
  /** 列表数据。 */
  private List<ShopItemVo> items;

  public List<ShopItemVo> items() {
    return items;
  }
}

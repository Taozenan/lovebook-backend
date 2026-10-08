package com.tzn.shop.vo;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopAccountVo {
  /** 可用余额。 */
  private BigDecimal balance;

  /** 冻结金额。 */
  private BigDecimal frozenAmount;

  public BigDecimal balance() {
    return balance;
  }

  public BigDecimal frozenAmount() {
    return frozenAmount;
  }
}

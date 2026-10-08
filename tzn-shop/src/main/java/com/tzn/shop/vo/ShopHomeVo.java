package com.tzn.shop.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopHomeVo {
  /** 横幅列表。 */
  private List<ShopBannerVo> banners;

  /** 分类列表。 */
  private List<ShopCategoryVo> categories;

  /** 绑定对象信息。 */
  private ShopPartnerVo partner;

  /** 推荐食品列表。 */
  private List<ShopItemVo> recommendedFoods;

  /** 热门服务列表。 */
  private List<ShopItemVo> hotServices;

  public List<ShopBannerVo> banners() {
    return banners;
  }

  public List<ShopCategoryVo> categories() {
    return categories;
  }

  public ShopPartnerVo partner() {
    return partner;
  }

  public List<ShopItemVo> recommendedFoods() {
    return recommendedFoods;
  }

  public List<ShopItemVo> hotServices() {
    return hotServices;
  }
}

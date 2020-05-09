package com.vergilyn.examples.es.usage.motianshi;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
    "_id":"https://detail.tmall.com/item.htm?id=38701730304u0026skuId=65205296562",
    "卖家地址":"江苏南京",
    "快递费":"快递: 0.00",
    "优惠活动":"满199减10,满299减30,满499减60,可跨店",
    "商品ID":"38701730304",
    "原价":"339.00",
    "活动开始时间":"2016-11-01 00:00:00",
    "活动结束时间":"2016-11-11 23:59:59",
    "标题":"送镜盒]库博酷柏硅水凝胶隐形近视眼镜爱维舒月抛6片",
    "服务保障":"正品保证;公益宝贝;赠运费险;不支持7天退换;极速退款",
    "会场":"健康保健会场",
    "现价":"109.00"
*/
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDocument {
    /** 商品ID */
    private String itemId;
    /** _id */
    private String urlId;
    /** 卖家地址 */
    private String sellAddress;
    /** 快递费 */
    private String courierFee;
    /** 优惠活动 */
    private String promotions;
    /** 原价 */
    private String originalPrice;
    /** 活动开始时间 */
    private String startTime;
    /** 活动结束时间 */
    private String endTime;
    /** 标题 */
    private String title;
    /** 服务保障 */
    private String serviceGuarantee;
    /** 会场 */
    private String venue;
    /** 现价 */
    private String currentPrice;
}

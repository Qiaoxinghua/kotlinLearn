package com.wbkj.kotlinlearn

/**
 * @author maohl
 * @date 2018/3/27
 * @description
 */
class Shop2 {
 var shopId: String? = null//店铺id
 var shopName: String? = null//店铺名称
 var goods: ArrayList<Goods>? = null
 var isShopSelect: Boolean = false//该店铺是否被选中

      class Goods {
        var goodsId: String? = null//商品id
        var goodsName: String? = null//商品名称
        var goodsImgUrl: String? = null//商品图片地址
        var goodsPrice: String? = null//商品价格
        var goodsCount: Int = 0//商品数量
        var isGoodsSelect: Boolean = false//该商品是否被选中
    }
}
package com.wbkj.kotlinlearn

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.text.DecimalFormat
//多店铺购物车kotlin版
class ShopCartActivity : AppCompatActivity() {

    private var tvCartTitle: TextView? = null
    private var tvCartEdit: TextView? = null
    private var elvCart: ExpandableListView? = null
    private var rlCartNormal: RelativeLayout? = null
    private var ivAllCheck: ImageView? = null
    private var tvCartTotalPrice: TextView? = null
    private var mode = false//false普通模式，true 编辑模式（删除，移入收藏夹等）
    private var rlCartEdit: RelativeLayout? = null
    private var tvCartCollect: TextView? = null
    private var tvCartDelete: TextView? = null
    private var ivAllEditCheck: ImageView? = null
    private val shopList = ArrayList<Shop2>()
    private var baseExpandableListAdapter: BaseExpandableListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_shop_cart)
        relativeLayout {
            backgroundColor = Color.WHITE
            relativeLayout {
                id = R.id.rl_cart_top
                backgroundColor = Color.parseColor("#ffffff")
                tvCartTitle = textView {
                    id = R.id.tv_cart_title
                    text = "购物车"
                    textColor = Color.parseColor("#000000")
                    textSize = 16f
                }.lparams {
                    width = wrapContent
                    height = wrapContent
                    centerInParent()
                }

                tvCartEdit = textView {
                    id = R.id.tv_cart_edit
                    text = "编辑"
                    textColor = Color.parseColor("#000000")
                    textSize = 16f
                    onClick {
                        //改变当前模式
                        changeMode()
                    }
                }.lparams {
                    width = wrapContent
                    height = wrapContent
                    alignParentRight()
                    centerVertically()
                    rightMargin = dip(10)
                }

                view {
                    backgroundColor = Color.parseColor("#e5e5e5")
                }.lparams {
                    alignParentBottom()
                    height = dip(1f)
                    width = matchParent
                }

            }.lparams {
                width = matchParent
                height = dip(52)
            }
            elvCart = expandableListView {
                id = R.id.elv_cart
                divider = null
                setGroupIndicator(null)
            }.lparams {
                below(R.id.rl_cart_top)
                above(R.id.ll_cart_bottom)
                width = matchParent
                height = matchParent
            }

            verticalLayout {
                id = R.id.ll_cart_bottom
                rlCartNormal = relativeLayout {
                    id = R.id.rl_cart_normal
                    //正常模式下全选/反选
                    ivAllCheck = imageView {
                        id = R.id.iv_cart_all_check
                        imageResource=R.mipmap.ic_uncheck
//                        setImageResource(R.mipmap.ic_uncheck)
                        onClick {
                            //全选与反选
                            selectAll()
                        }
                    }.lparams {
                        width = dip(30)
                        height = dip(30)
                        centerVertically()
//                        gravity = Gravity.CENTER
//                        leftMargin = dip(10)
                        padding = dip(14)
                    }

                    textView {
                        id = R.id.tv_cart_group_name
                        text = "全选"
                        textColor = Color.parseColor("#ffffff")
                        textSize = 16f
                    }.lparams {
                        width = wrapContent
                        height = wrapContent
                        centerVertically()
                        leftMargin = dip(10)
                        rightOf(R.id.iv_cart_all_check)
                        gravity = Gravity.CENTER_VERTICAL
                    }

                    textView {
                        id = R.id.tv
                        text = "合计:"
                        textColor = Color.parseColor("#ffffff")
                        textSize = 16f
                    }.lparams {
                        width = wrapContent
                        height = wrapContent
                        centerVertically()
                        leftMargin = dip(10)
                        rightOf(R.id.tv_cart_group_name)
                        gravity = Gravity.CENTER_VERTICAL

                    }

                    tvCartTotalPrice = textView {
                        id = R.id.tv_cart_total_price
                        text = ""
                        textColor = Color.parseColor("#ffffff")
                        textSize = 16f
                        singleLine = true
                    }.lparams {
                        width = wrapContent
                        height = wrapContent
                        centerVertically()
                        leftMargin = dip(10)
                        rightOf(R.id.tv)
                        gravity = Gravity.CENTER_VERTICAL

                    }
                }.lparams {
                    width = matchParent
                    height = matchParent
                    backgroundColor = Color.parseColor("#ff6666")
                }

                rlCartEdit = relativeLayout {
                    id = R.id.rl_cart_edit
                    visibility = View.GONE
                    //编辑模式下全选/反选
                    ivAllEditCheck = imageView {
                        id = R.id.iv_cart_all_edit_check
                        setImageResource(R.mipmap.ic_uncheck)
                        onClick {
                            //全选与反选
                            selectAll()
                        }
                    }.lparams {
                        width = dip(30)
                        height = dip(30)
                        centerVertically()
                        gravity = Gravity.CENTER
                        leftMargin = dip(10)
                        padding = dip(14)
                    }

                    textView {
                        text = "全选"
                        textColor = Color.parseColor("#ffffff")
                        textSize = 16f
                    }.lparams {
                        width = wrapContent
                        height = wrapContent
                        centerVertically()
                        leftMargin = dip(10)
                        rightOf(R.id.iv_cart_all_edit_check)
                        gravity = Gravity.CENTER_VERTICAL
                    }
                    //收藏按钮
                    tvCartCollect = textView {
                        id = R.id.tv_cart_collect
                        text = "移入收藏夹"
                        textColor = Color.parseColor("#ffffff")
                        textSize = 16f
                    }.lparams {
                        width = wrapContent
                        height = wrapContent
                        centerVertically()
                        leftMargin = dip(10)
                        gravity = Gravity.CENTER_VERTICAL
                        leftOf(R.id.tv_cart_delete)
                    }
                    //删除按钮
                    tvCartDelete = textView {
                        id = R.id.tv_cart_delete
                        text = "删除"
                        textColor = Color.parseColor("#ffffff")
                        textSize = 16f
                        singleLine = true
                        onClick { delete() }
                    }.lparams {
                        width = wrapContent
                        height = wrapContent
                        centerVertically()
                        alignParentRight()
                        leftMargin = dip(10)
                        rightOf(R.id.tv)
                        gravity = Gravity.CENTER_VERTICAL

                    }
                }.lparams {
                    width = matchParent
                    height = matchParent
                    backgroundColor = Color.parseColor("#ff6666")
                }


            }.lparams {
                width = matchParent
                height = dip(50)
                alignParentBottom()
            }
        }
        initData()
    }

    /**
     * 改变当前模式（普通模式，编辑模式）
     */
    private fun changeMode() {
        if (!mode) {
            //当前是普通模式,修改为编辑模式,右上角按钮变为‘完成’
            mode = true
            tvCartEdit?.text = "完成"
            rlCartEdit?.visibility = View.VISIBLE
            rlCartNormal?.visibility = View.GONE
        } else {
            //当前是编辑模式，修改为普通模式，右上角按钮重新变为‘编辑’
            mode = false
            tvCartEdit?.text = "编辑"
            rlCartEdit?.visibility = View.GONE
            rlCartNormal?.visibility = View.VISIBLE
        }
        //改变模式之后要清除之前的选中状态
        clearStatus()
    }

    /**
     * 清除选中的状态
     */
    private fun clearStatus() {
        ivAllCheck?.setImageResource(R.mipmap.ic_uncheck)
        ivAllEditCheck?.setImageResource(R.mipmap.ic_uncheck)
        //设置全部店铺，和店铺内商品为未选中状态
        for (j in shopList.indices) {
            shopList[j].isShopSelect = false
            for (k in 0 until shopList[j].goods!!.size) {
                shopList[j].goods!![k].isGoodsSelect = false
            }
        }
        baseExpandableListAdapter?.notifyDataSetChanged()
        //清除状态之后重新计算一下
        calculate()
    }

    /**
     * 设置ExpandableView适配器
     */
    private fun initData() {
        elvCart?.setOnGroupClickListener(ExpandableListView.OnGroupClickListener { parent, v, groupPosition, id -> true })
//        shopList = ArrayList<Shop>()
        var count = 0//购物车内商品数量
        for (i in 0..1) {
            //两个店铺
            val shop = Shop2()
            shop.shopId = "shop_id_$i"
            shop.shopName = "shop_name_$i"
            shop.isShopSelect = false//初始设置店铺不被选中
            val goodsList = ArrayList<Shop2.Goods>()
            for (j in 0..2) {
                //每个店铺三个商品
                val goods = Shop2.Goods()
                goods.goodsId = "goods_id_$i$j"
                goods.goodsName = "goods_name_$i$j"
                goods.goodsImgUrl = ""//图片地址先不设置
                goods.goodsPrice = (10 + j).toString() + ""
                goods.goodsCount = 2 + j//商品数量
                goods.isGoodsSelect = false//初始设置商品不被选中
                goodsList.add(goods)
                count++
            }
            shop.goods = goodsList
            shopList.add(shop)
        }
        tvCartTitle?.text = "购物车($count)"
        baseExpandableListAdapter = object : BaseExpandableListAdapter() {

            //设置总得分组
            override fun getGroupCount(): Int {
                return shopList.size
            }

            //设置每个分组有多少个小项
            override fun getChildrenCount(groupPosition: Int): Int {
                return shopList[groupPosition].goods!!.size
            }

            override fun getGroup(groupPosition: Int): Any {
                return shopList.get(groupPosition)
            }

            override fun getChild(groupPosition: Int, childPosition: Int): Any {
                return shopList[groupPosition].goods!![childPosition]
            }

            override fun getGroupId(groupPosition: Int): Long {
                return groupPosition.toLong()
            }

            override fun getChildId(groupPosition: Int, childPosition: Int): Long {
                return childPosition.toLong()
            }

            override fun hasStableIds(): Boolean {
                return true
            }

            //设置组的界面
            override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
                val view = LayoutInflater.from(this@ShopCartActivity).inflate(R.layout.group_view, parent, false)
//                val view = .f.inflate(this@ShopCartActivity, R.layout.group_view, null)
                val tvGroupName = view.findViewById<View>(R.id.tv_group_name) as TextView
                val rlGroup = view.findViewById<View>(R.id.rl_group) as RelativeLayout

                val ivGroupCheck = view.findViewById<View>(R.id.iv_group_icon) as ImageView
                if (isExpanded) {
                    ivGroupCheck.setImageResource(R.mipmap.ic_uncheck)
                } else {
                    ivGroupCheck.setImageResource(R.mipmap.ic_uncheck)
                }
                //绑定数据
                val shop = shopList[groupPosition]
                val shopName = shop.shopName//店铺名称
                tvGroupName.text = shopName
                if (shop.goods!!.isEmpty()) {
                    rlGroup.visibility = View.GONE
                } else {
                    rlGroup.visibility = View.VISIBLE
                }
                if (shop.isShopSelect) {
                    //设置当前店铺图标为选中状态
                    ivGroupCheck.setImageResource(R.mipmap.ic_checked)
                } else {
                    //设置为未选中状态
                    ivGroupCheck.setImageResource(R.mipmap.ic_uncheck)
                }
                //控制全选按钮的状态
                ivAllCheck?.setImageResource(R.mipmap.ic_checked)
                ivAllEditCheck?.setImageResource(R.mipmap.ic_checked)
                for (i in shopList.indices) {
                    if (!shopList[i].isShopSelect) {
                        //有一个店铺未选中，则全选按钮也设置未选中
                        ivAllCheck?.setImageResource(R.mipmap.ic_uncheck)
                        ivAllEditCheck?.setImageResource(R.mipmap.ic_uncheck)
                    }
                }
                ivGroupCheck.setOnClickListener {
                    if (shop.isShopSelect) {
                        //当前已选中，设置为未选中状态
                        shop.isShopSelect = false
                        ivGroupCheck.setImageResource(R.mipmap.ic_uncheck)
                        //并把当前店铺下的所有商品全部设置为未选中状态
                        val goods = shop.goods
                        for (good in goods!!) {
                            good.isGoodsSelect = false
                        }
                        //刷新一下
                        notifyDataSetChanged()
                    } else {
                        //当前未选中，设置为选中状态
                        shop.isShopSelect = true
                        ivGroupCheck.setImageResource(R.mipmap.ic_checked)
                        //并把当前店铺下的所有商品全部设置为选中状态
                        val goods = shop.goods
                        for (good in goods!!) {
                            good.isGoodsSelect = true
                        }
                        //刷新一下
                        notifyDataSetChanged()
                    }
                }
                return view
            }

            //设置组里面的项的界面
            override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
                val view = View.inflate(this@ShopCartActivity, R.layout.child_view, null)
                val ivChildCheck = view.findViewById<View>(R.id.iv_child_icon) as ImageView
                val tvChildName = view.findViewById<View>(R.id.tv_child_name) as TextView
                val tvChildPrice = view.findViewById<View>(R.id.tv_child_price) as TextView
                val tvChildNum = view.findViewById<View>(R.id.tv_child_num) as TextView
                val tvMinus = view.findViewById<View>(R.id.tv_minus) as TextView
                val tvPlus = view.findViewById<View>(R.id.tv_plus) as TextView
                val divider = view.findViewById<View>(R.id.divider)
                val goodsList = shopList[groupPosition].goods!!
                val goods = goodsList[childPosition]
                tvChildPrice.text = "￥" + goods.goodsPrice
                tvChildName.text = goods.goodsName
                tvChildNum.text = goods.goodsCount.toString()
                calculate()
                if (childPosition == goodsList.size - 1) {
                    divider.visibility = View.VISIBLE
                } else {
                    divider.visibility = View.GONE
                }

                if (goods.isGoodsSelect) {
                    //设置当前店铺图标为选中状态
                    ivChildCheck.setImageResource(R.mipmap.ic_checked)
                } else {
                    //设置为未选中状态
                    ivChildCheck.setImageResource(R.mipmap.ic_uncheck)
                }
                ivChildCheck.setOnClickListener {
                    if (goods.isGoodsSelect) {
                        //设置当前店铺图标为未选中状态
                        ivChildCheck.setImageResource(R.mipmap.ic_uncheck)
                        //并把当前商品的设置为未选中
                        goods.isGoodsSelect = false
                        //当前商品对应的店铺也设置为未选中
                        shopList[groupPosition].isShopSelect = false
                    } else {
                        //设置为选中状态
                        ivChildCheck.setImageResource(R.mipmap.ic_checked)
                        //并把当前商品的设置为选中
                        goods.isGoodsSelect = true
                        //先设置店铺选中
                        shopList[groupPosition].isShopSelect = true
                        for (i in goodsList.indices) {
                            if (!goodsList[i].isGoodsSelect) {
                                //如果有一个商品未选中就设置店铺未选中
                                shopList[groupPosition].isShopSelect = false
                            }
                        }
                    }
                    //刷新一下
                    notifyDataSetChanged()
                }
                //减按钮
                tvMinus.setOnClickListener {
                    val goodsCount = goods.goodsCount
                    if (goods.goodsCount > 1) {
                        goods.goodsCount = goodsCount - 1
                    } else {
                        Toast.makeText(this@ShopCartActivity, "商品数量不能为0", Toast.LENGTH_SHORT).show()
                    }
                    tvChildNum.text = goods.goodsCount.toString()
                    baseExpandableListAdapter?.notifyDataSetChanged()
                }
                //加按钮
                tvPlus.setOnClickListener {
                    val goodsCount = goods.goodsCount
                    goods.goodsCount = goodsCount + 1
                    tvChildNum.text = goods.goodsCount.toString()
                    baseExpandableListAdapter?.notifyDataSetChanged()
                }
                return view
            }

            //设置小项是否可以点击  一般设置都是可以点击
            override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
                return true
            }
        }
        elvCart?.setAdapter(baseExpandableListAdapter)
        /**
         * 展开所有的组
         */
        for (i in 0..1) {
            elvCart?.expandGroup(i)
        }
    }

    /**
     * 计算总金额
     */
    fun calculate() {
        var total = 0.00
        var count = 0
        for (i in shopList.indices) {
            val shop = shopList[i]
            val goodsList = shop.goods
            for (j in goodsList!!.indices) {
                val goods = goodsList[j]
                if (goods.isGoodsSelect) {
                    if (!TextUtils.isEmpty(goods.goodsPrice)) {
                        total += goods.goodsCount * java.lang.Double.parseDouble(goods.goodsPrice)
                    }
                }
                count++
            }
        }
        val df = DecimalFormat("0.00")
        tvCartTotalPrice?.text = "￥" + df.format(total)
        tvCartTitle?.text = "购物车($count)"
    }

    /**
     * 删除购物车内选中的商品
     */
    private fun delete() {
        //要删除的商品集合
        val goodsRemove = ArrayList<Shop2.Goods>()
        //要删除的店铺集合
        val shopRemove = ArrayList<Shop2>()
        for (i in shopList.indices) {
            goodsRemove.clear()
            for (k in 0 until shopList[i].goods!!.size) {
                if (shopList[i].goods!![k].isGoodsSelect) {
                    goodsRemove.add(shopList[i].goods!![k])
                }
            }
            shopList[i].goods!!.removeAll(goodsRemove)
            if (shopList[i].goods!!.isEmpty()) {
                shopRemove.add(shopList[i])
            }
        }
        shopList.removeAll(shopRemove)
        baseExpandableListAdapter?.notifyDataSetChanged()
        //删除之后清除一下状态
        clearStatus()
    }

    /**
     * 全选与反选
     */
    private fun selectAll() {
        for (i in shopList.indices) {
            if (!shopList[i].isShopSelect) {
                //如果有一个未选中，证明当前状态是非全选，则设置为全选
                ivAllCheck?.setImageResource(R.mipmap.ic_checked)
                ivAllEditCheck?.setImageResource(R.mipmap.ic_checked)
                //设置全部店铺，和店铺内商品为选中状态
                for (j in shopList.indices) {
                    shopList[j].isShopSelect = true
                    for (k in 0 until shopList[j].goods!!.size) {
                        shopList[j].goods!![k].isGoodsSelect = true
                    }
                }
                baseExpandableListAdapter?.notifyDataSetChanged()
                return
            } else {
                if (i == shopList.size - 1) {
                    for (j in shopList.indices) {
                        shopList[j].isShopSelect = false
                        for (k in 0 until shopList[j].goods!!.size) {
                            shopList[j].goods!![k].isGoodsSelect = false
                        }
                    }
                    ivAllCheck?.setImageResource(R.mipmap.ic_uncheck)
                    ivAllEditCheck?.setImageResource(R.mipmap.ic_uncheck)
                    baseExpandableListAdapter?.notifyDataSetChanged()
                }
            }
        }
    }
}

package com.didichuxing.doraemonkit.widget.dropdown

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import java.util.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-11-19:17
 * 描    述：下拉菜单
 * 修订历史：
 * ================================================
 */
class DkDropDownMenu : LinearLayout {
    //记录tabTexts的顺序
    var dropTabViews: MutableList<View> = ArrayList()

    //顶部菜单布局
    private var tabMenuView: LinearLayout? = null

    //底部容器，包含popupMenuViews，maskView
    private var containerView: FrameLayout? = null

    //弹出菜单父布局
    private var popupMenuViews: FrameLayout? = null

    //遮罩半透明View，点击可关闭DkDropDownMenu
    private var maskView: View? = null

    //tabMenuView里面选中的tab位置，-1表示未选中
    private var current_tab_position = -1
    private var dividerHeight = 0f

    //分割线颜色
    private var dividerColor = -0x333334

    //tab选中颜色
    private var textSelectedColor = -0x76f37b

    //tab未选中颜色
    private var textUnselectedColor = -0xeeeeef

    //遮罩颜色
    private var maskColor = -0x77777778

    //tab字体大小
    private var menuTextSize = 14
    private var mOrientation: Orientation? = null

    //tab选中图标
    private var menuSelectedIcon = 0

    //tab未选中图标
    private var menuUnselectedIcon = 0

    constructor(context: Context?) : super(context, null) {}

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        orientation = VERTICAL

        //为DkDropDownMenu添加自定义属性
        var menuBackgroundColor = -0x1
        var underlineColor = -0x333334
        val a = context.obtainStyledAttributes(attrs, R.styleable.DkDropDownMenu)
        underlineColor = a.getColor(R.styleable.DkDropDownMenu_dk_ddunderlineColor, underlineColor)
        dividerColor = a.getColor(R.styleable.DkDropDownMenu_dk_dddividerColor, dividerColor)
        textSelectedColor = a.getColor(R.styleable.DkDropDownMenu_dk_ddtextSelectedColor,
                textSelectedColor)
        textUnselectedColor = a.getColor(R.styleable.DkDropDownMenu_dk_ddtextUnselectedColor,
                textUnselectedColor)
        menuBackgroundColor = a.getColor(R.styleable.DkDropDownMenu_dk_ddmenuBackgroundColor,
                menuBackgroundColor)
        maskColor = a.getColor(R.styleable.DkDropDownMenu_dk_ddmaskColor, maskColor)
        menuTextSize = a.getDimensionPixelSize(R.styleable.DkDropDownMenu_dk_ddmenuTextSize,
                menuTextSize)
        dividerHeight = a.getDimensionPixelSize(R.styleable.DkDropDownMenu_dk_dddividerHeight, ViewGroup.LayoutParams.MATCH_PARENT).toFloat()
        menuSelectedIcon = a.getResourceId(R.styleable.DkDropDownMenu_dk_ddmenuSelectedIcon,
                menuSelectedIcon)
        menuUnselectedIcon = a.getResourceId(R.styleable.DkDropDownMenu_dk_ddmenuUnselectedIcon,
                menuUnselectedIcon)
        iconOrientation = a.getInt(R.styleable.DkDropDownMenu_dk_ddmenuIconOrientation, iconOrientation)
        a.recycle()
        //初始化位置参数
        mOrientation = Orientation(getContext())
        mOrientation!!.init(iconOrientation, menuSelectedIcon, menuUnselectedIcon)

        //初始化tabMenuView并添加到tabMenuView
        tabMenuView = LinearLayout(context)
        val params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        tabMenuView!!.orientation = HORIZONTAL
        tabMenuView!!.setBackgroundColor(menuBackgroundColor)
        tabMenuView!!.layoutParams = params
        addView(tabMenuView, 0)

        //为tabMenuView添加下划线
        val underLine = View(getContext())
        underLine.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                dpTpPx(1.0f))
        underLine.setBackgroundColor(underlineColor)
        addView(underLine, 1)

        //初始化containerView并将其添加到DkDropDownMenu
        containerView = FrameLayout(context)
        containerView!!.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        addView(containerView, 2)
    }

    /**
     * 初始化DkDropDownMenu
     *
     * @param tabTexts
     * @param popupViews
     * @param contentView
     */
    fun setDropDownMenu(tabTexts: List<String>, popupViews: List<View>,
                        contentView: View) {
        require(tabTexts.size == popupViews.size) {
            "params not match, tabTexts.size() should be equal" +
                    " popupViews.size()"
        }
        for (i in tabTexts.indices) {
            addTab(tabTexts, i)
        }
        containerView!!.addView(contentView, 0)
        maskView = View(context)
        maskView!!.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        maskView!!.setBackgroundColor(maskColor)
        maskView!!.setOnClickListener { closeMenu() }
        containerView!!.addView(maskView, 1)
        maskView!!.visibility = View.GONE
        popupMenuViews = FrameLayout(context)
        popupMenuViews!!.visibility = View.GONE
        containerView!!.addView(popupMenuViews, 2)
        for (i in popupViews.indices) {
            if (popupViews[i].layoutParams == null) {
                popupViews[i].layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            popupMenuViews!!.addView(popupViews[i], i)
        }
    }

    /**
     * 自定义插入的tabView，如果包含R.id.tv_tab就当做普通的tabtext会统一做变色处理和向下的角标处理
     *
     * @param tab
     * @param index 0start
     */
    fun addTab(tab: View?, index: Int) {
        if (index == (tabMenuView!!.childCount + 1) / 2) {
            addTabEnd(tab)
            return
        }
        tabMenuView!!.addView(tab, index * 2)
        tabMenuView!!.addView(dividerView, index * 2 + 1)
    }

    fun addTabEnd(tab: View?) {
        tabMenuView!!.addView(dividerView, tabMenuView!!.childCount)
        tabMenuView!!.addView(tab, tabMenuView!!.childCount)
    }

    private fun addTab(tabTexts: List<String>, i: Int) {
        val tab = View.inflate(context, R.layout.dk_dropdownmenu_tab_item, null)
        tab.layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f)
        val textView = getTabTextView(tab)
        textView.text = tabTexts[i]
        textView.setTextColor(textUnselectedColor)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize.toFloat())
        setTextDrawables(textView, true)
        tabMenuView!!.addView(tab)
        tab.setOnClickListener { v ->
            if (itemMenuClickListener != null) {
                itemMenuClickListener!!.OnItemMenuClick(textView, i)
            }
            switchMenu(v)
        }

        //添加分割线
        if (i < tabTexts.size - 1) {
            tabMenuView!!.addView(dividerView)
        }
        dropTabViews.add(tab)
    }

    private val dividerView: View
        private get() {
            val view = View(context)
            val height = (if (dividerHeight > 0) dpTpPx(dividerHeight).toDouble() else dividerHeight.toDouble()).toDouble()
            val params = LayoutParams(dpTpPx(0.5f), height.toInt())
            params.gravity = Gravity.CENTER_VERTICAL
            view.layoutParams = params
            view.setBackgroundColor(dividerColor)
            return view
        }

    /**
     * 获取tabView中id为tv_tab的textView
     *
     * @param tabView
     * @return
     */
    private fun getTabTextView(tabView: View): TextView {
        return tabView.findViewById<View>(R.id.tv_tab) as TextView
    }

    /**
     * 改变tab文字
     *
     * @param text
     */
    fun setTabText(text: String?) {
        if (current_tab_position != -1) {
            getTabTextView(tabMenuView!!.getChildAt(current_tab_position)).text = text
        }
    }

    /**
     * 重置tab状态
     *
     * @param tabs
     */
    fun resetTabText(tabs: Array<String?>) {
        //int count = tabMenuView.getChildCount();
        for (index in tabs.indices) {
            var tv: TextView
            var tvIndex: Int
            tvIndex = if (index == 0) {
                0
            } else {
                index + 1
            }
            tv = getTabTextView(tabMenuView!!.getChildAt(tvIndex))
            if (tv != null) {
                tv.text = tabs[index]
            }
        }
    }

    fun setTabClickable(clickable: Boolean) {
        var i = 0
        while (i < tabMenuView!!.childCount) {
            tabMenuView!!.getChildAt(i).isClickable = clickable
            i = i + 2
        }
    }

    /**
     * 关闭菜单
     */
    fun closeMenu() {
        if (current_tab_position != -1) {
            val textView = getTabTextView(tabMenuView!!.getChildAt(current_tab_position))
            textView.setTextColor(textUnselectedColor)
            setTextDrawables(textView, true)
            popupMenuViews!!.visibility = View.GONE
            popupMenuViews!!.animation = AnimationUtils.loadAnimation(context, R.anim.dk_dd_menu_out)
            maskView!!.visibility = View.GONE
            maskView!!.animation = AnimationUtils.loadAnimation(context, R.anim.dk_dd_mask_out)
            current_tab_position = -1
        }
    }

    val isActive: Boolean
        get() = current_tab_position != -1

    fun setTextDrawables(textview: TextView, close: Boolean) {
        textview.setCompoundDrawablesWithIntrinsicBounds(mOrientation!!.getLeft(close), mOrientation!!.getTop(close),
                mOrientation!!.getRight(close), mOrientation!!.getBottom(close))
    }

    /**
     * DkDropDownMenu是否处于可见状态
     *
     * @return
     */
    val isShowing: Boolean
        get() = current_tab_position != -1

    /**
     * 切换菜单
     *
     * @param target
     */
    private fun switchMenu(target: View) {
        //LogHelper.i(TAG, "current===>" + current_tab_position);
        var i = 0
        while (i < tabMenuView!!.childCount) {
            if (target === tabMenuView!!.getChildAt(i)) {
                if (current_tab_position == i) {
                    closeMenu() //关闭
                } else { //打开
                    if (current_tab_position == -1) {
                        popupMenuViews!!.visibility = View.VISIBLE
                        popupMenuViews!!.animation = AnimationUtils.loadAnimation(context, R.anim.dk_dd_menu_in)
                        maskView!!.visibility = View.VISIBLE
                        maskView!!.animation = AnimationUtils.loadAnimation(context, R.anim.dk_dd_mask_in)
                    }
                    val listView = getListView(tabMenuView!!.getChildAt(i))
                    if (listView != null) {
                        listView.visibility = View.VISIBLE
                    }
                    current_tab_position = i
                    val textView = getTabTextView(tabMenuView!!.getChildAt(i))
                    textView.setTextColor(textSelectedColor)
                    setTextDrawables(textView, false)
                }
            } else { //关闭
                val textView = getTabTextView(tabMenuView!!.getChildAt(i))
                textView?.setTextColor(textUnselectedColor)
                val listView = getListView(tabMenuView!!.getChildAt(i))
                if (listView != null) {
                    if (textView != null) {
                        setTextDrawables(textView, true)
                    }
                    listView.visibility = View.GONE
                }
            }
            i = i + 2
        }
    }

    var itemMenuClickListener: OnItemMenuClickListener? = null
    fun setOnItemMenuClickListener(listener: OnItemMenuClickListener?) {
        itemMenuClickListener = listener
    }

    interface OnItemMenuClickListener {
        fun OnItemMenuClick(tabView: TextView?, position: Int)
    }

    /**
     * 获取dropTabViews中对应popupMenuViews数组中的ListView
     *
     * @param view
     * @return
     */
    private fun getListView(view: View): View? {
        return if (dropTabViews.contains(view)) {
            val index = dropTabViews.indexOf(view)
            popupMenuViews!!.getChildAt(index)
        } else {
            null
        }
    }

    fun dpTpPx(value: Float): Int {
        val dm = resources.displayMetrics
        return (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm) + 0.5).toInt()
    }

    companion object {
        private const val TAG = "DkDropDownMenu"

        //icon的方向
        private var iconOrientation: Int = Orientation.Companion.right //默认右则
    }
}
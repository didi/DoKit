package com.didichuxing.doraemonkit.kit.test.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.kit.core.DoKitFrameLayout
import com.didichuxing.doraemonkit.util.ResourceUtils
import com.didichuxing.doraemonkit.kit.test.event.SystemViewNode
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.util.UIUtils

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/15-16:06
 * 描    述：
 * 修订历史：
 * ================================================
 */
object ViewPathUtil {


    fun getActivity(view: View?): Activity {
        return if (view != null && view.context is Activity) {
            view.context as Activity
        } else {
            ActivityUtils.getTopActivity()
        }
    }

    private fun addParentViewInfo(
        systemViewInfos: MutableList<SystemViewNode>,
        parent: ViewGroup?,
        view: View
    ) {
        parent?.let {
            when (parent) {
                is RecyclerView -> {
                    systemViewInfos.add(
                        SystemViewNode(
                            parent::class.tagName,
                            UIUtils.getRealIdText(parent),
                            parent.childCount,
                            parent.indexOfChild(view),
                            true,
                            parent.getChildAdapterPosition(view)
                        )
                    )
                }

                is ListView -> {
                    systemViewInfos.add(
                        SystemViewNode(
                            parent::class.tagName,
                            UIUtils.getRealIdText(parent),
                            parent.childCount,
                            parent.indexOfChild(view),
                            true,
                            parent.getPositionForView(view)
                        )
                    )
                }

                is ViewPager -> {
                    systemViewInfos.add(
                        SystemViewNode(
                            parent::class.tagName,
                            UIUtils.getRealIdText(parent),
                            parent.childCount,
                            parent.indexOfChild(view),
                            true,
                            parent.currentItem
                        )
                    )
                }
                else -> {
                    systemViewInfos.add(
                        SystemViewNode(
                            parent::class.tagName,
                            UIUtils.getRealIdText(parent),
                            parent.childCount,
                            parent.indexOfChild(view)
                        )
                    )
                }
            }
        }


    }


    private fun addSelfViewInfo(
        systemViewInfos: MutableList<SystemViewNode>,
        view: View
    ) {
        when (view) {
            is ViewPager,
            is ListView,
            is RecyclerView -> {
                systemViewInfos.add(
                    SystemViewNode(
                        view::class.tagName,
                        UIUtils.getRealIdText(view),
                        if (view is ViewGroup) {
                            view.childCount
                        } else {
                            -1
                        },
                        -1,
                        true,
                        -1,
                        isCurrentEventView = true
                    )
                )
            }
            else -> {
                systemViewInfos.add(
                    SystemViewNode(
                        view::class.tagName,
                        UIUtils.getRealIdText(view),
                        if (view is ViewGroup) {
                            view.childCount
                        } else {
                            -1
                        },
                        -1,
                        false,
                        -1,
                        isCurrentEventView = true
                    )
                )
            }
        }
    }

    /**
     * 一机多控服务端
     */
    fun createViewPathOfWindow(view: View): MutableList<SystemViewNode> {
        val systemViewInfos: MutableList<SystemViewNode> = mutableListOf()
        addSelfViewInfo(systemViewInfos, view)

        var currentView = view
        var parentView = currentView.parent

        while (parentView != null && parentView::class.tagName != "android.view.ViewRootImpl") {
            if (parentView is ViewGroup) {
                addParentViewInfo(systemViewInfos, parentView, currentView)
                currentView = parentView
                parentView = currentView.parent
            }
        }


        return systemViewInfos
    }


    fun findViewByViewParentInfo(
        decorView: ViewGroup,
        viewParentInfos: MutableList<SystemViewNode>?
    ): View? {
        if (viewParentInfos == null || viewParentInfos.size == 0) {
            return null
        }

        var targetView: View? = null
        var viewParent: View? = null
        //倒序查找
        for (index in (viewParentInfos.size - 1) downTo 0) {
            val viewParentInfo = viewParentInfos[index]
            //decorView 特殊处理
            if (index == viewParentInfos.size - 1) {
                if (viewParentInfo.viewClassName == decorView::class.tagName) {
                    viewParent = decorView.getChildAt(viewParentInfo.childIndexOfViewParent)
                }
                if (decorView is DoKitFrameLayout) {
                    if (viewParentInfos.size == 1) {
                        targetView = decorView
                    } else {
                        viewParent = decorView.getChildAt(viewParentInfo.childIndexOfViewParent)
                    }
                }
            } else {
                viewParent?.let {
                    if (it is ViewGroup) {
                        //处理特殊view
                        if (viewParentInfo.isSpecialView) {
                            //判断当前的view是否和数组的第一个info信息匹配
                            if (viewParentInfos[index].isCurrentEventView && it::class.tagName == viewParentInfos[0].viewClassName
                                && UIUtils.getRealIdText(it) == viewParentInfos[0].viewId
                            ) {
                                targetView = it
                            } else {
                                viewParent = dealSpecialViewGroup(viewParentInfo, it)
                            }
                        } else {
                            //判断当前的view是否和数组的第一个info信息匹配
                            if (viewParentInfos[index].isCurrentEventView && it::class.tagName == viewParentInfos[0].viewClassName
                                && UIUtils.getRealIdText(it) == viewParentInfos[0].viewId
                            ) {
                                targetView = it
                            } else {
                                viewParent =
                                    it.getChildAt(viewParentInfo.childIndexOfViewParent)
                            }
                        }


                    } else {
                        //判断当前的view是否和数组的第一个info信息匹配
                        if (viewParentInfos[index].isCurrentEventView && it::class.tagName == viewParentInfos[0].viewClassName
                            && UIUtils.getRealIdText(it) == viewParentInfos[0].viewId
                        ) {
                            targetView = it
                        }
                    }
                }


            }

        }

        //查询失败
        if (targetView == null) {
            val viewParentInfo = viewParentInfos[0]
            if (viewParentInfo.isCurrentEventView && viewParentInfo.viewId != "-1") {
                val targetViewId = ResourceUtils.getIdByName(viewParentInfo.viewId)
                targetView = decorView.findViewById(targetViewId)
            }
        }
        return targetView

    }

    private fun dealSpecialViewGroup(viewParentInfo: SystemViewNode, viewGroup: ViewGroup): View? {
        try {
            when (viewGroup) {
                is RecyclerView -> {
                    return viewGroup.layoutManager?.findViewByPosition(viewParentInfo.currentEventPosition)!!
                }

                is ListView -> {
                    return getViewByPosition(viewParentInfo.currentEventPosition, viewGroup)
                }

                is ViewPager -> {
                    if (viewGroup.currentItem != viewParentInfo.currentEventPosition) {
                        viewGroup.currentItem = viewParentInfo.currentEventPosition
                        Thread.sleep(10)
                    }

                    val adapter: PagerAdapter? = viewGroup.adapter
                    adapter?.let {
                        if (it is FragmentPagerAdapter) {
                            val fragment = it.getItem(viewParentInfo.currentEventPosition)
                            return fragment.requireView()
                        } else {
                            val item = it.instantiateItem(
                                viewGroup,
                                viewParentInfo.currentEventPosition
                            )
                            if (item is View) {
                                return item
                            } else {
                                viewGroup.getChildAt(viewParentInfo.childIndexOfViewParent)
                            }
                        }
                    }
                    return viewGroup.getChildAt(viewParentInfo.childIndexOfViewParent)
                }

                else -> {
                    return viewGroup.getChildAt(viewParentInfo.currentEventPosition)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 获取listView中的指定pos itemView
     */
    private fun getViewByPosition(pos: Int, listView: ListView): View {
        val firstListItemPosition: Int = listView.firstVisiblePosition
        val lastListItemPosition: Int = firstListItemPosition + listView.childCount - 1

        return if (pos < firstListItemPosition || pos > lastListItemPosition) {
            listView.adapter.getView(pos, null, listView)
        } else {
            val childIndex = pos - firstListItemPosition
            listView.getChildAt(childIndex)
        }
    }

}

package com.didichuxing.doraemonkit

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
//        String json = "{\"eventType\":\"WSE_ACCESS_EVENT\",\"from\":\"HOST\",\"msgMaps\":{\"activityName\":\"com.didichuxing.doraemondemo.mc.MCActivity\"},\"viewC12c\":{\"acc\":{\"mAction\":0,\"mContentChangeTypes\":0,\"mEventTime\":1163811924,\"mEventType\":1,\"mMovementGranularity\":0,\"mPackageName\":\"com.didichuxing.doraemondemo.java\",\"mRecords\":null,\"mWindowChangeTypes\":0,\"originStackTrace\":null,\"mAddedCount\":-1,\"mBeforeText\":null,\"mBooleanProperties\":514,\"mClassName\":\"android.widget.ImageView\",\"mConnectionId\":-1,\"mContentDescription\":null,\"mCurrentItemIndex\":-1,\"mFromIndex\":-1,\"mIsInPool\":false,\"mItemCount\":-1,\"mMaxScrollX\":-1,\"mMaxScrollY\":-1,\"mNext\":null,\"mParcelableData\":null,\"mRemovedCount\":-1,\"mScrollDeltaX\":-1,\"mScrollDeltaY\":-1,\"mScrollX\":-1,\"mScrollY\":-1,\"mSealed\":false,\"mSourceNodeId\":-4294966900,\"mSourceWindowId\":-1,\"mText\":[],\"mToIndex\":-1},\"childCount\":0,\"directParentId \":\"title_bar\",\"directParentViewClassName\":\"com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar\",\"hasChild\":false,\"id\":\"icon\",\"imgWHRatio\":\"1.00\",\"indexOfDirectParent\":1,\"svgc12c\":{\"currentViewIndexOfSpecialViewGroup\":-1,\"specialViewGroupClassName\":\"\"},\"text\":\"\",\"viewClassName\":\"androidx.appcompat.widget.AppCompatImageView\",\"windowId\":\"android.view.ViewRootImpl$W@d37e0b3\"}}";
//        WSEvent wsEvent = GsonUtils.fromJson(json, WSEvent.class);
//
//
//        System.out.println(wsEvent.getFrom());

        val list = listOf<Int>(1, 2, 3, 4, 5, 6, 7)
        list.forEach {
            if (it == 3) {
                return@forEach
            }
            println(it)
        }
    }
}
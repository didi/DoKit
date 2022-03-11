//package com.didichuxing.doraemondemo.dokit;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.FrameLayout;
//
//import com.didichuxing.doraemonkit.R;
//import com.didichuxing.doraemonkit.kit.core.AbsDokitView;
//import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams;
//import com.didichuxing.doraemonkit.kit.viewcheck.LayoutBorderView;
//import com.didichuxing.doraemonkit.model.ViewInfo;
//
///**
// * Created by jintai on 2019/09/26.
// * 在改布局上绘制相应的View
// */
//public class BorderDoKitView extends AbsDokitView {
//    private LayoutBorderView mLayoutBorderView = null;
//
//    @Override
//    public void onCreate(Context context) {
//
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//    }
//
//    @Override
//    public View onCreateView(Context context, FrameLayout view) {
//        return LayoutInflater.from(context).inflate(R.layout.dk_float_view_check_draw, null);
//    }
//
//
//    @Override
//    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
//        params.flags = DokitViewLayoutParams.FLAG_NOT_FOCUSABLE_AND_NOT_TOUCHABLE;
//        params.width = DokitViewLayoutParams.MATCH_PARENT;
//        params.height = DokitViewLayoutParams.MATCH_PARENT;
//    }
//
//
//    @Override
//    public void onViewCreated(FrameLayout view) {
//        mLayoutBorderView = findViewById(R.id.rect_view);
//        setDoKitViewNotResponseTouchEvent(getDoKitView());
//    }
//
//
//    /**
//     * 解决ViewCheckDrawDokitView的margin被改变的bug
//     */
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (getNormalLayoutParams() != null) {
//            FrameLayout.LayoutParams params = getNormalLayoutParams();
//            params.setMargins(0, 0, 0, 0);
//            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
//            params.height = FrameLayout.LayoutParams.MATCH_PARENT;
//            immInvalidate();
//        }
//    }
//
//    @Override
//    public boolean canDrag() {
//        return false;
//    }
//
//    public void showBorder(View target) {
//        if (target == null) {
//            mLayoutBorderView.showViewLayoutBorder((ViewInfo) null);
//        } else {
//            mLayoutBorderView.showViewLayoutBorder(new ViewInfo(target));
//        }
//    }
//
//}

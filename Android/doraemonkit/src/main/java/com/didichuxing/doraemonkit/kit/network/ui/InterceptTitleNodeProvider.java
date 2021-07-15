package com.didichuxing.doraemonkit.kit.network.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.network.bean.MockApiResponseBean;
import com.didichuxing.doraemonkit.kit.network.bean.MockInterceptTitleBean;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.kit.network.room_db.MockInterceptApiBean;
import com.didichuxing.doraemonkit.widget.brvah.entity.node.BaseNode;
import com.didichuxing.doraemonkit.widget.brvah.provider.BaseNodeProvider;
import com.didichuxing.doraemonkit.widget.brvah.viewholder.BaseViewHolder;

import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/30-15:50
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class InterceptTitleNodeProvider extends BaseNodeProvider {
    @Override
    public int getItemViewType() {
        return InterceptMockAdapter.TYPE_TITLE;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dk_mock_title_item;
    }


    @Override
    public void convert(@NonNull BaseViewHolder holder, BaseNode item) {
        if (item instanceof MockInterceptTitleBean) {
            final MockInterceptTitleBean mockTitleBean = (MockInterceptTitleBean) item;
            MockInterceptApiBean mockApi0 = (MockInterceptApiBean) mockTitleBean.getChildNode().get(0);
            holder.setText(R.id.tv_title, mockTitleBean.getName());
            if (mockTitleBean.isExpanded()) {
                holder.setImageResource(R.id.iv_more, R.mipmap.dk_arrow_open);
            } else {
                holder.setImageResource(R.id.iv_more, R.mipmap.dk_arrow_normal);
            }
            CheckBox checkBox = holder.getView(R.id.menu_switch);
            //建议将setOnCheckedChangeListener放在控件checkBox.setChecked前面 否则代码设置选中时会触发回调导致状态不正确
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    MockInterceptApiBean mockApi = (MockInterceptApiBean) mockTitleBean.getChildNode().get(0);
                    //LogHelper.i(TAG, "checkBox====>" + mockApi.getMockApiName() + "----" + isChecked);
                    mockApi.setOpen(isChecked);
                    //默认选中第一个场景
                    if (TextUtils.isEmpty(mockApi.getSelectedSceneId())) {
                        List<MockApiResponseBean.DataBean.DatalistBean.SceneListBean> sceneListBeans = mockApi.getSceneList();
                        if (sceneListBeans != null && sceneListBeans.size() > 0) {
                            MockApiResponseBean.DataBean.DatalistBean.SceneListBean sceneListBean = sceneListBeans.get(0);
                            mockApi.setSelectedSceneName(sceneListBean.getName());
                            mockApi.setSelectedSceneId(sceneListBean.get_id());
                        }
                    }

                    DokitDbManager.getInstance().updateInterceptApi(mockApi);

                }
            });
            //LogHelper.i(TAG, "init====>" + mockApi0.getMockApiName() + "----" + mockApi0.isOpen());
            if (mockApi0.isOpen()) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }

    }

    @Override
    public void onClick(BaseViewHolder holder, View view, BaseNode data, int position) {
        super.onClick(holder, view, data, position);
        if (data instanceof MockInterceptTitleBean && getAdapter() != null) {
            getAdapter().expandOrCollapse(position);
            final MockInterceptTitleBean mockTitleBean = (MockInterceptTitleBean) data;
            if (mockTitleBean.isExpanded()) {
                holder.setImageResource(R.id.iv_more, R.mipmap.dk_arrow_normal);
            } else {
                holder.setImageResource(R.id.iv_more, R.mipmap.dk_arrow_open);
            }
        }

    }


}

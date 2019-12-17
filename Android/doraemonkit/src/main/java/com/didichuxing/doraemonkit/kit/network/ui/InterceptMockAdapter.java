package com.didichuxing.doraemonkit.kit.network.ui;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.blankj.utilcode.util.EncodeUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.network.bean.MockApiResponseBean;
import com.didichuxing.doraemonkit.kit.network.bean.MockInterceptTitleBean;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.kit.network.room_db.MockInterceptApiBean;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.view.MultiLineRadioGroup;
import com.didichuxing.doraemonkit.view.jsonviewer.JsonRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-11-12-15:04
 * 描    述：mock adapter
 * 修订历史：
 * ================================================
 */
public class InterceptMockAdapter<T extends MultiItemEntity> extends BaseMultiItemQuickAdapter<T, BaseViewHolder> {
    public static final String TAG = "MockApiAdapter";
    public final static int TYPE_TITLE = 100;
    public final static int TYPE_CONTENT = 200;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public InterceptMockAdapter(List<T> data) {
        super(data);
        addItemType(TYPE_TITLE, R.layout.dk_mock_title_item);
        addItemType(TYPE_CONTENT, R.layout.dk_mock_intercept_content_item);
    }

    @Override
    protected void convert(@NonNull final BaseViewHolder holder, T item) {
        switch (holder.getItemViewType()) {
            case TYPE_TITLE:
                final MockInterceptTitleBean mockTitleBean = (MockInterceptTitleBean) item;
                MockInterceptApiBean mockApi0 = mockTitleBean.getSubItem(0);
                //设置item的点击事件
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        if (mockTitleBean.isExpanded()) {
                            collapse(pos);
                            holder.setImageResource(R.id.iv_more, R.drawable.dk_arrow_normal);
                        } else {
                            expand(pos);
                            holder.setImageResource(R.id.iv_more, R.drawable.dk_arrow_open);
                        }
                    }
                });


                holder.setText(R.id.tv_title, mockTitleBean.getName());
                if (mockTitleBean.isExpanded()) {
                    holder.setImageResource(R.id.iv_more, R.drawable.dk_arrow_open);
                } else {
                    holder.setImageResource(R.id.iv_more, R.drawable.dk_arrow_normal);
                }
                CheckBox checkBox = holder.getView(R.id.menu_switch);
                //建议将setOnCheckedChangeListener放在控件checkBox.setChecked前面 否则代码设置选中时会触发回调导致状态不正确
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        MockInterceptApiBean mockApi = mockTitleBean.getSubItem(0);
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


                break;

            case TYPE_CONTENT:
                final MockInterceptApiBean mockApi = (MockInterceptApiBean) item;
                holder.setText(R.id.tv_path, "path:" + mockApi.getPath());
                JsonRecyclerView jsonRecyclerView = holder.getView(R.id.jsonviewer);

                try {
                    holder.getView(R.id.rl_params).setVisibility(View.VISIBLE);
                    JSONObject jsonObject = new JSONObject(mockApi.getQuery());
                    if (jsonObject.length() == 0) {
                        holder.getView(R.id.rl_params).setVisibility(View.GONE);
                    } else {
                        jsonRecyclerView.bindJson(mockApi.getQuery());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    holder.getView(R.id.rl_params).setVisibility(View.GONE);
                }

                holder.setText(R.id.tv_group, "分组:" + mockApi.getGroup());
                holder.setText(R.id.tv_create, "创建人:" + mockApi.getCreatePerson());
                holder.setText(R.id.tv_modify, "修改人:" + mockApi.getModifyPerson());
                final MultiLineRadioGroup radioGroup = holder.getView(R.id.radio_group);
                if (mockApi.getSceneList() != null && mockApi.getSceneList().size() != 0) {
                    String[] radioButtons = new String[mockApi.getSceneList().size()];
                    for (int index = 0; index < mockApi.getSceneList().size(); index++) {
                        radioButtons[index] = mockApi.getSceneList().get(index).getName();
                    }
                    radioGroup.removeAllButtons();
                    radioGroup.addButtons(radioButtons);
                    radioGroup.setOnCheckedChangeListener(new MultiLineRadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(ViewGroup group, RadioButton button) {
                            int index = radioGroup.getCheckedRadioButtonIndex();
                            MockApiResponseBean.DataBean.DatalistBean.SceneListBean sceneListBean = mockApi.getSceneList().get(index);
                            mockApi.setSelectedSceneName(sceneListBean.getName());
                            mockApi.setSelectedSceneId(sceneListBean.get_id());
                            DokitDbManager.getInstance().updateInterceptApi(mockApi);
                        }
                    });
                    int index = 0;
                    for (int i = 0; i < mockApi.getSceneList().size(); i++) {
                        if (mockApi.getSceneList().get(i).get_id().equals(mockApi.getSelectedSceneId())) {
                            index = i;
                            break;
                        }

                    }
                    //默认选中第一个场景
                    radioGroup.checkAt(index);
                }

                break;
            default:
                break;
        }
    }
}

package com.didichuxing.doraemonkit.kit.network.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.network.bean.MockApiResponseBean;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.kit.network.room_db.MockInterceptApiBean;
import com.didichuxing.doraemonkit.widget.MultiLineRadioGroup;
import com.didichuxing.doraemonkit.widget.brvah.entity.node.BaseNode;
import com.didichuxing.doraemonkit.widget.brvah.provider.BaseNodeProvider;
import com.didichuxing.doraemonkit.widget.brvah.viewholder.BaseViewHolder;
import com.didichuxing.doraemonkit.widget.jsonviewer.JsonRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/30-15:50
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class InterceptDetailNodeProvider extends BaseNodeProvider {
    @Override
    public int getItemViewType() {
        return InterceptMockAdapter.TYPE_CONTENT;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dk_mock_intercept_content_item;
    }

    @Override
    public void convert(BaseViewHolder holder, BaseNode item) {
        if (item instanceof MockInterceptApiBean) {
            final MockInterceptApiBean mockApi = (MockInterceptApiBean) item;
            holder.setText(R.id.tv_path, "path:" + mockApi.getPath());
            JsonRecyclerView jsonQuery = holder.getView(R.id.jsonviewer_query);
            JsonRecyclerView jsonBody = holder.getView(R.id.jsonviewer_body);

            try {
                holder.getView(R.id.rl_query).setVisibility(View.VISIBLE);
                JSONObject jsonObject = new JSONObject(mockApi.getQuery());
                if (jsonObject.length() == 0) {
                    holder.getView(R.id.rl_query).setVisibility(View.GONE);
                } else {
                    jsonQuery.bindJson(mockApi.getQuery());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                holder.getView(R.id.rl_query).setVisibility(View.GONE);
            }

            try {
                holder.getView(R.id.rl_body).setVisibility(View.VISIBLE);
                JSONObject jsonObject = new JSONObject(mockApi.getBody());
                if (jsonObject.length() == 0) {
                    holder.getView(R.id.rl_body).setVisibility(View.GONE);
                } else {
                    jsonBody.bindJson(mockApi.getBody());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                holder.getView(R.id.rl_body).setVisibility(View.GONE);
            }


            holder.setText(R.id.tv_group, "group:" + mockApi.getGroup());
            holder.setText(R.id.tv_create, "create person:" + mockApi.getCreatePerson());
            holder.setText(R.id.tv_modify, "modify person:" + mockApi.getModifyPerson());
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
        }

    }
}

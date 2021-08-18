package com.didichuxing.doraemonkit.kit.network.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.kit.network.room_db.MockTemplateApiBean;
import com.didichuxing.doraemonkit.util.DoKitCommUtil;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.volley.VolleyManager;
import com.didichuxing.doraemonkit.widget.brvah.entity.node.BaseNode;
import com.didichuxing.doraemonkit.widget.brvah.provider.BaseNodeProvider;
import com.didichuxing.doraemonkit.widget.brvah.viewholder.BaseViewHolder;
import com.didichuxing.doraemonkit.widget.jsonviewer.JsonRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/30-15:50
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class TemplateDetailNodeProvider extends BaseNodeProvider {
    private static final String TAG = "TemplateDetailNodeProvider";

    @Override
    public int getItemViewType() {
        return TemplateMockAdapter.TYPE_CONTENT;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dk_mock_template_content_item;
    }

    @Override
    public void convert(BaseViewHolder holder, BaseNode item) {
        if (item instanceof MockTemplateApiBean) {
            final MockTemplateApiBean mockApi = (MockTemplateApiBean) item;
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
            final TextView tvView = holder.getView(R.id.tv_view);
            tvView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(mockApi.getStrResponse())) {
                        ToastUtils.showShort("no mock template data");
                        return;
                    }
                    //保存到全局
                    DokitDbManager.getInstance().setGlobalTemplateApiBean(mockApi);

                    DoKit.launchFullScreen(MockTemplatePreviewFragment.class, tvView.getContext());

                }
            });
            TextView tvUpload = holder.getView(R.id.tv_upload);
            tvUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> values = new HashMap<>();
                    values.put("projectId", mockApi.getProjectId());
                    values.put("id", mockApi.getId());
                    values.put("tempData", mockApi.getStrResponse());

                    Request<JSONObject> request = new JsonObjectRequest(Request.Method.PATCH, TemplateMockAdapter.TEMPLATER_UPLOAD_URL, new JSONObject(values), new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ToastUtils.showShort("upload template succeed");
                            LogHelper.i(TAG, "上传模板===>" + response.toString());
                        }
                    }, new com.android.volley.Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ToastUtils.showShort("upload template failed");
                            LogHelper.e(TAG, "error===>" + error.getMessage());
                        }
                    });

                    VolleyManager.INSTANCE.add(request);
                }
            });

            TextView tvHasLocalMockData = holder.getView(R.id.tv_local_has_mock_template);
            String hasLocalMockData;
            if (!TextUtils.isEmpty(mockApi.getStrResponse())) {
                hasLocalMockData = "Y";
                tvUpload.setClickable(true);
                tvUpload.setTextColor(tvUpload.getContext().getResources().getColor(R.color.dk_color_337CC4));
            } else {
                hasLocalMockData = "N";
                tvUpload.setClickable(false);
                tvUpload.setTextColor(tvUpload.getContext().getResources().getColor(R.color.dk_color_999999));
            }
            tvHasLocalMockData.setText(String.format(DoKitCommUtil.getString(R.string.dk_data_mock_template_tip), hasLocalMockData));

        }

    }
}

package com.didichuxing.doraemonkit.kit.network.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.kit.network.room_db.MockTemplateApiBean;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.volley.VolleyManager;
import com.didichuxing.doraemonkit.widget.jsonviewer.JsonRecyclerView;
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 魔板数据预览fragment
 */
public class MockTemplatePreviewFragment extends BaseFragment {


    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_mock_template_preview;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        if (getActivity() == null || DokitDbManager.getInstance().getGlobalTemplateApiBean() == null) {
            return;
        }
        HomeTitleBar homeTitleBar = findViewById(R.id.title_bar);
        homeTitleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                finish();
            }
        });
        TextView tvName = findViewById(R.id.tv_name);
        TextView tvPath = findViewById(R.id.tv_path);
        tvName.setText(String.format("mock api name:%s", DokitDbManager.getInstance().getGlobalTemplateApiBean().getMockApiName()));
        tvPath.setText(String.format("mock api path:%s", DokitDbManager.getInstance().getGlobalTemplateApiBean().getPath()));
        JsonRecyclerView jsonViewQuery = findViewById(R.id.json_query);
        JsonRecyclerView jsonRecycleView = findViewById(R.id.jsonviewer);

        TextView tvUpload = findViewById(R.id.tv_upload);
        tvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DokitDbManager.getInstance().getGlobalTemplateApiBean() == null) {
                    ToastUtils.showShort("no mock template data");
                    return;
                }
                MockTemplateApiBean mockApi = DokitDbManager.getInstance().getGlobalTemplateApiBean();

                Map<String, String> values = new HashMap<>();
                values.put("projectId", mockApi.getProjectId());
                values.put("id", mockApi.getId());
                values.put("tempData", mockApi.getStrResponse());

                Request<JSONObject> request = new JsonObjectRequest(Request.Method.PATCH, TemplateMockAdapter.TEMPLATER_UPLOAD_URL, new JSONObject(values), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ToastUtils.showShort("upload template succeed");
                        LogHelper.i(TAG, "上传模板===>" + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showShort("upload template failed");
                        LogHelper.e(TAG, "error===>" + error.getMessage());
                    }
                });

                VolleyManager.INSTANCE.add(request);
            }
        });

        if (DokitDbManager.getInstance().getGlobalTemplateApiBean() == null) {
            ToastUtils.showShort("no mock template data");
            return;
        }
        try {

            JSONObject jsonQuery = new JSONObject(DokitDbManager.getInstance().getGlobalTemplateApiBean().getQuery());
            if (jsonQuery.length() == 0) {
                jsonViewQuery.setVisibility(View.GONE);
            } else {
                jsonViewQuery.setVisibility(View.VISIBLE);
                jsonViewQuery.bindJson(jsonQuery);
            }

            new JSONObject(DokitDbManager.getInstance().getGlobalTemplateApiBean().getStrResponse());
            jsonRecycleView.setTextSize(16);
            jsonRecycleView.bindJson(DokitDbManager.getInstance().getGlobalTemplateApiBean().getStrResponse());
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtils.showShort("the data is not json");
        }

    }


}

package com.didichuxing.doraemonkit.kit.network.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.kit.network.room_db.MockTemplateApiBean;
import com.didichuxing.doraemonkit.okgo.DokitOkGo;
import com.didichuxing.doraemonkit.okgo.callback.StringCallback;
import com.didichuxing.doraemonkit.okgo.model.Response;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.view.jsonviewer.JsonRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

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
        tvName.setText(String.format("mock接口名称:%s", DokitDbManager.getInstance().getGlobalTemplateApiBean().getMockApiName()));
        tvPath.setText(String.format("mock接口路径:%s", DokitDbManager.getInstance().getGlobalTemplateApiBean().getPath()));
        JsonRecyclerView jsonViewQuery = findViewById(R.id.json_query);
        JsonRecyclerView jsonRecycleView = findViewById(R.id.jsonviewer);

        TextView tvUpload = findViewById(R.id.tv_upload);
        tvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DokitDbManager.getInstance().getGlobalTemplateApiBean() == null) {
                    ToastUtils.showShort("暂无本地mock数据模板");
                    return;
                }
                MockTemplateApiBean mockApi = DokitDbManager.getInstance().getGlobalTemplateApiBean();
                DokitOkGo.<String>patch(TemplateMockAdapter.TEMPLATER_UPLOAD_URL)
                        .params("projectId", mockApi.getProjectId())
                        .params("id", mockApi.getId())
                        .params("tempData", mockApi.getStrResponse())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                ToastUtils.showShort("上传模板成功");
                                LogHelper.i(TAG, "上传模板===>" + response.body());
                            }

                            @Override
                            public void onError(Response<String> response) {
                                super.onError(response);
                                ToastUtils.showShort("上传模板失败");
                                LogHelper.e(TAG, "error===>" + response.body());
                            }
                        });
            }
        });

        if (DokitDbManager.getInstance().getGlobalTemplateApiBean() == null) {
            ToastUtils.showShort("暂无本地mock数据模板");
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
            ToastUtils.showShort("数据不符合json格式");
        }

    }


}

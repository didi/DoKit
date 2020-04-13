package com.didichuxing.doraemonkit.kit.network.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.util.DokitUtil;
import com.didichuxing.doraemonkit.view.bravh.entity.node.BaseNode;
import com.didichuxing.doraemonkit.view.bravh.provider.BaseNodeProvider;
import com.didichuxing.doraemonkit.view.bravh.viewholder.BaseViewHolder;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.kit.network.room_db.MockTemplateApiBean;
import com.didichuxing.doraemonkit.okgo.DokitOkGo;
import com.didichuxing.doraemonkit.okgo.callback.StringCallback;
import com.didichuxing.doraemonkit.okgo.model.Response;
import com.didichuxing.doraemonkit.ui.UniversalActivity;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.view.jsonviewer.JsonRecyclerView;

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
                    Intent intent = new Intent(tvView.getContext(), UniversalActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_MOCK_TEMPLATE_PREVIEW);
                    tvView.getContext().startActivity(intent);
                }
            });
            TextView tvUpload = holder.getView(R.id.tv_upload);
            tvUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DokitOkGo.<String>patch(TemplateMockAdapter.TEMPLATER_UPLOAD_URL)
                            .params("projectId", mockApi.getProjectId())
                            .params("id", mockApi.getId())
                            .params("tempData", mockApi.getStrResponse())
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    LogHelper.i(TAG, "上传模板===>" + response.body());
                                    ToastUtils.showShort("upload template succeed");
                                }

                                @Override
                                public void onError(Response<String> response) {
                                    super.onError(response);
                                    ToastUtils.showShort("upload template failed");
                                    LogHelper.e(TAG, "上传模板失败===>" + response.body());
                                }
                            });

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
            tvHasLocalMockData.setText(String.format(DokitUtil.getString(R.string.dk_data_mock_template_tip), hasLocalMockData));

        }

    }
}

package com.didichuxing.doraemonkit.kit.network.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.bean.MockTemplateTitleBean;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.kit.network.room_db.MockTemplateApiBean;
import com.didichuxing.doraemonkit.okgo.OkGo;
import com.didichuxing.doraemonkit.okgo.callback.StringCallback;
import com.didichuxing.doraemonkit.okgo.model.Response;
import com.didichuxing.doraemonkit.ui.UniversalActivity;
import com.didichuxing.doraemonkit.util.LogHelper;
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
public class TemplateMockAdapter<T extends MultiItemEntity> extends BaseMultiItemQuickAdapter<T, BaseViewHolder> {

    public static String TEMPLATER_UPLOAD_URL = NetworkManager.MOCK_DOMAIN + "/api/app/interface";
    public static final String TAG = "TemplateMockAdapter";
    public final static int TYPE_TITLE = 100;
    public final static int TYPE_CONTENT = 200;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public TemplateMockAdapter(List<T> data) {
        super(data);
        addItemType(TYPE_TITLE, R.layout.dk_mock_title_item);
        addItemType(TYPE_CONTENT, R.layout.dk_mock_template_content_item);
    }

    @Override
    protected void convert(@NonNull final BaseViewHolder holder, T item) {
        final MockTemplateApiBean mockApi;
        switch (holder.getItemViewType()) {
            case TYPE_TITLE:
                final MockTemplateTitleBean mockTitleBean = (MockTemplateTitleBean) item;
                mockApi = mockTitleBean.getSubItem(0);
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
                        MockTemplateApiBean mockApi = mockTitleBean.getSubItem(0);
                        mockApi.setOpen(isChecked);
                        DokitDbManager.getInstance().updateTemplateApi(mockApi);

                    }
                });
                if (mockApi.isOpen()) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }


                break;

            case TYPE_CONTENT:
                mockApi = (MockTemplateApiBean) item;
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
                final TextView tvView = holder.getView(R.id.tv_view);
                tvView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(mockApi.getStrResponse())) {
                            ToastUtils.showShort("暂无mock模板数据");
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
                        OkGo.<String>patch(TEMPLATER_UPLOAD_URL)
                                .params("projectId", mockApi.getProjectId())
                                .params("id", mockApi.getId())
                                .params("tempData", mockApi.getStrResponse())
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        ToastUtils.showShort("上传模板成功");
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

                TextView tvHasLocalMockData = holder.getView(R.id.tv_local_has_mock_template);
                String hasLocalMockData;
                if (!TextUtils.isEmpty(mockApi.getStrResponse())) {
                    hasLocalMockData = "是";
                    tvUpload.setClickable(true);
                    tvUpload.setTextColor(tvUpload.getContext().getResources().getColor(R.color.dk_color_337CC4));
                } else {
                    hasLocalMockData = "否";
                    tvUpload.setClickable(false);
                    tvUpload.setTextColor(tvUpload.getContext().getResources().getColor(R.color.dk_color_999999));
                }
                tvHasLocalMockData.setText(String.format("本地是否存在mock模板数据:%s", hasLocalMockData));
                break;
            default:
                break;
        }
    }
}

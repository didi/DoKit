package com.didichuxing.doraemonkit.kit.network.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.network.bean.WhiteHostBean;
import com.didichuxing.doraemonkit.util.DoKitCommUtil;
import com.didichuxing.doraemonkit.widget.brvah.BaseQuickAdapter;
import com.didichuxing.doraemonkit.widget.brvah.viewholder.BaseViewHolder;

import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/19-14:41
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class WhiteHostAdapter extends BaseQuickAdapter<WhiteHostBean, BaseViewHolder> {


    WhiteHostAdapter(int layoutResId, @Nullable List<WhiteHostBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull final BaseViewHolder helper, final WhiteHostBean item) {
        if (item.isCanAdd()) {
            helper.<TextView>getView(R.id.tv_add).setText("+");
        } else {
            helper.<TextView>getView(R.id.tv_add).setText("-");
        }
        helper.<EditText>getView(R.id.ed_host).setText(item.getHost());
        helper.<EditText>getView(R.id.ed_host).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    item.setHost(s.toString());
                }
            }
        });
        helper.getView(R.id.fl_add_wrap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<WhiteHostBean> hostBeans = getData();
                String text = helper.<TextView>getView(R.id.tv_add).getText().toString();
                if (text.equals("+")) {
                    String editText = helper.<EditText>getView(R.id.ed_host).getText().toString();
                    if (TextUtils.isEmpty(editText)) {
                        ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_kit_net_monitor_white_host_edit_toast));
                        return;
                    }
                    for (WhiteHostBean hostBean : hostBeans) {
                        hostBean.setCanAdd(false);
                    }
                    hostBeans.add(new WhiteHostBean("", true));
                } else {
                    hostBeans.remove(item);
                }

                notifyDataSetChanged();
            }
        });

    }
}

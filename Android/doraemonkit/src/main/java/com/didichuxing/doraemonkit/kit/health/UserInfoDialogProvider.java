package com.didichuxing.doraemonkit.kit.health;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.widget.dialog.DialogListener;
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider;

/**
 * Created by jint on 2019/4/12
 * 完善健康体检用户信息dialog
 * @author jintai
 */
public class UserInfoDialogProvider<T> extends DialogProvider<T> {
    private TextView mPositive;
    private TextView mNegative;
    private TextView mClose;
    private EditText mCaseName;
    private EditText mUserName;

    UserInfoDialogProvider(T data, DialogListener listener) {
        super(data, listener);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dk_dialog_userinfo;
    }

    @Override
    protected void findViews(View view) {
        mPositive = view.findViewById(R.id.positive);
        mNegative = view.findViewById(R.id.negative);
        mClose = view.findViewById(R.id.close);
        mCaseName = view.findViewById(R.id.edit_case_name);
        mUserName = view.findViewById(R.id.edit_user_name);
    }

    @Override
    protected void bindData(Object data) {

    }

    @Override
    protected View getPositiveView() {
        return mPositive;
    }

    @Override
    protected View getNegativeView() {
        return mNegative;
    }

    @Override
    protected View getCancelView() {
        return mClose;
    }

    /**
     * 上传健康体检数据
     */
    boolean uploadAppHealthInfo(UploadAppHealthCallback uploadAppHealthCallBack) {
        if (!userInfoCheck()) {
            ToastUtils.showShort("请填写测试用例和测试人");
            return false;
        }
        String caseName = mCaseName.getText().toString();
        String userName = mUserName.getText().toString();

        AppHealthInfoUtil.getInstance().setBaseInfo(caseName, userName);
        //上传数据
        try {
            AppHealthInfoUtil.getInstance().post(uploadAppHealthCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 检查用户数据
     */
    private boolean userInfoCheck() {
        if (mCaseName == null || TextUtils.isEmpty(mCaseName.getText().toString())) {
            return false;
        }

        if (mUserName == null || TextUtils.isEmpty(mUserName.getText().toString())) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }
}
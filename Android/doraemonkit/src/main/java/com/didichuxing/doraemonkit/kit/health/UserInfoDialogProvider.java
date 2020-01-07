package com.didichuxing.doraemonkit.kit.health;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.dialog.DialogListener;
import com.didichuxing.doraemonkit.ui.dialog.DialogProvider;

/**
 * Created by jint on 2019/4/12
 * 完善健康体检用户信息dialog
 */
public class UserInfoDialogProvider extends DialogProvider<Object> {
    private TextView mPositive;
    private TextView mNegative;
    private EditText mCaseName;
    private EditText mUserName;

    public UserInfoDialogProvider(Object data, DialogListener listener) {
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

    /**
     * 上传健康体检数据
     */
    void uploadAppHealthInfo(UploadAppHealthCallback uploadAppHealthCallBack) {
        if (!userInfoCheck()) {
            ToastUtils.showShort("请填写测试用例和测试人");
            return;
        }
        String caseName = mCaseName.getText().toString();
        String userName = mUserName.getText().toString();

        AppHealthInfoUtil.getInstance().setBaseInfo(caseName, userName);
        //上传数据
        AppHealthInfoUtil.getInstance().post(uploadAppHealthCallBack);

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

}
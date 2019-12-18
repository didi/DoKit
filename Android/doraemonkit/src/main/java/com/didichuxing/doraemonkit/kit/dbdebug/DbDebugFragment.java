package com.didichuxing.doraemonkit.kit.dbdebug;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.amitshekhar.DebugDB;
import com.amitshekhar.debug.encrypt.sqlite.DebugDBEncryptFactory;
import com.amitshekhar.debug.sqlite.DebugDBFactory;
import com.blankj.utilcode.util.NetworkUtils;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.DoraemonKitReal;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.dialog.DialogInfo;
import com.didichuxing.doraemonkit.ui.dialog.SimpleDialogListener;
import com.didichuxing.doraemonkit.ui.setting.SettingItem;
import com.didichuxing.doraemonkit.ui.setting.SettingItemAdapter;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.DividerItemDecoration;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.util.DataCleanUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jintai
 * Created by jintai on 2019/10/17.
 * 数据库远程调试介绍页面
 */

public class DbDebugFragment extends BaseFragment {
    TextView tvIp;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_db_debug;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        if (!DebugDB.isServerRunning()) {
            DebugDB.initialize(DoraemonKitReal.APPLICATION, new DebugDBFactory());
            DebugDB.initialize(DoraemonKitReal.APPLICATION, new DebugDBEncryptFactory());
        }
        HomeTitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                finish();
            }
        });
        TextView tvTip = findViewById(R.id.tv_tip);
        tvTip.setText(Html.fromHtml(getResources().getString(R.string.dk_kit_db_debug_desc)));
        tvIp = findViewById(R.id.tv_ip);
        if (DebugDB.isServerRunning()) {
            tvIp.setText("" + DebugDB.getAddressLog().replace("Open ", "").replace("in your browser", ""));
        } else {
            tvIp.setText("servse is not start");
        }
    }

    /**
     * 网络变化时调用
     *
     * @param networkType
     */
    public void networkChanged(NetworkUtils.NetworkType networkType) {
        if (tvIp == null) {
            return;
        }
        if (networkType == NetworkUtils.NetworkType.NETWORK_NO) {
            tvIp.setText("please check network is connected");
        } else {
            tvIp.setText("" + DebugDB.getAddressLog().replace("Open ", "").replace("in your browser", ""));
        }
    }
}
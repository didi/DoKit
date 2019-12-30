package com.didichuxing.doraemonkit.kit.dbdebug;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.amitshekhar.DebugDB;
import com.amitshekhar.debug.encrypt.sqlite.DebugDBEncryptFactory;
import com.amitshekhar.debug.sqlite.DebugDBFactory;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.util.netstate.NetType;
import com.didichuxing.doraemonkit.util.netstate.NetWork;
import com.didichuxing.doraemonkit.util.netstate.NetworkManager;

/**
 * @author jintai
 * Created by jintai on 2019/10/17.
 * 数据库远程调试介绍页面
 */

public class DbDebugFragment extends BaseFragment {

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_db_debug;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NetworkManager.get().register(this);
        initView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NetworkManager.get().unRegister(this);
    }

    private void initView() {
        if (!DebugDB.isServerRunning()) {
            DebugDB.initialize(DoraemonKit.APPLICATION, new DebugDBFactory());
            DebugDB.initialize(DoraemonKit.APPLICATION, new DebugDBEncryptFactory());
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
        TextView tvIp = findViewById(R.id.tv_ip);
        if (DebugDB.isServerRunning()) {
            tvIp.setText("" + DebugDB.getAddressLog().replace("Open ", "").replace("in your browser", ""));
        } else {
            tvIp.setText("servse is not start");
        }
    }

    @NetWork(NetType.WIFI)
    public void listenWifi(NetType type) {
        if (type == NetType.NONE) {
            if (DebugDB.isServerRunning()) {
                DebugDB.shutDown();
            }
            TextView tvIp = findViewById(R.id.tv_ip);
            tvIp.setText("servse is not start");
        } else {
            initView();
        }
    }
}
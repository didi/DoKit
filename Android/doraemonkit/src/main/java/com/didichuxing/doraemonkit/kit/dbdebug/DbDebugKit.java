package com.didichuxing.doraemonkit.kit.dbdebug;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.TextView;

import com.amitshekhar.DebugDB;
import com.amitshekhar.debug.encrypt.sqlite.DebugDBEncryptFactory;
import com.amitshekhar.debug.sqlite.DebugDBFactory;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.ui.TranslucentActivity;
import com.didichuxing.doraemonkit.ui.UniversalActivity;
import com.didichuxing.doraemonkit.util.netstate.NetType;
import com.didichuxing.doraemonkit.util.netstate.NetWork;
import com.didichuxing.doraemonkit.util.netstate.NetworkManager;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-24-17:05
 * 描    述：数据库远程访问入口
 * 修订历史：
 * ================================================
 */
public class DbDebugKit implements IKit {
    @Override
    public int getCategory() {
        return Category.TOOLS;
    }

    @Override
    public int getName() {
        return R.string.dk_tools_dbdebug;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_db_view;
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent(context, UniversalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_DB_DEBUG);
        context.startActivity(intent);

    }

    @Override
    public void onAppInit(Context context) {
        NetworkManager.init(context);
        NetworkManager.get().register(this);
        DebugDB.initialize(DoraemonKit.APPLICATION, new DebugDBFactory());
        DebugDB.initialize(DoraemonKit.APPLICATION, new DebugDBEncryptFactory());
    }

    @NetWork(NetType.WIFI)
    public void listenWifi(NetType type) {
        if (type == NetType.NONE) {
            DebugDB.shutDown();
        } else {
            if (!DebugDB.isServerRunning()) {
                DebugDB.initialize(DoraemonKit.APPLICATION, new DebugDBFactory());
                DebugDB.initialize(DoraemonKit.APPLICATION, new DebugDBEncryptFactory());
            }
        }
    }
}

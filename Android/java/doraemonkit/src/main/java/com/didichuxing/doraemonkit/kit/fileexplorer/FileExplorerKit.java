package com.didichuxing.doraemonkit.kit.fileexplorer;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.google.auto.service.AutoService;

/**
 * Created by zhangweida on 2018/6/26.
 */
@AutoService(AbstractKit.class)
public class FileExplorerKit extends AbstractKit {


    @Override
    public int getName() {
        return R.string.dk_kit_file_explorer;
    }

    @Override
    public int getIcon() {
        return R.mipmap.dk_file_explorer;
    }

    @Override
    public void onClick(Context context) {
        startUniversalActivity(FileExplorerFragment.class, context, null,true);
    }

    @Override
    public void onAppInit(Context context) {

    }

    @Override
    public boolean isInnerKit() {
        return true;
    }

    @Override
    public String innerKitId() {
        return "dokit_sdk_comm_ck_sandbox";
    }
}

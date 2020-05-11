package com.didichuxing.doraemonkit.kit.fileexplorer;

import android.content.Context;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;

/**
 * Created by zhangweida on 2018/6/26.
 */
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
        startUniversalActivity(context, FragmentIndex.FRAGMENT_FILE_EXPLORER);
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

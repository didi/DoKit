package com.didichuxing.doraemonkit.plugin.transform;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.AppExtension;
import com.didichuxing.doraemonkit.plugin.DokitExtension;
import com.didichuxing.doraemonkit.plugin.MethodStackNodeUtil;
import com.didichuxing.doraemonkit.plugin.weaver.DokitMethodStackWeaver;
import com.quinn.hunter.transform.HunterTransform;
import com.quinn.hunter.transform.RunVariant;

import org.gradle.api.Project;

import java.io.IOException;
import java.util.Collection;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-18-11:38
 * 描    述：Dokit 函数调用栈 字节码转换器
 * 修订历史：
 * ================================================
 */
public class DokitMethodStack2Transform extends HunterTransform {

    private DokitExtension dokitExtension;
    private String extensionName = "dokitExt";
    private AppExtension appExtension;

    public DokitMethodStack2Transform(Project project) {
        super(project);
        try {
            this.appExtension = (AppExtension) project.getProperties().get("android");
            //创建自动的代码
            this.dokitExtension = (DokitExtension) project.getExtensions().getByName(extensionName);
            this.bytecodeWeaver = new DokitMethodStackWeaver(appExtension, MethodStackNodeUtil.LEVEL_2);
            this.bytecodeWeaver.setExtension(dokitExtension);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        try {
            super.transform(context, inputs, referencedInputs, outputProvider, isIncremental);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected RunVariant getRunVariant() {
        return dokitExtension.runVariant;
    }

    @Override
    protected boolean inDuplcatedClassSafeMode() {
        if (dokitExtension != null) {
            return dokitExtension.duplcatedClassSafeMode;
        }

        return false;
    }
}

package com.didichuxing.doraemonkit.plugin.transform;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.AppExtension;
import com.didichuxing.doraemonkit.plugin.DokitExtension;
import com.didichuxing.doraemonkit.plugin.weaver.DokitCommWeaver;
import com.didichuxing.doraemonkit.plugin.weaver.DokitSlowMethodWeaver;
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
 * 描    述：Dokit 慢函数 字节码插装
 * 修订历史：
 * ================================================
 */
public class DokitSlowMethodTransform extends HunterTransform {

    private DokitExtension dokitExtension;
    private String extensionName = "dokitExt";
    private AppExtension appExtension;

    public DokitSlowMethodTransform(Project project) {
        super(project);
        this.appExtension = (AppExtension) project.getProperties().get("android");
        //创建自动的代码
        this.bytecodeWeaver = new DokitSlowMethodWeaver(appExtension);
        this.dokitExtension = (DokitExtension) project.getExtensions().getByName(extensionName);
        this.bytecodeWeaver.setExtension(dokitExtension);
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental);
    }

    @Override
    protected RunVariant getRunVariant() {
        return dokitExtension.runVariant;
    }

    @Override
    protected boolean inDuplcatedClassSafeMode() {
        return dokitExtension.duplcatedClassSafeMode;
    }
}

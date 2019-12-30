package com.didichuxing.doraemonkit.plugin;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformOutputProvider;
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
 * 描    述：Dokit 字节码转换器
 * 修订历史：
 * ================================================
 */
public class DokitTransform extends HunterTransform {

    private Project project;
    private DokitExtension dokitExtension;
    private String extensionName = "dokitExt";

    DokitTransform(Project project) {
        super(project);
        this.project = project;
        //创建指定扩展
        project.getExtensions().create(extensionName, DokitExtension.class);
        //创建自动的代码
        this.bytecodeWeaver = new DokitWeaver();
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        dokitExtension = (DokitExtension) project.getExtensions().getByName(extensionName);
        this.bytecodeWeaver.setExtension(dokitExtension);
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

package com.didichuxing.doraemonkit.plugin.amap;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformOutputProvider;
import com.didichuxing.doraemonkit.plugin.amap.bytecode.AmapWeaver;
import com.quinn.hunter.transform.HunterTransform;
import com.quinn.hunter.transform.RunVariant;

import org.gradle.api.Project;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by jint on 13/12/2019.
 */
public final class AmapTransform extends HunterTransform {

    private Project project;
    private AmapExtension mAMapExtension;
    private String extensionName = "amapExt";

    public AmapTransform(Project project) {
        super(project);
        this.project = project;
        //创建指定扩展
        project.getExtensions().create(extensionName, AmapExtension.class);
        //创建自动的代码
        this.bytecodeWeaver = new AmapWeaver();
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        mAMapExtension = (AmapExtension) project.getExtensions().getByName(extensionName);
        this.bytecodeWeaver.setExtension(mAMapExtension);
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental);
    }

    @Override
    protected RunVariant getRunVariant() {
        return mAMapExtension.runVariant;
    }

    @Override
    protected boolean inDuplcatedClassSafeMode() {
        return mAMapExtension.duplcatedClassSafeMode;
    }
}

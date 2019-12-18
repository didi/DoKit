package com.didichuxing.doraemonkit.plugin.okhttp;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformOutputProvider;
import com.didichuxing.doraemonkit.plugin.okhttp.bytecode.OkHttpWeaver;
import com.quinn.hunter.transform.HunterTransform;
import com.quinn.hunter.transform.RunVariant;

import org.gradle.api.Project;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Quinn on 09/09/2018.
 */
public final class OkHttpTransform extends HunterTransform {

    private Project project;
    private OkHttpExtension okHttpHunterExtension;
    private String extensionName = "okHttpExt";

    public OkHttpTransform(Project project) {
        super(project);
        this.project = project;
        //创建指定扩展
        project.getExtensions().create(extensionName, OkHttpExtension.class);
        //创建自动的代码
        this.bytecodeWeaver = new OkHttpWeaver();
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        okHttpHunterExtension = (OkHttpExtension) project.getExtensions().getByName(extensionName);
        this.bytecodeWeaver.setExtension(okHttpHunterExtension);
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental);
    }

    @Override
    protected RunVariant getRunVariant() {
        return okHttpHunterExtension.runVariant;
    }

    @Override
    protected boolean inDuplcatedClassSafeMode() {
        return okHttpHunterExtension.duplcatedClassSafeMode;
    }
}

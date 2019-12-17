package com.didichuxing.doraemonkit.plugin.debug_flag;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformOutputProvider;
import com.didichuxing.doraemonkit.plugin.debug_flag.bytecode.FlagWeaver;
import com.quinn.hunter.transform.HunterTransform;
import com.quinn.hunter.transform.RunVariant;

import org.gradle.api.Project;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by jint on 13/12/2019.
 */
public final class FlagTransform extends HunterTransform {

    private Project project;
    private FlagExtension flagExtension;
    private String extensionName = "debugExt";

    public FlagTransform(Project project) {
        super(project);
        this.project = project;
        //创建指定扩展
        project.getExtensions().create(extensionName, FlagExtension.class);
        //创建自动的代码
        this.bytecodeWeaver = new FlagWeaver();
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        flagExtension = (FlagExtension) project.getExtensions().getByName(extensionName);
        this.bytecodeWeaver.setExtension(flagExtension);
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental);
    }

    @Override
    protected RunVariant getRunVariant() {
        return flagExtension.runVariant;
    }

    @Override
    protected boolean inDuplcatedClassSafeMode() {
        return flagExtension.duplcatedClassSafeMode;
    }
}

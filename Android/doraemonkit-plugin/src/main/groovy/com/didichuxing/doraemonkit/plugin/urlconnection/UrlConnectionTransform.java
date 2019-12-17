package com.didichuxing.doraemonkit.plugin.urlconnection;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformOutputProvider;
import com.didichuxing.doraemonkit.plugin.urlconnection.bytecode.UrlConnectionWeaver;
import com.quinn.hunter.transform.HunterTransform;
import com.quinn.hunter.transform.RunVariant;

import org.gradle.api.Project;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by jint on 13/12/2019.
 */
public final class UrlConnectionTransform extends HunterTransform {

    private Project project;
    private UrlConnectionExtension urlConnectionExtension;
    private String extensionName = "urlConnectionExt";

    public UrlConnectionTransform(Project project) {
        super(project);
        this.project = project;
        //创建指定扩展
        project.getExtensions().create(extensionName, UrlConnectionExtension.class);
        //创建自动的代码
        this.bytecodeWeaver = new UrlConnectionWeaver();
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        urlConnectionExtension = (UrlConnectionExtension) project.getExtensions().getByName(extensionName);
        this.bytecodeWeaver.setExtension(urlConnectionExtension);
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental);
    }

    @Override
    protected RunVariant getRunVariant() {
        return urlConnectionExtension.runVariant;
    }

    @Override
    protected boolean inDuplcatedClassSafeMode() {
        return urlConnectionExtension.duplcatedClassSafeMode;
    }
}

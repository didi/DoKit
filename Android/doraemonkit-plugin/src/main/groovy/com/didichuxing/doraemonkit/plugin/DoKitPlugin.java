package com.didichuxing.doraemonkit.plugin;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Collections;
import java.util.List;

/**
 * 调试准备
 * 调试前执行./gradlew clean -Dorg.gradle.daemon=false -Dorg.gradle.debug=true命令
 * ，然后按debug按钮，接着执行task debug便会生效
 */
public final class DoKitPlugin implements Plugin<Project> {

    @SuppressWarnings("NullableProblems")
    @Override
    public void apply(Project project) {
        AppExtension appExtension = (AppExtension) project.getProperties().get("android");
        List<String> taskNames = project.getGradle().getStartParameter().getTaskNames();
        //如果task包含release 则不进行字节码替换
        for (String taskName : taskNames) {
            if (taskName.contains("Release")) {
                return;
            }
        }
        appExtension.registerTransform(new DokitTransform(project), Collections.EMPTY_LIST);
    }

}

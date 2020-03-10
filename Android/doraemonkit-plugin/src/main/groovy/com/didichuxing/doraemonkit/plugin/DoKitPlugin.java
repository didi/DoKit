package com.didichuxing.doraemonkit.plugin;

import com.android.build.gradle.AppExtension;
import com.didichuxing.doraemonkit.plugin.transform.DokitCommTransform;
import com.didichuxing.doraemonkit.plugin.transform.DokitSlowMethodTransform;
import com.didichuxing.doraemonkit.plugin.transform.DokitUrlConnectionTransform;

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

        //创建指定扩展
        project.getExtensions().create("dokitExt", DokitExtension.class);

        List<String> taskNames = project.getGradle().getStartParameter().getTaskNames();

        //如果task包含release 则不进行字节码替换
        for (String taskName : taskNames) {
            if (taskName.contains("Release")) {
                return;
            }
        }


        //普通的插装
        appExtension.registerTransform(new DokitCommTransform(project), Collections.EMPTY_LIST);
        //urlConnection代理到OkHttp
        appExtension.registerTransform(new DokitUrlConnectionTransform(project), Collections.EMPTY_LIST);
        //慢函数
        appExtension.registerTransform(new DokitSlowMethodTransform(project), Collections.EMPTY_LIST);

    }

}

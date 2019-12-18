package com.didichuxing.doraemonkit.plugin;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.internal.dsl.BuildType;
import com.didichuxing.doraemonkit.plugin.amap.AmapTransform;
import com.didichuxing.doraemonkit.plugin.debug_flag.FlagTransform;
import com.didichuxing.doraemonkit.plugin.okhttp.OkHttpTransform;
import com.didichuxing.doraemonkit.plugin.platformhttp.PlatformHttpTransform;
import com.didichuxing.doraemonkit.plugin.urlconnection.UrlConnectionTransform;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Collections;
import java.util.List;

/**
 * 调试准备
 * 调试前执行./gradlew clean -Dorg.gradle.daemon=false -Dorg.gradle.debug=true命令
 * ，然后按debug按钮，接着执行task debug便会生效
 */
public final class DoraemonKitPlugin implements Plugin<Project> {

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
        appExtension.registerTransform(new FlagTransform(project), Collections.EMPTY_LIST);
        appExtension.registerTransform(new OkHttpTransform(project), Collections.EMPTY_LIST);
        appExtension.registerTransform(new PlatformHttpTransform(project), Collections.EMPTY_LIST);
        appExtension.registerTransform(new UrlConnectionTransform(project), Collections.EMPTY_LIST);
        appExtension.registerTransform(new AmapTransform(project), Collections.EMPTY_LIST);
    }

}

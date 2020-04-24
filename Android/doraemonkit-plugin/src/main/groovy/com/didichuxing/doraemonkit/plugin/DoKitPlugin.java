package com.didichuxing.doraemonkit.plugin;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.api.ApplicationVariant;
import com.android.build.gradle.api.BaseVariantOutput;
import com.android.build.gradle.tasks.ManifestProcessorTask;
import com.didichuxing.doraemonkit.plugin.transform.DokitBigImageTransform;
import com.didichuxing.doraemonkit.plugin.transform.DokitCommTransform;
import com.didichuxing.doraemonkit.plugin.transform.DokitMethodStack0Transform;
import com.didichuxing.doraemonkit.plugin.transform.DokitMethodStack1Transform;
import com.didichuxing.doraemonkit.plugin.transform.DokitMethodStack2Transform;
import com.didichuxing.doraemonkit.plugin.transform.DokitMethodStack3Transform;
import com.didichuxing.doraemonkit.plugin.transform.DokitMethodStack4Transform;
import com.didichuxing.doraemonkit.plugin.transform.DokitSlowMethodTransform;
import com.didichuxing.doraemonkit.plugin.transform.DokitUrlConnectionTransform;

import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.gradle.api.Action;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;


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

        project.afterEvaluate(new Action<Project>() {
            //项目评估之后回调
            @Override
            public void execute(Project partProject) {
                try {
                    DokitExtension dokitExtension = partProject.getExtensions().getByType(DokitExtension.class);
                    DokitExtUtil.getInstance().init(dokitExtension, appExtension);
                    System.out.println("====afterEvaluate====");
//                    MethodStackNodeUtil.firstMethodStackNodes.clear();
//                    MethodStackNodeUtil.secondMethodStackNodes.clear();
//                    MethodStackNodeUtil.thirdMethodStackNodes.clear();
//                    MethodStackNodeUtil.fourthlyMethodStackNodes.clear();
//                    MethodStackNodeUtil.fifthMethodStackNodes.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        //普通的插装
        appExtension.registerTransform(new DokitCommTransform(project), Collections.EMPTY_LIST);
        //urlConnection代理到OkHttp
        appExtension.registerTransform(new DokitUrlConnectionTransform(project), Collections.EMPTY_LIST);
        //大图检测
        appExtension.registerTransform(new DokitBigImageTransform(project), Collections.EMPTY_LIST);
        //Application启动函数调用栈查找 相同名字的transform 无法重复添加
        appExtension.registerTransform(new DokitMethodStack0Transform(project), Collections.EMPTY_LIST);
        appExtension.registerTransform(new DokitMethodStack1Transform(project), Collections.EMPTY_LIST);
        appExtension.registerTransform(new DokitMethodStack2Transform(project), Collections.EMPTY_LIST);
        appExtension.registerTransform(new DokitMethodStack3Transform(project), Collections.EMPTY_LIST);
        appExtension.registerTransform(new DokitMethodStack4Transform(project), Collections.EMPTY_LIST);
        //慢函数
        appExtension.registerTransform(new DokitSlowMethodTransform(project), Collections.EMPTY_LIST);


    }

}

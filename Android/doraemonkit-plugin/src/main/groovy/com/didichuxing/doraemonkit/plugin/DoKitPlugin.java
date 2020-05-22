package com.didichuxing.doraemonkit.plugin;

import com.android.build.gradle.AppExtension;
import com.didichuxing.doraemonkit.plugin.extension.DoKitExt;
import com.didichuxing.doraemonkit.plugin.classtransformer.DokitBigImageTransform;
import com.didichuxing.doraemonkit.plugin.classtransformer.DokitCommTransform;
import com.didichuxing.doraemonkit.plugin.classtransformer.DokitMethodStack0Transform;
import com.didichuxing.doraemonkit.plugin.classtransformer.DokitMethodStack1Transform;
import com.didichuxing.doraemonkit.plugin.classtransformer.DokitMethodStack2Transform;
import com.didichuxing.doraemonkit.plugin.classtransformer.DokitMethodStack3Transform;
import com.didichuxing.doraemonkit.plugin.classtransformer.DokitMethodStack4Transform;
import com.didichuxing.doraemonkit.plugin.classtransformer.DokitSlowMethodTransform;
import com.didichuxing.doraemonkit.plugin.classtransformer.DokitUrlConnectionTransform;
import com.didiglobal.booster.gradle.BaseVariantKt;
import com.didiglobal.booster.gradle.VariantScopeKt;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * 调试准备
 * 调试前执行./gradlew clean -Dorg.gradle.daemon=false -Dorg.gradle.debug=true命令
 * ，然后按debug按钮，接着执行task debug便会生效
 */
public final class DoKitPlugin implements Plugin<Project> {
    private boolean isDebug = true;

    @SuppressWarnings("NullableProblems")
    @Override
    public void apply(Project project) {
        AppExtension appExtension = (AppExtension) project.getProperties().get("android");

        //创建指定扩展 并将project 传入构造函数
        project.getExtensions().create("dokitExt", DoKitExt.class);

        List<String> taskNames = project.getGradle().getStartParameter().getTaskNames();

        //如果task包含release 则不进行字节码替换
        for (String taskName : taskNames) {
            if (taskName.contains("Release")) {
                isDebug = false;
                return;
            }
        }

        isDebug = true;

        //解析注册表文件
        appExtension.getApplicationVariants().all(applicationVariant -> {
            if (applicationVariant.getName().contains("debug")) {
                VariantScopeKt.getMergedManifests(BaseVariantKt.getScope(applicationVariant))
                        .forEach(file -> {
                            try {
                                String manifestPath = file.getPath() + "/AndroidManifest.xml";
                                //System.out.println("Dokit==manifestPath=>" + manifestPath);
                                File manifest = new File(manifestPath);
                                if (manifest.exists()) {
                                    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                                    CommHandler handler = new CommHandler();
                                    parser.parse(manifest, handler);
                                    DoKitExtUtil.getInstance().setApplications(handler.getApplication());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
            }

        });

        //项目评估之后回调
        project.afterEvaluate(partProject -> {
            try {
                if (isDebug) {
                    DoKitExt dokitExtension = partProject.getExtensions().getByType(DoKitExt.class);
                    //System.out.println("DokitPluginExt==>" + dokitExtension.toString());
                    DoKitExtUtil.getInstance().init(dokitExtension, appExtension);
                }


            } catch (Exception e) {
                e.printStackTrace();
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

package com.didichuxing.doraemonkit.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.didichuxing.doraemonkit.plugin.extension.DoKitExt
import com.didichuxing.doraemonkit.plugin.stack_method.MethodStackNodeUtil
import com.didichuxing.doraemonkit.plugin.transform.*
import com.didiglobal.booster.annotations.Priority
import com.didiglobal.booster.gradle.getAndroid
import com.didiglobal.booster.gradle.getProperty
import com.didiglobal.booster.task.spi.VariantProcessor
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/14-10:01
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        //创建指定扩展 并将project 传入构造函数

        //创建指定扩展 并将project 传入构造函数
        project.extensions.create("dokitExt", DoKitExt::class.java)

//        val debug = project.gradle.startParameter.taskNames.any {
//            it.contains("debug") || it.contains("Debug")
//        }
//        if (!debug) {
//            return
//        }
        project.gradle.addListener(DoKitTransformTaskExecutionListener(project))

        //println("project.plugins===>${project.plugins}")
        /**
         * when 也可以用来取代 if-else if链。
         * 如果不提供参数，所有的分支条件都是简单的布尔表达式，而当一个分支的条件为真时则执行该分支：
         */

        /**
         * 作用域函数:let、run、with、apply 以及 also
         * 它们的唯一目的是在对象的上下文中执行代码块
         * 由于作用域函数本质上都非常相似，因此了解它们之间的区别很重要。每个作用域函数之间有两个主要区别：
         * 引用上下文对象的方式:
         * 作为 lambda 表达式的接收者（this）或者作为 lambda 表达式的参数（it）
         * run、with 以及 apply 通过关键字 this 引用上下文对象
         * let 及 also 将上下文对象作为 lambda 表达式参数
         *
         * 返回值:
         * apply 及 also 返回上下文对象。
         * let、run 及 with 返回 lambda 表达式结果.
         */
        /**
         * 函数	对象引用	   返回值	    是否是扩展函数
         * let	 it	     Lambda 表达式结果	是
         * run	 this	 Lambda 表达式结果	是
         * run	  -	     Lambda 表达式结果	不是：调用无需上下文对象
         * with	 this	 Lambda 表达式结果	不是：把上下文对象当做参数
         * apply this	 上下文对象	        是
         * also	 it	     上下文对象	        是
         */

        /**
         *对一个非空（non-null）对象执行 lambda 表达式：let
         *将表达式作为变量引入为局部作用域中：let
         *对象配置：apply
         *对象配置并且计算结果：run
         *在需要表达式的地方运行语句：非扩展的 run
         *附加效果：also
         *一个对象的一组函数调用：with
         */
        when {
            project.plugins.hasPlugin("com.android.application") || project.plugins.hasPlugin("com.android.dynamic-feature") -> {
                project.getAndroid<AppExtension>().let { androidExt ->
                    val slowMethodSwitch = project.getProperty("DoKit_SLOW_METHOD_SWITCH", false)
                    val methodStackLevel = project.getProperty("DoKit_MethodStack_Level", 5)
                    DoKitExtUtil.mSlowMethodSwitch = slowMethodSwitch
                    DoKitExtUtil.mStackMethodLevel = methodStackLevel

                    MethodStackNodeUtil.METHOD_STACK_KEYS.clear()
                    //注册transform
                    androidExt.registerTransform(DoKitCommTransform(project))
                    MethodStackNodeUtil.METHOD_STACK_KEYS.add(0, mutableSetOf<String>())
                    if (slowMethodSwitch) {
                        val methodStackRange = 1 until methodStackLevel
                        if (methodStackLevel > 1) {
                            for (index in methodStackRange) {
                                MethodStackNodeUtil.METHOD_STACK_KEYS.add(index, mutableSetOf<String>())
                                androidExt.registerTransform(DoKitDependTransform(project, index))
                            }
                        }
                    }


                    //项目评估完毕回调
                    project.afterEvaluate {
                        this.variantProcessors.let { processors ->
                            androidExt.applicationVariants.forEach { variant ->
                                processors.forEach { processor ->
                                    processor.process(variant)
                                }
                            }
                        }
                    }
                }
            }

            project.plugins.hasPlugin("com.android.library") -> {
                project.getAndroid<LibraryExtension>().let { libraryExt ->
                    libraryExt.registerTransform(DoKitCommTransform(project))
                    project.afterEvaluate {
                        this.variantProcessors.let { processors ->
                            libraryExt.libraryVariants.forEach { variant ->
                                processors.forEach { processor ->
                                    processor.process(variant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private val variantProcessors: Collection<VariantProcessor>
        get() = ServiceLoader.load(VariantProcessor::class.java, javaClass.classLoader).sortedBy {
            it.javaClass.getAnnotation(Priority::class.java)?.value ?: 0
        }

}
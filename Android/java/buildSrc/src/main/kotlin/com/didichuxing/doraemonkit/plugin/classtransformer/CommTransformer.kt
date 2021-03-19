package com.didichuxing.doraemonkit.plugin.classtransformer

import com.didichuxing.doraemonkit.plugin.*
import com.didichuxing.doraemonkit.plugin.extension.SlowMethodExt
import com.didiglobal.booster.annotations.Priority
import com.didiglobal.booster.kotlinx.asIterable
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.didiglobal.booster.transform.asm.className
import com.google.auto.service.AutoService
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/14-18:07
 * 描    述：wiki:https://juejin.im/post/5e8d87c4f265da47ad218e6b
 * 修订历史：
 * ================================================
 */
@Priority(0)
@AutoService(ClassTransformer::class)
class CommTransformer : ClassTransformer {
    private val SHADOW_URL =
        "com/didichuxing/doraemonkit/aop/urlconnection/HttpUrlConnectionProxyUtil"
    private val DESC = "(Ljava/net/URLConnection;)Ljava/net/URLConnection;"


    /**
     * 转化
     */
    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        if (context.isRelease()) {
            return klass
        }

        if (!DoKitExtUtil.dokitPluginSwitchOpen()) {
            return klass
        }

        val className = klass.className
        val superName = klass.formatSuperName

        if (className.contains("didihttp")) {
            "${context.projectDir.lastPath()}==className===>$className".println()
        }

        //查找DoraemonKitReal&pluginConfig方法并插入指定字节码
        if (className == "com.didichuxing.doraemonkit.DoraemonKitReal") {
            //插件配置
            klass.methods?.find {
                it.name == "pluginConfig"
            }.let { methodNode ->
                "${context.projectDir.lastPath()}->insert map to the DoraemonKitReal pluginConfig succeed".println()
                methodNode?.instructions?.insert(createPluginConfigInsnList())
            }
            //三方库信息注入
            klass.methods?.find {
                it.name == "initThirdLibraryInfo"
            }.let { methodNode ->
                "${context.projectDir.lastPath()}->insert map to the DoraemonKitReal initThirdLibraryInfo succeed".println()
                methodNode?.instructions?.insert(createThirdLibInfoInsnList())
            }
        }

        //gps字节码操作
        if (DoKitExtUtil.commExt.gpsSwitch) {

            //插入高德地图定位相关字节码
            if (className == "com.amap.api.location.AMapLocationClient") {
                //设置监听器
                klass.methods?.find {
                    it.name == "setLocationListener"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook amap map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(createAmapLocationInsnList())
                }

                //反注册监听器
                klass.methods?.find {
                    it.name == "unRegisterLocationListener"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook amap map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        methodNode.instructions.insertBefore(
                            it,
                            createAmapLocationUnRegisterInsnList()
                        )
                    }
                }

                //代理getLastKnownLocation
                klass.methods?.find {
                    it.name == "getLastKnownLocation"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook AMapLocationClient getLastKnownLocation  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
//                    methodNode?.instructions?.clear()
//                    for (instruction in methodNode!!.instructions) {
////                        methodNode.instructions.remove(instruction)
//                        println("getLastKnownLocation===>${instruction.opcode}")
//                    }
                    methodNode?.instructions?.insert(createAMapClientLastKnownLocation())
                }

            }

            //插入高德地图导航相关字节码
            if (className == "com.amap.api.navi.AMapNavi") {
                //设置监听器
                klass.methods?.find {
                    it.name == "addAMapNaviListener"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook amap map navi  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(createAmapNaviInsnList())
                }

                //反注册监听器
                klass.methods?.find {
                    it.name == "removeAMapNaviListener"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook amap map navi  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        methodNode.instructions.insertBefore(
                            it,
                            createAmapNaviUnRegisterInsnList()
                        )
                    }
                }

            }


            //插入腾讯地图相关字节码
            if (className == "com.tencent.map.geolocation.TencentLocationManager") {
                //持续定位和单次定位
                klass.methods?.filter {
                    it.name == "requestSingleFreshLocation" || it.name == "requestLocationUpdates"
                }?.forEach { methodNode ->
                    "${context.projectDir.lastPath()}->hook tencent map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(createTencentLocationInsnList())
                }


                //反注册监听器
                klass.methods?.find {
                    it.name == "removeUpdates"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook tencent map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        methodNode.instructions.insertBefore(
                            it,
                            createTencentLocationUnRegisterInsnList()
                        )
                    }
                }
            }

            //插入百度地图相关字节码
            if (className == "com.baidu.location.LocationClient") {
                //拦截注册监听器
                klass.methods?.filter {
                    it.name == "registerLocationListener"
                            && (it.desc == "(Lcom/baidu/location/BDLocationListener;)V" || it.desc == "(Lcom/baidu/location/BDAbstractLocationListener;)V")
                }?.forEach { methodNode ->
                    "${context.projectDir.lastPath()}->hook baidu map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    if (methodNode.desc == "(Lcom/baidu/location/BDLocationListener;)V") {
                        methodNode?.instructions?.insert(createBDLocationListenerInsnList())
                    } else if (methodNode.desc == "(Lcom/baidu/location/BDAbstractLocationListener;)V") {
                        methodNode?.instructions?.insert(createBDLocationAbsListenerInsnList())
                    }
                }


                //反注册监听器
                klass.methods?.find {
                    it.name == "unRegisterLocationListener" && it.desc == "(Lcom/baidu/location/BDLocationListener;)V"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook baidu map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        methodNode.instructions.insertBefore(
                            it,
                            createBDLocationUnRegisterInsnList()
                        )
                    }
                }


                //反注册监听器
                klass.methods?.find {
                    it.name == "unRegisterLocationListener" && it.desc == "(Lcom/baidu/location/BDAbstractLocationListener;)V"
                }.let { methodNode ->
                    "${context.projectDir.lastPath()}->hook baidu map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        methodNode.instructions.insertBefore(
                            it,
                            createBDAbsLocationUnRegisterInsnList()
                        )
                    }
                }
            }


        }


        //网络 OkHttp&didi platform aop
        if (DoKitExtUtil.commExt.networkSwitch) {
            //okhttp
            if (className == "okhttp3.OkHttpClient\$Builder") {
                //空参数的构造方法
                klass.methods?.find {
                    it.name == "<init>" && it.desc == "()V"
                }.let { zeroConsMethodNode ->
                    "${context.projectDir.lastPath()}->hook OkHttp  succeed: ${className}_${zeroConsMethodNode?.name}_${zeroConsMethodNode?.desc}".println()
                    zeroConsMethodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        zeroConsMethodNode.instructions.insertBefore(
                            it,
                            createOkHttpZeroConsInsnList()
                        )
                    }
                }


                //一个参数的构造方法
                klass.methods?.find {
                    it.name == "<init>" && it.desc == "(Lokhttp3/OkHttpClient;)V"
                }.let { oneConsMethodNode ->
                    "${context.projectDir.lastPath()}->hook OkHttp  succeed: ${className}_${oneConsMethodNode?.name}_${oneConsMethodNode?.desc}".println()
                    oneConsMethodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        oneConsMethodNode.instructions.insertBefore(
                            it,
                            createOkHttpOneConsInsnList()
                        )
                    }
                }

            }

            //didi platform
            if (className == "didihttp.DidiHttpClient\$Builder") {
                "find DidiHttpClient succeed: ${className}".println()
                //空参数的构造方法
                klass.methods?.find {
                    it.name == "<init>" && it.desc == "()V"
                }.let { zeroConsMethodNode ->
                    "${context.projectDir.lastPath()}->hook didi http  succeed: ${className}_${zeroConsMethodNode?.name}_${zeroConsMethodNode?.desc}".println()
                    zeroConsMethodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        zeroConsMethodNode.instructions.insertBefore(
                            it,
                            createDidiHttpZeroConsInsnList()
                        )
                    }
                }


                //一个参数的构造方法
                klass.methods?.find {
                    it.name == "<init>" && it.desc == "(Ldidihttp/DidiHttpClient;)V"
                }.let { oneConsMethodNode ->
                    "${context.projectDir.lastPath()}->hook didi http  succeed: ${className}_${oneConsMethodNode?.name}_${oneConsMethodNode?.desc}".println()
                    oneConsMethodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        oneConsMethodNode.instructions.insertBefore(
                            it,
                            createDidiHttpOneConsInsnList()
                        )
                    }
                }
            }

            //webView 字节码操作
            if (DoKitExtUtil.commExt.webViewSwitch) {
                //普通的webview
                klass.methods.forEach { method ->
                    method.instructions?.iterator()?.asIterable()
                        ?.filterIsInstance(MethodInsnNode::class.java)?.filter {
                            it.opcode == INVOKEVIRTUAL &&
                                    it.name == "loadUrl" &&
                                    it.desc == "(Ljava/lang/String;)V" &&
                                    isWebViewOwnerNameMatched(it.owner)
                        }?.forEach {
                            "${context.projectDir.lastPath()}->hook WebView#loadurl method  succeed in :  ${className}_${method.name}_${method.desc} | ${it.owner}".println()
                            method.instructions.insertBefore(
                                it,
                                createWebViewInsnList()
                            )
                        }
                }
            }

            // url connection
            klass.methods.forEach { method ->
                method.instructions?.iterator()?.asIterable()
                    ?.filterIsInstance(MethodInsnNode::class.java)?.filter {
                        it.opcode == INVOKEVIRTUAL &&
                                it.owner == "java/net/URL" &&
                                it.name == "openConnection" &&
                                it.desc == "()Ljava/net/URLConnection;"
                    }?.forEach {
                        "${context.projectDir.lastPath()}->hook URL#openConnection method  succeed in : ${className}_${method.name}_${method.desc}".println()
                        method.instructions.insert(
                            it,
                            MethodInsnNode(INVOKESTATIC, SHADOW_URL, "proxy", DESC, false)
                        )
                    }
            }

        }

        //hook Androidx的ComponentActivity
        if (className != "com.didichuxing.doraemonkit.aop.mc.DoKitProxyActivity" && superName == "android.app.Activity") {
            createComponentActivitySuperActivityImpl(klass)
        }


        //hook androidx的AppCompatDelegateImpl
//        if (className == "androidx.appcompat.app.AppCompatDelegate") {
//            klass.methods?.filter {
//                it.name == "create"
//                        && (it.desc == "(Landroid/app/Activity;Landroidx/appcompat/app/AppCompatCallback;)Landroidx/appcompat/app/AppCompatDelegate;"
//                        || it.desc == "(Landroid/app/Dialog;Landroidx/appcompat/app/AppCompatCallback;)Landroidx/appcompat/app/AppCompatDelegate;"
//                        || it.desc == "(Landroid/content/Context;Landroid/view/Window;Landroidx/appcompat/app/AppCompatCallback;)Landroidx/appcompat/app/AppCompatDelegate;"
//                        || it.desc == "(Landroid/content/Context;Landroid/app/Activity;Landroidx/appcompat/app/AppCompatCallback;)Landroidx/appcompat/app/AppCompatDelegate;")
//            }?.asIterable()
//                ?.forEach { methodNode ->
//                    //操作方法
//                    val typeInsnNodes = methodNode.instructions
//                        ?.iterator()
//                        ?.asIterable()
//                        ?.filterIsInstance(TypeInsnNode::class.java)
//                    //操作New指令
//                    typeInsnNodes?.filter {
//                        it.opcode == NEW && it.desc == "androidx/appcompat/app/AppCompatDelegateImpl"
//                    }?.forEach {
//                        "${context.projectDir.lastPath()}->hook ${klass.name}-${methodNode.name}-${it.opcode}-${it.desc} succeed".println()
//                        methodNode.instructions.insertBefore(
//                            it,
//                            createNewDoKitAppCompatDelegateImplInsnList()
//                        )
//                    }
//
//                    //操作方法
//                    val methodInsnNodes = methodNode.instructions
//                        ?.iterator()
//                        ?.asIterable()
//                        ?.filterIsInstance(MethodInsnNode::class.java)
//
//                    methodInsnNodes?.filter {
//                        //"${context.projectDir.lastPath()}->hook AppCompatDelegate create matched ${it.opcode}  ${it.ownerClassName}  ${it.name}".println()
//                        it.opcode == INVOKESPECIAL
//                                && it.ownerClassName == "androidx.appcompat.app.AppCompatDelegateImpl"
//                                && it.name == "<init>"
//                    }?.forEach {
//                        //"${context.projectDir.lastPath()}->hook AppCompatDelegate create  method succeed".println()
//                        "${context.projectDir.lastPath()}->hook ${klass.name}-${methodNode.name}-${it.owner}-${it.name}-${it.desc} succeed".println()
//                        methodNode.instructions.insert(
//                            it,
//                            MethodInsnNode(
//                                INVOKESPECIAL,
//                                "androidx/appcompat/app/DoKitAppCompatDelegateImpl",
//                                "<init>",
//                                "(Landroidx/appcompat/app/AppCompatDelegate;)V",
//                                false
//                            )
//                        )
//                    }
//                }
//        }

        //hook 所有的view的事件
//        klass.methods?.forEach { methodNode ->
//            if (methodNode.name == "onClick" && methodNode.desc == "(Landroid/view/View;)V") {
//                val insnList = with(InsnList()) {
//                    add(VarInsnNode(ALOAD, 1))
//                    add(
//                        MethodInsnNode(
//                            INVOKESTATIC,
//                            "com/didichuxing/doraemonkit/aop/mc/DoKitListenerHelper",
//                            "hookViewClickListener",
//                            "(Landroid/view/View;)V",
//                            false
//                        )
//                    )
//                    this
//                }
//                methodNode.instructions.insert(insnList)
//            }
//        }


        // hook 所有的View
//        if (className != "com.didichuxing.doraemonkit.aop.mc.DoKitProxyView" && superName == "android.view.View") {
//            createViewImpl(klass)
//        }

        // hook 所有的ViewGroup
//        if (className != "com.didichuxing.doraemonkit.aop.mc.DoKitProxyViewGroup" && superName == "android.view.ViewGroup") {
//            createViewGroupImpl(klass)
//        }

//        if (className == "androidx.appcompat.app.AppCompatDelegateImpl") {
//            //插件配置
//            klass.methods?.find {
//                it.name == "onCreateView" && it.desc == "(Landroid/view/View;Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;)Landroid/view/View;"
//            }.let { method ->
//                method?.instructions?.iterator()?.asIterable()
//                    ?.filterIsInstance(MethodInsnNode::class.java)?.filter {
//                        it.opcode == INVOKEVIRTUAL &&
//                                it.owner == "androidx/appcompat/app/AppCompatDelegateImpl" &&
//                                it.name == "createView" &&
//                                it.desc == "(Landroid/view/View;Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;)Landroid/view/View;"
//                    }?.forEach {
//                        "${context.projectDir.lastPath()}->hook AppCompatDelegateImpl onCreateView method succeed".println()
//                        method.instructions.insert(
//                            it,
//                            MethodInsnNode(
//                                INVOKESTATIC,
//                                "com/didichuxing/doraemonkit/aop/mc/AppCompatDelegateImplProxy",
//                                "onCreateView",
//                                "(Landroid/view/View;)Landroid/view/View;",
//                                false
//                            )
//                        )
//                    }
//            }
//        }

        return klass
    }

    private fun isWebViewOwnerNameMatched(ownerName: String): Boolean {
        return ownerName == "android/webkit/WebView" ||
                ownerName == "com/tencent/smtt/sdk/WebView" ||
                ownerName.contentEquals("WebView") ||
                ownerName == DoKitExtUtil.WEBVIEW_CLASS_NAME
    }


    /**
     * 创建pluginConfig代码指令
     */
    private fun createPluginConfigInsnList(): InsnList {
        //val insnList = InsnList()
        return with(InsnList()) {
            //new HashMap
            add(TypeInsnNode(NEW, "java/util/HashMap"))
            add(InsnNode(DUP))
            add(MethodInsnNode(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false))
            //保存变量
            add(VarInsnNode(ASTORE, 0))
            //获取第一个变量
            //put("dokitPluginSwitch",true)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("dokitPluginSwitch"))
            add(InsnNode(if (DoKitExtUtil.dokitPluginSwitchOpen()) ICONST_1 else ICONST_0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(POP))

            //put("gpsSwitch",true)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("gpsSwitch"))
            add(InsnNode(if (DoKitExtUtil.commExt.gpsSwitch) ICONST_1 else ICONST_0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(POP))

            //put("networkSwitch",true)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("networkSwitch"))
            add(InsnNode(if (DoKitExtUtil.commExt.networkSwitch) ICONST_1 else ICONST_0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(POP))

            //put("bigImgSwitch",true)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("bigImgSwitch"))
            add(InsnNode(if (DoKitExtUtil.commExt.bigImgSwitch) ICONST_1 else ICONST_0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(POP))

            //put("methodSwitch",true)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("methodSwitch"))
            add(InsnNode(if (DoKitExtUtil.dokitSlowMethodSwitchOpen()) ICONST_1 else ICONST_0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(POP))


            //put("methodStrategy",0)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("methodStrategy"))
            add(InsnNode(if (DoKitExtUtil.SLOW_METHOD_STRATEGY == SlowMethodExt.STRATEGY_STACK) ICONST_0 else ICONST_1))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "java/lang/Integer",
                    "valueOf",
                    "(I)Ljava/lang/Integer;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(POP))

            //将HashMap注入到DokitPluginConfig中
            add(VarInsnNode(ALOAD, 0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/DokitPluginConfig",
                    "inject",
                    "(Ljava/util/Map;)V",
                    false
                )
            )

            this
        }

        //return insnList

    }


    /**
     * 创建pluginConfig代码指令
     */
    private fun createThirdLibInfoInsnList(): InsnList {
        //val insnList = InsnList()
        return with(InsnList()) {
            //new HashMap
            add(TypeInsnNode(NEW, "java/util/HashMap"))
            add(InsnNode(DUP))
            add(MethodInsnNode(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false))
            //保存变量
            add(VarInsnNode(ASTORE, 0))

            for (thirdLibInfo in DoKitExtUtil.THIRD_LIB_INFOS) {
                add(VarInsnNode(ALOAD, 0))
                add(LdcInsnNode(thirdLibInfo.variant))
                add(LdcInsnNode(thirdLibInfo.fileSize.toString()))

                add(
                    MethodInsnNode(
                        INVOKEINTERFACE,
                        "java/util/Map",
                        "put",
                        "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                        false
                    )
                )
                add(InsnNode(POP))
            }
//
//            //将HashMap注入到DokitPluginConfig中
            add(VarInsnNode(ALOAD, 0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/DokitThirdLibInfo",
                    "inject",
                    "(Ljava/util/Map;)V",
                    false
                )
            )

            this
        }

        //return insnList

    }


    /**
     * 创建Amap地图代码指令
     */
    private fun createAmapLocationInsnList(): InsnList {
        return with(InsnList()) {
            //在AMapLocationClient的setLocationListener方法之中插入自定义代理回调类
            add(TypeInsnNode(NEW, "com/didichuxing/doraemonkit/aop/map/AMapLocationListenerProxy"))
            add(InsnNode(DUP))
            //访问第一个参数
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESPECIAL,
                    "com/didichuxing/doraemonkit/aop/map/AMapLocationListenerProxy",
                    "<init>",
                    "(Lcom/amap/api/location/AMapLocationListener;)V",
                    false
                )
            )
            //对第一个参数进行重新赋值
            add(VarInsnNode(ASTORE, 1))
            this
        }

    }

    /**
     * 创建Amap地图导航代码指令
     */
    private fun createAmapNaviInsnList(): InsnList {
        return with(InsnList()) {
            //在AMapNavi的addAMapNaviListener方法之中插入自定义代理回调类
            add(TypeInsnNode(NEW, "com/didichuxing/doraemonkit/aop/map/AMapNaviListenerProxy"))
            add(InsnNode(DUP))
            //访问第一个参数
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESPECIAL,
                    "com/didichuxing/doraemonkit/aop/map/AMapNaviListenerProxy",
                    "<init>",
                    "(Lcom/amap/api/navi/AMapNaviListener;)V",
                    false
                )
            )
            //对第一个参数进行重新赋值
            add(VarInsnNode(ASTORE, 1))
            this
        }

    }

    /**
     * 创建AMapLocationClient#LastKnownLocation 字节码替换
     */
    private fun createAMapClientLastKnownLocation(): InsnList {
        return with(InsnList()) {
            add(VarInsnNode(ALOAD, 0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/map/AMapLocationClientProxy",
                    "getLastKnownLocation",
                    "(Lcom/amap/api/location/AMapLocationClient;)Lcom/amap/api/location/AMapLocation;",
                    false
                )
            )
//            add(VarInsnNode(ASTORE, 1))
//            add(VarInsnNode(ALOAD, 1))
            add(InsnNode(ARETURN))
            this
        }

    }

    /**
     * 创建Amap地图UnRegister代码指令
     */
    private fun createAmapLocationUnRegisterInsnList(): InsnList {
        return with(InsnList()) {
            //访问第一个参数
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/map/ThirdMapLocationListenerUtil",
                    "unRegisterAmapLocationListener",
                    "(Lcom/amap/api/location/AMapLocationListener;)V",
                    false
                )
            )
            this
        }

    }

    /**
     * 创建Amap地图 Navi UnRegister代码指令
     */
    private fun createAmapNaviUnRegisterInsnList(): InsnList {
        return with(InsnList()) {
            //访问第一个参数
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/map/ThirdMapLocationListenerUtil",
                    "unRegisterAmapNaviListener",
                    "(Lcom/amap/api/navi/AMapNaviListener;)V",
                    false
                )
            )
            this
        }

    }


    /**
     * 创建tencent地图代码指令
     */
    private fun createTencentLocationInsnList(): InsnList {
        return with(InsnList()) {
            //在AMapLocationClient的setLocationListener方法之中插入自定义代理回调类
            add(
                TypeInsnNode(
                    NEW,
                    "com/didichuxing/doraemonkit/aop/map/TencentLocationListenerProxy"
                )
            )
            add(InsnNode(DUP))
            //访问第一个参数
            add(VarInsnNode(ALOAD, 2))
            add(
                MethodInsnNode(
                    INVOKESPECIAL,
                    "com/didichuxing/doraemonkit/aop/map/TencentLocationListenerProxy",
                    "<init>",
                    "(Lcom/tencent/map/geolocation/TencentLocationListener;)V",
                    false
                )
            )
            //对第二个参数进行重新赋值
            add(VarInsnNode(ASTORE, 2))

            this
        }

    }


    /**
     * 创建Tencent地图UnRegister代码指令
     */
    private fun createTencentLocationUnRegisterInsnList(): InsnList {
        return with(InsnList()) {
            //访问第一个参数
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/map/ThirdMapLocationListenerUtil",
                    "unRegisterTencentLocationListener",
                    "(Lcom/tencent/map/geolocation/TencentLocationListener;)V",
                    false
                )
            )
            this
        }

    }


    /**
     * 创建百度地图代码指令
     */
    private fun createBDLocationListenerInsnList(): InsnList {
        return with(InsnList()) {
            //在LocationClient的registerLocationListener方法之中插入自定义代理回调类
            add(TypeInsnNode(NEW, "com/didichuxing/doraemonkit/aop/map/BDLocationListenerProxy"))
            add(InsnNode(DUP))
            //访问第一个参数
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESPECIAL,
                    "com/didichuxing/doraemonkit/aop/map/BDLocationListenerProxy",
                    "<init>",
                    "(Lcom/baidu/location/BDLocationListener;)V",
                    false
                )
            )
            //对第一个参数进行重新赋值
            add(VarInsnNode(ASTORE, 1))

            this
        }

    }

    /**
     * 创建百度地图代码指令
     */
    private fun createBDLocationAbsListenerInsnList(): InsnList {
        return with(InsnList()) {
            //在LocationClient的registerLocationListener方法之中插入自定义代理回调类
            add(TypeInsnNode(NEW, "com/didichuxing/doraemonkit/aop/map/BDAbsLocationListenerProxy"))
            add(InsnNode(DUP))
            //访问第一个参数
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESPECIAL,
                    "com/didichuxing/doraemonkit/aop/map/BDAbsLocationListenerProxy",
                    "<init>",
                    "(Lcom/baidu/location/BDAbstractLocationListener;)V",
                    false
                )
            )
            //对第一个参数进行重新赋值
            add(VarInsnNode(ASTORE, 1))
            this
        }
    }


    /**
     * 创建百度地图UnRegister代码指令
     */
    private fun createBDLocationUnRegisterInsnList(): InsnList {
        return with(InsnList()) {
            //访问第一个参数
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/map/ThirdMapLocationListenerUtil",
                    "unRegisterBDLocationListener",
                    "(Lcom/baidu/location/BDLocationListener;)V",
                    false
                )
            )
            this
        }

    }

    /**
     * 创建百度地图UnRegister代码指令
     */
    private fun createBDAbsLocationUnRegisterInsnList(): InsnList {
        return with(InsnList()) {
            //访问第一个参数
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/map/ThirdMapLocationListenerUtil",
                    "unRegisterBDLocationListener",
                    "(Lcom/baidu/location/BDAbstractLocationListener;)V",
                    false
                )
            )
            this
        }

    }

    /**
     * 创建百度地图代码指令
     */
    private fun createBaiduLocationInsnList(): InsnList {
        return with(InsnList()) {
            //在AMapLocationClient的setLocationListener方法之中插入自定义代理回调类
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/BDLocationUtil",
                    "proxy",
                    "(Lcom/baidu/location/BDLocation;)Lcom/baidu/location/BDLocation;",
                    false
                )
            )
            //对第一个参数进行重新赋值
            add(VarInsnNode(ASTORE, 1))
            this
        }

    }


    /**
     * 创建Okhttp Build 空参数构造函数指令
     */
    private fun createOkHttpZeroConsInsnList(): InsnList {
        return with(InsnList()) {
            //插入application 拦截器
            add(VarInsnNode(ALOAD, 0))
            add(
                FieldInsnNode(
                    GETFIELD,
                    "okhttp3/OkHttpClient\$Builder",
                    "interceptors",
                    "Ljava/util/List;"
                )
            )
            add(
                FieldInsnNode(
                    GETSTATIC,
                    "com/didichuxing/doraemonkit/aop/OkHttpHook",
                    "globalInterceptors",
                    "Ljava/util/List;"
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/List",
                    "addAll",
                    "(Ljava/util/Collection;)Z",
                    true
                )
            )
            add(InsnNode(POP))

            //插入NetworkInterceptor 拦截器
            add(VarInsnNode(ALOAD, 0))
            add(
                FieldInsnNode(
                    GETFIELD,
                    "okhttp3/OkHttpClient\$Builder",
                    "networkInterceptors",
                    "Ljava/util/List;"
                )
            )
            add(
                FieldInsnNode(
                    GETSTATIC,
                    "com/didichuxing/doraemonkit/aop/OkHttpHook",
                    "globalNetworkInterceptors",
                    "Ljava/util/List;"
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/List",
                    "addAll",
                    "(Ljava/util/Collection;)Z",
                    true
                )
            )
            add(InsnNode(POP))
            this
        }

    }


    /**
     * 创建Okhttp Build 一个参数构造函数指令
     */
    private fun createOkHttpOneConsInsnList(): InsnList {
        return with(InsnList()) {
            add(VarInsnNode(ALOAD, 0))
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/OkHttpHook",
                    "performOkhttpOneParamBuilderInit",
                    "(Ljava/lang/Object;Ljava/lang/Object;)V",
                    false
                )
            )
            this
        }

    }


    /**
     * 创建didiClient Build 空参数构造函数指令
     */
    private fun createDidiHttpZeroConsInsnList(): InsnList {
        return with(InsnList()) {
            //插入application 拦截器
            add(VarInsnNode(ALOAD, 0))
            add(
                FieldInsnNode(
                    GETFIELD,
                    "didihttp/DidiHttpClient\$Builder",
                    "interceptors",
                    "Ljava/util/List;"
                )
            )
            add(
                FieldInsnNode(
                    GETSTATIC,
                    "com/didichuxing/foundation/net/rpc/http/PlatformHttpHook",
                    "globalInterceptors",
                    "Ljava/util/List;"
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/List",
                    "addAll",
                    "(Ljava/util/Collection;)Z",
                    true
                )
            )
            add(InsnNode(POP))

            //插入NetworkInterceptor 拦截器
            add(VarInsnNode(ALOAD, 0))
            add(
                FieldInsnNode(
                    GETFIELD,
                    "didihttp/DidiHttpClient\$Builder",
                    "networkInterceptors",
                    "Ljava/util/List;"
                )
            )
            add(
                FieldInsnNode(
                    GETSTATIC,
                    "com/didichuxing/foundation/net/rpc/http/PlatformHttpHook",
                    "globalNetworkInterceptors",
                    "Ljava/util/List;"
                )
            )
            add(
                MethodInsnNode(
                    INVOKEINTERFACE,
                    "java/util/List",
                    "addAll",
                    "(Ljava/util/Collection;)Z",
                    true
                )
            )
            add(InsnNode(POP))
            this
        }

    }


    /**
     * 创建didiClient Build 一个参数构造函数指令
     */
    private fun createDidiHttpOneConsInsnList(): InsnList {
        return with(InsnList()) {
            add(VarInsnNode(ALOAD, 0))
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/foundation/net/rpc/http/PlatformHttpHook",
                    "performDidiHttpOneParamBuilderInit",
                    "(Ljava/lang/Object;Ljava/lang/Object;)V",
                    false
                )
            )
            this
        }
    }


    /**
     * 创建webView函数指令集
     * 参考:https://www.jianshu.com/p/7d623f441bed
     */
    private fun createWebViewInsnList(): InsnList {
        return with(InsnList()) {
            //复制栈顶的2个指令 指令集变为 比如 aload 2 aload0 aload 2 aload0
            add(InsnNode(DUP2))
            //抛出最上面的指令 指令集变为 aload 2 aload0 aload 2  其中 aload 2即为我们所需要的对象
            add(InsnNode(POP))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/WebViewHook",
                    "inject",
                    "(Ljava/lang/Object;)V",
                    false
                )
            )
            this
        }
    }

    /**
     * 创建new DoKitAppCompatDelegateImpl指令集
     */
    private fun createNewDoKitAppCompatDelegateImplInsnList(): InsnList {
        return with(InsnList()) {
            add(TypeInsnNode(NEW, "androidx/appcompat/app/DoKitAppCompatDelegateImpl"))
            add(InsnNode(DUP))
            this
        }
    }


    /**
     * 重置ComponentActivity的父类
     */
    private fun createComponentActivitySuperActivityImpl(klass: ClassNode) {
        /**
         * 修改继承的父类
         */
        klass.superName = "com/didichuxing/doraemonkit/aop/mc/DoKitProxyActivity"
    }


    /**
     * 重置View的父类
     */
    private fun createViewImpl(klass: ClassNode) {
        /**
         * 修改继承的父类
         */
        klass.superName = "com/didichuxing/doraemonkit/aop/mc/DoKitProxyView"
    }


    /**
     * 重置ViewGroup的父类
     */
    private fun createViewGroupImpl(klass: ClassNode) {
        /**
         * 修改继承的父类
         */
        klass.superName = "com/didichuxing/doraemonkit/aop/mc/DoKitProxyViewGroup"
    }

}
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
//@Priority(0)
//@AutoService(ClassTransformer::class)
class CommClassTransformer : AbsClassTransformer() {


    private val SHADOW_URL =
        "com/didichuxing/doraemonkit/aop/urlconnection/HttpUrlConnectionProxyUtil"
    private val DESC = "(Ljava/net/URLConnection;)Ljava/net/URLConnection;"


    /**
     * 转化
     */
    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {

        if (onCommInterceptor(context, klass)) {
            return klass
        }


        val className = klass.className
        val superName = klass.formatSuperName

//        if (className.contains("didihttp")) {
//            "${context.projectDir.lastPath()}==className===>$className".println()
//        }

        //查找DoraemonKitReal&pluginConfig方法并插入指定字节码
        if (className == "com.didichuxing.doraemonkit.DoKitReal") {
            //插件配置
            klass.methods?.find {
                it.name == "pluginConfig"
            }.let { methodNode ->
                "${context.projectDir.lastPath()}->insert map to the DoKitReal pluginConfig succeed".println()
                methodNode?.instructions?.insert(createPluginConfigInsnList())
            }
            //三方库信息注入
            klass.methods?.find {
                it.name == "initThirdLibraryInfo"
            }.let { methodNode ->
                "${context.projectDir.lastPath()}->insert map to the DoKitReal initThirdLibraryInfo succeed".println()
                methodNode?.instructions?.insert(createThirdLibInfoInsnList())
            }
        }

        //gps字节码操作
        if (DoKitExtUtil.commExt.gpsSwitch) {
            //系统 gpsStatus hook
            klass.methods.forEach { method ->
                method.instructions?.iterator()?.asIterable()
                    ?.filterIsInstance(MethodInsnNode::class.java)?.filter {
                        it.opcode == INVOKEVIRTUAL &&
                            it.owner == "android/location/LocationManager" &&
                            it.name == "getGpsStatus" &&
                            it.desc == "(Landroid/location/GpsStatus;)Landroid/location/GpsStatus;"
                    }?.forEach {
                        "${context.projectDir.lastPath()}->hook LocationManager#getGpsStatus method  succeed in : ${className}_${method.name}_${method.desc}".println()
                        method.instructions.insert(
                            it,
                            MethodInsnNode(
                                INVOKESTATIC,
                                "com/didichuxing/doraemonkit/aop/location/GpsStatusUtil",
                                "wrap",
                                "(Landroid/location/GpsStatus;)Landroid/location/GpsStatus;",
                                false
                            )
                        )
                    }
            }

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
                    methodNode?.instructions?.insert(createAMapClientLastKnownLocation())
                }

            }
            //插入高德 地图定位相关字节码
//            if (className == "com.amap.api.maps.AMap") {
//                //设置LocationSource代理
//                klass.methods?.find {
//                    it.name == "setLocationSource"
//                }.let { methodNode ->
//                    "${context.projectDir.lastPath()}->hook amap map LocationSource  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
//                    methodNode?.instructions?.insert(createAmapLocationSourceInsnList())
//                }
//            }

            //插入高德地图导航相关字节码
//            if (className == "com.amap.api.navi.AMapNavi") {
//                //设置监听器
//                klass.methods?.find {
//                    it.name == "addAMapNaviListener"
//                }.let { methodNode ->
//                    "${context.projectDir.lastPath()}->hook amap map navi  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
//                    methodNode?.instructions?.insert(createAmapNaviInsnList())
//                }
//
//                //反注册监听器
//                klass.methods?.find {
//                    it.name == "removeAMapNaviListener"
//                }.let { methodNode ->
//                    "${context.projectDir.lastPath()}->hook amap map navi  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
//                    methodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
//                        methodNode.instructions.insertBefore(
//                            it,
//                            createAmapNaviUnRegisterInsnList()
//                        )
//                    }
//                }
//            }


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

            // 插入新四地图相关字节码
            if (className == "com.didichuxing.bigdata.dp.locsdk.DIDILocationManager") {
                // 持续定位和单次定位
                klass.methods?.filter {
                    it.name == "requestLocationUpdateOnce" || it.name == "requestLocationUpdates"
                }?.forEach { methodNode ->
                    "${context.projectDir.lastPath()}->hook didi map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(createDMapLocationListenerInsnList())
                }

                // 反注册监听器
            }
        }


        //网络 OkHttp&didi platform aop
        if (DoKitExtUtil.commExt.networkSwitch) {
            //hook OkhttpClient
            if (className == "okhttp3.OkHttpClient") {
                klass.methods?.find {
                    it.name == "<init>" && it.desc != "()V"
                }.let {
                    "${context.projectDir.lastPath()}->hook OkhttpClient  succeed: ${className}_${it?.name}_${it?.desc}".println()
                    it?.instructions
                        ?.iterator()
                        ?.asIterable()
                        ?.filterIsInstance(FieldInsnNode::class.java)
                        ?.filter { fieldInsnNode ->
                            fieldInsnNode.opcode == PUTFIELD
                                && fieldInsnNode.owner == "okhttp3/OkHttpClient"
                                && fieldInsnNode.name == "networkInterceptors"
                                && fieldInsnNode.desc == "Ljava/util/List;"
                        }?.forEach { fieldInsnNode ->
                            it.instructions.insert(fieldInsnNode, createOkHttpClientInsnList())
                        }
                }
            }

            //didi platform 判断是否引入了dokit-rpc模块
            if (className == "didihttp.DidiHttpClient" && DoKitExtUtil.commExt.didinetSwitch && DoKitExtUtil.HAS_DOKIT_RPC_MODULE) {
                klass.methods?.find {
                    it.name == "<init>" && it.desc != "()V"
                }.let {
                    "${context.projectDir.lastPath()}->hook DidiHttpClient  succeed: ${className}_${it?.name}_${it?.desc}".println()
                    it?.instructions
                        ?.iterator()
                        ?.asIterable()
                        ?.filterIsInstance(FieldInsnNode::class.java)
                        ?.filter { fieldInsnNode ->
                            fieldInsnNode.opcode == PUTFIELD
                                && fieldInsnNode.owner == "didihttp/DidiHttpClient"
                                && fieldInsnNode.name == "networkInterceptors"
                                && fieldInsnNode.desc == "Ljava/util/List;"
                        }
                        ?.forEach { fieldInsnNode ->
                            it.instructions.insert(
                                fieldInsnNode,
                                createDidiHttpClientInsnList()
                            )
                        }
                }
            }


            //hook tcp 支持tcp消息hook
            if (className == "com.didi.daijia.tcp.message.MessageSender") {
                klass.methods?.find {
                    it.name == "sendMessage" && it.desc == "(Ljava/lang/String;)Z"
                }.let {
                    "${context.projectDir.lastPath()}->hook tcp MessageSender succeed: ${className}_${it?.name}_${it?.desc}".println()
                    val first = it?.instructions?.first
                    if (first != null) {
                        it.instructions.insert(first, createMessageSenderHookInsnList())
                    }
                }
            }
            if (className == "com.didi.daijia.tcp.message.MessageReceiver") {
                klass.methods?.find {
                    it.name == "channelRead" && it.desc == "(Ljava/lang/Object;)V"
                }.let {
                    "${context.projectDir.lastPath()}->hook tcp MessageReceiver succeed: ${className}_${it?.name}_${it?.desc}".println()
                    val first = it?.instructions?.first
                    if (first != null) {
                        it.instructions.insert(first, createMessageReceiverHookInsnList())
                    }
                }
            }


            //webView 字节码操作
            if (DoKitExtUtil.commExt.webViewSwitch) {
                //普通的webview
                klass.methods.forEach { method ->
                    method.instructions?.iterator()?.asIterable()
                        ?.filterIsInstance(MethodInsnNode::class.java)?.filter {
                            if ("loadUrl".equals(it.name)) {
                                "hook loadUrl() all ${className} ^${superName}^${it.owner} :: ${it.name} , ${it.desc} ,${it.opcode}".println()
                            }
                            (it.opcode == INVOKEVIRTUAL || it.opcode == INVOKESPECIAL) &&
                                it.name == "loadUrl" &&
                                (it.desc == "(Ljava/lang/String;)V" || it.desc == "(Ljava/lang/String;Ljava/util/Map;)V") &&
                                isWebViewOwnerNameMatched(it.owner)
                        }?.forEach {
                            "${context.projectDir.lastPath()}->hook WebView#loadurl method  succeed in :  ${className}_${method.name}_${method.desc} | ${it.owner}".println()
                            if (it.desc == "(Ljava/lang/String;)V") {
                                method.instructions.insertBefore(
                                    it,
                                    createWebViewInsnList()
                                )
                            } else {
                                val size = method.localVariables.size
                                val insn = with(InsnList()) {
                                    add(VarInsnNode(ASTORE, size + 1))
                                    add(VarInsnNode(ASTORE, size))
                                    add(InsnNode(DUP))
                                    add(
                                        MethodInsnNode(
                                            INVOKESTATIC,
                                            "com/didichuxing/doraemonkit/aop/WebViewHook",
                                            "inject",
                                            "(Ljava/lang/Object;)V",
                                            false
                                        )
                                    )
                                    add(VarInsnNode(ALOAD, size))
                                    add(
                                        MethodInsnNode(
                                            INVOKESTATIC,
                                            "com/didichuxing/doraemonkit/aop/WebViewHook",
                                            "getSafeUrl",
                                            "(Ljava/lang/String;)Ljava/lang/String;",
                                            false
                                        )
                                    )
                                    add(VarInsnNode(ALOAD, size + 1))
                                    this
                                }
                                method.instructions.insertBefore(it, insn)
                            }

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
            DoKitExtUtil.THIRD_LIB_INFOS.forEach { thirdLibInfo ->
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


            //将HashMap注入到DokitPluginConfig中
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
     * 创建Amap LocationSource代码指令
     */
    private fun createAmapLocationSourceInsnList(): InsnList {
        return with(InsnList()) {
            //在AMapNavi的addAMapNaviListener方法之中插入自定义代理回调类
            add(TypeInsnNode(NEW, "com/didichuxing/doraemonkit/aop/map/AMapLocationSourceProxy"))
            add(InsnNode(DUP))
            //访问第一个参数
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESPECIAL,
                    "com/didichuxing/doraemonkit/aop/map/AMapLocationSourceProxy",
                    "<init>",
                    "(Lcom/amap/api/maps/LocationSource;)V",
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
     * 创建新四地图代码指令
     */
    private fun createDMapLocationListenerInsnList(): InsnList {
        return with(InsnList()) {
            //在DIDILocationManager的requestLocationUpdateOnce方法之中插入自定义代理回调类
            add(TypeInsnNode(NEW, "com/didichuxing/doraemonkit/aop/map/DMapLocationListenerProxy"))
            add(InsnNode(DUP))
            //访问第一个参数
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESPECIAL,
                    "com/didichuxing/doraemonkit/aop/map/DMapLocationListenerProxy",
                    "<init>",
                    "(Lcom/didichuxing/bigdata/dp/locsdk/DIDILocationListener;)V",
                    false
                )
            )
            //对第一个参数进行重新赋值
            add(VarInsnNode(ASTORE, 1))
            this
        }
    }


    /**
     * 创建OkhttpClient一个数构造函数指令
     */
    private fun createOkHttpClientInsnList(): InsnList {
        return with(InsnList()) {
            //插入application 拦截器
            add(VarInsnNode(ALOAD, 0))

            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/OkHttpHook",
                    "addDoKitIntercept",
                    "(Lokhttp3/OkHttpClient;)V",
                    false
                )
            )
            this
        }

    }

    /**
     * 创建OkhttpClient一个数构造函数指令
     */
    private fun createMessageSenderHookInsnList(): InsnList {
        return with(InsnList()) {
            //插入application 拦截器
            val l0= LabelNode()
            add(l0)
            add(VarInsnNode(ALOAD, 0))
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/tcp/ability/MessageSenderHook",
                    "hookSendMessage",
                    "(Lcom/didi/daijia/tcp/message/MessageSender;Ljava/lang/Object;)Z",
                    false
                )
            )
            val l1= LabelNode()
            add(JumpInsnNode(IFEQ,l1))
            val l2= LabelNode()
            add(l2)
            add(InsnNode(ICONST_1))
            add(InsnNode(IRETURN))
            add(l1)
            add(FrameNode(F_SAME,0,null,0,null))
            this
        }

    }
    /**
     * 创建OkhttpClient一个数构造函数指令
     */
    private fun createMessageReceiverHookInsnList(): InsnList {
        return with(InsnList()) {
            //插入application 拦截器
            val l0= LabelNode()
            add(l0)
            add(VarInsnNode(ALOAD, 0))
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/tcp/ability/MessageReceiverHook",
                    "hookChannelRead",
                    "(Lcom/didi/daijia/tcp/message/MessageReceiver;Ljava/lang/Object;)Z",
                    false
                )
            )
            val l1= LabelNode()
            add(JumpInsnNode(IFEQ,l1))
            val l2= LabelNode()
            add(l2)
            add(InsnNode(RETURN))
            add(l1)
            add(FrameNode(F_SAME,0,null,0,null))
            this
        }

    }

    /**
     * 创建OkhttpClient一个数构造函数指令
     */
    private fun createDidiHttpClientInsnList(): InsnList {
        return with(InsnList()) {
            //插入application 拦截器
            add(VarInsnNode(ALOAD, 0))

            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/foundation/net/rpc/http/DidiHttpHook",
                    "addRpcIntercept",
                    "(Ldidihttp/DidiHttpClient;)V",
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
            //复制栈顶的2个指令 指令集变为 比如 aload 2 aload0 / aload 2 aload0
            add(InsnNode(DUP2))
            //抛出最上面的指令 指令集变为 aload 2 aload0 / aload 2  其中 aload 2即为我们所需要的对象
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
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/WebViewHook",
                    "getSafeUrl",
                    "(Ljava/lang/String;)Ljava/lang/String;",
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

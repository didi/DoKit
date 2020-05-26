package com.didichuxing.doraemonkit.plugin.classtransformer

import com.didichuxing.doraemonkit.plugin.DoKitExtUtil
import com.didichuxing.doraemonkit.plugin.extension.SlowMethodExt
import com.didichuxing.doraemonkit.plugin.getMethodExitInsnNodes
import com.didichuxing.doraemonkit.plugin.isRelease
import com.didichuxing.doraemonkit.plugin.println
import com.didiglobal.booster.annotations.Priority
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.didiglobal.booster.transform.asm.className
import com.google.auto.service.AutoService
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

    override fun onPreTransform(context: TransformContext) {
        super.onPreTransform(context)
        //println("CommTransformer===>${context.name}")
    }

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        if (context.isRelease()) {
            return klass
        }

        if (!DoKitExtUtil.dokitPluginSwitchOpen()) {
            return klass
        }

        val className = klass.className
        //查找DoraemonKitReal&pluginConfig方法并插入指定字节码
        if (className == "com.didichuxing.doraemonkit.DoraemonKitReal") {
            klass.methods?.find {
                it.name == "pluginConfig"
            }.let { methodNode ->
                "insert map to the DoraemonKitReal pluginConfig succeed".println()
                methodNode?.instructions?.insert(createPluginConfigInsnList())
            }
        }

        //gps字节码操作
        if (DoKitExtUtil.commExt.gpsSwitch) {
            //插入高德地图相关字节码
            if (className == "com.amap.api.location.AMapLocationClient") {
                klass.methods?.find {
                    it.name == "setLocationListener"
                }.let { methodNode ->
                    "hook amap  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(createAmapLocationInsnList())
                }

            }

            //插入腾讯地图相关字节码
            if (className == "com.tencent.map.geolocation.TencentLocationManager") {
                //持续定位和单次定位
                klass.methods?.filter {
                    it.name == "requestSingleFreshLocation" || it.name == "requestLocationUpdates"
                }?.forEach { methodNode ->
                    "hook tencent map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                    methodNode?.instructions?.insert(createTencentLocationInsnList())
                }
            }

            //插入百度地图相关字节码
            klass.methods?.find {
                it.name == "onReceiveLocation" && it.desc == "(Lcom/baidu/location/BDLocation;)V"
            }.let { methodNode ->
                "hook baidu map  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                methodNode?.instructions?.insert(createBaiduLocationInsnList())
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
                    "hook OkHttp  succeed: ${className}_${zeroConsMethodNode?.name}_${zeroConsMethodNode?.desc}".println()
                    zeroConsMethodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        zeroConsMethodNode.instructions.insertBefore(it, createOkHttpZeroConsInsnList())
                    }
                }


                //一个参数的构造方法
                klass.methods?.find {
                    it.name == "<init>" && it.desc == "(Lokhttp3/OkHttpClient;)V"
                }.let { oneConsMethodNode ->
                    "hook OkHttp  succeed: ${className}_${oneConsMethodNode?.name}_${oneConsMethodNode?.desc}".println()
                    oneConsMethodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        oneConsMethodNode.instructions.insertBefore(it, createOkHttpOneConsInsnList())
                    }
                }

            }

            //didi platform
            if (className == "didihttp/DidiHttpClient\$Builder") {
                //空参数的构造方法
                klass.methods?.find {
                    it.name == "<init>" && it.desc == "()V"
                }.let { zeroConsMethodNode ->
                    "hook didi http  succeed: ${className}_${zeroConsMethodNode?.name}_${zeroConsMethodNode?.desc}".println()
                    zeroConsMethodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        zeroConsMethodNode.instructions.insertBefore(it, createDidiHttpZeroConsInsnList())
                    }
                }


                //一个参数的构造方法
                klass.methods?.find {
                    it.name == "<init>" && it.desc == "(Ldidihttp/DidiHttpClient;)V"
                }.let { oneConsMethodNode ->
                    "hook didi http  succeed: ${className}_${oneConsMethodNode?.name}_${oneConsMethodNode?.desc}".println()
                    oneConsMethodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                        oneConsMethodNode.instructions.insertBefore(it, createDidiHttpOneConsInsnList())
                    }
                }
            }

        }

        return klass
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
            add(MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false))
            add(MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true))
            add(InsnNode(POP))

            //put("gpsSwitch",true)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("gpsSwitch"))
            add(InsnNode(if (DoKitExtUtil.commExt.gpsSwitch) ICONST_1 else ICONST_0))
            add(MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false))
            add(MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true))
            add(InsnNode(POP))

            //put("networkSwitch",true)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("networkSwitch"))
            add(InsnNode(if (DoKitExtUtil.commExt.networkSwitch) ICONST_1 else ICONST_0))
            add(MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false))
            add(MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true))
            add(InsnNode(POP))

            //put("bigImgSwitch",true)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("bigImgSwitch"))
            add(InsnNode(if (DoKitExtUtil.commExt.bigImgSwitch) ICONST_1 else ICONST_0))
            add(MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false))
            add(MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true))
            add(InsnNode(POP))

            //put("methodSwitch",true)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("methodSwitch"))
            add(InsnNode(if (DoKitExtUtil.dokitSlowMethodSwitchOpen()) ICONST_1 else ICONST_0))
            add(MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false))
            add(MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true))
            add(InsnNode(POP))


            //put("methodStrategy",0)
            add(VarInsnNode(ALOAD, 0))
            add(LdcInsnNode("methodStrategy"))
            add(InsnNode(if (DoKitExtUtil.slowMethodExt.strategy == SlowMethodExt.STRATEGY_STACK) ICONST_0 else ICONST_1))
            add(MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false))
            add(MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true))
            add(InsnNode(POP))
            //将HashMap注入到DokitPluginConfig中
            add(VarInsnNode(ALOAD, 0))
            add(MethodInsnNode(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/DokitPluginConfig", "inject", "(Ljava/util/Map;)V", false))

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
            add(TypeInsnNode(NEW, "com/didichuxing/doraemonkit/aop/AMapLocationListenerProxy"))
            add(InsnNode(DUP))
            //访问第一个参数
            add(VarInsnNode(ALOAD, 1))
            add(MethodInsnNode(INVOKESPECIAL, "com/didichuxing/doraemonkit/aop/AMapLocationListenerProxy", "<init>", "(Lcom/amap/api/location/AMapLocationListener;)V", false))
            //对第一个参数进行重新赋值
            add(VarInsnNode(ASTORE, 1))
            this
        }

    }


    /**
     * 创建tencent地图代码指令
     */
    private fun createTencentLocationInsnList(): InsnList {
        return with(InsnList()) {
            //在AMapLocationClient的setLocationListener方法之中插入自定义代理回调类
            add(TypeInsnNode(NEW, "com/didichuxing/doraemonkit/aop/TencentLocationListenerProxy"))
            add(InsnNode(DUP))
            //访问第一个参数
            add(VarInsnNode(ALOAD, 2))
            add(MethodInsnNode(INVOKESPECIAL, "com/didichuxing/doraemonkit/aop/TencentLocationListenerProxy", "<init>", "(Lcom/tencent/map/geolocation/TencentLocationListener;)V", false))
            //对第一个参数进行重新赋值
            add(VarInsnNode(ASTORE, 2))

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
            add(MethodInsnNode(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/BDLocationUtil", "proxy", "(Lcom/baidu/location/BDLocation;)Lcom/baidu/location/BDLocation;", false))
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
            add(FieldInsnNode(GETFIELD, "okhttp3/OkHttpClient\$Builder", "interceptors", "Ljava/util/List;"))
            add(FieldInsnNode(GETSTATIC, "com/didichuxing/doraemonkit/aop/OkHttpHook", "globalInterceptors", "Ljava/util/List;"))
            add(MethodInsnNode(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true))
            add(InsnNode(POP))

            //插入NetworkInterceptor 拦截器
            add(VarInsnNode(ALOAD, 0))
            add(FieldInsnNode(GETFIELD, "okhttp3/OkHttpClient\$Builder", "networkInterceptors", "Ljava/util/List;"))
            add(FieldInsnNode(GETSTATIC, "com/didichuxing/doraemonkit/aop/OkHttpHook", "globalNetworkInterceptors", "Ljava/util/List;"))
            add(MethodInsnNode(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true))
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
            add(MethodInsnNode(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/OkHttpHook", "performOkhttpOneParamBuilderInit", "(Ljava/lang/Object;Ljava/lang/Object;)V", false))
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
            add(FieldInsnNode(GETFIELD, "didihttp/DidiHttpClient\$Builder", "interceptors", "Ljava/util/List;"))
            add(FieldInsnNode(GETSTATIC, "com/didichuxing/foundation/net/rpc/http/PlatformHttpHook", "globalInterceptors", "Ljava/util/List;"))
            add(MethodInsnNode(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true))
            add(InsnNode(POP))

            //插入NetworkInterceptor 拦截器
            add(VarInsnNode(ALOAD, 0))
            add(FieldInsnNode(GETFIELD, "didihttp/DidiHttpClient\$Builder", "networkInterceptors", "Ljava/util/List;"))
            add(FieldInsnNode(GETSTATIC, "com/didichuxing/foundation/net/rpc/http/PlatformHttpHook", "globalNetworkInterceptors", "Ljava/util/List;"))
            add(MethodInsnNode(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true))
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
            add(MethodInsnNode(INVOKESTATIC, "com/didichuxing/foundation/net/rpc/http/PlatformHttpHook", "performDidiHttpOneParamBuilderInit", "(Ljava/lang/Object;Ljava/lang/Object;)V", false))
            this
        }
    }
}
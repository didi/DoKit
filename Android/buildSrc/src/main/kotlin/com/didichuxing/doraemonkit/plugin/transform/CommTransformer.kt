package com.didichuxing.doraemonkit.plugin.transform

import com.didichuxing.doraemonkit.plugin.DoKitExtUtil
import com.didichuxing.doraemonkit.plugin.extension.SlowMethodExt
import com.didichuxing.doraemonkit.plugin.methodExitInsnNode
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
    }

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        if (!DoKitExtUtil.dokitPluginSwitchOpen()) {
            return klass
        }

        val className = klass.className
        //查找DoraemonKitReal&pluginConfig方法并插入指定字节码
        if (className == "com.didichuxing.doraemonkit.DoraemonKitReal") {
            val method = klass.methods?.find {
                it.name == "pluginConfig"
            }
            method?.instructions?.insert(createPluginConfigInsnList())
        }

        //gps字节码操作
        if (DoKitExtUtil.commExt.gpsSwitch) {
            //插入高德地图相关字节码
            if (className == "com.amap.api.location.AMapLocationClient") {
                val methodNode = klass.methods?.find {
                    it.name == "setLocationListener"
                }

                methodNode?.instructions?.insert(createAmapLocationInsnList())
            }

            //插入腾讯地图相关字节码
            if (className == "com.tencent.map.geolocation.TencentLocationManager") {
                //持续定位
                val methodNode = klass.methods?.find {
                    it.name == "requestSingleFreshLocation" || it.name == "requestSingleFreshLocation"
                }
                methodNode?.instructions?.insert(createTencentLocationInsnList())
            }

            //插入百度地图相关字节码
            val methodNode = klass.methods?.find {
                it.name == "onReceiveLocation" && it.desc == "(Lcom/baidu/location/BDLocation;)V"
            }
            methodNode?.instructions?.insert(createBaiduLocationInsnList())
        }

        //网络 OkHttp&didi platform aop
        if (DoKitExtUtil.commExt.networkSwitch) {
            //okhttp
            if (className == "okhttp3.OkHttpClient\$Builder") {

                //空参数的构造方法
                val zeroConsMethodNode = klass.methods?.find {
                    it.name == "<init>" && it.desc == "()V"
                }
                val zeroReturnInsnNode = zeroConsMethodNode?.instructions?.methodExitInsnNode()
                zeroConsMethodNode?.instructions?.insertBefore(zeroReturnInsnNode, createOkHttpZeroConsInsnList())

                //一个参数的构造方法
                val oneConsMethodNode = klass.methods?.find {
                    it.name == "<init>" && it.desc == "(Lokhttp3/OkHttpClient;)V"
                }
                val oneReturnInsnNode = oneConsMethodNode?.instructions?.methodExitInsnNode()
                oneConsMethodNode?.instructions?.insertBefore(oneReturnInsnNode, createOkHttpOneConsInsnList())
            }

            //didi platform
            if (className == "didihttp/DidiHttpClient\$Builder") {
                //空参数的构造方法
                val zeroConsMethodNode = klass.methods?.find {
                    it.name == "<init>" && it.desc == "()V"
                }
                val zeroReturnInsnNode = zeroConsMethodNode?.instructions?.methodExitInsnNode()
                zeroConsMethodNode?.instructions?.insertBefore(zeroReturnInsnNode, createDidiHttpZeroConsInsnList())

                //一个参数的构造方法
                val oneConsMethodNode = klass.methods?.find {
                    it.name == "<init>" && it.desc == "(Ldidihttp/DidiHttpClient;)V"
                }

                val oneReturnInsnNode = oneConsMethodNode?.instructions?.methodExitInsnNode()
                oneConsMethodNode?.instructions?.insertBefore(oneReturnInsnNode, createDidiHttpOneConsInsnList())
            }

        }

        return klass
    }


    /**
     * 创建pluginConfig代码指令
     */
    private fun createPluginConfigInsnList(): InsnList {
        val insnList = InsnList()
        //new HashMap
        insnList.add(TypeInsnNode(NEW, "java/util/HashMap"))
        insnList.add(InsnNode(DUP))
        insnList.add(MethodInsnNode(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false))
        //保存变量
        insnList.add(VarInsnNode(ASTORE, 0))
        //获取第一个变量
        //put("dokitPluginSwitch",true)
        insnList.add(VarInsnNode(ALOAD, 0))
        insnList.add(LdcInsnNode("dokitPluginSwitch"))
        insnList.add(InsnNode(if (DoKitExtUtil.dokitPluginSwitchOpen()) ICONST_1 else ICONST_0))
        insnList.add(MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false))
        insnList.add(MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true))
        insnList.add(InsnNode(POP))

        //put("gpsSwitch",true)
        insnList.add(VarInsnNode(ALOAD, 0))
        insnList.add(LdcInsnNode("gpsSwitch"))
        insnList.add(InsnNode(if (DoKitExtUtil.commExt.gpsSwitch) ICONST_1 else ICONST_0))
        insnList.add(MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false))
        insnList.add(MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true))
        insnList.add(InsnNode(POP))

        //put("networkSwitch",true)
        insnList.add(VarInsnNode(ALOAD, 0))
        insnList.add(LdcInsnNode("networkSwitch"))
        insnList.add(InsnNode(if (DoKitExtUtil.commExt.networkSwitch) ICONST_1 else ICONST_0))
        insnList.add(MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false))
        insnList.add(MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true))
        insnList.add(InsnNode(POP))

        //put("bigImgSwitch",true)
        insnList.add(VarInsnNode(ALOAD, 0))
        insnList.add(LdcInsnNode("bigImgSwitch"))
        insnList.add(InsnNode(if (DoKitExtUtil.commExt.bigImgSwitch) ICONST_1 else ICONST_0))
        insnList.add(MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false))
        insnList.add(MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true))
        insnList.add(InsnNode(POP))

        //put("methodSwitch",true)
        insnList.add(VarInsnNode(ALOAD, 0))
        insnList.add(LdcInsnNode("methodSwitch"))
        insnList.add(InsnNode(if (DoKitExtUtil.slowMethodExt.methodSwitch) ICONST_1 else ICONST_0))
        insnList.add(MethodInsnNode(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false))
        insnList.add(MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true))
        insnList.add(InsnNode(POP))


        //put("methodStrategy",0)
        insnList.add(VarInsnNode(ALOAD, 0))
        insnList.add(LdcInsnNode("methodStrategy"))
        insnList.add(InsnNode(if (DoKitExtUtil.slowMethodExt.strategy == SlowMethodExt.STRATEGY_STACK) ICONST_0 else ICONST_1))
        insnList.add(MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false))
        insnList.add(MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true))
        insnList.add(InsnNode(POP))
        //将HashMap注入到DokitPluginConfig中
        insnList.add(VarInsnNode(ALOAD, 0))
        insnList.add(MethodInsnNode(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/DokitPluginConfig", "inject", "(Ljava/util/Map;)V", false))
        return insnList
    }


    /**
     * 创建Amap地图代码指令
     */
    private fun createAmapLocationInsnList(): InsnList {
        val insnList = InsnList()
        //在AMapLocationClient的setLocationListener方法之中插入自定义代理回调类
        insnList.add(TypeInsnNode(NEW, "com/didichuxing/doraemonkit/aop/AMapLocationListenerProxy"))
        insnList.add(InsnNode(DUP))
        //访问第一个参数
        insnList.add(VarInsnNode(ALOAD, 1))
        insnList.add(MethodInsnNode(INVOKESPECIAL, "com/didichuxing/doraemonkit/aop/AMapLocationListenerProxy", "<init>", "(Lcom/amap/api/location/AMapLocationListener;)V", false))
        //对第一个参数进行重新赋值
        insnList.add(VarInsnNode(ASTORE, 1))
        return insnList
    }


    /**
     * 创建tencent地图代码指令
     */
    private fun createTencentLocationInsnList(): InsnList {
        val insnList = InsnList()
        //在AMapLocationClient的setLocationListener方法之中插入自定义代理回调类
        insnList.add(TypeInsnNode(NEW, "com/didichuxing/doraemonkit/aop/TencentLocationListenerProxy"))
        insnList.add(InsnNode(DUP))
        //访问第一个参数
        insnList.add(VarInsnNode(ALOAD, 2))
        insnList.add(MethodInsnNode(INVOKESPECIAL, "com/didichuxing/doraemonkit/aop/TencentLocationListenerProxy", "<init>", "(Lcom/tencent/map/geolocation/TencentLocationListener;)V", false))
        //对第一个参数进行重新赋值
        insnList.add(VarInsnNode(ASTORE, 2))
        return insnList
    }


    /**
     * 创建百度地图代码指令
     */
    private fun createBaiduLocationInsnList(): InsnList {
        val insnList = InsnList()
        //在AMapLocationClient的setLocationListener方法之中插入自定义代理回调类
        insnList.add(VarInsnNode(ALOAD, 1))
        insnList.add(MethodInsnNode(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/BDLocationUtil", "proxy", "(Lcom/baidu/location/BDLocation;)Lcom/baidu/location/BDLocation;", false))
        //对第一个参数进行重新赋值
        insnList.add(VarInsnNode(ASTORE, 1))
        return insnList
    }


    /**
     * 创建Okhttp Build 空参数构造函数指令
     */
    private fun createOkHttpZeroConsInsnList(): InsnList {
        val insnList = InsnList()
        //插入application 拦截器
        insnList.add(VarInsnNode(ALOAD, 0))
        insnList.add(FieldInsnNode(GETFIELD, "okhttp3/OkHttpClient\$Builder", "interceptors", "Ljava/util/List;"))
        insnList.add(FieldInsnNode(GETSTATIC, "com/didichuxing/doraemonkit/aop/OkHttpHook", "globalInterceptors", "Ljava/util/List;"))
        insnList.add(MethodInsnNode(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true))
        insnList.add(InsnNode(POP))

        //插入NetworkInterceptor 拦截器
        insnList.add(VarInsnNode(ALOAD, 0))
        insnList.add(FieldInsnNode(GETFIELD, "okhttp3/OkHttpClient\$Builder", "networkInterceptors", "Ljava/util/List;"))
        insnList.add(FieldInsnNode(GETSTATIC, "com/didichuxing/doraemonkit/aop/OkHttpHook", "globalNetworkInterceptors", "Ljava/util/List;"))
        insnList.add(MethodInsnNode(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true))
        insnList.add(InsnNode(POP))
        return insnList
    }


    /**
     * 创建Okhttp Build 一个参数构造函数指令
     */
    private fun createOkHttpOneConsInsnList(): InsnList {
        val insnList = InsnList()
        insnList.add(VarInsnNode(ALOAD, 0))
        insnList.add(VarInsnNode(ALOAD, 1))
        insnList.add(MethodInsnNode(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/OkHttpHook", "performOkhttpOneParamBuilderInit", "(Ljava/lang/Object;Ljava/lang/Object;)V", false))
        return insnList
    }


    /**
     * 创建didiClient Build 空参数构造函数指令
     */
    private fun createDidiHttpZeroConsInsnList(): InsnList {
        val insnList = InsnList()
        //插入application 拦截器
        insnList.add(VarInsnNode(ALOAD, 0))
        insnList.add(FieldInsnNode(GETFIELD, "didihttp/DidiHttpClient\$Builder", "interceptors", "Ljava/util/List;"))
        insnList.add(FieldInsnNode(GETSTATIC, "com/didichuxing/foundation/net/rpc/http/PlatformHttpHook", "globalInterceptors", "Ljava/util/List;"))
        insnList.add(MethodInsnNode(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true))
        insnList.add(InsnNode(POP))

        //插入NetworkInterceptor 拦截器
        insnList.add(VarInsnNode(ALOAD, 0))
        insnList.add(FieldInsnNode(GETFIELD, "didihttp/DidiHttpClient\$Builder", "networkInterceptors", "Ljava/util/List;"))
        insnList.add(FieldInsnNode(GETSTATIC, "com/didichuxing/foundation/net/rpc/http/PlatformHttpHook", "globalNetworkInterceptors", "Ljava/util/List;"))
        insnList.add(MethodInsnNode(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true))
        insnList.add(InsnNode(POP))
        return insnList
    }


    /**
     * 创建didiClient Build 一个参数构造函数指令
     */
    private fun createDidiHttpOneConsInsnList(): InsnList {
        val insnList = InsnList()
        insnList.add(VarInsnNode(ALOAD, 0))
        insnList.add(VarInsnNode(ALOAD, 1))
        insnList.add(MethodInsnNode(INVOKESTATIC, "com/didichuxing/foundation/net/rpc/http/PlatformHttpHook", "performDidiHttpOneParamBuilderInit", "(Ljava/lang/Object;Ljava/lang/Object;)V", false))
        return insnList
    }


}
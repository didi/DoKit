package com.didichuxing.doraemonkit.plugin.bytecode;

import com.android.build.gradle.AppExtension;
import com.didichuxing.doraemonkit.plugin.StringUtils;
import com.didichuxing.doraemonkit.plugin.bytecode.method.AmapLocationMethodAdapter;
import com.didichuxing.doraemonkit.plugin.bytecode.method.ApplicationOnCreateMethodAdapter;
import com.didichuxing.doraemonkit.plugin.bytecode.method.BaiduLocationMethodAdapter;
import com.didichuxing.doraemonkit.plugin.bytecode.method.FlagMethodAdapter;
import com.didichuxing.doraemonkit.plugin.bytecode.method.GlobalMethodAdapter;
import com.didichuxing.doraemonkit.plugin.bytecode.method.OkHttpMethodAdapter;
import com.didichuxing.doraemonkit.plugin.bytecode.method.PlatformHttpMethodAdapter;
import com.didichuxing.doraemonkit.plugin.bytecode.method.TencentLocationMethodAdapter;
import com.didichuxing.doraemonkit.plugin.bytecode.method.TencentLocationSingleMethodAdapter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by jint on 13/12/2019.
 * 类访问器
 */
public final class DokitClassAdapter extends ClassVisitor {
    /**
     * 当前类型
     */
    private String className;
    /**
     * 当前类的父类 假如存在的话
     */
    private String superName;
    private AppExtension appExtension;
    String applicationId;

    /**
     * @param cv 传进来的是 ClassWriter
     */
    public DokitClassAdapter(final ClassVisitor cv, AppExtension appExtension) {
        super(Opcodes.ASM7, cv);
        this.appExtension = appExtension;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

        this.className = name;
        this.superName = superName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        //从传进来的ClassWriter中读取MethodVisitor
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (StringUtils.isEmpty(applicationId)) {
            applicationId = appExtension.getDefaultConfig().getApplicationId();
        }

        //开发者变量字节码替换
        if (className.equals("com/didichuxing/doraemonkit/DoraemonKitReal") && name.equals("install") && desc != null) {
            if (getParamsSize(desc) == 3) {
                log(className, access, name, desc, signature);
                //创建MethodVisitor代理
                return mv == null ? null : new FlagMethodAdapter(access, desc, mv);
            }
        }

        //高德地图字节码替换
        if (className.equals("com/amap/api/location/AMapLocationClient") && name.equals("setLocationListener")) {
            //创建MethodVisitor代理
            log(className, access, name, desc, signature);
            return mv == null ? null : new AmapLocationMethodAdapter(access, desc, mv);
        }

        //腾讯地图字节码替换
        if (className.equals("com/tencent/map/geolocation/TencentLocationManager") && name.equals("requestLocationUpdates")) {
            log(className, access, name, desc, signature);
            //创建MethodVisitor代理
            return mv == null ? null : new TencentLocationMethodAdapter(access, desc, mv);
        }

        //腾讯地图单次定位
        if (className.equals("com/tencent/map/geolocation/TencentLocationManager") && name.equals("requestSingleFreshLocation")) {
            log(className, access, name, desc, signature);
            //创建MethodVisitor代理
            return mv == null ? null : new TencentLocationSingleMethodAdapter(access, desc, mv);
        }

        //百度地图定位 汇报函数签名错误 暂时未找到原因
//        if (className.equals("com/baidu/location/LocationClient") && name.equals("registerLocationListener")) {
//            log(className, access, name, desc, signature);
//            //创建MethodVisitor代理
//            return mv == null ? null : new BaiduLocationMethodAdapter(access, desc, mv);
//        }

        //百度地图定位
        if (name.equals("onReceiveLocation") && desc.equals("(Lcom/baidu/location/BDLocation;)V")) {
            log(className, access, name, desc, signature);
            //创建MethodVisitor代理
            return mv == null ? null : new BaiduLocationMethodAdapter(access, desc, mv);
        }


        //okhttp 拦截器字节码替换
        if (className.equals("okhttp3/OkHttpClient$Builder") && name.equals("<init>")) {
            //创建MethodVisitor代理
            log(className, access, name, desc, signature);
            return mv == null ? null : new OkHttpMethodAdapter(access, desc, mv);
        }

        //didi平台端 网络 拦截器字节码替换
        if (className.equals("didihttp/DidiHttpClient$Builder") && name.equals("<init>")) {
            //创建MethodVisitor代理
            log(className, access, name, desc, signature);
            return mv == null ? null : new PlatformHttpMethodAdapter(access, desc, mv);
        }
        //app启动hook点 onCreate()函数 兼容MultiDex
        if (!StringUtils.isEmpty(superName) && (superName.equals("android/app/Application") || superName.equals("android/support/multidex/MultiDexApplication")) && name.equals("onCreate") && desc.equals("()V")) {
            log(className, access, name, desc, signature);
            return mv == null ? null : new ApplicationOnCreateMethodAdapter(access, name, desc, mv);
        }

        //过滤所有类中当前方法中所有的字节码
        return mv == null ? null : new GlobalMethodAdapter(access, desc, mv);

    }

    /**
     * 获取形参个数
     *
     * @param desc
     * @return
     */
    private int getParamsSize(String desc) {
        //(Landroid/app/Application;Ljava/util/List;Ljava/lang/String;)V 包含3个参数的install方法实例
        if (desc == null || desc.equals("")) {
            return 0;
        }
        return desc.split(";").length - 1;
    }

    /**
     * 日志输出
     *
     * @param className
     * @param access
     * @param name
     * @param desc
     * @param signature
     */
    private void log(String className, int access, String name, String desc, String signature) {
        System.out.println("matched====>" + "  className===" + className + "   access===" + access + "   methodName===" + name + "   desc===" + desc + "   signature===" + signature);
    }
}
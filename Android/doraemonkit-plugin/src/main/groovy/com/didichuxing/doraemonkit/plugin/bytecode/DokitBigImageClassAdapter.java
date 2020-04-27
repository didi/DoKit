package com.didichuxing.doraemonkit.plugin.bytecode;

import com.didichuxing.doraemonkit.plugin.DokitExtUtil;
import com.didichuxing.doraemonkit.plugin.bytecode.method.bigimg.FrescoMethodAdapter;
import com.didichuxing.doraemonkit.plugin.bytecode.method.bigimg.GlideMethodAdapter;
import com.didichuxing.doraemonkit.plugin.bytecode.method.bigimg.ImageLoaderMethodAdapter;
import com.didichuxing.doraemonkit.plugin.bytecode.method.bigimg.PicassoMethodAdapter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Created by jint on 13/12/2019.
 * 类访问器
 */
public final class DokitBigImageClassAdapter extends ClassVisitor {
    /**
     * 当前类型
     */
    private String className;
    /**
     * 当前类的父类 假如存在的话
     */
    private String superName;

    /**
     * @param cv cv
     */
    public DokitBigImageClassAdapter(final ClassVisitor cv) {
        super(Opcodes.ASM7, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
        this.superName = superName;
    }


    /**
     * Visits a method of the class. This method <i>must</i> return a new {@link MethodVisitor}
     * instance (or {@literal null}) each time it is called, i.e., it should not return a previously
     * returned visitor.
     *
     * @param access     the method's access flags (see {@link Opcodes}). This parameter also indicates if
     *                   the method is synthetic and/or deprecated.
     * @param methodName the method's name.
     * @param desc       the method's descriptor (see {@link Type}).
     * @param signature  the method's signature. May be {@literal null} if the method parameters,
     *                   return type and exceptions do not use generic types.
     * @param exceptions the internal names of the method's exception classes (see {@link
     *                   Type#getInternalName()}). May be {@literal null}.
     * @return an object to visit the byte code of the method, or {@literal null} if this class
     * visitor is not interested in visiting the code of this method.
     */
    @Override
    public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions) {
        //从传进来的ClassWriter中读取MethodVisitor
        MethodVisitor mv = cv.visitMethod(access, methodName, desc, signature, exceptions);
        //开关被关闭 不插入代码

        if (!DokitExtUtil.getInstance().getCommConfig().bigImgSwitch) {
            return mv;
        }

        //Glide v4字节码替换 glide v4.9 通过init注入 4.11.0 通用构造函数
        if (className.equals("com/bumptech/glide/request/SingleRequest") && (methodName.equals("init") || methodName.equals("<init>")) && desc != null) {
            log(className, access, methodName, desc, signature);
            //创建MethodVisitor代理
            return mv == null ? null : new GlideMethodAdapter(mv, access, methodName, desc);
        }

        //Glide v4字节码替换 transform 代码注入
//        if (className.equals("com/bumptech/glide/request/BaseRequestOptions") && methodName.equals("transform") && desc != null) {
//            if (desc.equals("(Lcom/bumptech/glide/load/Transformation;Z)Lcom/bumptech/glide/request/BaseRequestOptions;")) {
//                log(className, access, methodName, desc, signature);
//                //创建MethodVisitor代理
//                return mv == null ? null : new GlideTransformMethodAdapter(mv, access, methodName, desc);
//            }
//        }


        //Glide v3字节码替换
//        if (className.equals("com/bumptech/glide/GenericRequestBuilder") && methodName.equals("listener") && desc != null) {
//            log(className, access, methodName, desc, signature);
//            //创建MethodVisitor代理
//            return mv == null ? null : new GlideV3MethodAdapter(mv, access, methodName, desc);
//        }

        //Picasso 字节码替换
        if (className.equals("com/squareup/picasso/Request") && methodName.equals("<init>") && desc != null) {
            log(className, access, methodName, desc, signature);
//            (Lcom/bumptech/glide/load/Transformation;Z)Lcom/bumptech/glide/request/BaseRequestOptions;
            //创建MethodVisitor代理
            return mv == null ? null : new PicassoMethodAdapter(mv, access, methodName, desc);
        }

        //Fresco字节码替换
        if (className.equals("com/facebook/imagepipeline/request/ImageRequest") && methodName.equals("<init>") && desc != null) {
            log(className, access, methodName, desc, signature);
            //创建MethodVisitor代理
            return mv == null ? null : new FrescoMethodAdapter(mv, access, methodName, desc);
        }

        //imageLoader字节码替换
        if (className.equals("com/nostra13/universalimageloader/core/ImageLoadingInfo") && methodName.equals("<init>") && desc != null) {
            log(className, access, methodName, desc, signature);
            //创建MethodVisitor代理
            return mv == null ? null : new ImageLoaderMethodAdapter(mv, access, methodName, desc);
        }

        //过滤所有类中当前方法中所有的字节码
        return mv;

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
        System.out.println("DokitBigImageClassAdapter===matched====>" + "  className===" + className + "   access===" + access + "   methodName===" + name + "   desc===" + desc + "   signature===" + signature);
    }

}
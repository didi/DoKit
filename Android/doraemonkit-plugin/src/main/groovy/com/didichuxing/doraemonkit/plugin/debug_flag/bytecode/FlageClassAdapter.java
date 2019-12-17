package com.didichuxing.doraemonkit.plugin.debug_flag.bytecode;

import org.gradle.internal.impldep.org.apache.http.util.TextUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by jint on 13/12/2019.
 * 类访问器
 */
public final class FlageClassAdapter extends ClassVisitor {

    private String className;


    /**
     * @param cv 传进来的是 ClassWriter
     */
    FlageClassAdapter(final ClassVisitor cv) {
        super(Opcodes.ASM7, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;

    }

    /**
     * @param access
     * @param name
     * @param desc       方法参数类型
     * @param signature
     * @param exceptions
     * @return
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        //从传进来的ClassWriter中读取MethodVisitor
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        //判断指定并且类中的方法类型
        if (className.equals("com/didichuxing/doraemonkit/DoraemonKit") && name.equals("install") && desc != null) {
            //(Landroid/app/Application;Ljava/util/List;Ljava/lang/String;)V 包含3个参数的install方法
            String[] descs = desc.split(";");
            if (descs.length == 4) {
                System.out.println("className===>" + className + "   descriptor====>" + desc + "   name===>" + name);
                //创建MethodVisitor代理
                return mv == null ? null : new FlagMethodAdapter(access, desc, mv);
            } else {
                return mv;
            }

        }
        return mv;
    }
}
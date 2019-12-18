package com.didichuxing.doraemonkit.plugin.amap.bytecode;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by jint on 13/12/2019.
 * 类访问器
 */
public final class AmapClassAdapter extends ClassVisitor {

    private String className;


    /**
     * @param cv 传进来的是 ClassWriter
     */
    AmapClassAdapter(final ClassVisitor cv) {
        super(Opcodes.ASM7, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        //从传进来的ClassWriter中读取MethodVisitor
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        //判断指定并且类中的方法类型
        if (className.equals("com/amap/api/location/AMapLocationClient") && name.equals("setLocationListener")) {
            //创建MethodVisitor代理
            System.out.println("className===>" + className + "   descriptor====>" + desc + "   name===>" + name);
            return mv == null ? null : new AmapMethodAdapter(access, desc, mv);
        }
        return mv;
    }
}
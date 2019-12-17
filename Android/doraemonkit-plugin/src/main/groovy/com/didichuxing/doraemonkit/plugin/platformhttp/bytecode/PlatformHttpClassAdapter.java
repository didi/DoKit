package com.didichuxing.doraemonkit.plugin.platformhttp.bytecode;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by Quinn on 09/09/2018.
 * 类访问器
 */
public final class PlatformHttpClassAdapter extends ClassVisitor {

    private String className;

    private boolean weaveEventListener;

    /**
     * @param cv                 传进来的是 ClassWriter
     * @param weaveEventListener
     */
    PlatformHttpClassAdapter(final ClassVisitor cv, boolean weaveEventListener) {
        super(Opcodes.ASM7, cv);
        this.weaveEventListener = weaveEventListener;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name,
                                     final String desc, final String signature, final String[] exceptions) {
        //从传进来的ClassWriter中读取MethodVisitor
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        //判断指定并且类中的方法类型
        if (className.equals("didihttp/DidiHttpClient$Builder") && name.equals("<init>")) {
            //创建MethodVisitor代理
            System.out.println("className===>" + className + "desc====>" + desc + "   name===>" + name);
            return mv == null ? null : new PlatformHttpMethodAdapter(access, desc, mv, weaveEventListener);
        }
        return mv;
    }

}
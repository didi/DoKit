package com.didichuxing.doraemonkit.plugin.urlconnection.bytecode;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by jint on 13/12/2019.
 * 类访问器
 */
public final class UrlConnectionClassAdapter extends ClassVisitor {

    private String className;


    /**
     * @param cv 传进来的是 ClassWriter
     */
    UrlConnectionClassAdapter(final ClassVisitor cv) {
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

        //创建MethodVisitor代理
        return mv == null ? null : new UrlConnectionMethodAdapter(access, desc, mv);

    }
}
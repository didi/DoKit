package com.didichuxing.doraemonkit.plugin.bytecode.method.comm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;


public class ThreadLogAdapter extends LocalVariablesSorter implements Opcodes {
    public ThreadLogAdapter(int access, String desc, MethodVisitor mv) {
        super(org.objectweb.asm.Opcodes.ASM7, access, desc, mv);
    }
    //方法得开始
    @Override
    public void visitCode() {

        super.visitCode();
    }

    //方法结束
    public void visitInsn(int opcode) {
        //添加日志打印
        mv.visitVarInsn(ALOAD, 0);
       // mv.visitMethodInsn(INVOKESTATIC,"com/didichuxing/doraemonkit/aop/ThreadLog","log","(Ljava/lang/Thread;)V",false);
        super.visitInsn(opcode);
    }
}

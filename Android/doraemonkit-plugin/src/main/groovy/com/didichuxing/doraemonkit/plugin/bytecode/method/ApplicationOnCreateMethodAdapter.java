package com.didichuxing.doraemonkit.plugin.bytecode.method;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Created by jint on 13/12/2019.
 */
public final class ApplicationOnCreateMethodAdapter extends AdviceAdapter implements Opcodes {

    public ApplicationOnCreateMethodAdapter(int access, String name, String desc, MethodVisitor mv) {
        //super(Opcodes.ASM7, access, desc, mv);
        super(Opcodes.ASM7, mv, access, name, desc);
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        mv.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/kit/timecounter/TimeCounterManager", "get", "()Lcom/didichuxing/doraemonkit/kit/timecounter/TimeCounterManager;", false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/didichuxing/doraemonkit/kit/timecounter/TimeCounterManager", "onAppCreateStart", "(Landroid/app/Application;)V", false);
    }


    @Override
    protected void onMethodExit(int opcode) {
        mv.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/kit/timecounter/TimeCounterManager", "get", "()Lcom/didichuxing/doraemonkit/kit/timecounter/TimeCounterManager;", false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/didichuxing/doraemonkit/kit/timecounter/TimeCounterManager", "onAppCreateEnd", "(Landroid/app/Application;)V", false);
        super.onMethodExit(opcode);
    }


}

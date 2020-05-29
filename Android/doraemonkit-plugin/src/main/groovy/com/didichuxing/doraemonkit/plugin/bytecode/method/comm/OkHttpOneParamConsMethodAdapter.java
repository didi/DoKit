package com.didichuxing.doraemonkit.plugin.bytecode.method.comm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Only weave okhttp3/OkHttpClient's one param init method
 */
public final class OkHttpOneParamConsMethodAdapter extends AdviceAdapter {

    //private static final LoggerWrapper logger = LoggerWrapper.getLogger(OkHttpMethodAdapter.class);


    public OkHttpOneParamConsMethodAdapter(MethodVisitor methodVisitor, int access, String methodName, String descriptor) {
        super(Opcodes.ASM7, methodVisitor, access, methodName, descriptor);
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/OkHttpHook", "performOkhttpOneParamBuilderInit", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
    }
}

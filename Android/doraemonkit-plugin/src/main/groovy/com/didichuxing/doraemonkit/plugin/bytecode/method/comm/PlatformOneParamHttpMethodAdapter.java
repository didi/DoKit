package com.didichuxing.doraemonkit.plugin.bytecode.method.comm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Only weave didihttp/DidiHttpClient's init method
 * Created by Quinn on 09/09/2018.
 */
public final class PlatformOneParamHttpMethodAdapter extends AdviceAdapter {


    public PlatformOneParamHttpMethodAdapter(MethodVisitor methodVisitor, int access, String methodName, String descriptor) {
        super(Opcodes.ASM7, methodVisitor, access, methodName, descriptor);
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, "com/didichuxing/foundation/net/rpc/http/PlatformHttpHook", "performDidiHttpOneParamBuilderInit", "(Ljava/lang/Object;Ljava/lang/Object;)V", false);
    }

}

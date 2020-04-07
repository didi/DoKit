package com.didichuxing.doraemonkit.plugin.bytecode.method.bigimg;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by jint on 13/12/2019. 生命周期
 * <p>
 * visitAnnotationDefault?
 * ( visitAnnotation | visitParameterAnnotation | visitAttribute )*
 * ( visitCode
 * ( visitTryCatchBlock | visitLabel | visitFrame | visitXxxInsn |
 * visitLocalVariable | visitLineNumber )*
 * visitMaxs )?
 * visitEnd
 */
public final class GlideMethodAdapter extends AdviceAdapter {


    /**
     * Constructs a new {@link AdviceAdapter}.
     *
     * @param methodVisitor the method visitor to which this adapter delegates calls.
     * @param access        the method's access flags (see {@link Opcodes}).
     * @param methodName    the method's name.
     * @param descriptor    the method's descriptor.
     */
    public GlideMethodAdapter(MethodVisitor methodVisitor, int access, String methodName, String descriptor) {
        super(Opcodes.ASM7, methodVisitor, access, methodName, descriptor);
    }


    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);

//        mv.visitVarInsn(ALOAD, 0);
//        mv.visitVarInsn(ALOAD, 0);
//        mv.visitFieldInsn(GETFIELD, "com/bumptech/glide/request/SingleRequest", "requestListeners", "Ljava/util/List;");
//        mv.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/bigimg/glide/GlideHook", "proxy", "(Ljava/util/List;)Ljava/util/List;", false);
//        mv.visitFieldInsn(PUTFIELD, "com/bumptech/glide/request/SingleRequest", "requestListeners", "Ljava/util/List;");


        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/bigimg/glide/GlideHook", "proxy", "(Ljava/lang/Object;)V", false);
    }

}

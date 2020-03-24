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
public final class FrescoMethodAdapter extends AdviceAdapter {


    /**
     * Constructs a new {@link AdviceAdapter}.
     *
     * @param methodVisitor the method visitor to which this adapter delegates calls.
     * @param access        the method's access flags (see {@link Opcodes}).
     * @param methodName    the method's name.
     * @param descriptor    the method's descriptor.
     */
    public FrescoMethodAdapter(MethodVisitor methodVisitor, int access, String methodName, String descriptor) {
        super(Opcodes.ASM7, methodVisitor, access, methodName, descriptor);
    }




    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/imagepipeline/request/ImageRequestBuilder", "getSourceUri", "()Landroid/net/Uri;", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/imagepipeline/request/ImageRequestBuilder", "getPostprocessor", "()Lcom/facebook/imagepipeline/request/Postprocessor;", false);
        mv.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/bigimg/fresco/FrescoHook", "proxy", "(Landroid/net/Uri;Lcom/facebook/imagepipeline/request/Postprocessor;)Lcom/facebook/imagepipeline/request/Postprocessor;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/imagepipeline/request/ImageRequestBuilder", "setPostprocessor", "(Lcom/facebook/imagepipeline/request/Postprocessor;)Lcom/facebook/imagepipeline/request/ImageRequestBuilder;", false);
    }
}

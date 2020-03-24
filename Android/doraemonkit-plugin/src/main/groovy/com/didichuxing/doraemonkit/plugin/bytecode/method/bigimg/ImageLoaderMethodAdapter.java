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
public final class ImageLoaderMethodAdapter extends AdviceAdapter {


    /**
     * Constructs a new {@link AdviceAdapter}.
     *
     * @param methodVisitor the method visitor to which this adapter delegates calls.
     * @param access        the method's access flags (see {@link Opcodes}).
     * @param methodName    the method's name.
     * @param descriptor    the method's descriptor.
     */
    public ImageLoaderMethodAdapter(MethodVisitor methodVisitor, int access, String methodName, String descriptor) {
        super(Opcodes.ASM7, methodVisitor, access, methodName, descriptor);
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        //加载第6个参数 ImageLoadingListener
        mv.visitVarInsn(ALOAD, 6);
        mv.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/bigimg/imageloader/ImageLoaderHook", "proxy", "(Lcom/nostra13/universalimageloader/core/listener/ImageLoadingListener;)Lcom/nostra13/universalimageloader/core/listener/ImageLoadingListener;", false);
        //重新赋值给第6个参数
        mv.visitVarInsn(ASTORE, 6);

    }
}

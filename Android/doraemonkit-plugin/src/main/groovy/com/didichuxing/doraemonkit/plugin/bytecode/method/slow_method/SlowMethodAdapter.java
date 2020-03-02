package com.didichuxing.doraemonkit.plugin.bytecode.method.slow_method;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;

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
public final class SlowMethodAdapter extends AdviceAdapter {
    String className;
    int thresholdTime;

    /**
     * Constructs a new {@link AdviceAdapter}.
     *
     * @param api           the ASM API version implemented by this visitor. Must be one of {@link
     *                      Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
     * @param className     类名
     * @param thresholdTime 时间阈值
     * @param methodVisitor the method visitor to which this adapter delegates calls.
     * @param access        the method's access flags (see {@link Opcodes}).
     * @param methodName    the method's name.
     * @param descriptor    the method's descriptor (see {@link Type Type}).
     */
    public SlowMethodAdapter(MethodVisitor methodVisitor, String className, int thresholdTime, int access, String methodName, String descriptor) {
        super(Opcodes.ASM7, methodVisitor, access, methodName, descriptor);
        this.className = className;
        this.thresholdTime = thresholdTime;
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        try {
            mv.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/MethodCostUtil", "getInstance", "()Lcom/didichuxing/doraemonkit/aop/MethodCostUtil;", false);
            mv.visitLdcInsn(this.className + "&" + this.getName());
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/didichuxing/doraemonkit/aop/MethodCostUtil", "recodeMethodCostStart", "(Ljava/lang/String;)V", false);
        } catch (Exception e) {
            System.out.println("e===>" + e.getMessage());
        }

    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        try {
            mv.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/MethodCostUtil", "getInstance", "()Lcom/didichuxing/doraemonkit/aop/MethodCostUtil;", false);
            mv.visitIntInsn(SIPUSH, thresholdTime);
            mv.visitLdcInsn(this.className + "&" + this.getName());
            //mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/didichuxing/doraemonkit/aop/MethodCostUtil", "recodeMethodCostEnd", "(ILjava/lang/String;)V", false);
        } catch (Exception e) {
            System.out.println("e===>" + e.getMessage());
        }

    }
}

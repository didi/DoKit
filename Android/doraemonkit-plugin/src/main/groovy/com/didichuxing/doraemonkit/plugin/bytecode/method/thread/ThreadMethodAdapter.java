package com.didichuxing.doraemonkit.plugin.bytecode.method.thread;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;
/**
 * 方法访问,
 * 对所有start方法修改为 ThreadHook.start(this),
 * 这个静态方法,判断原函数的对象是Thread或其子类会打印日志,其他函数会不处理,调用原逻辑.
 */
public class ThreadMethodAdapter extends LocalVariablesSorter implements Opcodes {
    private String className;
    private String methodName;

    public ThreadMethodAdapter(String className, String methodName, int access, String desc, MethodVisitor mv) {
        super(Opcodes.ASM7, access, desc, mv);
        this.className = className;
        this.methodName = methodName;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isInterface) {
        if (!className.equals("com/didichuxing/doraemonkit/aop/ThreadHook") && opcode == Opcodes.INVOKEVIRTUAL
                && name.equals("start") && desc.equals("()V")) {
            super.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/ThreadHook", "start", "(Ljava/lang/Object;)V", false);
        } else {
            super.visitMethodInsn(opcode, owner, name, desc, isInterface);
        }
    }

}

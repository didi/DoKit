package com.didichuxing.doraemonkit.plugin.bytecode.method.application;

import com.android.tools.r8.code.InvokeStatic;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Created by jint on 13/12/2019.
 */
public final class ApplicationOnCreateMethodAdapter extends LocalVariablesSorter implements Opcodes {
    private String className;
    private String methodName;

    public ApplicationOnCreateMethodAdapter(String className, String methodName, int access, String desc, MethodVisitor mv) {
        super(Opcodes.ASM7, access, desc, mv);
        this.className = className;
        this.methodName = methodName;
    }

    /**
     * @param opcode      操作指令
     * @param owner       调用对象
     * @param name        函数名
     * @param desc        函数签名
     * @param isInterface 是否是接口
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isInterface) {
        //全局替换URL的openConnection方法为dokit的URLConnection
        if (opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKESTATIC) {
            log(opcode, owner, name, desc, isInterface);
            super.visitMethodInsn(opcode, owner, name, desc, isInterface);

            //super.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/urlconnection/HttpUrlConnectionProxyUtil", "proxy", "(Ljava/net/URLConnection;)Ljava/net/URLConnection;", false);
        } else {
            super.visitMethodInsn(opcode, owner, name, desc, isInterface);
        }
    }

    /**
     * 日志输出
     *
     * @param opcode
     * @param owner
     * @param name
     * @param desc
     */
    private void log(int opcode, String owner, String name, String desc, boolean isInterface) {
        System.out.println("ApplicationOnCreateMethodAdapter=matched=>" + "  opcode=" + opcode + " owner=" + owner + "  methodName=" + name + "  desc=" + desc + "  isInterface=" + isInterface);
        System.out.println("");
    }


}

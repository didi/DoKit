package com.didichuxing.doraemonkit.plugin.bytecode.method.urlconnection;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Only weave com/didichuxing/doraemonkit/aop/OkHttpHook  installInterceptor method
 * Created by jint on 13/12/2019.
 */
public final class UrlConnectionMethodAdapter extends LocalVariablesSorter implements Opcodes {


    public UrlConnectionMethodAdapter(int access, String desc, MethodVisitor mv) {
        super(Opcodes.ASM7, access, desc, mv);
    }


    /**
     * @param opcode      操作指令
     * @param owner       调用对象
     * @param name        函数名
     * @param desc        函数签名
     * @param isInterface
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isInterface) {
        //全局替换URL的openConnection方法为dokit的URLConnection
        if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("java/net/URL")
                && name.equals("openConnection") && desc.equals("()Ljava/net/URLConnection;")) {
            log(opcode, owner, name, desc, isInterface);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/net/URL", "openConnection", "()Ljava/net/URLConnection;", false);
            super.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/urlconnection/HttpUrlConnectionProxyUtil", "proxy", "(Ljava/net/URLConnection;)Ljava/net/URLConnection;", false);
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
        System.out.println("matched====>" + "   opcode====" + opcode + "  owner===" + owner + "   methodName===" + name + "   desc===" + desc + "   isInterface===" + isInterface);
    }
}

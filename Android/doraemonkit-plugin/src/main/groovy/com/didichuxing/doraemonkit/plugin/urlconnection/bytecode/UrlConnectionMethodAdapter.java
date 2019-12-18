package com.didichuxing.doraemonkit.plugin.urlconnection.bytecode;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Only weave com/didichuxing/doraemonkit/aop/OkHttpHook  installInterceptor method
 * Created by jint on 13/12/2019.
 */
public final class UrlConnectionMethodAdapter extends LocalVariablesSorter implements Opcodes {


    UrlConnectionMethodAdapter(int access, String desc, MethodVisitor mv) {
        super(Opcodes.ASM7, access, desc, mv);
    }


    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isInterface) {
        //全局替换URL的openConnection方法为dokit的URLConnection
        if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("java/net/URL")
                && name.equals("openConnection") && desc.equals("()Ljava/net/URLConnection;")) {
            System.out.println("===UrlConnectionMethodAdapter====openConnection=======");

            mv.visitMethodInsn(INVOKEVIRTUAL, "java/net/URL", "openConnection", "()Ljava/net/URLConnection;", false);
            super.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/urlconnection/HttpUrlConnectionProxyUtil", "proxy", "(Ljava/net/URLConnection;)Ljava/net/URLConnection;", false);
        } else {
            super.visitMethodInsn(opcode, owner, name, desc, isInterface);
        }

    }
}

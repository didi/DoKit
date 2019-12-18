package com.didichuxing.doraemonkit.plugin.okhttp.bytecode;

import com.android.build.gradle.internal.LoggerWrapper;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Only weave okhttp3/OkHttpClient's init method
 * Created by Quinn on 09/09/2018.
 */
public final class OkHttpMethodAdapter extends LocalVariablesSorter implements Opcodes {

    private static final LoggerWrapper logger = LoggerWrapper.getLogger(OkHttpMethodAdapter.class);

    private boolean weaveEventListener;

    OkHttpMethodAdapter(int access, String desc, MethodVisitor mv, boolean weaveEventListener) {
        super(Opcodes.ASM7, access, desc, mv);
        this.weaveEventListener = weaveEventListener;
    }

    @Override
    public void visitInsn(int opcode) {
        //添加全局的 Dokit Interceptor
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            //
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "okhttp3/OkHttpClient$Builder", "interceptors", "Ljava/util/List;");
            mv.visitFieldInsn(GETSTATIC, "com/didichuxing/doraemonkit/aop/OkHttpHook", "globalInterceptors", "Ljava/util/List;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true);
            mv.visitInsn(POP);

        }
        super.visitInsn(opcode);
    }

}

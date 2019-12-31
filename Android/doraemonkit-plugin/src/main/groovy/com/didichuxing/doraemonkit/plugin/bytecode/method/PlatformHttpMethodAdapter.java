package com.didichuxing.doraemonkit.plugin.bytecode.method;

import com.android.build.gradle.internal.LoggerWrapper;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Only weave okhttp3/OkHttpClient's init method
 * Created by Quinn on 09/09/2018.
 */
public final class PlatformHttpMethodAdapter extends LocalVariablesSorter implements Opcodes {


    public PlatformHttpMethodAdapter(int access, String desc, MethodVisitor mv) {
        super(Opcodes.ASM7, access, desc, mv);
    }

    @Override
    public void visitInsn(int opcode) {
        //添加全局的 platform Interceptor
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            //
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "didihttp/DidiHttpClient$Builder", "interceptors", "Ljava/util/List;");
            mv.visitFieldInsn(GETSTATIC, "com/didichuxing/foundation/net/rpc/http/PlatformHttpHook", "globalInterceptors", "Ljava/util/List;");
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true);
            mv.visitInsn(POP);
        }
        super.visitInsn(opcode);
    }

}

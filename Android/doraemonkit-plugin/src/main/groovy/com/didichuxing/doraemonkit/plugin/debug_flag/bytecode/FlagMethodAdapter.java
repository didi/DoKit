package com.didichuxing.doraemonkit.plugin.debug_flag.bytecode;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Only weave com/didichuxing/doraemonkit/aop/OkHttpHook  installInterceptor method
 * Created by jint on 13/12/2019.
 */
public final class FlagMethodAdapter extends LocalVariablesSorter implements Opcodes {


    FlagMethodAdapter(int access, String desc, MethodVisitor mv) {
        super(Opcodes.ASM7, access, desc, mv);
    }

    @Override
    public void visitCode() {
        //在installInterceptor 方法之前插入IS_HOOK = true 的代码
        mv.visitInsn(ICONST_1);
        mv.visitFieldInsn(PUTSTATIC, "com/didichuxing/doraemonkit/DoraemonKitReal", "IS_HOOK", "Z");
        super.visitCode();
    }


}

package com.didichuxing.doraemonkit.plugin.bytecode.method.comm;

import com.didichuxing.doraemonkit.plugin.DokitExtUtil;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import static com.didichuxing.doraemonkit.plugin.DokitExtension.SlowMethodConfig.STRATEGY_STACK;

/**
 * Created by jint on 13/12/2019.
 * 注入插件配置信息到dokit的DoraemonKitReal install中
 */
public final class PluginConfigMethodAdapter extends AdviceAdapter implements Opcodes {


    public PluginConfigMethodAdapter(int access, String desc, MethodVisitor mv, String methodName) {
        super(Opcodes.ASM7, mv, access, methodName, desc);
    }


    @Override
    protected void onMethodEnter() {
        mv.visitTypeInsn(NEW, "java/util/HashMap");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn("dokitPluginSwitch");
        mv.visitInsn(DokitExtUtil.getInstance().dokitPluginSwitchOpen() ? ICONST_1 : ICONST_0);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
        mv.visitInsn(POP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn("mapSwitch");
        mv.visitInsn(DokitExtUtil.getInstance().getCommConfig().mapSwitch ? ICONST_1 : ICONST_0);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
        mv.visitInsn(POP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn("networkSwitch");
        mv.visitInsn(DokitExtUtil.getInstance().getCommConfig().networkSwitch ? ICONST_1 : ICONST_0);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
        mv.visitInsn(POP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn("bigImgSwitch");
        mv.visitInsn(DokitExtUtil.getInstance().getCommConfig().bigImgSwitch ? ICONST_1 : ICONST_0);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
        mv.visitInsn(POP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn("methodSwitch");
        mv.visitInsn(DokitExtUtil.getInstance().getSlowMethodConfig().methodSwitch ? ICONST_1 : ICONST_0);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
        mv.visitInsn(POP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn("methodStrategy");
        mv.visitInsn(DokitExtUtil.getInstance().getSlowMethodConfig().strategy == STRATEGY_STACK ? ICONST_0 : ICONST_1);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
        mv.visitInsn(POP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/DokitPluginConfig", "inject", "(Ljava/util/Map;)V", false);

        super.onMethodEnter();
    }
}

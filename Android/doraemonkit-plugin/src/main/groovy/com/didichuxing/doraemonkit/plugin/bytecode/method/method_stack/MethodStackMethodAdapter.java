package com.didichuxing.doraemonkit.plugin.bytecode.method.method_stack;

import com.didichuxing.doraemonkit.plugin.MethodStackNode;
import com.didichuxing.doraemonkit.plugin.MethodStackNodeUtil;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by jint on 13/12/2019.
 */
public final class MethodStackMethodAdapter extends AdviceAdapter implements Opcodes {
    private String className;
    private String methodName;
    private String desc;
    private int level;
    /**
     * 函数耗时阈值
     */
    private int thresholdTime;
    /**
     * 是否属于静态方法
     */
    private boolean isStaticMethod;

    public MethodStackMethodAdapter(MethodVisitor methodVisitor, String className, int thresholdTime, int access, String methodName, String desc, int level) {
        super(Opcodes.ASM7, methodVisitor, access, methodName, desc);
        this.className = className;
        this.methodName = methodName;
        this.thresholdTime = thresholdTime;
        this.desc = desc;
        this.level = level;
        //access值得计算方式为 Opcodes.ACC_PUBLIC & Opcodes.ACC_STATIC
        this.isStaticMethod = (access & Opcodes.ACC_STATIC) != 0;
    }

    /**
     * @param opcode          操作指令
     * @param innerClassName  调用对象
     * @param innerMethodName 函数名
     * @param innerDesc       函数签名
     * @param isInterface     是否是接口
     */
    @Override
    public void visitMethodInsn(int opcode, String innerClassName, String innerMethodName, String innerDesc, boolean isInterface) {
        //全局替换URL的openConnection方法为dokit的URLConnection

        //普通方法 内部方法 静态方法
        if (opcode == Opcodes.INVOKEVIRTUAL || opcode == Opcodes.INVOKESTATIC || opcode == Opcodes.INVOKESPECIAL) {
            //过滤掉构造方法
            if (innerMethodName.equals("<init>")) {
                super.visitMethodInsn(opcode, innerClassName, innerMethodName, innerDesc, isInterface);
                return;
            }

            MethodStackNode methodStackNode = new MethodStackNode();
            methodStackNode.setClassName(innerClassName);
            methodStackNode.setMethodName(innerMethodName);
            methodStackNode.setDesc(innerDesc);
            methodStackNode.setParentClassName(className);
            methodStackNode.setParentMethodName(methodName);
            methodStackNode.setParentDesc(desc);
            switch (level) {
                case MethodStackNodeUtil.LEVEL_0:
                    methodStackNode.setLevel(MethodStackNodeUtil.LEVEL_1);
                    MethodStackNodeUtil.addFirstLevel(methodStackNode);
                    break;
                case MethodStackNodeUtil.LEVEL_1:
                    methodStackNode.setLevel(MethodStackNodeUtil.LEVEL_2);
                    MethodStackNodeUtil.addSecondLevel(methodStackNode);
                    break;
                case MethodStackNodeUtil.LEVEL_2:
                    methodStackNode.setLevel(MethodStackNodeUtil.LEVEL_3);
                    MethodStackNodeUtil.addThirdLevel(methodStackNode);
                    break;
                case MethodStackNodeUtil.LEVEL_3:
                    methodStackNode.setLevel(MethodStackNodeUtil.LEVEL_3);
                    MethodStackNodeUtil.addFourthlyLevel(methodStackNode);
                    break;

                case MethodStackNodeUtil.LEVEL_4:
                    methodStackNode.setLevel(MethodStackNodeUtil.LEVEL_3);
                    MethodStackNodeUtil.addFifthLevel(methodStackNode);
                    break;
                default:
                    break;
            }

        }
        super.visitMethodInsn(opcode, innerClassName, innerMethodName, innerDesc, isInterface);
    }


    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        try {
            if (isStaticMethod) {
                //静态方法需要插入的代码
                mv.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil", "getInstance", "()Lcom/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil;", false);
                mv.visitIntInsn(SIPUSH, thresholdTime);
                mv.visitInsn(level + ICONST_0);
                mv.visitLdcInsn(className);
                mv.visitLdcInsn(methodName);
                mv.visitLdcInsn(desc);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil", "recodeStaticMethodCostStart", "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);

            } else {
                //普通方法插入的代码
                mv.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil", "getInstance", "()Lcom/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil;", false);
                mv.visitIntInsn(SIPUSH, thresholdTime);
                mv.visitInsn(level + ICONST_0);
                mv.visitLdcInsn(className);
                mv.visitLdcInsn(methodName);
                mv.visitLdcInsn(desc);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil", "recodeObjectMethodCostStart", "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V", false);



            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        try {
            if (isStaticMethod) {
                //静态方法需要插入的代码
                mv.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil", "getInstance", "()Lcom/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil;", false);
                mv.visitIntInsn(SIPUSH, thresholdTime);
                mv.visitInsn(level + ICONST_0);
                mv.visitLdcInsn(className);
                mv.visitLdcInsn(methodName);
                mv.visitLdcInsn(desc);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil", "recodeStaticMethodCostEnd", "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);

            } else {
                //普通方法插入的代码
                mv.visitMethodInsn(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil", "getInstance", "()Lcom/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil;", false);
                mv.visitIntInsn(SIPUSH, thresholdTime);
                mv.visitInsn(level + ICONST_0);
                mv.visitLdcInsn(className);
                mv.visitLdcInsn(methodName);
                mv.visitLdcInsn(desc);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/didichuxing/doraemonkit/aop/method_stack/MethodStackUtil", "recodeObjectMethodCostEnd", "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

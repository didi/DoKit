package com.didichuxing.doraemonkit.plugin.bytecode;

import com.didichuxing.doraemonkit.plugin.DoKitExtUtil;
import com.didichuxing.doraemonkit.plugin.bytecode.method.slow.SlowMethodAdapter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Created by jint on 13/12/2019.
 * 类访问器
 * 生命周期
 * visit
 * visitSource?
 * visitOuterClass?
 * ( visitAnnotation | visitAttribute )*
 * ( visitInnerClass | visitField | visitMethod )*
 * visitEnd
 */
public final class DokitSlowMethodClassAdapter extends ClassVisitor {
    /**
     * 当前类型
     */
    private String className;


    /**
     * @param cv cv 传进来的是 ClassWriter
     */
    public DokitSlowMethodClassAdapter(final ClassVisitor cv) {
        super(Opcodes.ASM7, cv);
    }

    /**
     * Visits the header of the class.
     *
     * @param version    the class version. The minor version is stored in the 16 most significant bits,
     *                   and the major version in the 16 least significant bits.
     * @param access     the class's access flags (see {@link Opcodes}). This parameter also indicates if
     *                   the class is deprecated.
     * @param className  the internal name of the class (see {@link Type#getInternalName()}).
     * @param signature  the signature of this class. May be {@literal null} if the class is not a
     *                   generic one, and does not extend or implement generic classes or interfaces.
     * @param superName  the internal of name of the super class (see {@link Type#getInternalName()}).
     *                   For interfaces, the super class is {@link Object}. May be {@literal null}, but only for the
     *                   {@link Object} class.
     * @param interfaces the internal names of the class's interfaces (see {@link
     *                   Type#getInternalName()}). May be {@literal null}.
     */
    @Override
    public void visit(int version, int access, String className, String signature, String superName, String[] interfaces) {
        super.visit(version, access, className, signature, superName, interfaces);
        //过滤掉接口
        boolean isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
        if (isInterface) {
            return;
        }
        this.className = className;

    }


    /**
     * <p>
     * Visits a method of the class. This method <i>must</i> return a new {@link MethodVisitor}
     * instance (or {@literal null}) each time it is called, i.e., it should not return a previously
     * returned visitor.
     *
     * @param access     the method's access flags (see {@link Opcodes}). This parameter also indicates if
     *                   the method is synthetic and/or deprecated.
     * @param methodName the method's name.
     * @param desc       the method's descriptor (see {@link Type}).
     * @param signature  the method's signature. May be {@literal null} if the method parameters,
     *                   return type and exceptions do not use generic types.
     * @param exceptions the internal names of the method's exception classes (see {@link
     *                   Type#getInternalName()}). May be {@literal null}.
     * @return an object to visit the byte code of the method, or {@literal null} if this class
     * visitor is not interested in visiting the code of this method.
     */

    @Override
    public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions) {
        //从传进来的ClassWriter中读取MethodVisitor
        MethodVisitor mv = cv.visitMethod(access, methodName, desc, signature, exceptions);
        try {
            //插件开关被关闭
            if (!DoKitExtUtil.getInstance().dokitPluginSwitchOpen()) {
                return mv;
            }
            if (!DoKitExtUtil.getInstance().getSlowMethodExt().methodSwitch) {
                return mv;
            }

            if (DoKitExtUtil.getInstance().ignorePackageNames(className)) {
                return mv;
            }


            if (("<init>").equals(methodName)) {
                return mv;
            }

            boolean matchedMethod = false;

            for (String packageName : DoKitExtUtil.getInstance().getSlowMethodExt().normalMethod.packageNames) {
                if (className.contains(packageName)) {
                    if (DoKitExtUtil.getInstance().getSlowMethodExt().normalMethod.methodBlacklist.isEmpty()) {
                        matchedMethod = true;
                        break;
                    } else {
                        for (String blackStr : DoKitExtUtil.getInstance().getSlowMethodExt().normalMethod.methodBlacklist) {
                            //当前全路径类名不存在在黑名单中
                            if (!className.contains(blackStr)) {
                                matchedMethod = true;
                                break;
                            }
                        }
                    }
                }
            }

            if (matchedMethod) {
                System.out.println("DokitSlowMethod==className===>" + className + "   methodName===>" + methodName + "   thresholdTime==>" + DoKitExtUtil.getInstance().getSlowMethodExt().normalMethod.thresholdTime);
                return mv == null ? null : new SlowMethodAdapter(mv, className, DoKitExtUtil.getInstance().getSlowMethodExt().normalMethod.thresholdTime, access, methodName, desc);
            }

        } catch (Exception e) {
        }

        return mv;
    }


}
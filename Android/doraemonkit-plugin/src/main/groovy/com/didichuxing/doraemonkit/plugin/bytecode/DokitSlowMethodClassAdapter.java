package com.didichuxing.doraemonkit.plugin.bytecode;

import com.android.build.gradle.AppExtension;
import com.didichuxing.doraemonkit.plugin.DokitExtension;
import com.didichuxing.doraemonkit.plugin.bytecode.method.slow_method.SlowMethodAdapter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

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


    private boolean matchedMethod = false;
    private AppExtension appExtension;
    private DokitExtension dokitExtension;
    /**
     * 单位为500ms
     */
    private int thresholdTime = 500;

    /**
     * @param cv 传进来的是 ClassWriter
     */
    public DokitSlowMethodClassAdapter(final ClassVisitor cv, AppExtension appExtension, DokitExtension dokitExtension) {
        super(Opcodes.ASM7, cv);
        this.appExtension = appExtension;
        this.dokitExtension = dokitExtension;
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
//            System.out.println("isInterface===>" + "  access==>" + access + "  className==>" + className + "  signature===>" + signature + "  superName===>" + superName);
            return;
        }
        this.className = className;
        try {
            //需要将applicationId中的 .替换为/ 因为字节码中会把.转化为/
            String applicationId = appExtension.getDefaultConfig().getApplicationId().replaceAll("\\.", "/");
            boolean showMethodSwitch = true;
            List<String> packageNames = new ArrayList<>();
            if (dokitExtension != null) {
                showMethodSwitch = dokitExtension.slowMethodSwitch;
                packageNames = dokitExtension.packageNames;
                thresholdTime = dokitExtension.thresholdTime;
            } else {
                packageNames.add(applicationId);
            }

            if (showMethodSwitch) {
                for (String packageName : packageNames) {
                    packageName = packageName.replaceAll("\\.", "/");
                    if (className.contains(packageName)) {
                        matchedMethod = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("e====>" + e.getMessage());
        }


    }


    /**
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

            if (matchedMethod) {
                return mv == null ? null : new SlowMethodAdapter(mv, className, thresholdTime, access, methodName, desc);
            }

        } catch (Exception e) {
            System.out.println("DokitSlowMethodClassAdapter===>" + e.getMessage());
        }

        return mv;
    }

}
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


    boolean matchedMethod = false;
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

    @Override
    public void visit(int version, int access, String className, String signature, String superName, String[] interfaces) {
        super.visit(version, access, className, signature, superName, interfaces);
        this.className = className;
        //需要将applicationId中的 .替换为/ 因为字节码中会把.转化为/
        String applicationId = appExtension.getDefaultConfig().getApplicationId().replaceAll("\\.", "/");
        boolean showMethodSwitch = true;
        List<String> packageNames;
        if (dokitExtension != null) {
            showMethodSwitch = dokitExtension.slowMethodSwitch;
            packageNames = dokitExtension.packageNames;
            thresholdTime = dokitExtension.thresholdTime;
        } else {
            packageNames = new ArrayList<>();
            packageNames.add(applicationId);
        }

        if (packageNames == null) {
            packageNames = new ArrayList<>();
            packageNames.add(applicationId);
        } else if (packageNames.isEmpty()) {
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
        if (matchedMethod) {
            return new SlowMethodAdapter(mv, className, thresholdTime, access, methodName, desc);
        } else {
            return mv;
        }
    }

}
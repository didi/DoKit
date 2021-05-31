package com.didichuxing.doraemonkit.plugin.classtransformer

import com.didichuxing.doraemonkit.plugin.*
import com.didiglobal.booster.annotations.Priority
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.didiglobal.booster.transform.asm.className
import com.google.auto.service.AutoService
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.VarInsnNode

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/14-18:07
 * 描    述：wiki:https://juejin.im/post/5e8d87c4f265da47ad218e6b
 * 修订历史：
 * ================================================
 */
@Priority(1)
@AutoService(ClassTransformer::class)
class BigImgTransformer : AbsClassTransformer() {

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        if (onCommInterceptor(context, klass)) {
            return klass
        }


        if (!DoKitExtUtil.commExt.bigImgSwitch) {
            return klass
        }

        if (DoKitExtUtil.ignorePackageNames(klass.className)) {
            return klass
        }


        val className = klass.className
        //glide
        if (className == "com.bumptech.glide.request.SingleRequest") {
            klass.methods.find { methodNode ->
                (methodNode.name == "init" || methodNode.name == "<init>") && methodNode.desc != null
            }.let { methodNode ->
                //函数结束的地方插入
                methodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                    "${context.projectDir.lastPath()}->hook glide  succeed: ${className}_${methodNode.name}_${methodNode.desc}".println()
                    methodNode.instructions?.insertBefore(it, createGlideInsnList())
                }
            }
        }

        //picasso
        if (className == "com.squareup.picasso.Request") {
            klass.methods.find { methodNode ->
                methodNode.name == "<init>" && methodNode.desc != null
            }.let { methodNode ->
                //函数结束的地方插入
                methodNode?.instructions?.getMethodExitInsnNodes()?.forEach {
                    "${context.projectDir.lastPath()}->hook picasso  succeed: ${className}_${methodNode.name}_${methodNode.desc}".println()
                    methodNode.instructions?.insertBefore(it, createPicassoInsnList())
                }
            }
        }

        //Fresco
        if (className == "com.facebook.imagepipeline.request.ImageRequest") {
            klass.methods.find { methodNode ->
                methodNode.name == "<init>" && methodNode.desc != null
            }.let { methodNode ->
                "${context.projectDir.lastPath()}->hook Fresco succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                //函数开始的地方插入
                methodNode?.instructions?.insert(createFrescoInsnList())
            }
        }

        //ImageLoader
        if (className == "com.nostra13.universalimageloader.core.ImageLoadingInfo") {
            klass.methods.find { methodNode ->
                methodNode.name == "<init>" && methodNode.desc != null
            }.let { methodNode ->
                "${context.projectDir.lastPath()}->hook ImageLoader  succeed: ${className}_${methodNode?.name}_${methodNode?.desc}".println()
                methodNode?.instructions?.insert(createImageLoaderInsnList())
            }
        }


        return klass
    }

    /**
     * 创建Glide Aop代码指令
     */
    private fun createGlideInsnList(): InsnList {
        return with(InsnList()) {
            add(VarInsnNode(ALOAD, 0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/bigimg/glide/GlideHook",
                    "proxy",
                    "(Ljava/lang/Object;)V",
                    false
                )
            )
            this
        }
    }

    /**
     * 创建Picasso Aop代码指令
     */
    private fun createPicassoInsnList(): InsnList {
        return with(InsnList()) {
            add(VarInsnNode(ALOAD, 0))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/bigimg/picasso/PicassoHook",
                    "proxy",
                    "(Ljava/lang/Object;)V",
                    false
                )
            )
            this
        }

    }


    /**
     * 创建Fresco Aop代码指令
     */
    private fun createFrescoInsnList(): InsnList {
        return with(InsnList()) {
            add(VarInsnNode(ALOAD, 1))
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKEVIRTUAL,
                    "com/facebook/imagepipeline/request/ImageRequestBuilder",
                    "getSourceUri",
                    "()Landroid/net/Uri;",
                    false
                )
            )
            add(VarInsnNode(ALOAD, 1))
            add(
                MethodInsnNode(
                    INVOKEVIRTUAL,
                    "com/facebook/imagepipeline/request/ImageRequestBuilder",
                    "getPostprocessor",
                    "()Lcom/facebook/imagepipeline/request/Postprocessor;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/bigimg/fresco/FrescoHook",
                    "proxy",
                    "(Landroid/net/Uri;Lcom/facebook/imagepipeline/request/Postprocessor;)Lcom/facebook/imagepipeline/request/Postprocessor;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    INVOKEVIRTUAL,
                    "com/facebook/imagepipeline/request/ImageRequestBuilder",
                    "setPostprocessor",
                    "(Lcom/facebook/imagepipeline/request/Postprocessor;)Lcom/facebook/imagepipeline/request/ImageRequestBuilder;",
                    false
                )
            )
            this
        }

    }


    /**
     * 创建ImageLoader Aop代码指令
     */
    private fun createImageLoaderInsnList(): InsnList {
        return with(InsnList()) {
            add(VarInsnNode(ALOAD, 6))
            add(
                MethodInsnNode(
                    INVOKESTATIC,
                    "com/didichuxing/doraemonkit/aop/bigimg/imageloader/ImageLoaderHook",
                    "proxy",
                    "(Lcom/nostra13/universalimageloader/core/listener/ImageLoadingListener;)Lcom/nostra13/universalimageloader/core/listener/ImageLoadingListener;",
                    false
                )
            )
            add(VarInsnNode(ASTORE, 6))
            this
        }
    }
}


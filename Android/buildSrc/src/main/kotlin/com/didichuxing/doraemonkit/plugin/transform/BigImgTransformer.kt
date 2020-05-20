package com.didichuxing.doraemonkit.plugin.transform

import com.didichuxing.doraemonkit.plugin.DoKitExtUtil
import com.didichuxing.doraemonkit.plugin.methodExitInsnNode
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
@Priority(2)
@AutoService(ClassTransformer::class)
class BigImgTransformer : ClassTransformer {
    private val SHADOW_URL = "com/didichuxing/doraemonkit/aop/urlconnection/HttpUrlConnectionProxyUtil"
    private val DESC = "(Ljava/net/URLConnection;)Ljava/net/URLConnection;"

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        if (!DoKitExtUtil.dokitPluginSwitchOpen()) {
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
                methodNode?.instructions?.methodExitInsnNode().let {
                    methodNode?.instructions?.insertBefore(it, createGlideInsnList())
                }
            }
        }

        //picasso
        if (className == "com.squareup.picasso.Request") {
            klass.methods.find { methodNode ->
                methodNode.name == "<init>" && methodNode.desc != null
            }.let { methodNode ->
                //函数结束的地方插入
                methodNode?.instructions?.methodExitInsnNode().let {
                    methodNode?.instructions?.insertBefore(it, createPicassoInsnList())
                }
            }
        }

        //Fresco
        if (className == "com.facebook.imagepipeline.request.ImageRequest") {
            klass.methods.find { methodNode ->
                methodNode.name == "<init>" && methodNode.desc != null
            }.let { methodNode ->
                //函数开始的地方插入
                methodNode?.instructions?.insert(createFrescoInsnList())
            }
        }

        //ImageLoader
        if (className == "com.nostra13.universalimageloader.core.ImageLoadingInfo") {
            klass.methods.find { methodNode ->
                methodNode.name == "<init>" && methodNode.desc != null
            }.let { methodNode ->
                methodNode?.instructions?.insert(createImageLoaderInsnList())
            }
        }


        return klass
    }

    /**
     * 创建Glide Aop代码指令
     */
    private fun createGlideInsnList(): InsnList {
        val insnList = InsnList()
        insnList.add(VarInsnNode(ALOAD, 0))
        insnList.add(MethodInsnNode(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/bigimg/glide/GlideHook", "proxy", "(Ljava/lang/Object;)V", false))
        return insnList
    }

    /**
     * 创建Picasso Aop代码指令
     */
    private fun createPicassoInsnList(): InsnList {
        val insnList = InsnList()
        insnList.add(VarInsnNode(ALOAD, 0))
        insnList.add(MethodInsnNode(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/bigimg/picasso/PicassoHook", "proxy", "(Ljava/lang/Object;)V", false))
        return insnList
    }


    /**
     * 创建Fresco Aop代码指令
     */
    private fun createFrescoInsnList(): InsnList {
        val insnList = InsnList()
        insnList.add(VarInsnNode(ALOAD, 1))
        insnList.add(VarInsnNode(ALOAD, 1))
        insnList.add(MethodInsnNode(INVOKEVIRTUAL, "com/facebook/imagepipeline/request/ImageRequestBuilder", "getSourceUri", "()Landroid/net/Uri;", false))
        insnList.add(VarInsnNode(ALOAD, 1))
        insnList.add(MethodInsnNode(INVOKEVIRTUAL, "com/facebook/imagepipeline/request/ImageRequestBuilder", "getPostprocessor", "()Lcom/facebook/imagepipeline/request/Postprocessor;", false))
        insnList.add(MethodInsnNode(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/bigimg/fresco/FrescoHook", "proxy", "(Landroid/net/Uri;Lcom/facebook/imagepipeline/request/Postprocessor;)Lcom/facebook/imagepipeline/request/Postprocessor;", false))
        insnList.add(MethodInsnNode(INVOKEVIRTUAL, "com/facebook/imagepipeline/request/ImageRequestBuilder", "setPostprocessor", "(Lcom/facebook/imagepipeline/request/Postprocessor;)Lcom/facebook/imagepipeline/request/ImageRequestBuilder;", false))
        return insnList
    }


    /**
     * 创建ImageLoader Aop代码指令
     */
    private fun createImageLoaderInsnList(): InsnList {
        val insnList = InsnList()
        insnList.add(VarInsnNode(ALOAD, 6))
        insnList.add(MethodInsnNode(INVOKESTATIC, "com/didichuxing/doraemonkit/aop/bigimg/imageloader/ImageLoaderHook", "proxy", "(Lcom/nostra13/universalimageloader/core/listener/ImageLoadingListener;)Lcom/nostra13/universalimageloader/core/listener/ImageLoadingListener;", false))
        insnList.add(VarInsnNode(ASTORE, 6))
        return insnList
    }


}


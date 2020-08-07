package com.didichuxing.doraemonkit.kit.filemanager.convert

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.ContentConverter
import io.ktor.features.ContentNegotiation
import io.ktor.features.ContentTransformationException
import io.ktor.features.suitableCharset
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.request.contentCharset
import io.ktor.util.pipeline.PipelineContext
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.CopyableThrowable
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/7/16-18:05
 * 描    述：
 * 修订历史：
 * ================================================
 */
class GsonConverter(private val gson: Gson = Gson()) : ContentConverter {
    override suspend fun convertForSend(
            context: PipelineContext<Any, ApplicationCall>,
            contentType: ContentType,
            value: Any
    ): Any? {
        return TextContent(gson.toJson(value), contentType.withCharset(context.call.suitableCharset()))
    }

    override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
        val request = context.subject
        val channel = request.value as? ByteReadChannel ?: return null
        val reader = channel.toInputStream().reader(context.call.request.contentCharset()
                ?: Charsets.UTF_8)
        val type = request.type

        if (gson.isExcluded(type)) {
            throw ExcludedTypeGsonException(type)
        }

        return gson.fromJson(reader, type.javaObjectType) ?: throw UnsupportedNullValuesException()
    }
}

/**
 * Register GSON to [ContentNegotiation] feature
 */
fun ContentNegotiation.Configuration.gson(
        contentType: ContentType = ContentType.Application.Json,
        block: GsonBuilder.() -> Unit = {}
) {
    val builder = GsonBuilder()
    builder.apply(block)
    val converter = GsonConverter(builder.create())
    register(contentType, converter)
}

internal class ExcludedTypeGsonException(
        val type: KClass<*>
) : Exception("Type ${type.jvmName} is excluded so couldn't be used in receive"),
        CopyableThrowable<ExcludedTypeGsonException> {

    override fun createCopy(): ExcludedTypeGsonException? = ExcludedTypeGsonException(type).also {
        it.initCause(this)
    }
}

internal class UnsupportedNullValuesException :
        ContentTransformationException("Receiving null values is not supported")

private fun Gson.isExcluded(type: KClass<*>) =
        excluder().excludeClass(type.java, false)

package com.didichuxing.doraemonkit.plugin

import com.didiglobal.booster.task.spi.VariantProcessor
import com.didiglobal.booster.transform.Transformer
import org.gradle.api.Project
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.ServiceConfigurationError

//internal interface ServiceLoader<T> {
//    fun load(vararg args: Any): List<T>
//}

//internal class ServiceLoaderFactory<T>(private val classLoader: ClassLoader, private val service: Class<T>) {
//
//    fun newServiceLoader(vararg types: Class<*>) = object : ServiceLoader<T> {
//
//        @Suppress("UNCHECKED_CAST")
//        override fun load(vararg args: Any) = classLoader.getResources("META-INF/services/${service.name}")?.asSequence()?.map(::parse)?.flatten()?.toSet()?.map { provider ->
//            try {
//                val providerClass = Class.forName(provider, false, classLoader)
//                if (!service.isAssignableFrom(providerClass)) {
//                    throw ServiceConfigurationError("Provider $provider not a subtype")
//                }
//
//                try {
//                    providerClass.getConstructor(*types).newInstance(*args) as T
//                } catch (e: NoSuchMethodException) {
//                    providerClass.newInstance() as T
//                }
//            } catch (e: ClassNotFoundException) {
//                throw ServiceConfigurationError("Provider $provider not found")
//            }
//        } ?: emptyList()
//
//    }
//
//}
//
//internal inline fun <reified T> newServiceLoader(classLoader: ClassLoader, vararg types: Class<*>) = ServiceLoaderFactory(classLoader, T::class.java).newServiceLoader(*types)

/**
 * Load [Transformer]s with the specified [classLoader]
 */
//@Throws(ServiceConfigurationError::class)
//internal fun loadTransformers(classLoader: ClassLoader) = newServiceLoader<Transformer>(classLoader, ClassLoader::class.java).load(classLoader)

/**
 * Load [VariantProcessor]s with the specified [classLoader]
 */
//@Throws(ServiceConfigurationError::class)
//internal fun loadVariantProcessors(project: Project) = newServiceLoader<VariantProcessor>(project.buildscript.classLoader, Project::class.java).load(project)

@Throws(ServiceConfigurationError::class)
private fun parse(u: URL) = try {
    u.openStream().bufferedReader(StandardCharsets.UTF_8).readLines().filter {
        it.isNotEmpty() && it.isNotBlank() && !it.startsWith('#')
    }.map(String::trim).filter(::isJavaClassName)
} catch (e: Throwable) {
    emptyList<String>()
}

private fun isJavaClassName(text: String): Boolean {
    if (!Character.isJavaIdentifierStart(text[0])) {
        throw ServiceConfigurationError("Illegal provider-class name: $text")
    }

    for (i in 1 until text.length) {
        val cp = text.codePointAt(i)
        if (!Character.isJavaIdentifierPart(cp) && cp != '.'.toInt()) {
            throw ServiceConfigurationError("Illegal provider-class name: $text")
        }
    }

    return true
}
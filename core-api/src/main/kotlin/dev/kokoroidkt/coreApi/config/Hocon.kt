// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.coreApi.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigObject
import com.typesafe.config.ConfigRenderOptions
import dev.kokoroidkt.coreApi.annotation.WithComment
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import kotlinx.serialization.hocon.encodeToConfig
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

inline fun <reified T : Any> encodeHoconToString(data: T): String {
    val root = encodeHocon<T>(data)
    val clazz = data::class
    val comment = getComment(clazz)
    return (comment?.joinToString(separator = "\n") { "# $it" }?.plus("\n") ?: "") + root.render()
}

fun Config.render(): String {
    val option =
        ConfigRenderOptions
            .defaults()
            .setOriginComments(false)
            .setFormatted(true)
            .setJson(false)
            .setComments(true)
    return this.root().render(option)
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> encodeHocon(data: T): Config {
    val config: Config = Hocon.encodeToConfig<T>(data)
    var root = config.root()
    val clazz = data::class

    root = addCommentForElements(root, emptyList(), clazz)

    return root.toConfig()
}

internal inline fun <reified T : Any> encodeHoconToFile(
    data: T,
    file: File,
) {
    val hoconStr = encodeHoconToString<T>(data)
    file.parentFile?.mkdirs()
    file.writeText(hoconStr)
}

fun getComment(clazz: KClass<*>) =
    clazz.annotations
        .firstOrNull { it is WithComment }
        ?.let { it as WithComment }
        ?.comment

fun getComment(prop: KProperty<*>) =
    prop.annotations
        .firstOrNull { it is WithComment }
        ?.let { it as WithComment }
        ?.comment

internal fun Config.injectComment(
    pathSegments: List<String>,
    comment: List<String>,
    field: KProperty<*>? = null,
): Config {
    val path = if (field != null) (pathSegments + field.name).joinToString(".") else pathSegments.joinToString(".")
    val value = getValue(path)
    val newOrigin = value.origin().withComments(comment)
    val newValue = value.withOrigin(newOrigin)
    return withValue(path, newValue)
}

fun addCommentForElements(
    root: ConfigObject,
    pathSegments: List<String>,
    clazz: KClass<*>,
): ConfigObject {
    if (!clazz.isData) return root
    var result = root.toConfig()
    if (pathSegments.isNotEmpty()) {
        val comment = getComment(clazz)
        if (comment != null) {
            result = result.injectComment(pathSegments, comment.toList())
        }
    }
    for (field in clazz.memberProperties) {
        val comment = getComment(field)
        if (comment != null) {
            result = result.injectComment(pathSegments, comment.toList(), field)
        }

        result = addCommentForElements(result.root(), pathSegments + field.name, field.returnType.classifier as KClass<*>).toConfig()
    }
    return result.root()
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> decodeHoconFromHoconConfig(hoconConfig: Config): T = Hocon.decodeFromConfig<T>(hoconConfig)

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> decodeHoconString(hoconStr: String): T = Hocon.decodeFromConfig<T>(ConfigFactory.parseString(hoconStr))

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> decodeHoconFile(file: File): T = Hocon.decodeFromConfig<T>(ConfigFactory.parseFile(file))

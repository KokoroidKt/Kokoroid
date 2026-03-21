// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.classloader

import java.io.File
import java.io.IOException
import java.util.jar.JarFile

open class JarClassLoader(
    jarFile: File,
    parent: ClassLoader? = Thread.currentThread().contextClassLoader,
) : ClassLoader(parent) {
    private val jarFile = JarFile(jarFile)

    internal fun isExtensionCorrect(): Boolean = true

    override fun findClass(className: String): Class<*> {
        try {
            val entryName = className.replace('.', '/') + ".class"
            val entry =
                this@JarClassLoader.jarFile.getEntry(entryName) ?: return parent.loadClass(className)
            return this@JarClassLoader.jarFile.getInputStream(entry).use { input ->
                val bytes = input.readBytes()
                defineClass(className, bytes, 0, bytes.size)
            }
        } catch (e: IOException) {
            throw ClassNotFoundException("Failed to load class $className from JAR", e)
        } catch (e: ClassNotFoundException) {
            // 重新抛出父类加载器的 ClassNotFoundException
            throw ClassNotFoundException(
                "Failed to load class $className from JAR " +
                    "because cannot find this class from both JAR and parent",
                e,
            )
        }
    }
}

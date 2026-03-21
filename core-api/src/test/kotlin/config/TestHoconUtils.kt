// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.coreApi.test.config

import dev.kokoroidkt.coreApi.annotation.WithComment
import dev.kokoroidkt.coreApi.config.decodeHoconFile
import dev.kokoroidkt.coreApi.config.decodeHoconString
import dev.kokoroidkt.coreApi.config.encodeHoconToFile
import dev.kokoroidkt.coreApi.config.encodeHoconToString
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test
import java.io.File

@Serializable
@WithComment("blah")
data class IDK(
    @WithComment("hi")
    val idk: String,
    @WithComment("hihihi", "aaaa")
    val idk2: Int,
    val idk3: String,
    val anotherIDK: AnotherIDK,
    @WithComment("blah")
    val anotherIDK2: AnotherIDK2,
    val anotherIDK3: AnotherIDK2,
    @WithComment("blahblah")
    val anotherIDK4: AnotherIDK,
)

@Serializable
@WithComment("blah")
data class AnotherIDK(
    @WithComment("blahblahblah")
    val foo: String,
    val bar: Int,
)

@Serializable
data class AnotherIDK2(
    @WithComment("blahblahblah")
    val foo: String,
    val bar: Int,
)

class TestHoconUtils {
    val testData =
        IDK(
            idk = "idk",
            idk2 = 1,
            idk3 = "idk3",
            anotherIDK =
                AnotherIDK(
                    foo = "foo",
                    bar = 2,
                ),
            anotherIDK2 =
                AnotherIDK2(
                    foo = "foo2",
                    bar = 2,
                ),
            anotherIDK3 =
                AnotherIDK2(
                    foo = "foo3",
                    bar = 3,
                ),
            anotherIDK4 =
                AnotherIDK(
                    foo = "foo4",
                    bar = 4,
                ),
        )

    @Test
    fun testHoconEncoder() {
        val hocon = encodeHoconToString(testData)
        println("Will be decoded to:\n")
        println(hocon)
        val decode = decodeHoconString<IDK>(hocon)
        assert(decode == testData)
    }

    @Test
    fun testHoconEncoderFile() {
        val file = File("foo/bar/test.conf")
        encodeHoconToFile(testData, file)
        val decode = decodeHoconFile<IDK>(file)
        assert(decode == testData)
    }
}

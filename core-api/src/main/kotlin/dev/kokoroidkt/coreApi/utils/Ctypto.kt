package dev.kokoroidkt.coreApi.utils

import java.security.MessageDigest

internal fun sha256Fingerprint(input: String): String {
    val bytes = input.toByteArray()
    val digest = MessageDigest.getInstance("SHA-256")
    digest.update(bytes)
    val hashBytes = digest.digest()
    return hashBytes.joinToString("") { "%02x".format(it) }
}

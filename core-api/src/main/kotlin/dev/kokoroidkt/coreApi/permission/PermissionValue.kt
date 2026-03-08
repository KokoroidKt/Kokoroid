package dev.kokoroidkt.coreApi.permission

import jdk.jfr.ValueDescriptor

enum class PermissionValue(
    val description: String,
) {
    ALLOW("Allow"),
    DENY("Deny"),
    NOT_SET("NOT_SET"),
}

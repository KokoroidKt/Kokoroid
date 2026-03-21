package dev.kokoroidkt.coreApi.permission

interface PermissionManager {
    fun hasPermission(permission: Permission): Boolean
}

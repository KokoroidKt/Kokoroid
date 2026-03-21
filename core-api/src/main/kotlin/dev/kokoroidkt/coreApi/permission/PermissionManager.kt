package dev.kokoroidkt.coreApi.permission

interface PermissionManager {
    fun hasPermission(permissionItem: PermissionItem): Boolean
}

package dev.kokoroidkt.coreApi.permission

data class PermissionData(
    val permissionString: String,
    val permission: PermissionValue,
) {
    override fun toString(): String = "$permissionString: $permission"
}

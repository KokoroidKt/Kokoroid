package dev.kokoroidkt.coreApi.permission

class PermissionItem {
    val data: PermissionExtraData
    val namescape: String
    val node: String

    val checker: PermissionChecker

    private constructor(
        data: PermissionExtraData,
        namescape: String,
        node: String,
        checker: PermissionChecker,
    ) {
        this.data = data
        this.namescape = namescape
        this.node = node
        this.checker = checker
    }

    companion object {
        fun create(
            data: PermissionExtraData,
            namescape: String,
            node: String,
            checker: PermissionChecker = { _ -> true },
        ): PermissionItem = PermissionItem(data, namescape, node, checker)
    }

    fun verify(extraData: PermissionExtraData): Boolean = TODO("你还没做大傻缺")
}

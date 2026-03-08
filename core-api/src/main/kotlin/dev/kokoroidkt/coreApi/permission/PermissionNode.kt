package dev.kokoroidkt.coreApi.permission

class PermissionNode(
    val name: String = "",
    defaultValue: PermissionValue = PermissionValue.NOT_SET,
    val children: MutableMap<String, PermissionNode> = mutableMapOf(),
    val isWideMatch: Boolean = false,
    val isRecursiveMatch: Boolean = false,
) {
    var permission = defaultValue

    fun addChild(node: PermissionNode) = children.put(node.name, node)

    fun delChild(node: PermissionNode) = children.remove(node.name)

    operator fun get(nodeName: String) = children[nodeName]

    fun getNode(nodeName: String) = children[nodeName]

    fun getAllPermission(): List<String> {
        if (children.isEmpty()) {
            val result = mutableListOf(name)
            if (isWideMatch) {
                result.add("$name.*")
            }
            if (isRecursiveMatch) {
                result.add("$name.**")
            }
            return result
        }
        return children.values
            .flatMap { child ->
                child
                    .getAllPermission()
                    .map { it ->
                        "$name.$it"
                    }.toList()
            }
    }
}

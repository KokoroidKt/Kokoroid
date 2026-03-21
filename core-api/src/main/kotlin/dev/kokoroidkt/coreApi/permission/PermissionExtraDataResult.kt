package dev.kokoroidkt.coreApi.permission

sealed class PermissionExtraDataResult<T> {
    class KeyNotFound<T> : PermissionExtraDataResult<T>()

    class IsNull<T> : PermissionExtraDataResult<T>()

    class WrongType<T> : PermissionExtraDataResult<T>()

    class Success<T>(
        val value: T,
    ) : PermissionExtraDataResult<T>() {
        override fun getOrNull(): T? = value

        override val isSuccess: Boolean = true
    }

    /**
     * 是否获取成功
     * 只有Success时为True
     */
    open val isSuccess: Boolean = false

    /**
     * 在[isSuccess]时获取，其他时候为Null
     *
     * @return
     */
    open fun getOrNull(): T? = null
}

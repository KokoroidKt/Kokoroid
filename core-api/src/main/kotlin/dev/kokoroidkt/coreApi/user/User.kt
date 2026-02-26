/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.coreApi.user

abstract class User {
    /**
     * 唯一标识一个用户的ID
     * 推荐格式：平台ID@Adapter完全类名
     * 此Id不要与群组相关
     * 例如：如果平台有私聊/群聊，为了区分用户消息来自私聊还是群聊
     * 请定义一个新的字段来区分，而不是使用不同的userId
     */
    abstract val userId: String

    /**
     * 只要两个用户的[User.userId]是一样的，我们就认为他们是同一个人
     * 实现此类时需要调用超类的equals方法
     *
     * @param other
     * @return
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return userId == other.userId
    }

    override fun hashCode(): Int = userId.hashCode()
}

package dev.kokoroidkt.coreApi.user

interface UserContainer : Set<User> {
    fun getUserById(userId: String): User?
}

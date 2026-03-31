// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.user

object UserFactoryManager {
    typealias ExtensionId = String
    typealias UserFactoryFunc = (kokoroidUserId: String?, platfromUserId: String?) -> User?

    val factories = mutableMapOf<ExtensionId, UserFactoryFunc>()

    fun registerFactory(
        extensionId: ExtensionId,
        factory: UserFactoryFunc,
    ) {
        factories[extensionId] = factory
    }

    internal fun createUser(
        extensionId: ExtensionId,
        kokoroidUserId: String?,
        platfromUserId: String?,
    ): User? {
        val factory = factories[extensionId] ?: return null
        return factory(kokoroidUserId, platfromUserId)
    }

    @JvmName("createUserWithGeneric")
    internal inline fun <reified T : User> createUser(
        extensionId: ExtensionId,
        kokoroidUserId: String?,
        platfromUserId: String?,
    ): User? {
        val factory = factories[extensionId] ?: return null
        return factory(kokoroidUserId, platfromUserId) as T?
    }

    fun createUser(kokoroidUserId: String?): User? {
        kokoroidUserId ?: return null
        val items = kokoroidUserId.split("@")
        val internalUserId = items[0]
        val extensionId = items[1]
        return createUser(extensionId, kokoroidUserId, internalUserId)
    }

    @JvmName("createUserByFullIdWithGeneric")
    inline fun <reified T : User> createUser(kokoroidUserId: String?): T? = createUser(kokoroidUserId) as? T
}

package dev.kokoroidkt.coreApi.user

import dev.kokoroidkt.coreApi.database.tables.UserGroupTable
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID
import java.util.function.IntFunction
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class UserGroup private constructor(
    private val users: Users,
    val name: String,
) : List<User> by users {
    @OptIn(ExperimentalUuidApi::class)
    val uuid: Uuid = Uuid.random()

    @OptIn(ExperimentalUuidApi::class)
    val javaUuid: UUID = uuid.toJavaUuid()

    companion object {
        fun createSync(
            name: String,
            users: Users,
        ): UserGroup {
            val new = UserGroup(users, name)
            transaction {
                TODO()
            }
            return new
        }

        suspend fun create(
            name: String,
            users: Users,
        ): UserGroup {
            TODO()
        }
    }

    @Deprecated("Java-style toArray is not recommended in Kotlin.")
    override fun <T> toArray(generator: IntFunction<Array<out T?>?>): Array<out T?>? =
        generator.apply(users.size).also { array ->
            users.forEachIndexed { index, user ->
                @Suppress("UNCHECKED_CAST")
                (array as Array<T?>)[index] = user as T?
            }
        }
}

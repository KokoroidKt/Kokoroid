// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
// SPDX-License-Identifier: LGPL-2.1-or-later

@file:UseSerializers(PathSerializer::class)

package dev.kokoroidkt.core.config

import dev.kokoroidkt.core.constants.DefaultPaths
import dev.kokoroidkt.coreApi.annotation.WithComment
import dev.kokoroidkt.coreApi.config.PathSerializer
import dev.kokoroidkt.coreApi.database.DatabaseType
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.nio.file.Path

enum class ConversationSessionsStoreType {
    MUTEX,
    COW,
    // SHARDED_COW, // TODO 计划加入
}

@Serializable
@WithComment("会话性能设置")
data class Session(
    @WithComment(
        "会话表的并发实现策略",
        "MUTEX: Mutex + MutableList，默认值",
        "COW: Copy on Write，在你观察到 Mutex 竞争成为热点，使用COW",
        "<未实现，此值无效，计划中>SHARDED_COW: 分片Copy on Write，在你观察到 Mutex 竞争成为热点，且GC也成为你的开销时使用",
        "默认为MUTEX",
    )
    val storeType: ConversationSessionsStoreType,
    @WithComment(
        "如果会话表使用任何切片实现策略，切片的大小",
        "此值只有在会话表为切片类型（SHARDED_）时起效",
        "此值必须大于等于64",
    )
    val storeShardSize: Int,
)

@Serializable
@WithComment("全局事件分发性能设置")
data class Global(
    @WithComment(
        "是否在对话缓冲区满时，尝试重新投递事件",
        "虽然能保证事件不丢，这可能会导致协程意外挂起，导致全局循环受阻！",
        "默认为false",
    )
    val retryWhenBufferIsFull: Boolean,
    @WithComment(
        "全局缓冲区满时，最大重试次数",
        "此选项只有在retryWhenBufferIsFull=true时起效",
        "默认为100",
    )
    val maxRetryTimes: Int,
    @WithComment(
        "全局缓冲区满时，每次重试等待的时长",
        "此选项只有在retryWhenBufferIsFulltrue时起效",
        "默认为100",
    )
    val retryDelay: Long,
    @WithComment(
        "全局事件缓冲区大小",
        "默认为2048",
    )
    val bufferSize: Int,
)

@Serializable
@WithComment("Kokoroid 性能配置", "如果你不知道这是做什么的，请保持默认！")
data class PerformanceConfig(
    val global: Global,
    val session: Session,
    @WithComment("事件解码器最大协程数量", "默认为64")
    val eventDecoderMaxParallelism: Int,
) {
    private companion object {
        const val MIN_RETRY_DELAY_MS: Long = 50
    }

    init {
        if (global.retryWhenBufferIsFull) {
            require(global.maxRetryTimes > 0)
            { "retryOnGlobalEventBufferMaxRetryTimes must be > 0 when retry is enabled" }
            require(global.retryDelay >= MIN_RETRY_DELAY_MS)
            { "retryOnGlobalEventBufferRetryDelay must be >= $MIN_RETRY_DELAY_MS when retry is enabled" }
        }
        @Suppress("ktlint:standard:kdoc")
        when (session.storeType) {
            /** TODO
             ConversationSessionsStoreType.SHARDED_COW -> {
             require(session.storeShardSize >= 64)
             { "conversationSessionStoreShardSize must be >= 64 when using SHARDED_COW" }
             }
             **/

            else -> {} // do nothing
        }
    }
}

@Serializable
@WithComment("数据库配置", "如果你不知道这是做什么的，请保持默认！")
data class DatabaseConfig(
    @WithComment(
        "数据库类型",
        "支持SQLITE， H2， MYSQL， POSTGRESQL",
        "默认使用SQLITE",
        "H2建议只在测试时使用（内存数据库模式）",
    )
    val type: DatabaseType,
    @WithComment("jdbc连接字符串", "数据库名称请写在jdbc url中")
    val jdbc: String,
    @WithComment("数据库用户名", "SQLite/H2方式请忽略")
    val username: String,
    @WithComment("数据库密码", "SQLite/H2方式请忽略")
    val password: String,
)

@Serializable
@WithComment("Kokoroid 默认主配置文件")
data class BasicConfig(
    @WithComment("插件目录", "Kokoroid会在此目录下寻找插件")
    val pluginDirectory: Path,
    @WithComment("适配器目录", "Kokoroid会在此目录下寻找适配器")
    val adapterDirectory: Path,
    @WithComment("驱动器目录", "Kokoroid会在此目录下寻找驱动器")
    val driverDirectory: Path,
    val performance: PerformanceConfig,
    val database: DatabaseConfig,
) {
    init {
        pluginDirectory.toFile().mkdirs()
        adapterDirectory.toFile().mkdirs()
        driverDirectory.toFile().mkdirs()
    }

    companion object {
        fun createDefault(): BasicConfig =
            BasicConfig(
                pluginDirectory = DefaultPaths.DEFAULT_PLUGIN_DIRECTORY,
                adapterDirectory = DefaultPaths.DEFAULT_ADAPTER_DIRECTORY,
                driverDirectory = DefaultPaths.DEFAULT_DRIVER_DIRECTORY,
                performance =
                    PerformanceConfig(
                        global =
                            Global(
                                retryWhenBufferIsFull = false,
                                maxRetryTimes = 100,
                                retryDelay = 1000,
                                bufferSize = 2048,
                            ),
                        session =
                            Session(
                                storeType = ConversationSessionsStoreType.MUTEX,
                                storeShardSize = 64,
                            ),
                        eventDecoderMaxParallelism = 64,
                    ),
                database =
                    DatabaseConfig(
                        type = DatabaseType.SQLITE,
                        jdbc = "jdbc:sqlite:./kokoroid/datas/dev.kokoroid.core/data.db",
                        username = "nyanya",
                        password = "gulugulu",
                    ),
            )
    }
}

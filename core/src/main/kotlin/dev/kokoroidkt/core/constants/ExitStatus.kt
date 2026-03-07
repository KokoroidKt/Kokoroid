/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.constants

object ExitStatus {
    // 以下是所有正常退出

    /**
     * Kokoroid正常退出
     */
    const val SUCCESS_EXIT = 0

    // 以下是所有异常退出
    //
    // 异常退出∈[-1000, 0] */

    /**
     * 发生致命异常
     */
    const val CRITICAL_ERROR_EXIT = -1

    /**
     * 错误的最终状态（不是[dev.kokoroidkt.core.runtime.state.InternalState.Stopped]）
     */
    const val WRONG_EXIT_STATE = -2

    // 验证模式：验证失败退出∈[-2000, -1000)
    const val DRIVER_LOADED_FAILED = -1001
    const val PLUGIN_LOADED_FAILED = -1002
    const val ADAPTER_LOADED_FAILED = -1003
}

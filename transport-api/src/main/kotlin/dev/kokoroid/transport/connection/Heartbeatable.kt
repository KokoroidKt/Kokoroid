package dev.kokoroid.transport.connection

interface Heartbeatable {
    val delayMillisecond: Long

    suspend fun heartbeat()
}

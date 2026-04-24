package dev.kokoroidkt.transport.connection

interface Heartbeatable {
    val delayMillisecond: Long

    suspend fun heartbeat()
}

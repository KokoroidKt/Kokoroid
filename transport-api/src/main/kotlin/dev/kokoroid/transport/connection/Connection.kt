package dev.kokoroid.transport.connection

import dev.kokoroid.transport.decoder.Decoder

interface Connection {
    val state: ConnectionState

    fun registerDecoder(decoder: Decoder)

    suspend fun run()

    fun close()
}

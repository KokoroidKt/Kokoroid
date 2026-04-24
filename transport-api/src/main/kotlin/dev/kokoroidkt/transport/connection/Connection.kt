package dev.kokoroidkt.transport.connection

import dev.kokoroidkt.transport.decoder.Decoder

interface Connection {
    val state: ConnectionState

    fun registerDecoder(decoder: Decoder)

    suspend fun run()

    fun close()
}

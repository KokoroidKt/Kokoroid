package dev.kokoroidkt.transport.connection

object ConnectionManager {
    val connections: List<Connection>
        get() = _connections
    private val _connections = mutableListOf<Connection>()
    val size get() = _connections.size

    fun register(connection: Connection) {
        _connections.add(connection)
    }
}

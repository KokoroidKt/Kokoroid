package dev.kokoroidkt.coreApi.permission

import com.google.protobuf.Message
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.user.User

typealias PermissionChecker = (User, PermissionExtraData, Message?, Event?) -> Boolean

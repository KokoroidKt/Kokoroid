/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.coreApi.event

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.user.UserGroup
import dev.kokoroidkt.coreApi.user.special.NoUser
import java.time.Instant

/**
 * No user event
 *
 * @constructor
 *
 * @param eventId
 * @param timestamp
 */
abstract class NoUserEvent(
    eventId: String,
    timestamp: Instant,
    bot: Bot,
) : Event(eventId, timestamp, NoUser.NO_USER_GROUP, bot)

/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.coreApi.message

import kotlinx.serialization.json.Json

class MessageChain :
    List<MessageSegment>,
    TextConvertible {
    private constructor(messageSegmentList: List<MessageSegment>) {
        messages = messageSegmentList
    }

    private constructor(vararg segments: MessageSegment) {
        messages = listOf(*segments)
    }

    companion object {
        fun empty(): MessageChain = MessageChain()

        fun of(vararg segments: MessageSegment): MessageChain = MessageChain(segments.toList())

        fun of(segments: List<MessageSegment>): MessageChain = MessageChain(segments)
    }

    private var messages = listOf<MessageSegment>()
    override val size: Int
        get() = messages.size

    override fun isEmpty(): Boolean = messages.isEmpty()

    override fun contains(element: MessageSegment): Boolean = messages.contains(element)

    override fun iterator(): Iterator<MessageSegment> = messages.iterator()

    override fun containsAll(elements: Collection<MessageSegment>): Boolean = messages.containsAll(elements)

    override fun get(index: Int): MessageSegment = messages[index]

    override fun indexOf(element: MessageSegment): Int = messages.indexOf(element)

    override fun lastIndexOf(element: MessageSegment): Int = messages.lastIndexOf(element)

    override fun listIterator(): ListIterator<MessageSegment> = messages.listIterator()

    override fun listIterator(index: Int): ListIterator<MessageSegment> = messages.listIterator(index)

    override fun subList(
        fromIndex: Int,
        toIndex: Int,
    ): List<MessageSegment> = messages.subList(fromIndex, toIndex)

    fun toJson(): String = Json.encodeToString(this.messages.map { it.rawData }.toList())

    operator fun plus(segment: MessageSegment): MessageChain = MessageChain(messages + segment)

    operator fun plus(chain: MessageChain): MessageChain = MessageChain(messages + chain.messages)

    override fun toPlainText(): String {
        val sb = StringBuilder()
        sb.append(
            messages
                .mapNotNull {
                    (it as? TextConvertible)?.toPlainText()
                }.toList(),
        )
        return sb.toString()
    }

    override fun toString(): String = "MessageChain=[${messages.joinToString(", ")}]"
}

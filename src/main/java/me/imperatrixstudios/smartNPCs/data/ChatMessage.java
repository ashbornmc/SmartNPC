package me.imperatrixstudios.smartNPCs.data;

/**
 * An immutable data record representing a single message in a conversation history.
 * This structure mirrors the message format used by the OpenAI API.
 *
 * @param role The role of the message sender ("user" or "assistant").
 * @param content The actual text of the message.
 */
public record ChatMessage(String role, String content) {
}

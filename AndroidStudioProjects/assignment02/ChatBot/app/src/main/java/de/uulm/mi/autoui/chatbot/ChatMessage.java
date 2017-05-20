package de.uulm.mi.autoui.chatbot;

/**
 * Represents a chat message.
 *
 * Created by Luis on 11.05.2017.
 */
class ChatMessage {
    private String sender;
    private String text;

    public ChatMessage(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

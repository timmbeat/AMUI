package de.uulm.mi.autoui.chatbot;

import java.util.Random;

/**
 * Represents a chat message.
 *
 * Created by Luis on 11.05.2017.
 */
class ChatMessage {
    private String sender;
    private String text;
    //Added Id for identification for the message
    private String msgid;

    public ChatMessage(String sender, String text, String ID) {
        this.sender = sender;
        this.text = text;
        this.msgid = ID;
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

    public String getMsgid(){return msgid;}

    public void setMsgID() {

        msgid += "-" + String.format("%02d", new Random().nextInt(100));

    }
}

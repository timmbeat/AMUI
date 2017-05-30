package de.uulm.mi.autoui.chatbot;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static de.uulm.mi.autoui.chatbot.MainActivity.FILE_NAME;

/**
 * Created by Tim on 29.05.2017.
 * This class will load and save Messages in an internal file.
 */
public class MessageHandler {
    private static BufferedReader reader;
    private static List<ChatMessage> chatMessageList = new ArrayList<>();
    private static FileOutputStream outputStream;

    /**
     * This Method will load all previous saved Messages from an internal file.
     * @param context is need because openFileInput can only started from a context
     * @return returns a ArrayList
     */
    public static List<ChatMessage> loadMessages(Context context){
        try {
            FileInputStream input= context.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(input);
            reader = new BufferedReader(isr);
            String line = reader.readLine();
            while(line != null) {
                String[] message = line.split(":");
                chatMessageList.add(new ChatMessage(message[1], message[2], message[0]));
                line = reader.readLine();
            }
            reader.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return chatMessageList;
    }

    /**
     * This will save a Message in an internal File. Or Append it to others
     * @param message The message which should be saved
     * @param context The context from where openFileOutput can be called
     */
    public static void saveMessage(ChatMessage message, Context context){
        //Create String to read it out later
        String _message = message.getMsgid() + ":"
                + message.getSender() + ":" + message.getText() + "\n";
        try{
            outputStream = context.openFileOutput(FILE_NAME, Context.MODE_APPEND);
            outputStream.write(_message.getBytes());
            outputStream.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}

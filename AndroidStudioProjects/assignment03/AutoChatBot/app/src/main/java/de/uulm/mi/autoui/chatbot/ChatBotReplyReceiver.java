package de.uulm.mi.autoui.chatbot;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Random;

/**
 * Created by Tim Mend on 22.05.2017.
 * This class will handle all Reply intents for the Android Auto
 */

public class ChatBotReplyReceiver extends BroadcastReceiver {
    Random random;
    public static final String MESSAGE_AUTO_UI = "de.uulm.mi.autoui.chatbot.MESSAGE_AUTO_UI";

    /**
     * Should be called when the Android Head Unit or. the Android Auto App sends a Message from the
     * User. But it doesn´t.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        int thisConversationId = intent.getIntExtra("conversation_id", -1);
        random = new Random();
        CharSequence message = getMessageText(intent);
        //ToDo: Maybe find a better solution...but it works.
        MessageHandler.saveMessage(new ChatMessage("Me".toString(), message.toString(), "" + random.nextInt()),context);
       //intent.putExtra(MESSAGE_AUTO_UI, message.toString());
       //intent.setAction(MESSAGE_AUTO_UI);
        //intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
       //intent.setPackage("de.uulm.mi.autoui.chatbot");

    }

    /**
     * This Method will get the Message Text from the Android Head Unit. It will pass it forward
     * that it will get displayed in the Chat as a Message to the Chatbot
     * @param intent This is the Intent from the Android Auto Head Unit
     * @return Will return null when nothing was send from the User
     */
    private CharSequence getMessageText(Intent intent){
        Bundle remoteInput =
                RemoteInput.getResultsFromIntent(intent);
        if(remoteInput != null){
            return remoteInput.getCharSequence(MainActivity.KEY_REPLY_LABEL);
        }
        return null;
    }

}

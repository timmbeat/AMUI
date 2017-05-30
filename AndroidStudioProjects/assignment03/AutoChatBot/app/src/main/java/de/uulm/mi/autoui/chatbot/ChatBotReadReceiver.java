package de.uulm.mi.autoui.chatbot;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Tim  Mend on 22.05.2017.
 * This Class will handle all Read Intent for the Android Auto
 */

public class ChatBotReadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        int thisConversationId = intent.getIntExtra("conversation_id", -1);
    }


}

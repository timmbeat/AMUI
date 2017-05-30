package de.uulm.mi.autoui.chatbot;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_ID = 123;
    //private static final String TAG = "MainActivity";
    public static final String KEY_REPLY_LABEL = "reply_here";
    public static final String FILE_NAME = "de.uulm.mi.autoui.chatbot.CHAT_MESSAGES";
    private final String ACTION_REPLY = "de.uulm.mi.autoui.chatbot.MY_ACTION_MESSAGE_REPLY";
    private final String CATEGORY = "de.uulm.mi.autoui.chatbot";
    private final String VOICE_REPLY_KEY = "OKBOT";
    private List<ChatMessage> chatMessageList;
    ChatMessageAdapter messageAdapter;

    EditText editTextMessage;
    ImageView imageViewSendBtn;
    /**
     * saved in a external file, because shared preferences are not the right
     * place to save this amount of data
     */
    //SharedPreferences sharedPref;
    Random random;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        random = new Random();

        chatMessageList = MessageHandler.loadMessages(getApplicationContext());

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REPLY);
        filter.addCategory(CATEGORY);

        this.registerReceiver(new Receiver(), filter, null, null);

        File file = new File(this.getFilesDir(), FILE_NAME);
        if(!file.exists()) {
            file = new File(this.getFilesDir(), FILE_NAME);
        }
        //Load all saved messages
        if(!file.isDirectory() && file.exists()){
            chatMessageList = MessageHandler.loadMessages(getApplicationContext());
        }




        if (savedInstanceState != null) {
            List<String> senders = savedInstanceState.getStringArrayList("senders");
            List<String> messageTexts = savedInstanceState.getStringArrayList("messageTexts");

            if (senders != null && messageTexts != null) {
                for (int i = 0; i < senders.size(); i++) {
                    ChatMessage cm = new ChatMessage(senders.get(i), messageTexts.get(i),
                            "" + random.nextInt(1000));
                    cm.setMsgID();
                    chatMessageList.add(cm);
                }
            }
        }

        messageAdapter = new ChatMessageAdapter(
                this,
                android.R.layout.simple_list_item_2,
                chatMessageList
        );

        ListView listViewMessages = (ListView) findViewById(R.id.listViewMessages);
        listViewMessages.setAdapter(messageAdapter);

        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        imageViewSendBtn = (ImageView) findViewById(R.id.imageViewSendButton);

        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // is the text field empty?
                if (s.toString().isEmpty()) {
                    // disable the send button
                    imageViewSendBtn.setColorFilter(ContextCompat.getColor(
                            MainActivity.this, R.color.colorSendDisabled));
                    imageViewSendBtn.setClickable(false);
                } else {
                    // enable the send button
                    imageViewSendBtn.setColorFilter(ContextCompat.getColor(
                            MainActivity.this, R.color.colorPrimary));
                    imageViewSendBtn.setClickable(true);
                }
            }
        });


    }

    /**
     * Created by Tim Mend on 29.05.2017
     * This is a inner Class with a BroadcastReceiver which will get the Broadcast from the ChatBot
     * ReplyReceiver
     */
    public class Receiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent){
           CharSequence message = getMessageText(intent);
            System.out.println("Hallo");
            System.out.println(message);
            ChatMessage chatmessage = new ChatMessage("Me".toString(), message.toString() , " " + random.nextInt(1000));
            chatmessage.setMsgID();
            chatMessageList.add(chatmessage);
            messageAdapter.notifyDataSetChanged();

            new ChatResponseTask().execute(message.toString());
            editTextMessage.setText("");
        }

        /**
         * This Method will get the Message Text from the Android Head Unit. It will pass it forward
         * that it will get displayed in the Chat as a Message to the Chatbot
         * @param intent This is the Intent from the Android Auto Head Unit
         * @return Will return null when nothing was send from the User
         */
        private CharSequence getMessageText(Intent intent){
            Bundle remoteInput =
                    android.app.RemoteInput.getResultsFromIntent(intent);
            if(remoteInput != null){
                return remoteInput.getCharSequence(VOICE_REPLY_KEY);
            }
            return null;
        }
    }


    /**
     * Gets the text from the text field and adds it to the chatMessageList list
     * (see onClick attribute in layout/activity_main.xml)
     */
    public void sendMessage(View view) {
        String messageText = editTextMessage.getText().toString();
        ChatMessage chatMessage = new ChatMessage(
                getText(R.string.message_sender_me).toString(), messageText, "" + random.nextInt(1000));
        chatMessage.setMsgID();
        chatMessageList.add(chatMessage);
        MessageHandler.saveMessage(chatMessage, getApplicationContext());

        messageAdapter.notifyDataSetChanged();

        // start the AsyncTask for response
        new ChatResponseTask().execute(messageText);
        editTextMessage.setText("");
    }




    public ArrayList<String> getMessageTexts() {
        ArrayList<String> texts = new ArrayList<>();
        for (ChatMessage cm : chatMessageList) {
            texts.add(cm.getText());
        }
        return texts;
    }

    public ArrayList<String> getMessageSenders() {
        ArrayList<String> senders = new ArrayList<>();
        for (ChatMessage cm : chatMessageList) {
            senders.add(cm.getSender());
        }
        return senders;
    }




    /**
     *
     *
     *
    */
    private class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

        ChatMessageAdapter(Context context, int resource, List<ChatMessage> messages) {
            super(context, resource, messages);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                convertView = layoutInflater.inflate(android.R.layout.simple_list_item_2, null);
            }

            ChatMessage chatMessage = getItem(position);

            if (chatMessage != null) {

                TextView textViewMessage = (TextView) convertView.findViewById(android.R.id.text1);
                TextView textViewSender = (TextView) convertView.findViewById(android.R.id.text2);

                textViewMessage.setText(chatMessage.getText());
                textViewSender.setText(chatMessage.getSender());

                textViewSender.setTextColor(ContextCompat.getColor(MainActivity.this,
                        android.R.color.secondary_text_dark));

                // did the user send the message?
                if (chatMessage.getSender().equals(getString(R.string.message_sender_me))) {
                    // put the text to the right
                    textViewMessage.setGravity(Gravity.END);
                    textViewSender.setGravity(Gravity.END);
                } else {
                    // put the text on the left
                    textViewMessage.setGravity(Gravity.START);
                    textViewSender.setGravity(Gravity.START);
                }

            }

            return convertView;
        }
    }

    /**
    *
    *
    *
    *
     */
    private class ChatResponseTask extends AsyncTask<String, Void, String> {
        private Random random;
        @Override
        protected String doInBackground(String... params) {

            // wait for a 1-30 seconds
            int waitTimeSecs =  new Random().nextInt(30) + 1;
            try {
                Thread.sleep(waitTimeSecs * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // then pass the user's message to onPostExecute
            return params[0];
        }

        @Override
        protected void onPostExecute(String userMessage) {
            // use an incredibly sophisticated algorithm to figure out a smart response
            random = new Random();
            String responseText = findSuperSmartResponse(userMessage);
            String botSenderName = getText(R.string.message_sender_bot).toString();

            ChatMessage botResponse = new ChatMessage(
                    botSenderName, responseText, "" + random.nextInt(1000));
            botResponse.setMsgID();
            MessageHandler.saveMessage(botResponse, getApplicationContext());
            // add the message to the list
            chatMessageList.add(botResponse);
            messageAdapter.notifyDataSetChanged();




            // Creates an explicit intent for an Activity in your app
            Intent msgReadIntent = new Intent()
                    .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                    .setAction("de.uulm.mi.autoui.chatbot.MY_ACTION_MESSAGE_READ")
                    .putExtra("conversation_id", 0)
                    .setPackage("de.uulm.mi.autoui.chatbot");

            PendingIntent msgReadPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            0,
                            msgReadIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            //For Reply´s
            Intent msgReplyIntent = new Intent()
                    .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                    .setAction("de.uulm.mi.autoui.chatbot.MY_ACTION_MESSAGE_REPLY")
                    .putExtra("conversation_id", 0)
                    .setPackage("de.uulm.mi.autoui.chatbot");

            PendingIntent msgReplyPendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(),
                    0,
                    msgReplyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            String replyLabel = "Enter your Reply here";
            RemoteInput remote = new RemoteInput.Builder(VOICE_REPLY_KEY)
                    .setLabel(replyLabel)
                    .build();
            NotificationCompat.CarExtender.UnreadConversation.Builder unreadConvBuilder =
                    new NotificationCompat.CarExtender.UnreadConversation.Builder("Chatbot")
                            .setReadPendingIntent(msgReadPendingIntent)
                            .setReplyAction(msgReplyPendingIntent, remote);

            //Create Timestamp...required for the following code.
            //TODO: Clean that codegulasch up
            unreadConvBuilder.addMessage(userMessage)
                    .setLatestTimestamp(new Date().getTime());

            //Create Notification with actual information
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(MainActivity.this)
                            .setSmallIcon(R.drawable.ic_message_black_24dp)
                            .setContentTitle("New Message from '" + botSenderName + "'")
                            .setContentText(responseText)
                            .setContentIntent(msgReadPendingIntent)
                            .setContentIntent(msgReplyPendingIntent);

            //Extend the Conversation builder
            mBuilder.extend(new NotificationCompat.CarExtender().setUnreadConversation(unreadConvBuilder.build()));
            //ToDo: Do for msgReadPendingIntent????
            //mBuilder.setContentIntent(msgReplyPendingIntent);

            mBuilder.setAutoCancel(true);


            //ToDo: Drink a shot every time something "failed to open" in logcat, then die
            NotificationManagerCompat mNotificationManager =
                    NotificationManagerCompat.from(getApplicationContext());
            // mId allows you to update the notification later on.
            mNotificationManager.notify("Chatbot", NOTIFICATION_ID, mBuilder.build());
        }

        /**
         * best AI chat bot algorithm ever.
         * @param inputText the user's message
         * @return the bot's response
         */
        private String findSuperSmartResponse(String inputText) {
            String response = "";
            String lowerCaseText = inputText.toLowerCase();

            // flag to help decide what part of the response is suitable
            boolean greeting = false;

            if (lowerCaseText.contains("hello")
                    || lowerCaseText.contains("hi")
                    || lowerCaseText.contains("hey")) {
                response += "Hi, fellow human. I am totally not a robot. ";

                greeting = true;
            }
            // check if there is any text apart from the greeting
            if (!greeting) {
                if (lowerCaseText.contains("?")) {
                    response += "Interesting question! Maybe google it? ";
                } else if (!lowerCaseText.replace("hello", "").replace("hi", "").replace("hey","")
                        .replaceAll("[^A-Za-z]+", "").isEmpty()){
                    response += "Wow! You are really smart. And handsome. ";
                }
            }
            if (lowerCaseText.contains("bye") || lowerCaseText.contains("see you")) {
                response += "It was fun having a completely human conversation with you!";
            }

            return response;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList("senders", getMessageSenders());
        outState.putStringArrayList("messageTexts", getMessageTexts());
    }
}

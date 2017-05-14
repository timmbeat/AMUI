package de.uulm.mi.autoui.chatbot;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_ID = 123;

    private List<ChatMessage> chatMessageList;
    ChatMessageAdapter messageAdapter;

    EditText editTextMessage;
    ImageView imageViewSendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatMessageList = new ArrayList<ChatMessage>();

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
     * Gets the text from the text field and adds it to the chatMessageList list
     * (see onClick attribute in layout/activity_main.xml)
     */
    public void sendMessage(View view) {
        String messageText = editTextMessage.getText().toString();
        ChatMessage chatMessage = new ChatMessage(
                getText(R.string.message_sender_me).toString(), messageText);
        chatMessageList.add(chatMessage);

        messageAdapter.notifyDataSetChanged();

        // start the AsyncTask for response
        new ChatResponseTask().execute(messageText);

        editTextMessage.setText("");
    }


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

    private class ChatResponseTask extends AsyncTask<String, Void, String> {
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
            String responseText = findSuperSmartResponse(userMessage);
            String botSenderName = getText(R.string.message_sender_bot).toString();

            ChatMessage botResponse = new ChatMessage(
                    botSenderName, responseText);

            // add the message to the list
            chatMessageList.add(botResponse);
            messageAdapter.notifyDataSetChanged();

            // TODO the notification redirects to an empty "new" MainActivity
            // maybe persist messages using sharedPreferences?
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(MainActivity.this)
                            .setSmallIcon(R.drawable.ic_message_black_24dp)
                            .setContentTitle("New Message from '" + botSenderName + "'")
                            .setContentText(responseText);
            // Creates an explicit intent for an Activity in your app
            Intent intent = new Intent(MainActivity.this, MainActivity.class);

            PendingIntent pendingIntent =
                    PendingIntent.getActivity(
                            MainActivity.this,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(pendingIntent);

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
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
}

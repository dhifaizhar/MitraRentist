package id.rentist.mitrarentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.rentist.mitrarentist.adapter.MessageDetailAdapter;
import id.rentist.mitrarentist.modul.MessageDetailModul;
import id.rentist.mitrarentist.tools.SessionManager;

public class MessageDetailActivity extends AppCompatActivity {
    //LinearLayout layout;
    //ListView listOfMessages;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference, reference1, reference2;
    Toolbar toolbar;
    SessionManager sm;
    Intent detIntent;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    Map map1, map2;

    int row = 0;
    private List<MessageDetailModul> mMsg = new ArrayList<>();;
    private static final String TAG = "MessageAdapter";
    private static final String TOKEN = "secretissecret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_chat);

        detIntent = getIntent();
        sm = new SessionManager(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(detIntent.getStringExtra("phone"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //layout = (LinearLayout)findViewById(R.id.linear_layout_message);
        //listOfMessages = (ListView)findViewById(R.id.list_of_messages);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        Firebase.setAndroidContext(this);
        getDataMessage();
    }

    public void getDataMessage(){
        reference = new Firebase("https://rentistid-174904.firebaseio.com/messages/" + detIntent.getStringExtra("key"));
        reference1 = new Firebase("https://rentistid-174904.firebaseio.com/messages/" + detIntent.getStringExtra("key") + "/" + sm.getPreferences("hp"));
        reference2 = new Firebase("https://rentistid-174904.firebaseio.com/messages/" + detIntent.getStringExtra("key") + "/" + detIntent.getStringExtra("phone"));

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("id", String.valueOf(sm.getIntPreferences("id")));
                    map.put("date_send", DateFormat.getDateTimeInstance().format(new Date()));
                    map.put("message", messageText);
                    map.put("read", "0");

                    reference1.push().setValue(map);

                    addMessageBox();
                    messageArea.setText("");
                }
            }
        });

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ++row;
                Map map = dataSnapshot.getValue(Map.class);
                Log.e("Data Primary " + row, String.valueOf(map.values()));

                JSONArray jsonArray = new JSONArray(map.values());
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        String id = jsonobject.getString("id");
                        String date = jsonobject.getString("date_send");
                        String message = jsonobject.getString("message");
                        String read = jsonobject.getString("read");

                        MessageDetailModul msgModul = new MessageDetailModul();
                        msgModul.setMessageText(message);
                        msgModul.setMessageUser(detIntent.getStringExtra("phone"));
                        msgModul.setMessageTime(date);
                        msgModul.setType(row);

                        mMsg.add(msgModul);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.e("Data Chat " + row, String.valueOf(mMsg));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

//        reference1.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                map1 = dataSnapshot.getValue(Map.class);
//
//                String id = map1.get("id").toString();
//                String date = map1.get("date_send").toString();
//                String message = map1.get("message").toString();
//                String read = map1.get("read").toString();
//
//                MessageDetailModul msgModul = new MessageDetailModul();
//                msgModul.setMessageText(message);
//                msgModul.setMessageUser(detIntent.getStringExtra("phone"));
//                msgModul.setMessageTime(date);
//                msgModul.setType(1);
//
//                mMsg.add(msgModul);
//                addMessageBox();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });

//        reference2.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                map2 = dataSnapshot.getValue(Map.class);
//
//                String id = map2.get("id").toString();
//                String date = map2.get("date_send").toString();
//                String message = map2.get("message").toString();
//                String read = map2.get("read").toString();
//
//                MessageDetailModul msgModul = new MessageDetailModul();
//                msgModul.setMessageText(message);
//                msgModul.setMessageUser(detIntent.getStringExtra("phone"));
//                msgModul.setMessageTime(date);
//                msgModul.setType(2);
//
//                mMsg.add(msgModul);
//                addMessageBox();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
    }

    public void addMessageBox(){
        mRecyclerView = (RecyclerView) findViewById(R.id.dmsg_recyclerView);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mAdapter = new MessageDetailAdapter(getApplicationContext(),mMsg);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
//        TextView textView = new TextView(MessageDetailActivity.this);
//        textView.setText(message);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(0, 0, 0, 10);
//        textView.setLayoutParams(lp);
//
//        if(type == 1) {
//            textView.setBackgroundResource(R.drawable.rounded_corner1);
//            messageArea.setText("");
//        }
//        else{
//            textView.setBackgroundResource(R.drawable.rounded_corner2);
//        }
//
//        layout.addView(textView);
//        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        this.finish();
        return true;
    }
}

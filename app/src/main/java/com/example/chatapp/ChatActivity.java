package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etSentMessage;
    ImageView ivAttachment,ivSend;
    DatabaseReference rootRef;
    FirebaseAuth firebaseAuth;
    String currentuserId, chatuserId;
    private RecyclerView rvMessages;
    private SwipeRefreshLayout swlMessages;
    private MessagesAdapter messagesAdapter;
    private List<MessageModel> messageModelList;
    private int currentPage=1;
    private static final int RECORD_PER_PAGE = 30;

    private DatabaseReference databaseReferenceMessages;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        etSentMessage = findViewById(R.id.etMessage);
        ivSend = findViewById(R.id.ivSendMessage);

        rvMessages = findViewById(R.id.rvChatActivity);
        swlMessages = findViewById(R.id.swlChatActivity);

        ivSend.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        currentuserId = firebaseAuth.getCurrentUser().getUid();

        if(getIntent().hasExtra("user_key")){
            chatuserId = getIntent().getStringExtra("user_key");
        }

        messageModelList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this,messageModelList);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(messagesAdapter);

        loadMessages();
        rvMessages.scrollToPosition(messageModelList.size()-1);
        swlMessages.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                loadMessages();
            }
        });

    }

    private void sendMessage(String message,String messageType, String pushId){
            try {
                if(!message.equals("")){
                    HashMap hashMap = new HashMap();
                    hashMap.put(NodeNames.MESSAGE_ID,pushId);
                    hashMap.put(NodeNames.MESSAGE,message);
                    hashMap.put(NodeNames.MESSAGE_TYPE,messageType);
                    hashMap.put(NodeNames.MESSAGE_FROM,currentuserId);
                    hashMap.put(NodeNames.MESSAGE_TIME, ServerValue.TIMESTAMP);

                    String currentuserRef = NodeNames.MESSAGES+"/"+currentuserId+"/"+chatuserId;
                    String chatuserRef = NodeNames.MESSAGES+"/"+chatuserId+"/"+currentuserId;

                    HashMap messageuserMap = new HashMap();
                    messageuserMap.put(currentuserRef+"/"+pushId,hashMap);
                    messageuserMap.put(chatuserRef+"/"+pushId,hashMap);

                    etSentMessage.setText("");
                    rootRef.updateChildren(messageuserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if(databaseError!=null){
                                    Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                                }
                        }
                    });
                }



            }catch (Exception e){
                Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
            }
    }

    private void loadMessages(){
        messageModelList.clear();
        databaseReferenceMessages = rootRef.child(NodeNames.MESSAGES).child(currentuserId).child(chatuserId);

        Query messageQuery = databaseReferenceMessages.limitToLast(currentPage*RECORD_PER_PAGE);

        if(childEventListener!=null){
            messageQuery.removeEventListener(childEventListener);
        }
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                messageModelList.add(messageModel);
                messagesAdapter.notifyDataSetChanged();
                rvMessages.scrollToPosition(messageModelList.size()-1);   // it will start from botton messages in chat activity
                swlMessages.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                swlMessages.setRefreshing(false);
            }
        };

        messageQuery.addChildEventListener(childEventListener);
    }


    @Override
    public void onClick(View v) {
        AESAlgo aesAlgo = new AESAlgo();

        switch (v.getId()){
            case R.id.ivSendMessage:

                DatabaseReference userMessagePush = rootRef.child(NodeNames.MESSAGES).child(currentuserId).child(chatuserId).push();
                String pushId = userMessagePush.getKey();
                String normalMessage = etSentMessage.getText().toString();
                try {
                    String encryptedMessage = aesAlgo.encrypt(normalMessage);
                    sendMessage(encryptedMessage,Constants.MESSAGE_TYPE_TEXT,pushId);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to send message ", Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }

}

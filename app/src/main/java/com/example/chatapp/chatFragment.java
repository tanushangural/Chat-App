package com.example.chatapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class chatFragment extends Fragment {

    private RecyclerView rvChat;
    private View progressBar;
    private TextView tvChat;
    private ChatlistAdapter chatlistAdapter;
    private List<ChatlistModel> chatlistModelList;


    private DatabaseReference databaseReferenceChat,databaseReferenceUser;
    private FirebaseUser currentUser;

    private ChildEventListener childEventListener;
    private Query query;

    public chatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvChat = view.findViewById(R.id.rvChat);
        tvChat = view.findViewById(R.id.tvChat);

        chatlistModelList = new ArrayList<>();
        chatlistAdapter = new ChatlistAdapter(chatlistModelList, getActivity());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(linearLayoutManager);
        rvChat.setAdapter(chatlistAdapter);

        progressBar = view.findViewById(R.id.pbFragmentChat);
        databaseReferenceChat = FirebaseDatabase.getInstance().getReference().child(NodeNames.CHATS);
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        query = databaseReferenceChat.orderByChild(NodeNames.TIME_STAMP);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                updateList(dataSnapshot,true,dataSnapshot.getKey().toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                updateList(dataSnapshot,false,dataSnapshot.getKey().toString());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query.addChildEventListener(childEventListener);
        progressBar.setVisibility(View.VISIBLE);
        tvChat.setVisibility(View.VISIBLE);

    }

    // onchildAdded and onchildchanged have same function here thats why make a common method
    private void updateList(DataSnapshot dataSnapshot, boolean isNew, final String userId){

        progressBar.setVisibility(View.GONE);
        tvChat.setVisibility(View.GONE);

        final String  lastMessage,unreadCount, lastmessageTime;
        lastMessage = "";
        unreadCount = "";
        lastmessageTime = "";

        databaseReferenceUser.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String  username = dataSnapshot.child(NodeNames.NAME).getValue().toString()!=null?dataSnapshot.child(NodeNames.NAME).getValue().toString():"";
               String  photoname = dataSnapshot.child(NodeNames.PHOTO).getValue().toString()!=null?dataSnapshot.child(NodeNames.PHOTO).getValue().toString():"";

               ChatlistModel chatlistModel = new ChatlistModel(userId,username,lastMessage,unreadCount,lastmessageTime,photoname);
               chatlistModelList.add(chatlistModel);
               chatlistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(), "Failed to fetch Chat List", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        query.removeEventListener(childEventListener);
    }
}

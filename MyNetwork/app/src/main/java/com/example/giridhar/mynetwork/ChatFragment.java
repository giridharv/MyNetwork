package com.example.giridhar.mynetwork;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment implements AdapterView.OnItemClickListener {
 View v;
    ListView userlist;
    String chatUserName;
    ArrayAdapter<String> userListForChat;
    ArrayList<String> usernamesList = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    ArrayList<String>firebaseUsers = new ArrayList<>();
    ArrayList<String> chatKeyList = new ArrayList<>();
    FirebaseAuth firebaseAuth;
    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_chat, container, false);
        userlist =(ListView)v.findViewById(R.id.listForChatUsers);
        firebaseAuth=FirebaseAuth.getInstance();
       // Bundle bdl =getArguments();
        chatUserName= firebaseAuth.getCurrentUser().getDisplayName();
        //System.out.println(chatUserName);
        reference=FirebaseDatabase.getInstance().getReference("listOfChats");
        //reference =firebaseDatabase.getReference();
        initialise(reference);
        userListForChat= new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,usernamesList);
        userlist.setAdapter(userListForChat);
        userlist.setOnItemClickListener(this);
        //initialise(chatUserName);
        return v;
    }

    private void initialise(DatabaseReference reference)
    {
     reference.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
             retrieveUsers(dataSnapshot);
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {

         }
     });

    }

    private void retrieveUsers(DataSnapshot dataSnapshot)
    {
        ArrayList<String>keyList = new ArrayList<>();
       // System.out.println(dataSnapshot.getChildren());
        //System.out.println(dataSnapshot.getKey());
        ArrayList<String> KeyListForChat = new ArrayList<>();
        for(DataSnapshot data :dataSnapshot.getChildren())
        {
            keyList.add(data.getKey());
        }
        String first = new String();
        String second= new String();
        for(String each : keyList)
        {
            if(each.contains(chatUserName)) {
                String[] res = each.split(chatUserName);
                if (res.length > 1) {
                    //first = res[0];
                    second = res[1];
                    KeyListForChat.add(second);
                } else {
                    first = res[0];
                    KeyListForChat.add(first);

                }
            }

        }
   for(String s :KeyListForChat)
   {

       usernamesList.add(s);
   }
   userListForChat.notifyDataSetChanged();

 }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
       String clickeduser= parent.getItemAtPosition(position).toString();
       Bundle bdl =new Bundle();
        bdl.putString("currentuser",chatUserName);
        bdl.putString("clickeduser",clickeduser);
        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setArguments(bdl);
        FragmentManager fm =getFragmentManager();
        FragmentTransaction ft =fm.beginTransaction();
        ft.replace(R.id.fragHolderForChat,messageFragment);
        ft.commit();
    }
}

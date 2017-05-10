package com.example.giridhar.mynetwork;


import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements View.OnClickListener {
View v;
EditText etMessage;
    Button btSend;
    ListView messageList;
    ArrayAdapter<String> messageAdapter;
    ArrayList<String> messagesList;
    FirebaseDatabase firebaseDatabase;
    String currentuser,clickeduser,chatkey;
    DatabaseReference refobj;
    DatabaseReference reference;
    private DataCommunicationHelper.MessageCommunicator messageCommunicator;
    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_message, container, false);
        Bundle bdl =getArguments();
        clickeduser= bdl.getString("clickeduser");
        currentuser =bdl.getString("currentuser");
       // getActivity().setTitle(username);
        etMessage=(EditText)v.findViewById(R.id.editText9);
        btSend=(Button)v.findViewById(R.id.messageSend);
        btSend.setOnClickListener(this);
        messageList=(ListView)v.findViewById(R.id.listForChat);
        messagesList= new ArrayList<>();
        firebaseDatabase =FirebaseDatabase.getInstance();
        chatkey = createUniqueKeyForChat(currentuser,clickeduser);
        refobj = firebaseDatabase.getReference("listOfChats").child(chatkey);
        getAllMessages(refobj);
        //reference =firebaseDatabase.getReference("users");
       // String res= refobj.equalTo(chatkey).toString();


       // firebaseDatabase = FirebaseDatabase.getInstance();
        return v;
    }

    private void getAllMessages(DatabaseReference refobj)
    {
        refobj.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                retrieveMessages(dataSnapshot.getChildren());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void retrieveMessages(Iterable<DataSnapshot> children)
    {
        messagesList.clear();
        for(DataSnapshot data:children)
        {
            MessageHelper messages = data.getValue(MessageHelper.class);
            messagesList.add(messages.getSenderName() + " says " + messages.getMessage());
        }
        messageAdapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        messageAdapter= new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,messagesList);
        messageList.setAdapter(messageAdapter);
       // messageCommunicator = DataCommunicationHelper.appendMessageListener(chatkey, getContext());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.messageSend:
                sendMessage();
                break;
        }
    }

    public void sendMessage()
    {
        String messageToBeSent = etMessage.getText().toString();
        MessageHelper message = new MessageHelper();
        message.setMessage(messageToBeSent);
        message.setSenderName(currentuser);
        message.setRecieverName(clickeduser);
        //db.addMessageToFirebase(message,chatkey);
        String key = refobj.push().getKey();
        refobj.child(key).setValue(message);
        etMessage.setText("");
    }

    private void getMessageList(Iterable<DataSnapshot> children)
    {
        ArrayList<String > messagesListFromFire = new ArrayList<>();
        for(DataSnapshot data:children)
        {
            MessageHelper messageHelper = data.getValue(MessageHelper.class);
            messagesListFromFire.add(messageHelper.getSenderName() + messageHelper.getMessage());
            System.out.println(messageHelper.getSenderName() + " " + messageHelper.getSenderName());
        }
    }

    public String createUniqueKeyForChat(String current, String clicked)
    {
        String chatKey="";
        int resvalue= current.compareToIgnoreCase(clicked);
        if(resvalue < 0)
        {
            chatKey = current + clicked;
        }
        else
        {
            chatKey = clicked + current;
        }
        return chatKey;
    }

 //   @Override
//    public void onDestroy() {
//        super.onDestroy();
//        DataCommunicationHelper.stop((DataCommunicationHelper.MessageAddedIndicator) messageCommunicator);
//    }
}

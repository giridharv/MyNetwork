package com.example.giridhar.mynetwork;

import android.app.Notification;
import android.content.Context;
import android.os.Message;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by giridhar on 4/13/17.
 */

public class DataCommunicationHelper
{
    private static FirebaseDatabase firebaseInstance =FirebaseDatabase.getInstance();
    private  static DatabaseReference reference =firebaseInstance.getReference("lisOfChats");



    public void addMessageToFirebase(MessageHelper message,String chatKey)
    {
        Map<String,String> messageObject = new HashMap<>();
        messageObject.put("Message", message.getMessage());
        messageObject.put("Sender",message.getSenderName());
        messageObject.put("Reciever",message.getRecieverName());
        //messageObject.put("chatKey",chatKey);
        reference.child(chatKey).setValue(messageObject);
    }



    public static class MessageCommunicator implements ChildEventListener
    {
        MessageAddedIndicator messageAddedIndicator;
        MessageCommunicator(MessageAddedIndicator messageAddedIndicator)
        {
            this.messageAddedIndicator=messageAddedIndicator;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s)
        {
           HashMap<String ,String> addedMessage = new HashMap<>();
            MessageHelper messageHelper = new MessageHelper();
            messageHelper.setMessage(addedMessage.get("Message"));
            messageHelper.setSenderName(addedMessage.get("Sender"));
            messageHelper.setRecieverName(addedMessage.get("Reciever"));

            if(messageAddedIndicator==null)
            {
                //
            }
            else
            {
                messageAddedIndicator.checkMessageAdded(messageHelper);
            }

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
        public void onCancelled(DatabaseError databaseError) {

        }
    }
//    public static MessageCommunicator appendMessageListener(String chatkey, final Context messageAddedIndicator)
//    {
//        MessageCommunicator listener = new MessageCommunicator((MessageAddedIndicator) messageAddedIndicator);
//        reference.child(chatkey).addChildEventListener(listener);
//        return listener;
//    }

     public interface MessageAddedIndicator
     {
         public void checkMessageAdded(MessageHelper messageHelper);
     }
//    public static void stop(MessageAddedIndicator listener){
//        reference.removeEventListener((ChildEventListener) listener);
//    }

}

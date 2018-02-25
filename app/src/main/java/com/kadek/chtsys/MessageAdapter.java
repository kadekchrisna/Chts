package com.kadek.chtsys;

import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by kuro on 2/25/18.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    public MessageAdapter(List<Messages> mMessageList){
        this.mMessageList = mMessageList;
    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public TextView timeText;
        public TextView displayName;
        public CircleImageView profileImage;

        public MessageViewHolder(View view){
            super(view);

            messageText = (TextView)view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView)view.findViewById(R.id.message_profile_layout);
            displayName = (TextView)view.findViewById(R.id.name_text_layout);
            //timeText = (TextView)view.findViewById(R.id.message_layout)

        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder viewHolder, int i) {

        mAuth = FirebaseAuth.getInstance();

        String current_user_id = mAuth.getCurrentUser().getUid();

        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();

        if (from_user.equals(current_user_id)){

            viewHolder.messageText.setBackgroundColor(Color.WHITE);
            viewHolder.messageText.setTextColor(Color.BLACK);

        }else {

            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
            viewHolder.messageText.setTextColor(Color.WHITE);

        }
        viewHolder.messageText.setText(c.getMessage());
        //viewHolder.timeText.setText(c.getMessage());

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}

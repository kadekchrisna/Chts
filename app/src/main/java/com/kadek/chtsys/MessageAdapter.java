package com.kadek.chtsys;

import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by kuro on 2/25/18.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;

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
        public CircleImageView profileImage;

        public MessageViewHolder(View view){
            super(view);

            messageText = (TextView)view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView)view.findViewById(R.id.message_profile_layout);

            //timeText = (TextView)view.findViewById(R.id.message__layout)

        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder viewHolder, int i) {

        Messages c = mMessageList.get(i);
        viewHolder.messageText.setText(c.getMessage());
        //viewHolder.timeText.setText(c.getMessage());

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}

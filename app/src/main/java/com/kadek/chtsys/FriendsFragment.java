package com.kadek.chtsys;


import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendList;

    private DatabaseReference mFriendDatabase, mUserDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        mFriendList = (RecyclerView) mMainView.findViewById(R.id.friend_list);

        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);
        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));



        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

            Friends.class,
            R.layout.user_single_layout,
            FriendsViewHolder.class,
            mFriendDatabase

            ){
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Friends friends, int i) {
                friendsViewHolder.setDate(friends.getDate());

                String list_user_id = getRef(i).getKey();

                mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        String userStatus = dataSnapshot.child("status").getValue().toString();

                        if (dataSnapshot.hasChild("online")){
                            Boolean userOnline = (Boolean) dataSnapshot.child("online").getValue();
                            friendsViewHolder.setUserOnline(userOnline);
                        }


                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setImage(userThumb, getContext());
                        friendsViewHolder.setStatus(userStatus);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mFriendList.setAdapter(friendsRecyclerViewAdapter);


    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }
        public void setDate(String date){
            TextView userDate = (TextView) mView.findViewById(R.id.date_allusers);
            userDate.setVisibility(mView.VISIBLE);
            userDate.setText(date);
        }

        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.name_allusers);
            userNameView.setText(name);

        }
        public void setStatus(String status) {
            TextView mUserStatusView = (TextView) mView.findViewById(R.id.status_allusers);
            mUserStatusView.setText(status);
        }
        public void setImage(final String thumb_image, final Context ctx){

            final CircleImageView mUserImageView = (CircleImageView)mView.findViewById(R.id.circle_image_alluser);
            Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar_pic).into(mUserImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.avatar_pic).into(mUserImageView);

                }
            });

        }
        public void setUserOnline(boolean online_status ){

            ImageView userOnlineView = (ImageView)mView.findViewById(R.id.online_allusers);

            if (online_status == true){

                userOnlineView.setVisibility(View.VISIBLE);

            }else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }
}


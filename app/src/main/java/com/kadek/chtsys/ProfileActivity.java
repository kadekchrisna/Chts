package com.kadek.chtsys;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private TextView mProfileName, mProfileStatus, mProfileFriends;
    private ImageView mProfileImage;
    private Button mProfileReqButton, mProfileDecReqButton;

    private DatabaseReference mUsersDatabase, mFriendReqDatabase, mFriendDatabase, mNotificationDatabase;
    private FirebaseUser mCurrentUser;

    private ProgressDialog mProgressDialog;

    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

        mProfileName = (TextView) findViewById(R.id.profile_display_name);
        mProfileName.setText(user_id);

        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileStatus = (TextView) findViewById(R.id.profile_user_status);
        mProfileReqButton = (Button) findViewById(R.id.profile_req_friend_button);
        mProfileFriends = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileDecReqButton = (Button) findViewById(R.id.profile_decline_firend_button);

        mCurrent_state = "not_friend";



        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar_pic).into(mProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.avatar_pic).into(mProfileImage);
                    }
                });

                //--------------FIREND LIST / REQ FEATURE------------------

                mFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)) {

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (req_type.equals("recieved")) {

                                mProfileReqButton.setText("Accept Firend Request");
                                mCurrent_state = "req_recieved";
                                mProfileDecReqButton.setVisibility(View.VISIBLE);
                                mProfileDecReqButton.setEnabled(true);


                            } else if (req_type.equals("sent")) {

                                mProfileReqButton.setText("Cancel Firend Request");
                                mCurrent_state = "req_sent";

                                mProfileDecReqButton.setVisibility(View.INVISIBLE);
                                mProfileDecReqButton.setEnabled(false);

                            }
                            mProgressDialog.dismiss();
                        }else{

                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {


                                    if (dataSnapshot.hasChild(user_id)){

                                        mProfileReqButton.setText("Unfriend this Person");
                                        mCurrent_state = "friends";
                                        mProfileDecReqButton.setVisibility(View.INVISIBLE);
                                        mProfileDecReqButton.setEnabled(false);


                                    }
                                    mProgressDialog.dismiss();

                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    mProgressDialog.dismiss();

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mProgressDialog.dismiss();

                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

        //------------------------------ KEADAAN BELUM BERTEMAN -----------------------------
                mProfileReqButton.setEnabled(false);
                if (mCurrent_state.equals("not_friend")){

                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                mFriendReqDatabase.child(user_id).child(mCurrentUser.getUid()).child("request_type")
                                        .setValue("recieved").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String, String> notificationData = new HashMap<>();
                                        notificationData.put("from", mCurrentUser.getUid());
                                        notificationData.put("type", "request");

                                        mNotificationDatabase.child(user_id).push().setValue(notificationData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                mProfileReqButton.setText("Cancel Firend Request");
                                                mCurrent_state = "req_sent";

                                                mProfileDecReqButton.setVisibility(View.INVISIBLE);
                                                mProfileDecReqButton.setEnabled(false);

                                                Toast.makeText(ProfileActivity.this, "Friend Request Sent. ", Toast.LENGTH_LONG).show();

                                            }
                                        });
                                    }
                                });

                            }else {
                                Toast.makeText(ProfileActivity.this, "Failed Sending Request. ",Toast.LENGTH_LONG ).show();

                            }
                            mProfileReqButton.setEnabled(true);
                        }
                    });

                }
            //---------------Membatalakn Req Friend-----------------------
                if(mCurrent_state.equals("req_sent")){

                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendReqDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mProfileReqButton.setEnabled(true);
                                            mProfileReqButton.setText("Sent Friend Request");
                                            mCurrent_state = "not_friend";

                                            mProfileDecReqButton.setVisibility(View.INVISIBLE);
                                            mProfileDecReqButton.setEnabled(false);


                                        }
                                    });

                        }

                    });
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProfileReqButton.setEnabled(true);
                            Toast.makeText(ProfileActivity.this, "Cancel Sending Request Failed. ",Toast.LENGTH_LONG ).show();

                        }
                    });

                }
                //------ REQ RECIEVED STATE --------
                if (mCurrent_state.equals("req_recieved")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).child("date").setValue(currentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                  mFriendDatabase.child(user_id).child(mCurrentUser.getUid()).child("date").setValue(currentDate)
                                          .addOnSuccessListener(new OnSuccessListener<Void>() {
                                              @Override
                                              public void onSuccess(Void aVoid) {


                                                  mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue()
                                                          .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                              @Override
                                                              public void onSuccess(Void aVoid) {

                                                                  mFriendReqDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue()
                                                                          .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                              @Override
                                                                              public void onSuccess(Void aVoid) {

                                                                                  mProfileReqButton.setEnabled(true);
                                                                                  mProfileReqButton.setText("Unfriend this Person");
                                                                                  mCurrent_state = "friends";

                                                                                  mProfileDecReqButton.setVisibility(View.INVISIBLE);
                                                                                  mProfileDecReqButton.setEnabled(false);


                                                                              }
                                                                          });

                                                              }

                                                          });
                                                  mFriendReqDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnFailureListener(new OnFailureListener() {
                                                      @Override
                                                      public void onFailure(@NonNull Exception e) {
                                                          mProfileReqButton.setEnabled(true);
                                                          Toast.makeText(ProfileActivity.this, "Acceppting Request. ",Toast.LENGTH_LONG ).show();

                                                      }
                                                  });


                                              }
                                          });
                                }
                            });

                }

                if (mCurrent_state.equals("friends")){
                    mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileReqButton.setText("Sent Friend Request");
                                    mCurrent_state = "not_friend";
                                    mProfileReqButton.setEnabled(true);

                                    mProfileDecReqButton.setVisibility(View.INVISIBLE);
                                    mProfileDecReqButton.setEnabled(false);

                                }
                            });


                        }
                    });
                    mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           Toast.makeText(ProfileActivity.this, "Failed to Delete this Person. ",Toast.LENGTH_LONG).show();
                            mProfileReqButton.setEnabled(true);
                            mProfileDecReqButton.setVisibility(View.INVISIBLE);
                            mProfileDecReqButton.setEnabled(false);
                        }
                    });
                }
            }
        });
    }
}

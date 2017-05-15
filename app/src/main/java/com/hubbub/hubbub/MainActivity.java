package com.hubbub.hubbub;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.hubbub.hubbub.adapters.EventAdapter;
import com.hubbub.hubbub.models.Account;
import com.hubbub.hubbub.models.Event;
import com.hubbub.hubbub.models.Profile;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {
    private TextView mProfileNameText;
    private TextView mProfileHandleText;
    private CircleImageView mCircleView;

    private ListView mUpcomingEvents;
    private ArrayList<Event> slotsArray = new ArrayList<Event>();
    private EventAdapter adapter;

    private static final String TAG = "MainProfilePageActivity";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProfileNameText = (TextView) findViewById(R.id.profile_full_name);
        mProfileHandleText = (TextView) findViewById(R.id.profile_handle);
        mCircleView = (CircleImageView) findViewById(R.id.profile_photo);

        mUpcomingEvents = (ListView) findViewById(R.id.upcoming_events);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextScreen = new Intent(MainActivity.this, JoinEvent.class);
                startActivity(nextScreen);
            }
        });
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        final String userId = getUid();
        setUpListView();
        DatabaseReference profileRef = mDatabase.child("profiles").child(userId);
        profileRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);

                        if (profile != null) {
                            mProfileNameText.setText(profile.name);
                            mProfileHandleText.setText(profile.handle);
                            downloadAndSetPhoto(profile.photo);
                        }
                    }

                    private void downloadAndSetPhoto(String photo_url) {
                        Picasso.with(getApplicationContext()).load(photo_url)//download URL
                            .placeholder(R.drawable.placeholder_bub)//use default image
                            .error(R.drawable.placeholder_bub)//if failed
                            .into(mCircleView);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                }
        );
        mDatabase.child("accounts").child(userId).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // switch to account
                        Account account = dataSnapshot.getValue(Account.class);

                        if (account == null) {
                            Log.e(TAG, "User's " + userId + "account is unexpectedly null");
                            Toast.makeText(MainActivity.this,
                                    "Error: could not fetch user's account.",
                                    Toast.LENGTH_SHORT).show();
                        } if (account.events != null) {
                            slotsArray.clear();
                            slotsArray.addAll(account.events.values());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getAccount:onCancelled", databaseError.toException());
                    }
                }
        );
    }

    private void setUpListView() {
        adapter = new EventAdapter(this,
                R.layout.event_view, slotsArray);
        mUpcomingEvents.setAdapter(adapter);
    }

}

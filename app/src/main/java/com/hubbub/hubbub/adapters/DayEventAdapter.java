package com.hubbub.hubbub.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hubbub.hubbub.JoinEvent;
import com.hubbub.hubbub.MainActivity;
import com.hubbub.hubbub.R;
import com.hubbub.hubbub.models.Account;
import com.hubbub.hubbub.models.Event;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.hubbub.hubbub.WebViewActivity.JSON;

/**
 * Created by sgoldblatt on 5/4/17.
 */

public class DayEventAdapter extends ArrayAdapter<HashMap.Entry<String, ArrayList<Event>>> {
    private OkHttpClient client;
    Context context;
    int layoutResourceId;
    DatabaseReference mDatabase;
    FirebaseUser mUser;
    String mUserUid;

    ArrayList<String> eventsUserIsPending = new ArrayList<String>();

    private static final String TAG = "DayEvent";

    ArrayList<HashMap.Entry<String, ArrayList<Event>>> data = null;

    SimpleDateFormat day = new SimpleDateFormat("dd");
    SimpleDateFormat weekday = new SimpleDateFormat("EEE");
    SimpleDateFormat timeAM_PM = new SimpleDateFormat("h a");

    // HashmapEntry is a single Key Value pair gotten
    public DayEventAdapter(Context context, int resource, ArrayList<HashMap.Entry<String, ArrayList<Event>>> objects) {
        super(context, resource, objects);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = objects;

        mUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        client = new OkHttpClient(); //TODO (is this a good idea, does this mean there is a new http client for each object)

        fillUserPendingEvents();
    }

    private void fillUserPendingEvents() {
        mDatabase.child("accounts").child(mUserUid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // switch to account
                        Account account = dataSnapshot.getValue(Account.class);
                        if (account != null && account.events != null) {
                            eventsUserIsPending.clear();
                            for (Event event : account.events.values()) {
                                eventsUserIsPending.add(event.id);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getAccount:onCancelled", databaseError.toException());
                    }
                }
        );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DayEventAdapter.DayEventHolder holder = null;
        HashMap.Entry<String, ArrayList<Event>> item = data.get(position);

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new DayEventAdapter.DayEventHolder();
            holder.day = (TextView) row.findViewById(R.id.day);
            holder.weekday = (TextView) row.findViewById(R.id.weekday);
            holder.individual_events = (LinearLayout) row.findViewById(R.id.individual_events);
            // DO THE INDIVIDUAL EVENTS HERE.
            dynamicallyAddEvents(holder.individual_events, item.getValue());

            row.setTag(holder);
        }
        else
        {
            holder = (DayEventAdapter.DayEventHolder) row.getTag();
        }


        holder.day.setText(day.format(getDate(item)));
        holder.weekday.setText(weekday.format(getDate(item)));


        return row;
    }

    private String makeRequestJson(Event event) {
        JSONObject jObject = new JSONObject();
        try {
            jObject.put("id", event.id);
            jObject.put("userId", mUserUid);
        } catch (JSONException e) {
            // TODO (fix this to not throw an error? Like why even)
            e.printStackTrace();
        }

        return jObject.toString();
    }

    private void run(final Event event, final boolean willAttend, final Callback callback) {
        mUser.getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            String url = context.getString(R.string.hubbub_functions_url) + (willAttend ? "/joinEvent" : "/leaveEvent");
                            RequestBody body = RequestBody.create(JSON, makeRequestJson(event));
                            Request request = new Request.Builder()
                                    .url(url)
                                    .post(body)
                                    .header("Authorization", "Bearer " + idToken)
                                    .build();

                            Call call = client.newCall(request);
                            call.enqueue(callback);
                        }
                    };

                });
    }

    private void dynamicallyAddEvents(LinearLayout layout, ArrayList<Event> events) {
        for(final Event event : events) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            View child = inflater.inflate(R.layout.individual_event, null);

            TextView individualNameView = (TextView) child.findViewById(R.id.name);
            TextView individualTimeView = (TextView) child.findViewById(R.id.timeStart);
            TextView individualPlaceView = (TextView) child.findViewById(R.id.location);
            final CheckBox checkBox = (CheckBox) child.findViewById(R.id.checkbox_meat);

            individualNameView.setText(event.name);
            individualTimeView.setText(timeAM_PM.format(new Date(event.startAt)));
            individualPlaceView.setText(event.location);

            final boolean[] userInEvent = {eventsUserIsPending.contains(event.id)};
            Log.d(TAG, "is user in event? " + userInEvent[0]);
            checkBox.setChecked(userInEvent[0]);
            
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    run(event, !userInEvent[0], new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "ERROR: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                Log.d(TAG, response.request().toString());
                                userInEvent[0] = userInEvent[0];
                            }
                        }
                    });
                }
            });

            layout.addView(child);
        }
    }

    private Date getDate(HashMap.Entry<String, ArrayList<Event>> dayWithEvents) {
        ArrayList<Event> slotsForDay = dayWithEvents.getValue();
        if (slotsForDay.size() > 0) {
            return new Date(slotsForDay.get(0).startAt * 1000);
        } else {
            // Shouldn't get to this point, but return current date
            return new Date();
        }
    }

    private class DayEventHolder {

        public TextView day;
        public TextView weekday;
        public LinearLayout individual_events;

        private  DayEventHolder( ) {}
    }
}

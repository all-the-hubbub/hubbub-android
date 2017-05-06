package com.hubbub.hubbub.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hubbub.hubbub.R;
import com.hubbub.hubbub.models.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by sgoldblatt on 5/4/17.
 */

public class DayEventAdapter extends ArrayAdapter<HashMap.Entry<String, ArrayList<Event>>> {
    Context context;
    int layoutResourceId;
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
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DayEventAdapter.DayEventHolder holder = null;
        HashMap.Entry<String, ArrayList<Event>> item = data.get(position);

        if(row == null)
        {
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

    public void onCheckboxClicked() {
        System.out.print("click");
    }

    private void dynamicallyAddEvents(LinearLayout layout, ArrayList<Event> events) {
        for(Event event : events) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            View child = inflater.inflate(R.layout.individual_event, null);
            TextView individualNameView = (TextView) child.findViewById(R.id.name);
            TextView individualTimeView = (TextView) child.findViewById(R.id.timeStart);
            TextView individualPlaceView = (TextView) child.findViewById(R.id.location);
            CheckBox checkBox = (CheckBox) child.findViewById(R.id.checkbox_meat);

            individualNameView.setText(event.name);
            individualTimeView.setText(timeAM_PM.format(new Date(event.startAt)));
            individualPlaceView.setText(event.location);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("send of the http request here");
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

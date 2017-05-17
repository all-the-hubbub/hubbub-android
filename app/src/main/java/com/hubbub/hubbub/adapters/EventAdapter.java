package com.hubbub.hubbub.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hubbub.hubbub.BeaconViewActivity;
import com.hubbub.hubbub.LoginActivity;
import com.hubbub.hubbub.MainActivity;
import com.hubbub.hubbub.R;
import com.hubbub.hubbub.models.Event;
import com.hubbub.hubbub.models.Topic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by sgoldblatt on 5/4/17.
 */

public class EventAdapter extends ArrayAdapter<Event> {
    Context context;
    int layoutResourceId;
    ArrayList<Event> data = null;
    SimpleDateFormat day = new SimpleDateFormat("dd");
    SimpleDateFormat weekday = new SimpleDateFormat("EEE");
    SimpleDateFormat timeAM_PM = new SimpleDateFormat("h:mm a");

    public EventAdapter(Context context, int resource, ArrayList<Event> objects) {
        super(context, resource, objects);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = (ArrayList) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SlotHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new SlotHolder();
            holder.day = (TextView) row.findViewById(R.id.day);
            holder.weekday = (TextView) row.findViewById(R.id.weekday);
            holder.timeStart = (TextView) row.findViewById(R.id.timeStart);
            holder.location = (TextView)row.findViewById(R.id.location);
            holder.state = (TextView)row.findViewById(R.id.state);
            holder.name = (TextView)row.findViewById(R.id.name);

            row.setTag(holder);
        }
        else
        {
            holder = (SlotHolder) row.getTag();
        }

        final Event item = data.get(position);
        holder.day.setText(day.format(new Date(item.startAt * 1000)));
        holder.weekday.setText(weekday.format(new Date(item.startAt * 1000)));
        holder.timeStart.setText(timeAM_PM.format(new Date(item.startAt * 1000)));

        holder.location.setText(item.location);
        holder.name.setText(item.name);
        setTopic(row, holder, item);
        return row;
    }

    private void setTopic(View row, SlotHolder holder, final Event item) {
        if (item.topic != null) {
            final HashMap<String, String> event_topic = item.topic;
            holder.state.setText(event_topic.get("name"));
            holder.state.setBackgroundColor(ContextCompat.getColor(row.getContext(),
                    R.color.colorAccentTransparent));
            holder.state.setTextColor(ContextCompat.getColor(row.getContext(),
                    R.color.colorFontTransparent));
            holder.state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), BeaconViewActivity.class);
                    intent.putExtra("slotId", item.id);
                    intent.putExtra("topicId", event_topic.get("id"));
                    intent.putExtra("topicName", event_topic.get("name"));
                    v.getContext().startActivity(intent);
                    ((Activity)context).finish();
                }
            });
        } else {
            holder.state.setBackgroundColor(ContextCompat.getColor(row.getContext(), R.color.grey_100));
            holder.state.setTextColor(ContextCompat.getColor(row.getContext(), R.color.font_dark));
            holder.state.setOnClickListener(null);
            holder.state.setText("pending...");
        }
    }

    private class SlotHolder {
        
        public TextView day;
        public TextView weekday;
        public TextView timeStart;
        public TextView state;
        public TextView location;
        public TextView name;

        private  SlotHolder( ) {}
    }
}

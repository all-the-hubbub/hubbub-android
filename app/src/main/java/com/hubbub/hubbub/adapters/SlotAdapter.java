package com.hubbub.hubbub.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hubbub.hubbub.R;
import com.hubbub.hubbub.models.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sgoldblatt on 5/4/17.
 */

public class SlotAdapter extends ArrayAdapter<Event> {
    Context context;
    int layoutResourceId;
    ArrayList<Event> data = null;
    SimpleDateFormat day = new SimpleDateFormat("dd");
    SimpleDateFormat weekday = new SimpleDateFormat("EEE");
    SimpleDateFormat timeAM_PM = new SimpleDateFormat("h a");

    public SlotAdapter(Context context, int resource, ArrayList<Event> objects) {
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
            holder.id = (TextView)row.findViewById(R.id.id);
            holder.name = (TextView)row.findViewById(R.id.name);

            row.setTag(holder);
        }
        else
        {
            holder = (SlotHolder) row.getTag();
        }

        Event item = data.get(position);
        holder.day.setText(day.format(new Date(item.startAt * 1000)));
        holder.weekday.setText(weekday.format(new Date(item.startAt * 1000)));
        holder.timeStart.setText(timeAM_PM.format(new Date(item.startAt * 1000)));

        holder.location.setText(item.location);
        holder.id.setText(item.id);
        holder.name.setText(item.name);

        return row;
    }

    private class SlotHolder {
        
        public TextView day;
        public TextView weekday;
        public TextView timeStart;
        public TextView id;
        public TextView location;
        public TextView name;

        private  SlotHolder( ) {}
    }
}

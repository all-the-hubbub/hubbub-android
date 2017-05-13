package com.hubbub.hubbub.holders;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hubbub.hubbub.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class EventHolder extends RecyclerView.ViewHolder {

    SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEE");
    SimpleDateFormat timeFormat = new SimpleDateFormat("h a");

    private TextView day;
    private TextView weekday;
    private TextView timeStart;
    private TextView state;
    private TextView location;
    private TextView name;

    public EventHolder(View view) {
        super(view);
        day = (TextView) view.findViewById(R.id.day);
        weekday = (TextView) view.findViewById(R.id.weekday);
        timeStart = (TextView) view.findViewById(R.id.timeStart);
        location = (TextView)view.findViewById(R.id.location);
        state = (TextView)view.findViewById(R.id.state);
        name = (TextView)view.findViewById(R.id.name);
    }

    public void setDay(long startAt) {
        day.setText(dayFormat.format(new Date(startAt * 1000)));
    }

    public void setWeekday(long startAt) {
        weekday.setText(weekdayFormat.format(new Date(startAt * 1000)));
    }

    public void setTimeStart(long startAt) {
        timeStart.setText(timeFormat.format(new Date(startAt * 1000)));
    }

    public void setLocation(String locationInput) {
        location.setText(locationInput);
    }

    public void setName(String nameInput) {
        name.setText(nameInput);
    }

    public void setState(String stateInput) {
        state.setText(stateInput);
    }
}
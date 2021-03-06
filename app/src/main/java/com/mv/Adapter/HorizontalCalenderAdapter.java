package com.mv.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mv.ActivityMenu.TrainingCalender;
import com.mv.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HorizontalCalenderAdapter extends RecyclerView.Adapter<HorizontalCalenderAdapter.ViewHolder> {

    private TrainingCalender trainingCalender;
    private DateFormat dateFormat;
    private DateFormat dayFormat;
    private List<Date> dateList;
    private List<Date> eventDateList;
    private int CurrentDate;
    private int row_index;

    @SuppressLint("SimpleDateFormat")
    public HorizontalCalenderAdapter(Activity context, List<Date> processAllLis, int currentDate, List<Date> eventDate) {
        trainingCalender = (TrainingCalender) context;
        this.dateList = processAllLis;
        dateFormat = new SimpleDateFormat("dd");
        dayFormat = new SimpleDateFormat("EEE");
        this.CurrentDate = currentDate;
        this.eventDateList = eventDate;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.each_horizontal_calender, parent, false);

        // create ViewHolder
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView day, date;
        LinearLayout layoutMain;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            layoutMain = itemLayoutView.findViewById(R.id.layout_main);
            layoutMain.setOnClickListener(v -> {
                if (dateList.size() > getAdapterPosition()) {
                    trainingCalender.selectDate(dateList.get(getAdapterPosition()));
                }
                trainingCalender.binding.fabAddBroadcast.show();
                row_index = getAdapterPosition();
                notifyDataSetChanged();

            });

            date = itemLayoutView.findViewById(R.id.hv_date);
            day = itemLayoutView.findViewById(R.id.hv_day);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.date.setText(dateFormat.format(dateList.get(position)));
        holder.day.setText(dayFormat.format(dateList.get(position)));

        if (row_index == position) {
            holder.layoutMain.setBackgroundColor(Color.parseColor("#22B1B0"));
            holder.date.setTextColor(Color.parseColor("#ffffff"));
            holder.day.setTextColor(Color.parseColor("#ffffff"));
        } else {
            if (CurrentDate == position) {
                holder.layoutMain.setBackgroundColor(Color.parseColor("#DD4E42"));
                holder.date.setTextColor(Color.parseColor("#ffffff"));
                holder.day.setTextColor(Color.parseColor("#ffffff"));
            } else {
                if (eventDateList.contains(dateList.get(position))) {
                    holder.layoutMain.setBackgroundColor(Color.parseColor("#2C599D"));
                    holder.date.setTextColor(Color.parseColor("#ffffff"));
                    holder.day.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    holder.layoutMain.setBackgroundColor(Color.parseColor("#ffffff"));
                    holder.date.setTextColor(Color.parseColor("#000000"));
                    holder.day.setTextColor(Color.parseColor("#000000"));
                }
            }
        }
    }
}
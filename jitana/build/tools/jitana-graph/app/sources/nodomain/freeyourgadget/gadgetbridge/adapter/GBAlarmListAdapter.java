package nodomain.freeyourgadget.gadgetbridge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.activities.ConfigureAlarms;
import nodomain.freeyourgadget.gadgetbridge.database.DBHelper;
import nodomain.freeyourgadget.gadgetbridge.entities.Alarm;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;

public class GBAlarmListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private ArrayList<Alarm> alarmList;
    /* access modifiers changed from: private */
    public final Context mContext;

    public GBAlarmListAdapter(Context context) {
        this.mContext = context;
    }

    public void setAlarmList(List<Alarm> alarms) {
        this.alarmList = new ArrayList<>(alarms);
    }

    public ArrayList<Alarm> getAlarmList() {
        return this.alarmList;
    }

    /* access modifiers changed from: private */
    public void updateInDB(Alarm alarm) {
        DBHelper.store(alarm);
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(C0889R.layout.item_alarm, parent, false));
    }

    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Alarm alarm = this.alarmList.get(position);
        holder.alarmDayMonday.setChecked(alarm.getRepetition(1));
        holder.alarmDayTuesday.setChecked(alarm.getRepetition(2));
        holder.alarmDayWednesday.setChecked(alarm.getRepetition(4));
        holder.alarmDayThursday.setChecked(alarm.getRepetition(8));
        holder.alarmDayFriday.setChecked(alarm.getRepetition(16));
        holder.alarmDaySaturday.setChecked(alarm.getRepetition(32));
        holder.alarmDaySunday.setChecked(alarm.getRepetition(64));
        holder.container.setAlpha(alarm.getUnused() ? 0.5f : 1.0f);
        holder.isEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarm.setEnabled(isChecked);
                GBAlarmListAdapter.this.updateInDB(alarm);
            }
        });
        holder.container.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((ConfigureAlarms) GBAlarmListAdapter.this.mContext).configureAlarm(alarm);
            }
        });
        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                Alarm alarm = alarm;
                alarm.setUnused(!alarm.getUnused());
                holder.container.setAlpha(alarm.getUnused() ? 0.5f : 1.0f);
                GBAlarmListAdapter.this.updateInDB(alarm);
                return true;
            }
        });
        holder.alarmTime.setText(DateTimeUtils.formatTime(alarm.getHour(), alarm.getMinute()));
        holder.isEnabled.setChecked(alarm.getEnabled());
        if (alarm.getSmartWakeup()) {
            holder.isSmartWakeup.setVisibility(0);
        } else {
            holder.isSmartWakeup.setVisibility(8);
        }
    }

    public int getItemCount() {
        return this.alarmList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckedTextView alarmDayFriday;
        CheckedTextView alarmDayMonday;
        CheckedTextView alarmDaySaturday;
        CheckedTextView alarmDaySunday;
        CheckedTextView alarmDayThursday;
        CheckedTextView alarmDayTuesday;
        CheckedTextView alarmDayWednesday;
        TextView alarmTime;
        CardView container;
        Switch isEnabled;
        TextView isSmartWakeup;

        ViewHolder(View view) {
            super(view);
            this.container = (CardView) view.findViewById(C0889R.C0891id.card_view);
            this.alarmTime = (TextView) view.findViewById(C0889R.C0891id.alarm_item_time);
            this.isEnabled = (Switch) view.findViewById(C0889R.C0891id.alarm_item_toggle);
            this.isSmartWakeup = (TextView) view.findViewById(C0889R.C0891id.alarm_smart_wakeup);
            this.alarmDayMonday = (CheckedTextView) view.findViewById(C0889R.C0891id.alarm_item_monday);
            this.alarmDayTuesday = (CheckedTextView) view.findViewById(C0889R.C0891id.alarm_item_tuesday);
            this.alarmDayWednesday = (CheckedTextView) view.findViewById(C0889R.C0891id.alarm_item_wednesday);
            this.alarmDayThursday = (CheckedTextView) view.findViewById(C0889R.C0891id.alarm_item_thursday);
            this.alarmDayFriday = (CheckedTextView) view.findViewById(C0889R.C0891id.alarm_item_friday);
            this.alarmDaySaturday = (CheckedTextView) view.findViewById(C0889R.C0891id.alarm_item_saturday);
            this.alarmDaySunday = (CheckedTextView) view.findViewById(C0889R.C0891id.alarm_item_sunday);
        }
    }
}

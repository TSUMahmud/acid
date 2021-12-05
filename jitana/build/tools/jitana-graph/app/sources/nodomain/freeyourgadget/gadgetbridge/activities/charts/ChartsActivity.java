package nodomain.freeyourgadget.gadgetbridge.activities.charts;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractFragmentPagerAdapter;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractGBFragmentActivity;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceCoordinator;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.RecordedDataTypes;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;
import nodomain.freeyourgadget.gadgetbridge.util.DeviceHelper;
import nodomain.freeyourgadget.gadgetbridge.util.LimitedQueue;

public class ChartsActivity extends AbstractGBFragmentActivity implements ChartsHost {
    private ViewGroup dateBar;
    LimitedQueue mActivityAmountCache = new LimitedQueue(60);
    private TextView mDateControl;
    private Date mEndDate;
    /* access modifiers changed from: private */
    public GBDevice mGBDevice;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String str = (String) Objects.requireNonNull(intent.getAction());
            if (((str.hashCode() == 2101976453 && str.equals(GBDevice.ACTION_DEVICE_CHANGED)) ? (char) 0 : 65535) == 0) {
                ChartsActivity.this.refreshBusyState((GBDevice) intent.getParcelableExtra(GBDevice.EXTRA_DEVICE));
            }
        }
    };
    private Date mStartDate;
    private SwipeRefreshLayout swipeLayout;

    private static class ShowDurationDialog extends Dialog {
        private TextView durationLabel;
        private final String mDuration;

        ShowDurationDialog(String duration, Context context) {
            super(context);
            this.mDuration = duration;
        }

        /* access modifiers changed from: protected */
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(C0889R.layout.activity_charts_durationdialog);
            this.durationLabel = (TextView) findViewById(C0889R.C0891id.charts_duration_label);
            setDuration(this.mDuration);
        }

        public void setDuration(String duration) {
            if (this.mDuration != null) {
                this.durationLabel.setText(duration);
            } else {
                this.durationLabel.setText("");
            }
        }
    }

    /* access modifiers changed from: private */
    public void refreshBusyState(GBDevice dev) {
        if (dev.isBusy()) {
            this.swipeLayout.setRefreshing(true);
        } else {
            boolean wasBusy = this.swipeLayout.isRefreshing();
            this.swipeLayout.setRefreshing(false);
            if (wasBusy) {
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(REFRESH));
            }
        }
        enableSwipeRefresh(true);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0889R.layout.activity_charts);
        initDates();
        IntentFilter filterLocal = new IntentFilter();
        filterLocal.addAction(GBDevice.ACTION_DEVICE_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver, filterLocal);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mGBDevice = (GBDevice) extras.getParcelable(GBDevice.EXTRA_DEVICE);
            this.swipeLayout = (SwipeRefreshLayout) findViewById(C0889R.C0891id.activity_swipe_layout);
            this.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                public void onRefresh() {
                    ChartsActivity.this.fetchActivityData();
                }
            });
            enableSwipeRefresh(true);
            NonSwipeableViewPager viewPager = (NonSwipeableViewPager) findViewById(C0889R.C0891id.charts_pager);
            viewPager.setAdapter(getPagerAdapter());
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                public void onPageSelected(int position) {
                }

                public void onPageScrollStateChanged(int state) {
                    ChartsActivity.this.enableSwipeRefresh(state == 0);
                }
            });
            this.dateBar = (ViewGroup) findViewById(C0889R.C0891id.charts_date_bar);
            this.mDateControl = (TextView) findViewById(C0889R.C0891id.charts_text_date);
            this.mDateControl.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new ShowDurationDialog(ChartsActivity.this.formatDetailedDuration(), ChartsActivity.this).show();
                }
            });
            ((Button) findViewById(C0889R.C0891id.charts_previous_day)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ChartsActivity.this.handleButtonClicked(ChartsHost.DATE_PREV_DAY);
                }
            });
            ((Button) findViewById(C0889R.C0891id.charts_next_day)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ChartsActivity.this.handleButtonClicked(ChartsHost.DATE_NEXT_DAY);
                }
            });
            ((Button) findViewById(C0889R.C0891id.charts_previous_week)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ChartsActivity.this.handleButtonClicked(ChartsHost.DATE_PREV_WEEK);
                }
            });
            ((Button) findViewById(C0889R.C0891id.charts_next_week)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ChartsActivity.this.handleButtonClicked(ChartsHost.DATE_NEXT_WEEK);
                }
            });
            ((Button) findViewById(C0889R.C0891id.charts_previous_month)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ChartsActivity.this.handleButtonClicked(ChartsHost.DATE_PREV_MONTH);
                }
            });
            ((Button) findViewById(C0889R.C0891id.charts_next_month)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ChartsActivity.this.handleButtonClicked(ChartsHost.DATE_NEXT_MONTH);
                }
            });
            return;
        }
        throw new IllegalArgumentException("Must provide a device when invoking this activity");
    }

    /* access modifiers changed from: private */
    public String formatDetailedDuration() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return getString(C0889R.string.sleep_activity_date_range, new Object[]{dateFormat.format(getStartDate()), dateFormat.format(getEndDate())});
    }

    /* access modifiers changed from: protected */
    public void initDates() {
        setEndDate(new Date());
        setStartDate(DateTimeUtils.shiftByDays(getEndDate(), -1));
    }

    public GBDevice getDevice() {
        return this.mGBDevice;
    }

    public void setStartDate(Date startDate) {
        this.mStartDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.mEndDate = endDate;
    }

    public Date getStartDate() {
        return this.mStartDate;
    }

    public Date getEndDate() {
        return this.mEndDate;
    }

    /* access modifiers changed from: private */
    public void handleButtonClicked(String Action) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Action));
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mReceiver);
        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(C0889R.C0893menu.menu_charts, menu);
        DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(this.mGBDevice);
        if (this.mGBDevice.isConnected() && coordinator.supportsActivityDataFetching()) {
            return true;
        }
        menu.removeItem(C0889R.C0891id.charts_fetch_activity_data);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == C0889R.C0891id.charts_fetch_activity_data) {
            fetchActivityData();
            return true;
        } else if (itemId != C0889R.C0891id.prefs_charts_menu) {
            return super.onOptionsItemSelected(item);
        } else {
            startActivityForResult(new Intent(this, ChartsPreferencesActivity.class), 1);
            return true;
        }
    }

    /* access modifiers changed from: private */
    public void enableSwipeRefresh(boolean enable) {
        this.swipeLayout.setEnabled(enable && DeviceHelper.getInstance().getCoordinator(this.mGBDevice).allowFetchActivityData(this.mGBDevice));
    }

    /* access modifiers changed from: private */
    public void fetchActivityData() {
        if (getDevice().isInitialized()) {
            GBApplication.deviceService().onFetchRecordedData(RecordedDataTypes.TYPE_ACTIVITY);
            return;
        }
        this.swipeLayout.setRefreshing(false);
        C1238GB.toast((Context) this, getString(C0889R.string.device_not_connected), 0, 3);
    }

    public void setDateInfo(String dateInfo) {
        this.mDateControl.setText(dateInfo);
    }

    /* access modifiers changed from: protected */
    public AbstractFragmentPagerAdapter createFragmentPagerAdapter(FragmentManager fragmentManager) {
        return new SectionsPagerAdapter(fragmentManager);
    }

    public ViewGroup getDateBar() {
        return this.dateBar;
    }

    public class SectionsPagerAdapter extends AbstractFragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            if (position == 0) {
                return new ActivitySleepChartFragment();
            }
            if (position == 1) {
                return new SleepChartFragment();
            }
            if (position == 2) {
                return new WeekSleepChartFragment();
            }
            if (position == 3) {
                return new WeekStepsChartFragment();
            }
            if (position == 4) {
                return new SpeedZonesFragment();
            }
            if (position != 5) {
                return null;
            }
            return new LiveActivityFragment();
        }

        public int getCount() {
            if (DeviceHelper.getInstance().getCoordinator(ChartsActivity.this.mGBDevice).supportsRealtimeData()) {
                return 6;
            }
            return 5;
        }

        private String getSleepTitle() {
            if (GBApplication.getPrefs().getBoolean("charts_range", true)) {
                return ChartsActivity.this.getString(C0889R.string.weeksleepchart_sleep_a_month);
            }
            return ChartsActivity.this.getString(C0889R.string.weeksleepchart_sleep_a_week);
        }

        public String getStepsTitle() {
            if (GBApplication.getPrefs().getBoolean("charts_range", true)) {
                return ChartsActivity.this.getString(C0889R.string.weekstepschart_steps_a_month);
            }
            return ChartsActivity.this.getString(C0889R.string.weekstepschart_steps_a_week);
        }

        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return ChartsActivity.this.getString(C0889R.string.activity_sleepchart_activity_and_sleep);
            }
            if (position == 1) {
                return ChartsActivity.this.getString(C0889R.string.sleepchart_your_sleep);
            }
            if (position == 2) {
                return getSleepTitle();
            }
            if (position == 3) {
                return getStepsTitle();
            }
            if (position == 4) {
                return ChartsActivity.this.getString(C0889R.string.stats_title);
            }
            if (position != 5) {
                return super.getPageTitle(position);
            }
            return ChartsActivity.this.getString(C0889R.string.liveactivity_live_activity);
        }
    }
}

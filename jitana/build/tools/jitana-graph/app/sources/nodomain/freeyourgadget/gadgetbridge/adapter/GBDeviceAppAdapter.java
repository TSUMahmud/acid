package nodomain.freeyourgadget.gadgetbridge.adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.activities.appmanager.AbstractAppManagerFragment;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceApp;

public class GBDeviceAppAdapter extends RecyclerView.Adapter<AppViewHolder> {
    private final List<GBDeviceApp> appList;
    private final int mLayoutId;
    /* access modifiers changed from: private */
    public final AbstractAppManagerFragment mParentFragment;

    public List<GBDeviceApp> getAppList() {
        return this.appList;
    }

    public GBDeviceAppAdapter(List<GBDeviceApp> list, int layoutId, AbstractAppManagerFragment parentFragment) {
        this.mLayoutId = layoutId;
        this.appList = list;
        this.mParentFragment = parentFragment;
    }

    public long getItemId(int position) {
        return this.appList.get(position).getUUID().getLeastSignificantBits();
    }

    public int getItemCount() {
        return this.appList.size();
    }

    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppViewHolder(LayoutInflater.from(parent.getContext()).inflate(this.mLayoutId, parent, false));
    }

    public void onBindViewHolder(final AppViewHolder holder, int position) {
        final GBDeviceApp deviceApp = this.appList.get(position);
        holder.mDeviceAppVersionAuthorLabel.setText(GBApplication.getContext().getString(C0889R.string.appversion_by_creator, new Object[]{deviceApp.getVersion(), deviceApp.getCreator()}));
        holder.mDeviceAppNameLabel.setText(deviceApp.getName());
        int i = C10764.f123x7d95cefd[deviceApp.getType().ordinal()];
        if (i == 1) {
            holder.mDeviceImageView.setImageResource(C0889R.C0890drawable.ic_watchapp);
        } else if (i == 2) {
            holder.mDeviceImageView.setImageResource(C0889R.C0890drawable.ic_activitytracker);
        } else if (i == 3) {
            holder.mDeviceImageView.setImageResource(C0889R.C0890drawable.ic_systemapp);
        } else if (i != 4) {
            holder.mDeviceImageView.setImageResource(C0889R.C0890drawable.ic_watchapp);
        } else {
            holder.mDeviceImageView.setImageResource(C0889R.C0890drawable.ic_watchface);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                GBApplication.deviceService().onAppStart(deviceApp.getUUID(), true);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                return GBDeviceAppAdapter.this.mParentFragment.openPopupMenu(view, deviceApp);
            }
        });
        holder.mDragHandle.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                GBDeviceAppAdapter.this.mParentFragment.startDragging(holder);
                return true;
            }
        });
    }

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.adapter.GBDeviceAppAdapter$4 */
    static /* synthetic */ class C10764 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$impl$GBDeviceApp$Type */
        static final /* synthetic */ int[] f123x7d95cefd = new int[GBDeviceApp.Type.values().length];

        static {
            try {
                f123x7d95cefd[GBDeviceApp.Type.APP_GENERIC.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f123x7d95cefd[GBDeviceApp.Type.APP_ACTIVITYTRACKER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f123x7d95cefd[GBDeviceApp.Type.APP_SYSTEM.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f123x7d95cefd[GBDeviceApp.Type.WATCHFACE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public void onItemMove(int from, int to) {
        Collections.swap(this.appList, from, to);
        notifyItemMoved(from, to);
    }

    public class AppViewHolder extends RecyclerView.ViewHolder {
        final TextView mDeviceAppNameLabel;
        final TextView mDeviceAppVersionAuthorLabel;
        final ImageView mDeviceImageView;
        final ImageView mDragHandle;

        AppViewHolder(View itemView) {
            super(itemView);
            this.mDeviceAppVersionAuthorLabel = (TextView) itemView.findViewById(C0889R.C0891id.item_details);
            this.mDeviceAppNameLabel = (TextView) itemView.findViewById(C0889R.C0891id.item_name);
            this.mDeviceImageView = (ImageView) itemView.findViewById(C0889R.C0891id.item_image);
            this.mDragHandle = (ImageView) itemView.findViewById(C0889R.C0891id.drag_handle);
        }
    }
}

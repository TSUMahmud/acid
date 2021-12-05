package nodomain.freeyourgadget.gadgetbridge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.C0889R;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDeviceCandidate;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;

public class DeviceCandidateAdapter extends ArrayAdapter<GBDeviceCandidate> {
    private final Context context;

    public DeviceCandidateAdapter(Context context2, List<GBDeviceCandidate> deviceCandidates) {
        super(context2, 0, deviceCandidates);
        this.context = context2;
    }

    public View getView(int position, View view, ViewGroup parent) {
        GBDeviceCandidate device = (GBDeviceCandidate) getItem(position);
        if (view == null) {
            view = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(C0889R.layout.item_with_details, parent, false);
        }
        ((TextView) view.findViewById(C0889R.C0891id.item_name)).setText(formatDeviceCandidate(device));
        ((TextView) view.findViewById(C0889R.C0891id.item_details)).setText(device.getMacAddress());
        ((ImageView) view.findViewById(C0889R.C0891id.item_image)).setImageResource(device.getDeviceType().getIcon());
        return view;
    }

    private String formatDeviceCandidate(GBDeviceCandidate device) {
        if (device.getRssi() <= 0) {
            return device.getName();
        }
        return this.context.getString(C0889R.string.device_with_rssi, new Object[]{device.getName(), C1238GB.formatRssi(device.getRssi())});
    }
}

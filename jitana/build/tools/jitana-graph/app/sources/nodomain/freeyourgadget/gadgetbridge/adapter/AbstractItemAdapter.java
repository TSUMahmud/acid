package nodomain.freeyourgadget.gadgetbridge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.C0889R;

public abstract class AbstractItemAdapter<T> extends ArrayAdapter<T> {
    public static final int SIZE_LARGE = 3;
    public static final int SIZE_MEDIUM = 2;
    public static final int SIZE_SMALL = 1;
    private final Context context;
    private boolean horizontalAlignment;
    private final List<T> items;
    private int size;

    /* access modifiers changed from: protected */
    public abstract String getDetails(T t);

    /* access modifiers changed from: protected */
    public abstract int getIcon(T t);

    /* access modifiers changed from: protected */
    public abstract String getName(T t);

    public AbstractItemAdapter(Context context2) {
        this(context2, new ArrayList());
    }

    public AbstractItemAdapter(Context context2, List<T> items2) {
        super(context2, 0, items2);
        this.size = 2;
        this.context = context2;
        this.items = items2;
    }

    public void setHorizontalAlignment(boolean horizontalAlignment2) {
        this.horizontalAlignment = horizontalAlignment2;
    }

    public View getView(int position, View view, ViewGroup parent) {
        T item = getItem(position);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService("layout_inflater");
            if (this.horizontalAlignment) {
                view = inflater.inflate(C0889R.layout.item_with_details_horizontal, parent, false);
            } else if (this.size != 1) {
                view = inflater.inflate(C0889R.layout.item_with_details, parent, false);
            } else {
                view = inflater.inflate(C0889R.layout.item_with_details_small, parent, false);
            }
        }
        ((TextView) view.findViewById(C0889R.C0891id.item_name)).setText(getName(item));
        ((TextView) view.findViewById(C0889R.C0891id.item_details)).setText(getDetails(item));
        ((ImageView) view.findViewById(C0889R.C0891id.item_image)).setImageResource(getIcon(item));
        return view;
    }

    public void setSize(int size2) {
        this.size = size2;
    }

    public int getSize() {
        return this.size;
    }

    public List<T> getItems() {
        return this.items;
    }

    public void loadItems() {
    }

    public void setItems(List<T> items2, boolean notify) {
        this.items.clear();
        this.items.addAll(items2);
        if (notify) {
            notifyDataSetChanged();
        }
    }
}

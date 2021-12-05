package nodomain.freeyourgadget.gadgetbridge.adapter;

import android.content.Context;
import java.util.List;
import nodomain.freeyourgadget.gadgetbridge.model.ItemWithDetails;

public class ItemWithDetailsAdapter extends AbstractItemAdapter<ItemWithDetails> {
    public ItemWithDetailsAdapter(Context context, List<ItemWithDetails> items) {
        super(context, items);
    }

    /* access modifiers changed from: protected */
    public String getName(ItemWithDetails item) {
        return item.getName();
    }

    /* access modifiers changed from: protected */
    public String getDetails(ItemWithDetails item) {
        return item.getDetails();
    }

    /* access modifiers changed from: protected */
    public int getIcon(ItemWithDetails item) {
        return item.getIcon();
    }
}

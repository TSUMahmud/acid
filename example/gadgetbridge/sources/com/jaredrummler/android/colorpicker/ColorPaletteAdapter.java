package com.jaredrummler.android.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;

class ColorPaletteAdapter extends BaseAdapter {
    int colorShape;
    final int[] colors;
    final OnColorSelectedListener listener;
    int selectedPosition;

    interface OnColorSelectedListener {
        void onColorSelected(int i);
    }

    ColorPaletteAdapter(OnColorSelectedListener listener2, int[] colors2, int selectedPosition2, int colorShape2) {
        this.listener = listener2;
        this.colors = colors2;
        this.selectedPosition = selectedPosition2;
        this.colorShape = colorShape2;
    }

    public int getCount() {
        return this.colors.length;
    }

    public Object getItem(int position) {
        return Integer.valueOf(this.colors[position]);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder(parent.getContext());
            convertView = holder.view;
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.setup(position);
        return convertView;
    }

    /* access modifiers changed from: package-private */
    public void selectNone() {
        this.selectedPosition = -1;
        notifyDataSetChanged();
    }

    private final class ViewHolder {
        ColorPanelView colorPanelView;
        ImageView imageView;
        int originalBorderColor;
        View view;

        ViewHolder(Context context) {
            int layoutResId;
            if (ColorPaletteAdapter.this.colorShape == 0) {
                layoutResId = C0763R.layout.cpv_color_item_square;
            } else {
                layoutResId = C0763R.layout.cpv_color_item_circle;
            }
            this.view = View.inflate(context, layoutResId, (ViewGroup) null);
            this.colorPanelView = (ColorPanelView) this.view.findViewById(C0763R.C0765id.cpv_color_panel_view);
            this.imageView = (ImageView) this.view.findViewById(C0763R.C0765id.cpv_color_image_view);
            this.originalBorderColor = this.colorPanelView.getBorderColor();
            this.view.setTag(this);
        }

        /* access modifiers changed from: package-private */
        public void setup(int position) {
            int color = ColorPaletteAdapter.this.colors[position];
            int alpha = Color.alpha(color);
            this.colorPanelView.setColor(color);
            this.imageView.setImageResource(ColorPaletteAdapter.this.selectedPosition == position ? C0763R.C0764drawable.cpv_preset_checked : 0);
            if (alpha == 255) {
                setColorFilter(position);
            } else if (alpha <= 165) {
                this.colorPanelView.setBorderColor(color | ViewCompat.MEASURED_STATE_MASK);
                this.imageView.setColorFilter(ViewCompat.MEASURED_STATE_MASK, PorterDuff.Mode.SRC_IN);
            } else {
                this.colorPanelView.setBorderColor(this.originalBorderColor);
                this.imageView.setColorFilter(-1, PorterDuff.Mode.SRC_IN);
            }
            setOnClickListener(position);
        }

        private void setOnClickListener(final int position) {
            this.colorPanelView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (ColorPaletteAdapter.this.selectedPosition != position) {
                        ColorPaletteAdapter.this.selectedPosition = position;
                        ColorPaletteAdapter.this.notifyDataSetChanged();
                    }
                    ColorPaletteAdapter.this.listener.onColorSelected(ColorPaletteAdapter.this.colors[position]);
                }
            });
            this.colorPanelView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    ViewHolder.this.colorPanelView.showHint();
                    return true;
                }
            });
        }

        private void setColorFilter(int position) {
            if (position != ColorPaletteAdapter.this.selectedPosition || ColorUtils.calculateLuminance(ColorPaletteAdapter.this.colors[position]) < 0.65d) {
                this.imageView.setColorFilter((ColorFilter) null);
            } else {
                this.imageView.setColorFilter(ViewCompat.MEASURED_STATE_MASK, PorterDuff.Mode.SRC_IN);
            }
        }
    }
}

package androidx.palette.graphics;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TimingLogger;
import androidx.collection.ArrayMap;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import com.github.mikephil.charting.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class Palette {
    static final int DEFAULT_CALCULATE_NUMBER_COLORS = 16;
    static final Filter DEFAULT_FILTER = new Filter() {
        private static final float BLACK_MAX_LIGHTNESS = 0.05f;
        private static final float WHITE_MIN_LIGHTNESS = 0.95f;

        public boolean isAllowed(int rgb, float[] hsl) {
            return !isWhite(hsl) && !isBlack(hsl) && !isNearRedILine(hsl);
        }

        private boolean isBlack(float[] hslColor) {
            return hslColor[2] <= BLACK_MAX_LIGHTNESS;
        }

        private boolean isWhite(float[] hslColor) {
            return hslColor[2] >= WHITE_MIN_LIGHTNESS;
        }

        private boolean isNearRedILine(float[] hslColor) {
            return hslColor[0] >= 10.0f && hslColor[0] <= 37.0f && hslColor[1] <= 0.82f;
        }
    };
    static final int DEFAULT_RESIZE_BITMAP_AREA = 12544;
    static final String LOG_TAG = "Palette";
    static final boolean LOG_TIMINGS = false;
    static final float MIN_CONTRAST_BODY_TEXT = 4.5f;
    static final float MIN_CONTRAST_TITLE_TEXT = 3.0f;
    private final Swatch mDominantSwatch = findDominantSwatch();
    private final Map<Target, Swatch> mSelectedSwatches = new ArrayMap();
    private final List<Swatch> mSwatches;
    private final List<Target> mTargets;
    private final SparseBooleanArray mUsedColors = new SparseBooleanArray();

    public interface Filter {
        boolean isAllowed(int i, float[] fArr);
    }

    public interface PaletteAsyncListener {
        void onGenerated(Palette palette);
    }

    public static Builder from(Bitmap bitmap) {
        return new Builder(bitmap);
    }

    public static Palette from(List<Swatch> swatches) {
        return new Builder(swatches).generate();
    }

    @Deprecated
    public static Palette generate(Bitmap bitmap) {
        return from(bitmap).generate();
    }

    @Deprecated
    public static Palette generate(Bitmap bitmap, int numColors) {
        return from(bitmap).maximumColorCount(numColors).generate();
    }

    @Deprecated
    public static AsyncTask<Bitmap, Void, Palette> generateAsync(Bitmap bitmap, PaletteAsyncListener listener) {
        return from(bitmap).generate(listener);
    }

    @Deprecated
    public static AsyncTask<Bitmap, Void, Palette> generateAsync(Bitmap bitmap, int numColors, PaletteAsyncListener listener) {
        return from(bitmap).maximumColorCount(numColors).generate(listener);
    }

    Palette(List<Swatch> swatches, List<Target> targets) {
        this.mSwatches = swatches;
        this.mTargets = targets;
    }

    public List<Swatch> getSwatches() {
        return Collections.unmodifiableList(this.mSwatches);
    }

    public List<Target> getTargets() {
        return Collections.unmodifiableList(this.mTargets);
    }

    public Swatch getVibrantSwatch() {
        return getSwatchForTarget(Target.VIBRANT);
    }

    public Swatch getLightVibrantSwatch() {
        return getSwatchForTarget(Target.LIGHT_VIBRANT);
    }

    public Swatch getDarkVibrantSwatch() {
        return getSwatchForTarget(Target.DARK_VIBRANT);
    }

    public Swatch getMutedSwatch() {
        return getSwatchForTarget(Target.MUTED);
    }

    public Swatch getLightMutedSwatch() {
        return getSwatchForTarget(Target.LIGHT_MUTED);
    }

    public Swatch getDarkMutedSwatch() {
        return getSwatchForTarget(Target.DARK_MUTED);
    }

    public int getVibrantColor(int defaultColor) {
        return getColorForTarget(Target.VIBRANT, defaultColor);
    }

    public int getLightVibrantColor(int defaultColor) {
        return getColorForTarget(Target.LIGHT_VIBRANT, defaultColor);
    }

    public int getDarkVibrantColor(int defaultColor) {
        return getColorForTarget(Target.DARK_VIBRANT, defaultColor);
    }

    public int getMutedColor(int defaultColor) {
        return getColorForTarget(Target.MUTED, defaultColor);
    }

    public int getLightMutedColor(int defaultColor) {
        return getColorForTarget(Target.LIGHT_MUTED, defaultColor);
    }

    public int getDarkMutedColor(int defaultColor) {
        return getColorForTarget(Target.DARK_MUTED, defaultColor);
    }

    public Swatch getSwatchForTarget(Target target) {
        return this.mSelectedSwatches.get(target);
    }

    public int getColorForTarget(Target target, int defaultColor) {
        Swatch swatch = getSwatchForTarget(target);
        return swatch != null ? swatch.getRgb() : defaultColor;
    }

    public Swatch getDominantSwatch() {
        return this.mDominantSwatch;
    }

    public int getDominantColor(int defaultColor) {
        Swatch swatch = this.mDominantSwatch;
        return swatch != null ? swatch.getRgb() : defaultColor;
    }

    /* access modifiers changed from: package-private */
    public void generate() {
        int count = this.mTargets.size();
        for (int i = 0; i < count; i++) {
            Target target = this.mTargets.get(i);
            target.normalizeWeights();
            this.mSelectedSwatches.put(target, generateScoredTarget(target));
        }
        this.mUsedColors.clear();
    }

    private Swatch generateScoredTarget(Target target) {
        Swatch maxScoreSwatch = getMaxScoredSwatchForTarget(target);
        if (maxScoreSwatch != null && target.isExclusive()) {
            this.mUsedColors.append(maxScoreSwatch.getRgb(), true);
        }
        return maxScoreSwatch;
    }

    private Swatch getMaxScoredSwatchForTarget(Target target) {
        float maxScore = 0.0f;
        Swatch maxScoreSwatch = null;
        int count = this.mSwatches.size();
        for (int i = 0; i < count; i++) {
            Swatch swatch = this.mSwatches.get(i);
            if (shouldBeScoredForTarget(swatch, target)) {
                float score = generateScore(swatch, target);
                if (maxScoreSwatch == null || score > maxScore) {
                    maxScoreSwatch = swatch;
                    maxScore = score;
                }
            }
        }
        return maxScoreSwatch;
    }

    private boolean shouldBeScoredForTarget(Swatch swatch, Target target) {
        float[] hsl = swatch.getHsl();
        if (hsl[1] < target.getMinimumSaturation() || hsl[1] > target.getMaximumSaturation() || hsl[2] < target.getMinimumLightness() || hsl[2] > target.getMaximumLightness() || this.mUsedColors.get(swatch.getRgb())) {
            return false;
        }
        return true;
    }

    private float generateScore(Swatch swatch, Target target) {
        float[] hsl = swatch.getHsl();
        float saturationScore = 0.0f;
        float luminanceScore = 0.0f;
        float populationScore = 0.0f;
        Swatch swatch2 = this.mDominantSwatch;
        int maxPopulation = swatch2 != null ? swatch2.getPopulation() : 1;
        if (target.getSaturationWeight() > 0.0f) {
            saturationScore = target.getSaturationWeight() * (1.0f - Math.abs(hsl[1] - target.getTargetSaturation()));
        }
        if (target.getLightnessWeight() > 0.0f) {
            luminanceScore = target.getLightnessWeight() * (1.0f - Math.abs(hsl[2] - target.getTargetLightness()));
        }
        if (target.getPopulationWeight() > 0.0f) {
            populationScore = target.getPopulationWeight() * (((float) swatch.getPopulation()) / ((float) maxPopulation));
        }
        return saturationScore + luminanceScore + populationScore;
    }

    private Swatch findDominantSwatch() {
        int maxPop = Integer.MIN_VALUE;
        Swatch maxSwatch = null;
        int count = this.mSwatches.size();
        for (int i = 0; i < count; i++) {
            Swatch swatch = this.mSwatches.get(i);
            if (swatch.getPopulation() > maxPop) {
                maxSwatch = swatch;
                maxPop = swatch.getPopulation();
            }
        }
        return maxSwatch;
    }

    public static final class Swatch {
        private final int mBlue;
        private int mBodyTextColor;
        private boolean mGeneratedTextColors;
        private final int mGreen;
        private float[] mHsl;
        private final int mPopulation;
        private final int mRed;
        private final int mRgb;
        private int mTitleTextColor;

        public Swatch(int color, int population) {
            this.mRed = Color.red(color);
            this.mGreen = Color.green(color);
            this.mBlue = Color.blue(color);
            this.mRgb = color;
            this.mPopulation = population;
        }

        Swatch(int red, int green, int blue, int population) {
            this.mRed = red;
            this.mGreen = green;
            this.mBlue = blue;
            this.mRgb = Color.rgb(red, green, blue);
            this.mPopulation = population;
        }

        Swatch(float[] hsl, int population) {
            this(ColorUtils.HSLToColor(hsl), population);
            this.mHsl = hsl;
        }

        public int getRgb() {
            return this.mRgb;
        }

        public float[] getHsl() {
            if (this.mHsl == null) {
                this.mHsl = new float[3];
            }
            ColorUtils.RGBToHSL(this.mRed, this.mGreen, this.mBlue, this.mHsl);
            return this.mHsl;
        }

        public int getPopulation() {
            return this.mPopulation;
        }

        public int getTitleTextColor() {
            ensureTextColorsGenerated();
            return this.mTitleTextColor;
        }

        public int getBodyTextColor() {
            ensureTextColorsGenerated();
            return this.mBodyTextColor;
        }

        private void ensureTextColorsGenerated() {
            int i;
            int i2;
            if (!this.mGeneratedTextColors) {
                int lightBodyAlpha = ColorUtils.calculateMinimumAlpha(-1, this.mRgb, Palette.MIN_CONTRAST_BODY_TEXT);
                int lightTitleAlpha = ColorUtils.calculateMinimumAlpha(-1, this.mRgb, Palette.MIN_CONTRAST_TITLE_TEXT);
                if (lightBodyAlpha == -1 || lightTitleAlpha == -1) {
                    int darkBodyAlpha = ColorUtils.calculateMinimumAlpha(ViewCompat.MEASURED_STATE_MASK, this.mRgb, Palette.MIN_CONTRAST_BODY_TEXT);
                    int darkTitleAlpha = ColorUtils.calculateMinimumAlpha(ViewCompat.MEASURED_STATE_MASK, this.mRgb, Palette.MIN_CONTRAST_TITLE_TEXT);
                    if (darkBodyAlpha == -1 || darkTitleAlpha == -1) {
                        if (lightBodyAlpha != -1) {
                            i = ColorUtils.setAlphaComponent(-1, lightBodyAlpha);
                        } else {
                            i = ColorUtils.setAlphaComponent(ViewCompat.MEASURED_STATE_MASK, darkBodyAlpha);
                        }
                        this.mBodyTextColor = i;
                        if (lightTitleAlpha != -1) {
                            i2 = ColorUtils.setAlphaComponent(-1, lightTitleAlpha);
                        } else {
                            i2 = ColorUtils.setAlphaComponent(ViewCompat.MEASURED_STATE_MASK, darkTitleAlpha);
                        }
                        this.mTitleTextColor = i2;
                        this.mGeneratedTextColors = true;
                        return;
                    }
                    this.mBodyTextColor = ColorUtils.setAlphaComponent(ViewCompat.MEASURED_STATE_MASK, darkBodyAlpha);
                    this.mTitleTextColor = ColorUtils.setAlphaComponent(ViewCompat.MEASURED_STATE_MASK, darkTitleAlpha);
                    this.mGeneratedTextColors = true;
                    return;
                }
                this.mBodyTextColor = ColorUtils.setAlphaComponent(-1, lightBodyAlpha);
                this.mTitleTextColor = ColorUtils.setAlphaComponent(-1, lightTitleAlpha);
                this.mGeneratedTextColors = true;
            }
        }

        public String toString() {
            return getClass().getSimpleName() + " [RGB: #" + Integer.toHexString(getRgb()) + ']' + " [HSL: " + Arrays.toString(getHsl()) + ']' + " [Population: " + this.mPopulation + ']' + " [Title Text: #" + Integer.toHexString(getTitleTextColor()) + ']' + " [Body Text: #" + Integer.toHexString(getBodyTextColor()) + ']';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Swatch swatch = (Swatch) o;
            if (this.mPopulation == swatch.mPopulation && this.mRgb == swatch.mRgb) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return (this.mRgb * 31) + this.mPopulation;
        }
    }

    public static final class Builder {
        private final Bitmap mBitmap;
        private final List<Filter> mFilters = new ArrayList();
        private int mMaxColors = 16;
        private Rect mRegion;
        private int mResizeArea = Palette.DEFAULT_RESIZE_BITMAP_AREA;
        private int mResizeMaxDimension = -1;
        private final List<Swatch> mSwatches;
        private final List<Target> mTargets = new ArrayList();

        public Builder(Bitmap bitmap) {
            if (bitmap == null || bitmap.isRecycled()) {
                throw new IllegalArgumentException("Bitmap is not valid");
            }
            this.mFilters.add(Palette.DEFAULT_FILTER);
            this.mBitmap = bitmap;
            this.mSwatches = null;
            this.mTargets.add(Target.LIGHT_VIBRANT);
            this.mTargets.add(Target.VIBRANT);
            this.mTargets.add(Target.DARK_VIBRANT);
            this.mTargets.add(Target.LIGHT_MUTED);
            this.mTargets.add(Target.MUTED);
            this.mTargets.add(Target.DARK_MUTED);
        }

        public Builder(List<Swatch> swatches) {
            if (swatches == null || swatches.isEmpty()) {
                throw new IllegalArgumentException("List of Swatches is not valid");
            }
            this.mFilters.add(Palette.DEFAULT_FILTER);
            this.mSwatches = swatches;
            this.mBitmap = null;
        }

        public Builder maximumColorCount(int colors) {
            this.mMaxColors = colors;
            return this;
        }

        @Deprecated
        public Builder resizeBitmapSize(int maxDimension) {
            this.mResizeMaxDimension = maxDimension;
            this.mResizeArea = -1;
            return this;
        }

        public Builder resizeBitmapArea(int area) {
            this.mResizeArea = area;
            this.mResizeMaxDimension = -1;
            return this;
        }

        public Builder clearFilters() {
            this.mFilters.clear();
            return this;
        }

        public Builder addFilter(Filter filter) {
            if (filter != null) {
                this.mFilters.add(filter);
            }
            return this;
        }

        public Builder setRegion(int left, int top, int right, int bottom) {
            if (this.mBitmap != null) {
                if (this.mRegion == null) {
                    this.mRegion = new Rect();
                }
                this.mRegion.set(0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight());
                if (!this.mRegion.intersect(left, top, right, bottom)) {
                    throw new IllegalArgumentException("The given region must intersect with the Bitmap's dimensions.");
                }
            }
            return this;
        }

        public Builder clearRegion() {
            this.mRegion = null;
            return this;
        }

        public Builder addTarget(Target target) {
            if (!this.mTargets.contains(target)) {
                this.mTargets.add(target);
            }
            return this;
        }

        public Builder clearTargets() {
            List<Target> list = this.mTargets;
            if (list != null) {
                list.clear();
            }
            return this;
        }

        public Palette generate() {
            List<Swatch> swatches;
            Filter[] filterArr;
            TimingLogger logger = null;
            Bitmap bitmap = this.mBitmap;
            if (bitmap != null) {
                Bitmap bitmap2 = scaleBitmapDown(bitmap);
                if (logger != null) {
                    logger.addSplit("Processed Bitmap");
                }
                Rect region = this.mRegion;
                if (!(bitmap2 == this.mBitmap || region == null)) {
                    double width = (double) bitmap2.getWidth();
                    double width2 = (double) this.mBitmap.getWidth();
                    Double.isNaN(width);
                    Double.isNaN(width2);
                    double scale = width / width2;
                    double d = (double) region.left;
                    Double.isNaN(d);
                    region.left = (int) Math.floor(d * scale);
                    double d2 = (double) region.top;
                    Double.isNaN(d2);
                    region.top = (int) Math.floor(d2 * scale);
                    double d3 = (double) region.right;
                    Double.isNaN(d3);
                    region.right = Math.min((int) Math.ceil(d3 * scale), bitmap2.getWidth());
                    double d4 = (double) region.bottom;
                    Double.isNaN(d4);
                    region.bottom = Math.min((int) Math.ceil(d4 * scale), bitmap2.getHeight());
                }
                int[] pixelsFromBitmap = getPixelsFromBitmap(bitmap2);
                int i = this.mMaxColors;
                if (this.mFilters.isEmpty()) {
                    filterArr = null;
                } else {
                    List<Filter> list = this.mFilters;
                    filterArr = (Filter[]) list.toArray(new Filter[list.size()]);
                }
                ColorCutQuantizer quantizer = new ColorCutQuantizer(pixelsFromBitmap, i, filterArr);
                if (bitmap2 != this.mBitmap) {
                    bitmap2.recycle();
                }
                swatches = quantizer.getQuantizedColors();
                if (logger != null) {
                    logger.addSplit("Color quantization completed");
                }
            } else if (this.mSwatches != null) {
                swatches = this.mSwatches;
            } else {
                throw new AssertionError();
            }
            Palette p = new Palette(swatches, this.mTargets);
            p.generate();
            if (logger != null) {
                logger.addSplit("Created Palette");
                logger.dumpToLog();
            }
            return p;
        }

        public AsyncTask<Bitmap, Void, Palette> generate(final PaletteAsyncListener listener) {
            if (listener != null) {
                return new AsyncTask<Bitmap, Void, Palette>() {
                    /* access modifiers changed from: protected */
                    public Palette doInBackground(Bitmap... params) {
                        try {
                            return Builder.this.generate();
                        } catch (Exception e) {
                            Log.e(Palette.LOG_TAG, "Exception thrown during async generate", e);
                            return null;
                        }
                    }

                    /* access modifiers changed from: protected */
                    public void onPostExecute(Palette colorExtractor) {
                        listener.onGenerated(colorExtractor);
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Bitmap[]{this.mBitmap});
            }
            throw new IllegalArgumentException("listener can not be null");
        }

        private int[] getPixelsFromBitmap(Bitmap bitmap) {
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            int[] pixels = new int[(bitmapWidth * bitmapHeight)];
            bitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
            Rect rect = this.mRegion;
            if (rect == null) {
                return pixels;
            }
            int regionWidth = rect.width();
            int regionHeight = this.mRegion.height();
            int[] subsetPixels = new int[(regionWidth * regionHeight)];
            for (int row = 0; row < regionHeight; row++) {
                System.arraycopy(pixels, ((this.mRegion.top + row) * bitmapWidth) + this.mRegion.left, subsetPixels, row * regionWidth, regionWidth);
            }
            return subsetPixels;
        }

        private Bitmap scaleBitmapDown(Bitmap bitmap) {
            int maxDimension;
            int i;
            double scaleRatio = -1.0d;
            if (this.mResizeArea > 0) {
                int bitmapArea = bitmap.getWidth() * bitmap.getHeight();
                int i2 = this.mResizeArea;
                if (bitmapArea > i2) {
                    double d = (double) i2;
                    double d2 = (double) bitmapArea;
                    Double.isNaN(d);
                    Double.isNaN(d2);
                    scaleRatio = Math.sqrt(d / d2);
                }
            } else if (this.mResizeMaxDimension > 0 && (maxDimension = Math.max(bitmap.getWidth(), bitmap.getHeight())) > (i = this.mResizeMaxDimension)) {
                double d3 = (double) i;
                double d4 = (double) maxDimension;
                Double.isNaN(d3);
                Double.isNaN(d4);
                scaleRatio = d3 / d4;
            }
            if (scaleRatio <= Utils.DOUBLE_EPSILON) {
                return bitmap;
            }
            double width = (double) bitmap.getWidth();
            Double.isNaN(width);
            double height = (double) bitmap.getHeight();
            Double.isNaN(height);
            return Bitmap.createScaledBitmap(bitmap, (int) Math.ceil(width * scaleRatio), (int) Math.ceil(height * scaleRatio), false);
        }
    }
}

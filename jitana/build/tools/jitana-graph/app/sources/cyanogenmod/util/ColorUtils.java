package cyanogenmod.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.MathUtils;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.InputDeviceCompat;
import androidx.core.view.ViewCompat;
import com.android.internal.util.cm.palette.Palette;
import com.github.mikephil.charting.utils.Utils;
import java.util.Collections;
import java.util.Comparator;
import p005ch.qos.logback.classic.Level;

public class ColorUtils {
    private static int[] SOLID_COLORS = {SupportMenu.CATEGORY_MASK, -23296, InputDeviceCompat.SOURCE_ANY, -16711936, -16711681, -16776961, -65281, -1, ViewCompat.MEASURED_STATE_MASK};
    private static final double[] sColorTable = {1.0d, 0.18172716d, Utils.DOUBLE_EPSILON, 1.0d, 0.25503671d, Utils.DOUBLE_EPSILON, 1.0d, 0.30942099d, Utils.DOUBLE_EPSILON, 1.0d, 0.35357379d, Utils.DOUBLE_EPSILON, 1.0d, 0.39091524d, Utils.DOUBLE_EPSILON, 1.0d, 0.42322816d, Utils.DOUBLE_EPSILON, 1.0d, 0.45159884d, Utils.DOUBLE_EPSILON, 1.0d, 0.47675916d, Utils.DOUBLE_EPSILON, 1.0d, 0.49923747d, Utils.DOUBLE_EPSILON, 1.0d, 0.51943421d, Utils.DOUBLE_EPSILON, 1.0d, 0.54360078d, 0.08679949d, 1.0d, 0.56618736d, 0.14065513d, 1.0d, 0.58734976d, 0.18362641d, 1.0d, 0.60724493d, 0.22137978d, 1.0d, 0.62600248d, 0.2559195d, 1.0d, 0.64373109d, 0.28819679d, 1.0d, 0.66052319d, 0.31873863d, 1.0d, 0.67645822d, 0.34786758d, 1.0d, 0.69160518d, 0.37579588d, 1.0d, 0.70602449d, 0.40267128d, 1.0d, 0.71976951d, 0.42860152d, 1.0d, 0.7328876d, 0.45366838d, 1.0d, 0.74542112d, 0.47793608d, 1.0d, 0.75740814d, 0.50145662d, 1.0d, 0.76888303d, 0.52427322d, 1.0d, 0.77987699d, 0.54642268d, 1.0d, 0.79041843d, 0.56793692d, 1.0d, 0.80053332d, 0.58884417d, 1.0d, 0.81024551d, 0.60916971d, 1.0d, 0.81957693d, 0.62893653d, 1.0d, 0.82854786d, 0.6481657d, 1.0d, 0.83717703d, 0.66687674d, 1.0d, 0.84548188d, 0.68508786d, 1.0d, 0.85347859d, 0.70281616d, 1.0d, 0.86118227d, 0.72007777d, 1.0d, 0.86860704d, 0.73688797d, 1.0d, 0.87576611d, 0.75326132d, 1.0d, 0.88267187d, 0.76921169d, 1.0d, 0.88933596d, 0.78475236d, 1.0d, 0.89576933d, 0.79989606d, 1.0d, 0.9019823d, 0.81465502d, 1.0d, 0.90963069d, 0.8283821d, 1.0d, 0.91710889d, 0.84190889d, 1.0d, 0.92441842d, 0.85523742d, 1.0d, 0.93156127d, 0.86836903d, 1.0d, 0.93853986d, 0.88130458d, 1.0d, 0.94535695d, 0.8940447d, 1.0d, 0.95201559d, 0.90658983d, 1.0d, 0.95851906d, 0.91894041d, 1.0d, 0.96487079d, 0.9310969d, 1.0d, 0.97107439d, 0.94305985d, 1.0d, 0.97713351d, 0.95482993d, 1.0d, 0.98305189d, 0.96640795d, 1.0d, 0.98883326d, 0.97779486d, 1.0d, 0.99448139d, 0.98899179d, 1.0d, 1.0d, 1.0d, 0.98947904d, 0.99348723d, 1.0d, 0.97940448d, 0.98722715d, 1.0d, 0.96975025d, 0.98120637d, 1.0d, 0.96049223d, 0.9754124d, 1.0d, 0.95160805d, 0.96983355d, 1.0d, 0.94303638d, 0.96443333d, 1.0d, 0.93480451d, 0.9592308d, 1.0d, 0.92689056d, 0.95421394d, 1.0d, 0.91927697d, 0.9493733d, 1.0d, 0.91194747d, 0.94470005d, 1.0d, 0.9048869d, 0.94018594d, 1.0d, 0.89808115d, 0.93582323d, 1.0d, 0.8915171d, 0.93160469d, 1.0d, 0.88518247d, 0.92752354d, 1.0d, 0.87906581d, 0.9235734d, 1.0d, 0.8731564d, 0.91974827d, 1.0d, 0.86744421d, 0.91604254d, 1.0d, 0.86191983d, 0.91245088d, 1.0d, 0.85657444d, 0.90896831d, 1.0d, 0.85139976d, 0.90559011d, 1.0d, 0.84638799d, 0.90231183d, 1.0d, 0.8415318d, 0.89912926d, 1.0d, 0.8368243d, 0.89603843d, 1.0d, 0.83225897d, 0.89303558d, 1.0d, 0.82782969d, 0.89011714d, 1.0d, 0.82353066d, 0.88727974d, 1.0d, 0.81935641d, 0.88452017d, 1.0d, 0.81530175d, 0.88183541d, 1.0d, 0.8113618d, 0.87922257d, 1.0d, 0.80753191d, 0.87667891d, 1.0d, 0.80380769d, 0.87420182d, 1.0d, 0.80018497d, 0.87178882d, 1.0d, 0.7966598d, 0.86943756d, 1.0d, 0.79322843d, 0.86714579d, 1.0d, 0.78988728d, 0.86491137d, 1.0d, 0.78663296d, 0.86273225d, 1.0d, 0.78346225d, 0.8606065d, 1.0d, 0.78037207d, 0.85853224d, 1.0d, 0.7773595d, 0.85650771d, 1.0d, 0.77442176d, 0.85453121d, 1.0d, 0.77155617d, 0.85260112d, 1.0d, 0.76876022d, 0.85071588d, 1.0d, 0.76603147d, 0.84887402d, 1.0d, 0.76336762d, 0.84707411d, 1.0d, 0.76076645d, 0.84531479d, 1.0d, 0.75822586d, 0.84359476d, 1.0d, 0.75574383d, 0.84191277d, 1.0d, 0.75331843d, 0.84026762d, 1.0d, 0.7509478d, 0.83865816d, 1.0d, 0.74863017d, 0.83708329d, 1.0d, 0.74636386d, 0.83554194d, 1.0d, 0.74414722d, 0.83403311d, 1.0d, 0.74197871d, 0.83255582d, 1.0d, 0.73985682d, 0.83110912d, 1.0d, 0.73778012d, 0.82969211d, 1.0d, 0.73574723d, 0.82830393d, 1.0d, 0.73375683d, 0.82694373d, 1.0d, 0.73180765d, 0.82561071d, 1.0d, 0.72989845d, 0.8243041d, 1.0d, 0.72802807d, 0.82302316d, 1.0d, 0.72619537d, 0.82176715d, 1.0d, 0.72439927d, 0.82053539d, 1.0d, 0.72263872d, 0.81932722d, 1.0d, 0.7209127d, 0.81814197d, 1.0d, 0.71922025d, 0.81697905d, 1.0d, 0.71756043d, 0.81583783d, 1.0d, 0.71593234d, 0.81471775d, 1.0d, 0.7143351d, 0.81361825d, 1.0d, 0.71276788d, 0.81253878d, 1.0d, 0.71122987d, 0.81147883d, 1.0d, 0.70972029d, 0.81043789d, 1.0d, 0.70823838d, 0.80941546d, 1.0d, 0.70678342d, 0.80841109d, 1.0d, 0.70535469d, 0.80742432d, 1.0d, 0.70395153d, 0.80645469d, 1.0d, 0.70257327d, 0.8055018d, 1.0d, 0.70121928d, 0.80456522d, 1.0d, 0.69988894d, 0.80364455d, 1.0d, 0.69858167d, 0.80273941d, 1.0d, 0.69729688d, 0.80184943d, 1.0d, 0.69603402d, 0.80097423d, 1.0d, 0.69479255d, 0.80011347d, 1.0d, 0.69357196d, 0.79926681d, 1.0d, 0.69237173d, 0.79843391d, 1.0d, 0.69119138d, 0.79761446d, 1.0d, 0.69003044d, 0.79680814d, 1.0d, 0.68888844d, 0.79601466d, 1.0d, 0.68776494d, 0.79523371d, 1.0d, 0.68665951d, 0.79446502d, 1.0d, 0.68557173d, 0.7937083d, 1.0d, 0.68450119d, 0.7929633d, 1.0d, 0.68344751d, 0.79222975d, 1.0d, 0.68241029d, 0.7915074d, 1.0d, 0.68138918d, 0.790796d, 1.0d, 0.6803838d, 0.79009531d, 1.0d, 0.67939381d, 0.78940511d, 1.0d, 0.67841888d, 0.78872517d, 1.0d, 0.67745866d, 0.78805526d, 1.0d, 0.67651284d, 0.78739518d, 1.0d, 0.67558112d, 0.78674472d, 1.0d, 0.67466317d, 0.78610368d, 1.0d, 0.67375872d, 0.78547186d, 1.0d, 0.67286748d, 0.78484907d, 1.0d, 0.67198916d, 0.78423512d, 1.0d, 0.6711235d, 0.78362984d, 1.0d, 0.67027024d, 0.78303305d, 1.0d, 0.66942911d, 0.78244457d, 1.0d, 0.66859988d, 0.78186425d, 1.0d, 0.66778228d, 0.78129191d, 1.0d, 0.6669761d, 0.7807274d, 1.0d, 0.6661811d, 0.78017057d, 1.0d, 0.66539706d, 0.77962127d, 1.0d, 0.66462376d, 0.77907934d, 1.0d, 0.66386098d, 0.77854465d, 1.0d, 0.66310852d, 0.77801705d, 1.0d, 0.66236618d, 0.77749642d, 1.0d, 0.66163375d, 0.77698261d, 1.0d, 0.66091106d, 0.77647551d, 1.0d, 0.66019791d, 0.77597498d, 1.0d, 0.65949412d, 0.7754809d, 1.0d, 0.65879952d, 0.77499315d, 1.0d, 0.65811392d, 0.77451161d, 1.0d, 0.65743716d, 0.77403618d, 1.0d, 0.65676908d, 0.77356673d, 1.0d, 0.65610952d, 0.77310316d, 1.0d, 0.65545831d, 0.77264537d, 1.0d, 0.6548153d, 0.77219324d, 1.0d, 0.65418036d, 0.77174669d, 1.0d, 0.65355332d, 0.7713056d, 1.0d, 0.65293404d, 0.77086988d, 1.0d, 0.6523224d, 0.77043944d, 1.0d, 0.65171824d, 0.77001419d, 1.0d, 0.65112144d, 0.76959404d, 1.0d, 0.65053187d, 0.76917889d, 1.0d, 0.64994941d, 0.76876866d, 1.0d, 0.64937392d, 0.76836326d, 1.0d};

    public static int dropAlpha(int rgba) {
        return 16777215 & rgba;
    }

    public static float[] convertRGBtoLAB(int rgb) {
        float r;
        float g;
        float b;
        float fx;
        float fy;
        float fz;
        float[] lab = new float[3];
        float r2 = ((float) Color.red(rgb)) / 255.0f;
        float g2 = ((float) Color.green(rgb)) / 255.0f;
        float b2 = ((float) Color.blue(rgb)) / 255.0f;
        if (((double) r2) <= 0.04045d) {
            r = r2 / 12.0f;
        } else {
            double d = (double) r2;
            Double.isNaN(d);
            r = (float) Math.pow((d + 0.055d) / 1.055d, 2.4d);
        }
        if (((double) g2) <= 0.04045d) {
            g = g2 / 12.0f;
        } else {
            double d2 = (double) g2;
            Double.isNaN(d2);
            g = (float) Math.pow((d2 + 0.055d) / 1.055d, 2.4d);
        }
        if (((double) b2) <= 0.04045d) {
            b = b2 / 12.0f;
        } else {
            double d3 = (double) b2;
            Double.isNaN(d3);
            b = (float) Math.pow((d3 + 0.055d) / 1.055d, 2.4d);
        }
        float X = (0.43605202f * r) + (0.3850816f * g) + (0.14308742f * b);
        float xr = X / 0.964221f;
        float yr = (((0.22249159f * r) + (0.71688604f * g)) + (0.060621485f * b)) / 1.0f;
        float zr = (((0.013929122f * r) + (0.097097f * g)) + (0.7141855f * b)) / 0.825211f;
        if (xr > 0.008856452f) {
            float f = r;
            fx = (float) Math.pow((double) xr, 0.3333333333333333d);
        } else {
            float f2 = r;
            double d4 = (double) (903.2963f * xr);
            Double.isNaN(d4);
            fx = (float) ((d4 + 16.0d) / 116.0d);
        }
        if (yr > 0.008856452f) {
            float f3 = X;
            fy = (float) Math.pow((double) yr, 0.3333333333333333d);
        } else {
            double d5 = (double) (903.2963f * yr);
            Double.isNaN(d5);
            fy = (float) ((d5 + 16.0d) / 116.0d);
        }
        if (zr > 0.008856452f) {
            float f4 = g;
            fz = (float) Math.pow((double) zr, 0.3333333333333333d);
        } else {
            double d6 = (double) (903.2963f * zr);
            Double.isNaN(d6);
            fz = (float) ((d6 + 16.0d) / 116.0d);
        }
        lab[0] = (2.55f * ((116.0f * fy) - 16.0f)) + 0.5f;
        lab[1] = ((fx - fy) * 500.0f) + 0.5f;
        lab[2] = ((fy - fz) * 200.0f) + 0.5f;
        return lab;
    }

    public static double calculateDeltaE(double L1, double a1, double b1, double L2, double a2, double b2) {
        double d;
        double d2;
        double d3 = b1;
        double d4 = b2;
        double Lmean = (L1 + L2) / 2.0d;
        double C1 = Math.sqrt((a1 * a1) + (d3 * d3));
        double Cmean = (C1 + Math.sqrt((a2 * a2) + (d4 * d4))) / 2.0d;
        double G = (1.0d - Math.sqrt(Math.pow(Cmean, 7.0d) / (Math.pow(Cmean, 7.0d) + Math.pow(25.0d, 7.0d)))) / 2.0d;
        double a1prime = a1 * (G + 1.0d);
        double a2prime = a2 * (G + 1.0d);
        double C1prime = Math.sqrt((a1prime * a1prime) + (d3 * d3));
        double C2prime = Math.sqrt((a2prime * a2prime) + (d4 * d4));
        double d5 = C1;
        double Cmeanprime = (C1prime + C2prime) / 2.0d;
        double atan2 = Math.atan2(d3, a1prime);
        double d6 = (double) (Math.atan2(d3, a1prime) < Utils.DOUBLE_EPSILON ? 1 : 0);
        Double.isNaN(d6);
        double h1prime = atan2 + (d6 * 6.283185307179586d);
        double atan22 = Math.atan2(d4, a2prime);
        double d7 = (double) (Math.atan2(d4, a2prime) < Utils.DOUBLE_EPSILON ? 1 : 0);
        Double.isNaN(d7);
        double h2prime = atan22 + (d7 * 6.283185307179586d);
        if (Math.abs(h1prime - h2prime) > 3.141592653589793d) {
            d2 = h1prime + h2prime + 6.283185307179586d;
            d = 2.0d;
        } else {
            d = 2.0d;
            d2 = h1prime + h2prime;
        }
        double Hmeanprime = d2 / d;
        double T = (((1.0d - (Math.cos(Hmeanprime - 0.5235987755982988d) * 0.17d)) + (Math.cos(Hmeanprime * 2.0d) * 0.24d)) + (Math.cos((3.0d * Hmeanprime) + 0.10471975511965977d) * 0.32d)) - (Math.cos((4.0d * Hmeanprime) - 1.0995574287564276d) * 0.2d);
        double deltaLprime = L2 - L1;
        double deltaCprime = C2prime - C1prime;
        double deltaHprime = Math.sqrt(C1prime * C2prime) * 2.0d * Math.sin((Math.abs(h1prime - h2prime) <= 3.141592653589793d ? h2prime - h1prime : h2prime <= h1prime ? (h2prime - h1prime) + 6.283185307179586d : (h2prime - h1prime) - 6.283185307179586d) / 2.0d);
        double SL = ((((Lmean - 50.0d) * 0.015d) * (Lmean - 50.0d)) / Math.sqrt(((Lmean - 50.0d) * (Lmean - 50.0d)) + 20.0d)) + 1.0d;
        double SC = (0.045d * Cmeanprime) + 1.0d;
        double SH = (0.015d * Cmeanprime * T) + 1.0d;
        double d8 = h2prime;
        double d9 = Lmean;
        double d10 = Hmeanprime;
        double d11 = a2prime;
        return Math.sqrt(((deltaLprime / (1.0d * SL)) * (deltaLprime / (1.0d * SL))) + ((deltaCprime / (1.0d * SC)) * (deltaCprime / (1.0d * SC))) + ((deltaHprime / (1.0d * SH)) * (deltaHprime / (1.0d * SH))) + ((deltaCprime / (1.0d * SC)) * (-(Math.sqrt(Math.pow(Cmeanprime, 7.0d) / (Math.pow(Cmeanprime, 7.0d) + Math.pow(25.0d, 7.0d))) * 2.0d)) * Math.sin(2.0d * Math.exp((-(((Hmeanprime * 57.29577951308232d) - 275.0d) / 25.0d)) * (((57.29577951308232d * Hmeanprime) - 275.0d) / 25.0d)) * 0.5235987755982988d) * (deltaHprime / (1.0d * SH))));
    }

    public static int findPerceptuallyNearestColor(int rgb, int[] colors) {
        int[] iArr = colors;
        int nearestColor = 0;
        double closest = Double.MAX_VALUE;
        float[] original = convertRGBtoLAB(rgb);
        int i = 0;
        while (i < iArr.length) {
            float[] cl = convertRGBtoLAB(iArr[i]);
            double d = (double) cl[0];
            int nearestColor2 = nearestColor;
            double d2 = d;
            double deltaE = calculateDeltaE((double) original[0], (double) original[1], (double) original[2], d2, (double) cl[1], (double) cl[2]);
            if (deltaE < closest) {
                closest = deltaE;
                nearestColor = colors[i];
            } else {
                nearestColor = nearestColor2;
            }
            i++;
            iArr = colors;
        }
        return nearestColor;
    }

    public static int findPerceptuallyNearestSolidColor(int rgb) {
        return findPerceptuallyNearestColor(rgb, SOLID_COLORS);
    }

    public static Palette.Swatch getDominantSwatch(Palette palette) {
        if (palette == null || palette.getSwatches().size() == 0) {
            return null;
        }
        return (Palette.Swatch) Collections.max(palette.getSwatches(), new Comparator<Palette.Swatch>() {
            public int compare(Palette.Swatch sw1, Palette.Swatch sw2) {
                return Integer.compare(sw1.getPopulation(), sw2.getPopulation());
            }
        });
    }

    public static int generateAlertColorFromDrawable(Drawable drawable) {
        Bitmap bitmap;
        int alertColor = ViewCompat.MEASURED_STATE_MASK;
        if (drawable == null) {
            return ViewCompat.MEASURED_STATE_MASK;
        }
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            bitmap = Bitmap.createBitmap(Math.max(1, drawable.getIntrinsicWidth()), Math.max(1, drawable.getIntrinsicHeight()), Bitmap.Config.ARGB_8888);
            drawable.draw(new Canvas(bitmap));
        }
        if (bitmap != null) {
            Palette p = Palette.from(bitmap).generate();
            if (p == null) {
                return ViewCompat.MEASURED_STATE_MASK;
            }
            Palette.Swatch dominantSwatch = getDominantSwatch(p);
            if (dominantSwatch != null) {
                alertColor = findPerceptuallyNearestSolidColor(dominantSwatch.getRgb());
            }
            if (alertColor == -16777216 || alertColor == -1) {
                alertColor = findPerceptuallyNearestSolidColor(p.getVibrantColor(-1));
            }
            if (!(drawable instanceof BitmapDrawable)) {
                bitmap.recycle();
            }
        }
        return alertColor;
    }

    public static float[] temperatureToRGB(int degreesK) {
        int k = MathUtils.constrain(degreesK, 1000, Level.INFO_INT);
        float a = ((float) (k % 100)) / 100.0f;
        int i = ((k + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) / 100) * 3;
        return new float[]{interp(i, a), interp(i + 1, a), interp(i + 2, a)};
    }

    private static float interp(int i, float a) {
        double[] dArr = sColorTable;
        return MathUtils.lerp((float) dArr[i], (float) dArr[i + 3], a);
    }
}

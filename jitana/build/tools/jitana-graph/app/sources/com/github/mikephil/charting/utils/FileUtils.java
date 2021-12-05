package com.github.mikephil.charting.utils;

import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.CharEncoding;

public class FileUtils {
    private static final String LOG = "MPChart-FileUtils";

    public static List<Entry> loadEntriesFromFile(String path) {
        File file = new File(Environment.getExternalStorageDirectory(), path);
        List<Entry> entries = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while (true) {
                String readLine = br.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                String[] split = line.split("#");
                if (split.length <= 2) {
                    entries.add(new Entry(Float.parseFloat(split[0]), (float) Integer.parseInt(split[1])));
                } else {
                    float[] vals = new float[(split.length - 1)];
                    for (int i = 0; i < vals.length; i++) {
                        vals[i] = Float.parseFloat(split[i]);
                    }
                    entries.add(new BarEntry((float) Integer.parseInt(split[split.length - 1]), vals));
                }
            }
        } catch (IOException e) {
            Log.e(LOG, e.toString());
        }
        return entries;
    }

    public static List<Entry> loadEntriesFromAssets(AssetManager am, String path) {
        List<Entry> entries = new ArrayList<>();
        BufferedReader reader = null;
        try {
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(am.open(path), CharEncoding.UTF_8));
            for (String line = reader2.readLine(); line != null; line = reader2.readLine()) {
                String[] split = line.split("#");
                if (split.length <= 2) {
                    entries.add(new Entry(Float.parseFloat(split[1]), Float.parseFloat(split[0])));
                } else {
                    float[] vals = new float[(split.length - 1)];
                    for (int i = 0; i < vals.length; i++) {
                        vals[i] = Float.parseFloat(split[i]);
                    }
                    entries.add(new BarEntry((float) Integer.parseInt(split[split.length - 1]), vals));
                }
            }
            try {
                reader2.close();
            } catch (IOException e) {
                Log.e(LOG, e.toString());
            }
        } catch (IOException e2) {
            Log.e(LOG, e2.toString());
            if (reader != null) {
                reader.close();
            }
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    Log.e(LOG, e3.toString());
                }
            }
            throw th;
        }
        return entries;
    }

    public static void saveToSdCard(List<Entry> entries, String path) {
        File saved = new File(Environment.getExternalStorageDirectory(), path);
        if (!saved.exists()) {
            try {
                saved.createNewFile();
            } catch (IOException e) {
                Log.e(LOG, e.toString());
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(saved, true));
            for (Entry e2 : entries) {
                buf.append(e2.getY() + "#" + e2.getX());
                buf.newLine();
            }
            buf.close();
        } catch (IOException e3) {
            Log.e(LOG, e3.toString());
        }
    }

    public static List<BarEntry> loadBarEntriesFromAssets(AssetManager am, String path) {
        List<BarEntry> entries = new ArrayList<>();
        BufferedReader reader = null;
        try {
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(am.open(path), CharEncoding.UTF_8));
            for (String line = reader2.readLine(); line != null; line = reader2.readLine()) {
                String[] split = line.split("#");
                entries.add(new BarEntry(Float.parseFloat(split[1]), Float.parseFloat(split[0])));
            }
            try {
                reader2.close();
            } catch (IOException e) {
                Log.e(LOG, e.toString());
            }
        } catch (IOException e2) {
            Log.e(LOG, e2.toString());
            if (reader != null) {
                reader.close();
            }
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    Log.e(LOG, e3.toString());
                }
            }
            throw th;
        }
        return entries;
    }
}

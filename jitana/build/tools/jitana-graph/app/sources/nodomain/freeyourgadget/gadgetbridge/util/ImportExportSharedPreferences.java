package nodomain.freeyourgadget.gadgetbridge.util;

import android.content.SharedPreferences;
import android.util.Xml;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import org.apache.commons.lang3.CharEncoding;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class ImportExportSharedPreferences {
    private static final String BOOLEAN = Boolean.class.getSimpleName();
    private static final String FLOAT = Float.class.getSimpleName();
    private static final String HASHSET = HashSet.class.getSimpleName();
    private static final String INTEGER = Integer.class.getSimpleName();
    private static final String LONG = Long.class.getSimpleName();
    private static final String NAME = "name";
    private static final String PREFERENCES = "preferences";
    private static final String STRING = String.class.getSimpleName();

    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0013, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0014, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0017, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x000e, code lost:
        r2 = move-exception;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void exportToFile(android.content.SharedPreferences r4, java.io.File r5, java.util.Set<java.lang.String> r6) throws java.io.IOException {
        /*
            java.io.FileWriter r0 = new java.io.FileWriter
            r0.<init>(r5)
            export(r4, r0, r6)     // Catch:{ all -> 0x000c }
            r0.close()
            return
        L_0x000c:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x000e }
        L_0x000e:
            r2 = move-exception
            r0.close()     // Catch:{ all -> 0x0013 }
            goto L_0x0017
        L_0x0013:
            r3 = move-exception
            r1.addSuppressed(r3)
        L_0x0017:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.util.ImportExportSharedPreferences.exportToFile(android.content.SharedPreferences, java.io.File, java.util.Set):void");
    }

    private static void export(SharedPreferences sharedPreferences, Writer writer, Set<String> doNotExport) throws IOException {
        Object valueObject;
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(writer);
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        serializer.startDocument(CharEncoding.UTF_8, true);
        serializer.startTag("", PREFERENCES);
        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            String key = entry.getKey();
            if ((doNotExport == null || !doNotExport.contains(key)) && (valueObject = entry.getValue()) != null) {
                String valueType = valueObject.getClass().getSimpleName();
                String value = valueObject.toString();
                serializer.startTag("", valueType);
                serializer.attribute("", "name", key);
                serializer.text(value);
                serializer.endTag("", valueType);
            }
        }
        serializer.endTag("", PREFERENCES);
        serializer.endDocument();
    }

    public static boolean importFromFile(SharedPreferences sharedPreferences, File inFile) throws Exception {
        return importFromReader(sharedPreferences, new FileReader(inFile));
    }

    private static boolean importFromReader(SharedPreferences sharedPreferences, Reader in) throws Exception {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in);
        String name = null;
        String key = null;
        for (int eventType = parser.getEventType(); eventType != 1; eventType = parser.next()) {
            if (eventType == 2) {
                name = parser.getName();
                key = parser.getAttributeValue("", "name");
            } else if (eventType == 3) {
                name = null;
            } else if (eventType == 4 && name != null) {
                String text = parser.getText();
                if (BOOLEAN.equals(name)) {
                    editor.putBoolean(key, Boolean.parseBoolean(text));
                } else if (FLOAT.equals(name)) {
                    editor.putFloat(key, Float.parseFloat(text));
                } else if (INTEGER.equals(name)) {
                    editor.putInt(key, Integer.parseInt(text));
                } else if (LONG.equals(name)) {
                    editor.putLong(key, Long.parseLong(text));
                } else if (STRING.equals(name)) {
                    editor.putString(key, text);
                } else if (HASHSET.equals(name)) {
                    char c = 65535;
                    int hashCode = key.hashCode();
                    if (hashCode != -1724274340) {
                        if (hashCode != 226313720) {
                            if (hashCode == 389167204 && key.equals(GBPrefs.PACKAGE_BLACKLIST)) {
                                c = 0;
                            }
                        } else if (key.equals(GBPrefs.PACKAGE_PEBBLEMSG_BLACKLIST)) {
                            c = 1;
                        }
                    } else if (key.equals(GBPrefs.CALENDAR_BLACKLIST)) {
                        c = 2;
                    }
                    if (c == 0) {
                        Set<String> apps_blacklist = new HashSet<>();
                        String text2 = text.replace("[", "").replace("]", "");
                        for (String trim : text2.split(",")) {
                            apps_blacklist.add(trim.trim());
                        }
                        GBApplication.setAppsNotifBlackList(apps_blacklist);
                    } else if (c == 1) {
                        Set<String> apps_pebble_blacklist = new HashSet<>();
                        String text3 = text.replace("[", "").replace("]", "");
                        for (String trim2 : text3.split(",")) {
                            apps_pebble_blacklist.add(trim2.trim());
                        }
                        GBApplication.setAppsPebbleBlackList(apps_pebble_blacklist);
                    } else if (c == 2) {
                        Set<String> calendars_blacklist = new HashSet<>();
                        String text4 = text.replace("[", "").replace("]", "");
                        for (String trim3 : text4.split(",")) {
                            calendars_blacklist.add(trim3.trim());
                        }
                        GBApplication.setCalendarsBlackList(calendars_blacklist);
                    }
                } else if (!PREFERENCES.equals(name)) {
                    throw new Exception("Unknown type " + name);
                }
            }
        }
        return editor.commit();
    }
}

package nodomain.freeyourgadget.gadgetbridge.util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;
import p005ch.qos.logback.core.rolling.helper.DateTokenConverter;
import p005ch.qos.logback.core.rolling.helper.IntegerTokenConverter;

public class BengaliLanguageUtils extends LanguageUtils {
    private static final Pattern bengaliRegex = Pattern.compile(pattern);
    private static final HashMap<String, String> composites = new HashMap<String, String>() {
        {
            put("ক্ষ", "kkh");
            put("ঞ্চ", "NC");
            put("ঞ্ছ", "NCh");
            put("ঞ্জ", "Ng");
            put("জ্ঞ", "gg");
            put("ঞ্ঝ", "Ngh");
            put("্র", "r");
            put("্ল", "l");
            put("ষ্ম", "SSh");
            put("র্", "r");
            put("্য", "y");
            put("্ব", "w");
        }
    };
    private static final HashMap<String, String> letters = new HashMap<String, String>() {
        {
            put("আ", "aa");
            put("অ", "a");
            put("ই", IntegerTokenConverter.CONVERTER_KEY);
            put("ঈ", "ii");
            put("উ", "u");
            put("ঊ", "uu");
            put("ঋ", "ri");
            put("এ", "e");
            put("ঐ", "oi");
            put("ও", "o");
            put("ঔ", "ou");
            put("ক", "k");
            put("খ", "kh");
            put("গ", "g");
            put("ঘ", "gh");
            put("ঙ", "ng");
            put("চ", "ch");
            put("ছ", "chh");
            put("জ", "j");
            put("ঝ", "jh");
            put("ঞ", "Ng");
            put("ট", "T");
            put("ঠ", "Th");
            put("ড", "D");
            put("ঢ", "Dh");
            put("ণ", "N");
            put("ত", "t");
            put("থ", "th");
            put("দ", DateTokenConverter.CONVERTER_KEY);
            put("ধ", "dh");
            put("ন", "n");
            put("প", "p");
            put("ফ", "ph");
            put("ব", "b");
            put("ভ", "bh");
            put("ম", "m");
            put("য", "J");
            put("র", "r");
            put("ল", "l");
            put("শ", "sh");
            put("ষ", "Sh");
            put("স", "s");
            put("হ", "h");
            put("ড়", "rh");
            put("ঢ়", "rH");
            put("য়", "y");
            put("ৎ", "t");
            put("০", "0");
            put("১", MiBandConst.MI_1);
            put("২", MiBandConst.MI_PRO);
            put("৩", "3");
            put("৪", "4");
            put("৫", "5");
            put("৬", "6");
            put("৭", "7");
            put("৮", "8");
            put("৯", "9");
            put("া", "aa");
            put("ি", IntegerTokenConverter.CONVERTER_KEY);
            put("ী", "ii");
            put("ু", "u");
            put("ূ", "uu");
            put("ৃ", "r");
            put("ে", "e");
            put("ো", "o");
            put("ৈ", "oi");
            put("ৗ", "ou");
            put("ৌ", "ou");
            put("ং", "ng");
            put("ঃ", "h");
            put("ঁ", "nN");
            put("।", ".");
        }
    };
    private static final String pattern = "(র্){0,1}(([অ-হড়-য়])(্([অ-মশ-হড়-য়]))*)((‍){0,1}(্([য-ল]))){0,1}([া-ৌ]){0,1}|([্ঁঃংৎ০-৯।])|(\\s)";
    private static final HashMap<String, String> vowels = new HashMap<String, String>() {
        {
            put("আ", "aa");
            put("অ", "a");
            put("ই", IntegerTokenConverter.CONVERTER_KEY);
            put("ঈ", "ii");
            put("উ", "u");
            put("ঊ", "uu");
            put("ঋ", "ri");
            put("এ", "e");
            put("ঐ", "oi");
            put("ও", "o");
            put("ঔ", "ou");
        }
    };
    private static final HashMap<String, String> vowelsAndHasants = new HashMap<String, String>() {
        {
            put("আ", "aa");
            put("অ", "a");
            put("ই", IntegerTokenConverter.CONVERTER_KEY);
            put("ঈ", "ii");
            put("উ", "u");
            put("ঊ", "uu");
            put("ঋ", "ri");
            put("এ", "e");
            put("ঐ", "oi");
            put("ও", "o");
            put("ঔ", "ou");
            put("া", "aa");
            put("ি", IntegerTokenConverter.CONVERTER_KEY);
            put("ী", "ii");
            put("ু", "u");
            put("ূ", "uu");
            put("ৃ", "r");
            put("ে", "e");
            put("ো", "o");
            put("ৈ", "oi");
            put("ৗ", "ou");
            put("ৌ", "ou");
            put("ং", "ng");
            put("ঃ", "h");
            put("।", ".");
        }
    };

    private static String getVal(String key) {
        if (key == null) {
            return null;
        }
        String comp = composites.get(key);
        if (comp != null) {
            return comp;
        }
        if (letters.get(key) != null) {
            return letters.get(key);
        }
        return null;
    }

    public static String transliterate(String txt) {
        boolean thisNeedsO;
        String appendableString;
        boolean thisNeedsO2;
        boolean thisNeedsO3;
        boolean lastHadKaar;
        String str = txt;
        if (txt.isEmpty()) {
            return str;
        }
        Matcher m = bengaliRegex.matcher(str);
        StringBuffer sb = new StringBuffer();
        String lastChar = "";
        boolean lastHadComposition = false;
        boolean lastHadKaar2 = false;
        boolean nextNeedsO = false;
        int lastHadO = 0;
        while (m.find()) {
            boolean changePronounciation = false;
            String appendableString2 = "";
            String reff = m.group(1);
            if (reff != null) {
                appendableString2 = appendableString2 + "rr";
            }
            String mainPart = getVal(m.group(2));
            if (mainPart == null) {
                String firstPart = getVal(m.group(3));
                if (firstPart != null) {
                    appendableString2 = appendableString2 + firstPart;
                }
                int g = 4;
                while (true) {
                    if (g >= 6) {
                        thisNeedsO = false;
                        appendableString = appendableString2;
                        break;
                    }
                    String part = getVal(m.group(g));
                    if (part != null) {
                        thisNeedsO = false;
                        appendableString = appendableString2 + part;
                        break;
                    }
                    g++;
                    String part2 = txt;
                }
            } else {
                thisNeedsO = false;
                appendableString = appendableString2 + mainPart;
            }
            if (m.group(2) == null || !m.group(2).equals("ক্ষ")) {
                thisNeedsO2 = thisNeedsO;
            } else {
                changePronounciation = true;
                thisNeedsO2 = true;
            }
            int g2 = 6;
            while (true) {
                if (g2 >= 10) {
                    break;
                }
                String key = getVal(m.group(g2));
                if (key != null) {
                    appendableString = appendableString + key;
                    break;
                }
                g2++;
            }
            String phala = m.group(8);
            if (phala != null && phala.equals("্য")) {
                changePronounciation = true;
                thisNeedsO2 = true;
            }
            if (m.group(4) != null) {
                thisNeedsO2 = true;
            }
            String kaar = m.group(10);
            if (kaar != null) {
                thisNeedsO3 = thisNeedsO2;
                String kaarStr = letters.get(kaar);
                if (kaarStr != null) {
                    int i = g2;
                    appendableString = appendableString + kaarStr;
                    if (kaarStr.equals(IntegerTokenConverter.CONVERTER_KEY) || kaarStr.equals("ii") || kaarStr.equals("u") || kaarStr.equals("uu")) {
                        changePronounciation = true;
                    }
                }
            } else {
                thisNeedsO3 = thisNeedsO2;
                int i2 = g2;
            }
            String singleton = m.group(11);
            if (singleton != null) {
                String singleStr = letters.get(singleton);
                if (singleStr != null) {
                    String str2 = singleton;
                    appendableString = appendableString + singleStr;
                }
            }
            if (changePronounciation && lastChar.equals("a")) {
                sb.setCharAt(sb.length() - 1, 'o');
            }
            String others = m.group(0);
            if (others != null && appendableString.length() <= 0) {
                appendableString = appendableString + others;
            }
            String whitespace = m.group(12);
            String str3 = lastChar;
            if (nextNeedsO && kaar == null && whitespace == null) {
                boolean z = nextNeedsO;
                String str4 = others;
                String str5 = phala;
                if (!vowels.containsKey(m.group(0))) {
                    appendableString = appendableString + "o";
                    lastHadO++;
                    thisNeedsO3 = false;
                }
            } else {
                String str6 = others;
                String str7 = phala;
            }
            if (((kaar != null && lastHadO > 1) || whitespace != null) && !lastHadKaar2 && sb.length() > 0 && sb.charAt(sb.length() - 1) == 'o' && !lastHadComposition) {
                sb.deleteCharAt(sb.length() - 1);
                lastHadO = 0;
            }
            nextNeedsO = false;
            if (thisNeedsO3 && kaar == null && whitespace == null) {
                String str8 = whitespace;
                if (!vowels.containsKey(m.group(0))) {
                    appendableString = appendableString + "o";
                    lastHadO++;
                }
            }
            if (appendableString.length() > 0 && !vowelsAndHasants.containsKey(m.group(0)) && kaar == null) {
                nextNeedsO = true;
            }
            if (reff == null && m.group(4) == null && m.group(6) == null) {
                lastHadComposition = false;
            } else {
                lastHadComposition = true;
            }
            if (kaar != null) {
                lastHadKaar = true;
            } else {
                lastHadKaar = false;
            }
            lastHadKaar2 = lastHadKaar;
            m.appendReplacement(sb, appendableString);
            lastChar = appendableString;
            String str9 = txt;
        }
        String str10 = lastChar;
        boolean z2 = nextNeedsO;
        if (!lastHadKaar2 && sb.length() > 0 && sb.charAt(sb.length() - 1) == 'o' && !lastHadComposition) {
            sb.deleteCharAt(sb.length() - 1);
        }
        m.appendTail(sb);
        return sb.toString();
    }
}

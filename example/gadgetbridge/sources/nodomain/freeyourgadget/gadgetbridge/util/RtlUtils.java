package nodomain.freeyourgadget.gadgetbridge.util;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import org.apache.commons.lang3.StringEscapeUtils;
import p005ch.qos.logback.core.CoreConstants;

public class RtlUtils {
    private static Map<Character, Character> contextualArabicBeginning = new HashMap<Character, Character>() {
        {
            put(1576, 65169);
            put(1578, 65175);
            put(1579, 65179);
            put(1580, 65183);
            put(1581, 65187);
            put(1582, 65191);
            put(1587, 65203);
            put(1588, 65207);
            put(1589, 65211);
            put(1590, 65215);
            put(1591, 65219);
            put(1592, 65223);
            put(1593, 65227);
            put(1594, 65231);
            put(1601, 65235);
            put(1602, 65239);
            put(1603, 65243);
            put(1604, 65247);
            put(1605, 65251);
            put(1606, 65255);
            put(1607, 65259);
            put(1610, 65267);
            put(1574, 65163);
            put(1711, 64404);
            put(1705, 64400);
            put(1670, 64380);
            put(1662, 64344);
            put(1740, 64510);
        }
    };
    private static Map<Character, Character> contextualArabicEnd = new HashMap<Character, Character>() {
        {
            put(1575, 65166);
            put(1576, 65168);
            put(1578, 65174);
            put(1579, 65178);
            put(1580, 65182);
            put(1581, 65186);
            put(1582, 65190);
            put(1583, 65194);
            put(1584, 65196);
            put(1585, 65198);
            put(1586, 65200);
            put(1587, 65202);
            put(1588, 65206);
            put(1589, 65210);
            put(1590, 65214);
            put(1591, 65218);
            put(1592, 65222);
            put(1593, 65226);
            put(1594, 65230);
            put(1601, 65234);
            put(1602, 65238);
            put(1603, 65242);
            put(1604, 65246);
            put(1605, 65250);
            put(1606, 65254);
            put(1607, 65258);
            put(1608, 65262);
            put(1610, 65266);
            put(1570, 65154);
            put(1577, 65172);
            put(1609, 65264);
            put(1574, 65162);
            put(1573, 65160);
            put(1571, 65156);
            put(1572, 65158);
            put(3174, 65270);
            put(3175, 65272);
            put(3177, 65274);
            put(3179, 65276);
            put(1711, 64403);
            put(1705, 64399);
            put(1670, 64379);
            put(1662, 64343);
            put(1688, 64395);
            put(1740, 64509);
        }
    };
    private static Map<Character, Character> contextualArabicIsolated = new HashMap<Character, Character>() {
        {
            put(1575, 65165);
            put(1576, 65167);
            put(1578, 65173);
            put(1579, 65177);
            put(1580, 65181);
            put(1581, 65185);
            put(1582, 65189);
            put(1583, 65193);
            put(1584, 65195);
            put(1585, 65197);
            put(1586, 65199);
            put(1587, 65201);
            put(1588, 65205);
            put(1589, 65209);
            put(1590, 65213);
            put(1591, 65217);
            put(1592, 65221);
            put(1593, 65225);
            put(1594, 65229);
            put(1601, 65233);
            put(1602, 65237);
            put(1603, 65241);
            put(1604, 65245);
            put(1605, 65249);
            put(1606, 65253);
            put(1607, 65257);
            put(1608, 65261);
            put(1610, 65265);
            put(1570, 65153);
            put(1577, 65171);
            put(1609, 65263);
            put(1574, 65161);
            put(1573, 65159);
            put(1571, 65155);
            put(1569, 65152);
            put(1572, 65157);
            put(3174, 65269);
            put(3175, 65271);
            put(3177, 65273);
            put(3179, 65275);
            put(1711, 64402);
            put(1705, 64398);
            put(1670, 64378);
            put(1662, 64342);
            put(1688, 64394);
            put(1740, 64508);
        }
    };
    private static Map<Character, Character> contextualArabicMiddle = new HashMap<Character, Character>() {
        {
            put(1576, 65170);
            put(1578, 65176);
            put(1579, 65180);
            put(1580, 65184);
            put(1581, 65188);
            put(1582, 65192);
            put(1587, 65204);
            put(1588, 65208);
            put(1589, 65212);
            put(1590, 65216);
            put(1591, 65220);
            put(1592, 65224);
            put(1593, 65228);
            put(1594, 65232);
            put(1601, 65236);
            put(1602, 65240);
            put(1603, 65244);
            put(1604, 65248);
            put(1605, 65252);
            put(1606, 65256);
            put(1607, 65260);
            put(1610, 65268);
            put(1574, 65164);
            put(1711, 64405);
            put(1705, 64401);
            put(1670, 64381);
            put(1662, 64345);
            put(1740, 64511);
        }
    };
    private static Map<Character, Character> directionSignsMap = new HashMap<Character, Character>() {
        {
            Character valueOf = Character.valueOf(CoreConstants.LEFT_PARENTHESIS_CHAR);
            Character valueOf2 = Character.valueOf(CoreConstants.RIGHT_PARENTHESIS_CHAR);
            put(valueOf, valueOf2);
            put(valueOf2, valueOf);
            put('[', ']');
            put(']', '[');
            Character valueOf3 = Character.valueOf(CoreConstants.CURLY_LEFT);
            Character valueOf4 = Character.valueOf(CoreConstants.CURLY_RIGHT);
            put(valueOf3, valueOf4);
            put(valueOf4, valueOf3);
        }
    };

    public enum characterType {
        ltr,
        rtl,
        rtl_arabic,
        punctuation,
        lineEnd,
        space
    }

    public enum contextualState {
        isolate,
        begin,
        middle,
        end
    }

    public static boolean rtlSupport() {
        return GBApplication.getPrefs().getBoolean(GBPrefs.RTL_SUPPORT, false);
    }

    public static characterType getCharacterType(Character c) {
        byte directionality = Character.getDirectionality(c.charValue());
        if (directionality == 1) {
            return characterType.rtl;
        }
        if (directionality == 2) {
            return characterType.rtl_arabic;
        }
        if (!(directionality == 4 || directionality == 5 || directionality == 7)) {
            if (directionality == 9 || directionality == 10) {
                return characterType.lineEnd;
            }
            if (directionality == 12) {
                return characterType.space;
            }
            if (directionality != 13) {
                return characterType.ltr;
            }
        }
        return characterType.punctuation;
    }

    public static boolean contextualSupport() {
        return GBApplication.getPrefs().getBoolean(GBPrefs.RTL_CONTEXTUAL_ARABIC, false);
    }

    public static boolean isHebrew(Character c) {
        return getCharacterType(c) == characterType.rtl;
    }

    public static boolean isArabic(Character c) {
        return getCharacterType(c) == characterType.rtl_arabic;
    }

    public static boolean isLtr(Character c) {
        return getCharacterType(c) == characterType.ltr;
    }

    public static boolean isRtl(Character c) {
        return getCharacterType(c) == characterType.rtl || getCharacterType(c) == characterType.rtl_arabic;
    }

    public static boolean isPunctuations(Character c) {
        return getCharacterType(c) == characterType.punctuation;
    }

    public static boolean isSpaceSign(Character c) {
        return getCharacterType(c) == characterType.space;
    }

    public static boolean isEndLineSign(Character c) {
        return getCharacterType(c) == characterType.lineEnd;
    }

    public static boolean exceptionAfterLam(Character c) {
        char charValue = c.charValue();
        if (charValue == 1570 || charValue == 1571 || charValue == 1573 || charValue == 1575) {
            return true;
        }
        return false;
    }

    /* renamed from: nodomain.freeyourgadget.gadgetbridge.util.RtlUtils$6 */
    static /* synthetic */ class C12476 {

        /* renamed from: $SwitchMap$nodomain$freeyourgadget$gadgetbridge$util$RtlUtils$contextualState */
        static final /* synthetic */ int[] f206x913bbfd0 = new int[contextualState.values().length];

        static {
            try {
                f206x913bbfd0[contextualState.begin.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f206x913bbfd0[contextualState.middle.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f206x913bbfd0[contextualState.end.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f206x913bbfd0[contextualState.isolate.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public static Character getContextualSymbol(Character c, contextualState state) {
        Character newChar;
        int i = C12476.f206x913bbfd0[state.ordinal()];
        if (i == 1) {
            newChar = contextualArabicBeginning.get(c);
        } else if (i == 2) {
            newChar = contextualArabicMiddle.get(c);
        } else if (i != 3) {
            newChar = contextualArabicIsolated.get(c);
        } else {
            newChar = contextualArabicEnd.get(c);
        }
        if (newChar != null) {
            return newChar;
        }
        return c;
    }

    public static contextualState getCharContextualState(contextualState prevState, Character curChar, Character nextChar) {
        if ((prevState == contextualState.isolate || prevState == contextualState.end) && contextualArabicBeginning.containsKey(curChar) && contextualArabicEnd.containsKey(nextChar)) {
            return contextualState.begin;
        }
        if ((prevState != contextualState.begin && prevState != contextualState.middle) || !contextualArabicEnd.containsKey(curChar)) {
            return contextualState.isolate;
        }
        if (!contextualArabicMiddle.containsKey(curChar) || !contextualArabicEnd.containsKey(nextChar)) {
            return contextualState.end;
        }
        return contextualState.middle;
    }

    public static String convertToContextual(String s) {
        if (s == null || s.isEmpty() || s.length() == 1) {
            return s;
        }
        int length = s.length();
        StringBuilder newWord = new StringBuilder(length);
        Character nextChar = Character.valueOf(s.charAt(0));
        contextualState prevState = contextualState.isolate;
        contextualState curState = contextualState.isolate;
        int i = 0;
        while (true) {
            if (i >= length - 1) {
                break;
            }
            Character curChar = nextChar;
            nextChar = Character.valueOf(s.charAt(i + 1));
            if (curChar.charValue() == 1604 && exceptionAfterLam(nextChar)) {
                i++;
                curChar = Character.valueOf((char) (nextChar.charValue() + curChar.charValue()));
                if (i >= length - 1) {
                    nextChar = curChar;
                    prevState = curState;
                    break;
                }
                nextChar = Character.valueOf(s.charAt(i + 1));
            }
            curState = getCharContextualState(prevState, curChar, nextChar);
            newWord.append(getContextualSymbol(curChar, curState));
            prevState = curState;
            i++;
        }
        newWord.append(getContextualSymbol(nextChar, getCharContextualState(prevState, nextChar, (Character) null)));
        return newWord.toString();
    }

    public static String reverse(String s) {
        int j = s.length();
        char[] newWord = new char[j];
        if (j == 0) {
            return s;
        }
        for (int i = 0; i < s.length() - 0; i++) {
            if (directionSignsMap.containsKey(Character.valueOf(s.charAt(i)))) {
                j--;
                newWord[j] = directionSignsMap.get(Character.valueOf(s.charAt(i))).charValue();
            } else {
                j--;
                newWord[j] = s.charAt(i);
            }
        }
        return new String(newWord);
    }

    public static String fixWhitespace(String s) {
        int length = s.length();
        if (length <= 0 || !isSpaceSign(Character.valueOf(s.charAt(length - 1)))) {
            return s;
        }
        return s.charAt(length - 1) + s.substring(0, length - 1);
    }

    public static String fixRtl(String oldString) {
        characterType CurRtlType;
        characterType CurRtlType2;
        String phraseString;
        characterType CurRtlType3;
        String str = oldString;
        if (str == null || oldString.isEmpty()) {
            return str;
        }
        debug("before: |" + StringEscapeUtils.escapeJava(oldString) + "|");
        int length = oldString.length();
        String newString = "";
        List<String> lines = new ArrayList<>();
        char[] newWord = new char[length];
        int line_max_size = GBApplication.getPrefs().getInt("rtl_max_line_length", 18);
        characterType PhraseRtlType = isRtl(Character.valueOf(str.charAt(0))) ? characterType.rtl : characterType.ltr;
        StringBuilder word = new StringBuilder();
        StringBuilder phrase = new StringBuilder();
        StringBuilder line = new StringBuilder();
        int i = 0;
        Object obj = "";
        characterType PhraseRtlType2 = PhraseRtlType;
        while (i < length) {
            Character c = Character.valueOf(str.charAt(i));
            boolean addCharToWord = false;
            String newString2 = newString;
            StringBuilder sb = new StringBuilder();
            char[] newWord2 = newWord;
            sb.append("char: ");
            sb.append(c);
            sb.append(" :");
            sb.append(Character.getDirectionality(c.charValue()));
            debug(sb.toString());
            if (isLtr(c)) {
                CurRtlType = characterType.ltr;
            } else if (isRtl(c)) {
                CurRtlType = characterType.rtl;
            } else {
                CurRtlType = PhraseRtlType;
            }
            if (CurRtlType == PhraseRtlType2 && !isSpaceSign(c) && !isEndLineSign(c)) {
                debug("add: " + c + " to: " + word);
                word.append(c);
                addCharToWord = true;
                if (i < length - 1) {
                    CurRtlType2 = CurRtlType;
                    i++;
                    newString = newString2;
                    newWord = newWord2;
                    PhraseRtlType = CurRtlType2;
                }
            }
            while (true) {
                if (line.length() + phrase.length() + word.length() < line_max_size || (line.length() == 0 && word.length() >= line_max_size)) {
                    if (isSpaceSign(c)) {
                        word.append(c);
                        addCharToWord = true;
                    }
                    phrase.append(word);
                    word.setLength(0);
                    if (isSpaceSign(c)) {
                        CurRtlType2 = CurRtlType;
                        break;
                    }
                }
                String phraseString2 = phrase.toString();
                StringBuilder sb2 = new StringBuilder();
                CurRtlType2 = CurRtlType;
                sb2.append("phrase:   |");
                sb2.append(phraseString2);
                sb2.append("|");
                debug(sb2.toString());
                if (PhraseRtlType2 == characterType.rtl) {
                    if (contextualSupport()) {
                        phraseString2 = convertToContextual(phraseString2);
                    }
                    phraseString = reverse(phraseString2);
                } else {
                    phraseString = phraseString2;
                }
                line.insert(0, fixWhitespace(phraseString));
                debug("line now: |" + line + "|");
                phrase.setLength(0);
                if (word.length() > 0) {
                    line.append(10);
                } else if (isEndLineSign(c)) {
                    line.append(c);
                } else if (!addCharToWord) {
                    word.append(c);
                    if (i == length - 1) {
                        addCharToWord = true;
                        CurRtlType3 = CurRtlType2;
                    } else {
                        PhraseRtlType2 = PhraseRtlType2 == characterType.rtl ? characterType.ltr : characterType.rtl;
                    }
                }
                lines.add(line.toString());
                debug("line: |" + line + "|");
                line.setLength(0);
                if (word.length() == 0) {
                    break;
                }
                CurRtlType3 = CurRtlType2;
            }
            i++;
            newString = newString2;
            newWord = newWord2;
            PhraseRtlType = CurRtlType2;
        }
        char[] cArr = newWord;
        lines.add(line.toString());
        String newString3 = TextUtils.join("", lines);
        debug("after : |" + StringEscapeUtils.escapeJava(newString3) + "|");
        return newString3;
    }

    private static void debug(String s) {
    }
}

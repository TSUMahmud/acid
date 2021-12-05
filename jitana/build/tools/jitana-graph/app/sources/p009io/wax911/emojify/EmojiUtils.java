package p009io.wax911.emojify;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;
import java.util.List;

/* renamed from: io.wax911.emojify.EmojiUtils */
public class EmojiUtils extends AbstractEmoji {
    public static List<Emoji> getAllEmojis() {
        return EmojiManager.emojiData;
    }

    /* JADX WARNING: Removed duplicated region for block: B:6:0x001d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static p009io.wax911.emojify.Emoji getEmoji(java.lang.String r5) {
        /*
            com.google.code.regexp.Pattern r0 = shortCodePattern
            com.google.code.regexp.Matcher r0 = r0.matcher(r5)
            boolean r1 = r0.find()
            if (r1 == 0) goto L_0x0011
            r1 = 1
            java.lang.String r5 = r0.group((int) r1)
        L_0x0011:
            java.util.List<io.wax911.emojify.Emoji> r1 = p009io.wax911.emojify.EmojiManager.emojiData
            java.util.Iterator r1 = r1.iterator()
        L_0x0017:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0096
            java.lang.Object r2 = r1.next()
            io.wax911.emojify.Emoji r2 = (p009io.wax911.emojify.Emoji) r2
            org.hamcrest.Matcher r3 = org.hamcrest.Matchers.equalTo(r5)
            java.lang.String r4 = r2.getEmoji()
            boolean r3 = r3.matches(r4)
            if (r3 != 0) goto L_0x0095
            org.hamcrest.Matcher r3 = org.hamcrest.Matchers.equalToIgnoringCase(r5)
            java.lang.String r4 = r2.getEmoji()
            boolean r3 = r3.matches(r4)
            if (r3 != 0) goto L_0x0095
            org.hamcrest.Matcher r3 = org.hamcrest.Matchers.equalToIgnoringCase(r5)
            java.lang.String r4 = r2.getHexHtml()
            boolean r3 = r3.matches(r4)
            if (r3 != 0) goto L_0x0095
            org.hamcrest.Matcher r3 = org.hamcrest.Matchers.equalToIgnoringCase(r5)
            java.lang.String r4 = r2.getDecimalHtml()
            boolean r3 = r3.matches(r4)
            if (r3 != 0) goto L_0x0095
            org.hamcrest.Matcher r3 = org.hamcrest.Matchers.equalToIgnoringCase(r5)
            java.lang.String r4 = r2.getDecimalSurrogateHtml()
            boolean r3 = r3.matches(r4)
            if (r3 != 0) goto L_0x0095
            org.hamcrest.Matcher r3 = org.hamcrest.Matchers.equalToIgnoringCase(r5)
            java.lang.String r4 = r2.getHexHtmlShort()
            boolean r3 = r3.matches(r4)
            if (r3 != 0) goto L_0x0095
            org.hamcrest.Matcher r3 = org.hamcrest.Matchers.equalToIgnoringCase(r5)
            java.lang.String r4 = r2.getDecimalHtmlShort()
            boolean r3 = r3.matches(r4)
            if (r3 != 0) goto L_0x0095
            org.hamcrest.Matcher r3 = org.hamcrest.Matchers.hasItem(r5)
            java.util.List r4 = r2.getAliases()
            boolean r3 = r3.matches(r4)
            if (r3 == 0) goto L_0x0094
            goto L_0x0095
        L_0x0094:
            goto L_0x0017
        L_0x0095:
            return r2
        L_0x0096:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: p009io.wax911.emojify.EmojiUtils.getEmoji(java.lang.String):io.wax911.emojify.Emoji");
    }

    public static boolean isEmoji(String code) {
        return getEmoji(code) != null;
    }

    public static String emojify(String text) {
        return emojify(text, 0);
    }

    private static String emojify(String text, int startIndex) {
        return processStringWithRegex(processStringWithRegex(text, shortCodeOrHtmlEntityPattern, startIndex, true), shortCodeOrHtmlEntityPattern, startIndex, true);
    }

    private static String processStringWithRegex(String text, Pattern pattern, int startIndex, boolean recurseEmojify) {
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        int resetIndex = 0;
        if (startIndex > 0) {
            matcher.region(startIndex, text.length());
        }
        while (true) {
            if (!matcher.find()) {
                break;
            }
            String emojiCode = matcher.group();
            Emoji emoji = getEmoji(emojiCode);
            if (emoji != null) {
                matcher.appendReplacement(sb, emoji.getEmoji());
            } else if (htmlSurrogateEntityPattern2.matcher(emojiCode).matches()) {
                String highSurrogate1 = matcher.group("H1");
                String highSurrogate2 = matcher.group("H2");
                String lowSurrogate1 = matcher.group("L1");
                String lowSurrogate2 = matcher.group("L2");
                matcher.appendReplacement(sb, processStringWithRegex(highSurrogate1 + highSurrogate2, shortCodeOrHtmlEntityPattern, 0, false));
                if (sb.toString().endsWith(highSurrogate2)) {
                    resetIndex = sb.length() - highSurrogate2.length();
                } else {
                    resetIndex = sb.length();
                }
                sb.append(lowSurrogate1);
                sb.append(lowSurrogate2);
            } else if (htmlSurrogateEntityPattern.matcher(emojiCode).matches()) {
                String highSurrogate = matcher.group("H");
                String lowSurrogate = matcher.group("L");
                matcher.appendReplacement(sb, processStringWithRegex(highSurrogate, htmlEntityPattern, 0, true));
                resetIndex = sb.length();
                sb.append(lowSurrogate);
                break;
            } else {
                matcher.appendReplacement(sb, emojiCode);
            }
        }
        matcher.appendTail(sb);
        if (!recurseEmojify || resetIndex <= 0) {
            return sb.toString();
        }
        return emojify(sb.toString(), resetIndex);
    }

    public static int countEmojis(String text) {
        Matcher matcher = htmlEntityPattern.matcher(htmlify(text));
        int counter = 0;
        while (matcher.find()) {
            if (isEmoji(matcher.group())) {
                counter++;
            }
        }
        return counter;
    }

    public static String htmlify(String text) {
        return htmlifyHelper(emojify(text), false, false);
    }

    public static String htmlify(String text, boolean asSurrogate) {
        return htmlifyHelper(emojify(text), false, asSurrogate);
    }

    public static String hexHtmlify(String text) {
        return htmlifyHelper(emojify(text), true, false);
    }

    public static String shortCodify(String text) {
        String emojifiedText = emojify(text);
        for (Emoji emoji : EmojiManager.emojiData) {
            String emoji2 = emoji.getEmoji();
            emojifiedText = emojifiedText.replace(emoji2, ":" + emoji.getAliases().get(0) + ":");
        }
        return emojifiedText;
    }

    public static String removeAllEmojis(String emojiText) {
        for (Emoji emoji : EmojiManager.emojiData) {
            emojiText = emojiText.replace(emoji.getEmoji(), "");
        }
        return emojiText;
    }
}

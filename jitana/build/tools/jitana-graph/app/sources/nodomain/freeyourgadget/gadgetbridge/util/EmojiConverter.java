package nodomain.freeyourgadget.gadgetbridge.util;

import android.content.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p009io.wax911.emojify.EmojiManager;
import p009io.wax911.emojify.EmojiUtils;

public class EmojiConverter {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) EmojiConverter.class);
    private static boolean isEmojiDataInitialised = false;
    private static final String[][] simpleEmojiMapping = {new String[]{"😀", ":-D"}, new String[]{"😁", ":-D"}, new String[]{"😂", ":'D"}, new String[]{"😃", ":-D"}, new String[]{"😄", ":-D"}, new String[]{"😅", ":'D"}, new String[]{"😆", "X-D"}, new String[]{"😇", "O:-)"}, new String[]{"😉", ";-)"}, new String[]{"😊", ":-)"}, new String[]{"😋", ":-p"}, new String[]{"😎", "B-)"}, new String[]{"😕", ":-/"}, new String[]{"😖", ":-S"}, new String[]{"😗", ":*"}, new String[]{"😘", ";-*"}, new String[]{"😙", ":-*"}, new String[]{"😚", ":-*"}, new String[]{"😛", ":-P"}, new String[]{"😜", ";-P"}, new String[]{"😝", "X-P"}, new String[]{"😞", ":-S"}, new String[]{"😠", ":-@"}, new String[]{"😡", ":-@"}, new String[]{"😢", ":'("}, new String[]{"😣", ":-("}, new String[]{"😥", ":'("}, new String[]{"😭", ":'("}, new String[]{"😮", ":-O"}, new String[]{"😲", "X-o"}, new String[]{"🙂", ":)"}, new String[]{"🙃", "(-:"}, new String[]{"☹", ":-("}, new String[]{"❤", "<3"}};

    private static String convertSimpleEmojiToAscii(String text) {
        String text2 = text;
        for (String[] emojiMap : simpleEmojiMapping) {
            text2 = text2.replace(emojiMap[0], emojiMap[1]);
        }
        return text2;
    }

    private static synchronized void initEmojiData(Context context) {
        synchronized (EmojiConverter.class) {
            if (!isEmojiDataInitialised) {
                EmojiManager.initEmojiData(context);
                isEmojiDataInitialised = true;
            }
        }
    }

    private static String convertAdvancedEmojiToAscii(String text, Context context) {
        initEmojiData(context);
        try {
            return EmojiUtils.shortCodify(text);
        } catch (Exception e) {
            Logger logger = LOG;
            logger.warn("An exception occured when converting advanced emoji to ASCII: " + text);
            return text;
        }
    }

    public static String convertUnicodeEmojiToAscii(String text, Context context) {
        return convertAdvancedEmojiToAscii(convertSimpleEmojiToAscii(text), context);
    }
}

package p009io.wax911.emojify;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/* renamed from: io.wax911.emojify.EmojiManager */
public final class EmojiManager {
    static List<Emoji> emojiData;

    public static void initEmojiData(Context context) {
        List<Emoji> list = emojiData;
        if (list == null || list.size() < 1) {
            try {
                Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setLenient().create();
                BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("emoticons/emoji.json")));
                emojiData = (List) gson.fromJson((Reader) reader, new TypeToken<ArrayList<Emoji>>() {
                }.getType());
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

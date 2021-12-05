package p009io.wax911.emojify;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.code.regexp.Matcher;
import java.util.List;

/* renamed from: io.wax911.emojify.Emoji */
public final class Emoji extends AbstractEmoji implements Parcelable {
    public static final Parcelable.Creator<Emoji> CREATOR = new Parcelable.Creator<Emoji>() {
        public Emoji createFromParcel(Parcel in) {
            return new Emoji(in);
        }

        public Emoji[] newArray(int size) {
            return new Emoji[size];
        }
    };
    private List<String> aliases;
    private String category;
    private String decimalHtml;
    private String decimalHtmlShort;
    private String decimalSurrogateHtml;
    private String description;
    private String emoji;
    private String hexHtml;
    private String hexHtmlShort;
    private List<String> tags;

    protected Emoji(Parcel in) {
        this.emoji = in.readString();
        this.aliases = in.createStringArrayList();
        this.category = in.readString();
        this.description = in.readString();
        this.tags = in.createStringArrayList();
        this.hexHtml = in.readString();
        this.decimalHtml = in.readString();
        this.decimalHtmlShort = in.readString();
        this.hexHtmlShort = in.readString();
        this.decimalSurrogateHtml = in.readString();
    }

    public String getEmoji() {
        return this.emoji;
    }

    public void setEmoji(String emoji2) {
        setDecimalHtml(EmojiUtils.htmlifyHelper(emoji2, false, false));
        setHexHtml(EmojiUtils.htmlifyHelper(emoji2, true, false));
        setDecimalSurrogateHtml(EmojiUtils.htmlifyHelper(emoji2, false, true));
        this.emoji = emoji2;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public String getCategory() {
        return this.category;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public String getHexHtml() {
        return this.hexHtml;
    }

    public void setHexHtml(String hexHtml2) {
        this.hexHtml = hexHtml2;
        Matcher matcher = htmlSurrogateEntityPattern.matcher(hexHtml2);
        if (matcher.find()) {
            setHexHtmlShort(matcher.group("H"));
        } else {
            setHexHtmlShort(hexHtml2);
        }
    }

    public String getDecimalHtml() {
        return this.decimalHtml;
    }

    public void setDecimalHtml(String decimalHtml2) {
        this.decimalHtml = decimalHtml2;
        Matcher matcher = htmlSurrogateEntityPattern.matcher(decimalHtml2);
        if (matcher.find()) {
            setDecimalHtmlShort(matcher.group("H"));
        } else {
            setDecimalHtmlShort(decimalHtml2);
        }
    }

    public String getDecimalSurrogateHtml() {
        return this.decimalSurrogateHtml;
    }

    public void setDecimalSurrogateHtml(String decimalSurrogateHtml2) {
        this.decimalSurrogateHtml = decimalSurrogateHtml2;
    }

    public String getDecimalHtmlShort() {
        return this.decimalHtmlShort;
    }

    public void setDecimalHtmlShort(String decimalHtmlShort2) {
        this.decimalHtmlShort = decimalHtmlShort2;
    }

    public String getHexHtmlShort() {
        return this.hexHtmlShort;
    }

    public void setHexHtmlShort(String hexHtmlShort2) {
        this.hexHtmlShort = hexHtmlShort2;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.emoji);
        dest.writeStringList(this.aliases);
        dest.writeString(this.category);
        dest.writeString(this.description);
        dest.writeStringList(this.tags);
        dest.writeString(this.hexHtml);
        dest.writeString(this.decimalHtml);
        dest.writeString(this.decimalHtmlShort);
        dest.writeString(this.hexHtmlShort);
        dest.writeString(this.decimalSurrogateHtml);
    }
}

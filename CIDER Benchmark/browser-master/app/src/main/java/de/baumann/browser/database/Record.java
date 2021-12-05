package de.baumann.browser.database;

public class Record {

    private Boolean isDesktopMode;
    private long iconColor;
    private String title;

    public long getIconColor() { return iconColor; }
    public void setIconColor(long iconColor) {this.iconColor = iconColor; }

    public Boolean getDesktopMode() {return isDesktopMode; }
    public void setDesktopMode(Boolean desktopMode) {isDesktopMode = desktopMode; }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    private String url;
    public String getURL() {
        return url;
    }
    public void setURL(String url) {
        this.url = url;
    }

    private long time;
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    private final int ordinal;
    int getOrdinal() {
        return ordinal;
    }

    private int type;     //0 History, 1 Start site, 2 Bookmark
    public int getType() {
        return type;
    }
    public void setType(int type){this.type=type;}

    public Record() {
        this.title = null;
        this.url = null;
        this.time = 0L;
        this.ordinal = -1;
        this.type = -1;
        this.isDesktopMode = null;
        this.iconColor = 0L;
    }

    public Record(String title, String url, long time, int ordinal, int type, Boolean DesktopMode, long iconColor) {
        this.title = title;
        this.url = url;
        this.time = time;
        this.ordinal = ordinal;
        this.type = type;
        this.isDesktopMode = DesktopMode;
        this.iconColor = iconColor;
    }
}

package cyanogenmod.app;

public enum ThemeComponent {
    UNKNOWN(-1),
    OVERLAY(0),
    BOOT_ANIM(1),
    WALLPAPER(2),
    LOCKSCREEN(3),
    FONT(4),
    ICON(5),
    SOUND(6);
    

    /* renamed from: id */
    public int f103id;

    private ThemeComponent(int id) {
        this.f103id = id;
    }
}

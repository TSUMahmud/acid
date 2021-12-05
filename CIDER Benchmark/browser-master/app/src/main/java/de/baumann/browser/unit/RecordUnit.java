package de.baumann.browser.unit;

public class RecordUnit {

    public static final String TABLE_START = "GRID";
    public static final String TABLE_BOOKMARK = "BOOKAMRK";
    public static final String TABLE_HISTORY = "HISTORY";

    public static final String TABLE_TRUSTED = "JAVASCRIPT";
    public static final String TABLE_PROTECTED = "COOKIE";
    public static final String TABLE_STANDARD = "REMOTE";

    public static final String TABLE_WHITELIST = "WHITELIST";
    public static final String TABLE_TAB = "TAB";

    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_URL = "URL";
    public static final String COLUMN_TIME = "TIME";
    public static final String COLUMN_DOMAIN = "DOMAIN";
    public static final String COLUMN_FILENAME = "FILENAME";
    public static final String COLUMN_ORDINAL = "ORDINAL";

    public static final String CREATE_BOOKMARK = "CREATE TABLE "
            + TABLE_BOOKMARK
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_TIME + " integer"
            + ")";

    public static final String CREATE_HISTORY = "CREATE TABLE "
            + TABLE_HISTORY
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_TIME + " integer"
            + ")";

    public static final String CREATE_TAB = "CREATE TABLE "
            + TABLE_TAB
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_TIME + " integer"
            + ")";

    public static final String CREATE_WHITELIST = "CREATE TABLE "
            + TABLE_WHITELIST
            + " ("
            + " " + COLUMN_DOMAIN + " text"
            + ")";

    public static final String CREATE_TRUSTED = "CREATE TABLE "
            + TABLE_TRUSTED
            + " ("
            + " " + COLUMN_DOMAIN + " text"
            + ")";

    public static final String CREATE_PROTECTED = "CREATE TABLE "
            + TABLE_PROTECTED
            + " ("
            + " " + COLUMN_DOMAIN + " text"
            + ")";

    public static final String CREATE_STANDARD = "CREATE TABLE "
            + TABLE_STANDARD
            + " ("
            + " " + COLUMN_DOMAIN + " text"
            + ")";

    public static final String CREATE_START = "CREATE TABLE "
            + TABLE_START
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_FILENAME + " text,"
            + " " + COLUMN_ORDINAL + " integer"
            + ")";
}
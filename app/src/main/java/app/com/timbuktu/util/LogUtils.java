package app.com.timbuktu.util;

public abstract interface LogUtils
{
    public static int LOGLEVEL = 0;
    public static boolean WARN = LOGLEVEL > 1;
    public static boolean DEBUG = LOGLEVEL > 0;
    // To test early db creation process
    public static boolean TEST_DB_INITIAL_CREATION_IN_NO_INTERNET_STATE = false;
    public static boolean TEST_DB_INITIAL_CREATION_AND_CACHE_UPDATE_FOR_PLACE = false;
}
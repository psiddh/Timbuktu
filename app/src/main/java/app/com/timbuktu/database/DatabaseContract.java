package app.com.timbuktu.database;

import android.provider.BaseColumns;

public final class DatabaseContract {

    public static final  int    DATABASE_VERSION   = 1;
    public static final String DATABASE_NAME      = "timbuktu.db";

    // To prevent someone from accidentally instantiating the
    // DatabaseContract contract class, give it an empty private constructor.
    private DatabaseContract() {}

    public static abstract class MediaTable implements BaseColumns {
        public static final String TABLE_NAME       = "media_table";

        public static final String COLUMN_ID = "_id";
        public static final String MEDIA_ID = "media_id";
        public static final String MEDIA_TYPE = "media_type";
        public static final String MEDIA_PATH = "media_path";
        public static final String MEDIA_CITY = "media_city";
        public static final String MEDIA_STATE = "media_state";
        public static final String MEDIA_COUNTRY = "media_country";
        public static final String MEDIA_LAT = "media_lat";
        public static final String MEDIA_LONG = "media_long";

        public static final String CREATE_TABLE = "create table if not exists "
                + TABLE_NAME + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + MEDIA_ID  + " integer,"
                + MEDIA_TYPE  + " integer,"
                + MEDIA_PATH  + " text,"
                + MEDIA_CITY + " text,"
                + MEDIA_STATE + " text,"
                + MEDIA_COUNTRY + " text,"
                + MEDIA_LAT   + " real,"
                + MEDIA_LONG  + " real);";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String SELECT_ALL_BY_MEDIA_ID = "Select * from " + TABLE_NAME + " where " + MEDIA_ID + " =";
        public static final String ROW_MEDIA_ID_EXISTS = "Select * from " + TABLE_NAME + " where " + MEDIA_ID + " =";
        public static final String COUNT_ROWS = "select * from " + TABLE_NAME;
    }
}
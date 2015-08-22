package app.com.timbuktu.database;

import android.provider.BaseColumns;

public final class DatabaseContract {

    public static final  int    DATABASE_VERSION   = 1;
    public static final String DATABASE_NAME      = "timbuktu.db";

    // To prevent someone from accidentally instantiating the
    // DatabaseContract contract class, give it an empty private constructor.
    private DatabaseContract() {}

    public static abstract class GalleryTable implements BaseColumns {
        public static final String TABLE_NAME       = "gallery_table";

        public static final String TABLE_GALLERY = "gallery";
        public static final String COLUMN_ID = "_id";
        public static final String MEDIA_ID = "media_id";
        public static final String MEDIA_TYPE = "media_type";
        public static final String MEDIA_PATH = "media_path";
        public static final String MEDIA_PLACE = "media_place";
        public static final String MEDIA_COUNTRY = "media_country";
        public static final String MEDIA_ADMIN = "media_admin";
        public static final String MEDIA_LAT = "media_lat";
        public static final String MEDIA_LONG = "media_long";

        public static final String CREATE_TABLE = "create table if not exists "
                + TABLE_GALLERY + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + MEDIA_ID  + " integer,"
                + MEDIA_TYPE  + " integer,"
                + MEDIA_PATH  + " text,"
                + MEDIA_PLACE + " text,"
                + MEDIA_COUNTRY + " text,"
                + MEDIA_ADMIN + " text,"
                + MEDIA_LAT   + " real,"
                + MEDIA_LONG  + " real);";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String ROW_MEDIA_ID_EXISTS = "Select * from " + TABLE_GALLERY + " where " + MEDIA_ID + "=";
        public static final String COUNT_ROWS = "select * from " + TABLE_GALLERY;
    }
}
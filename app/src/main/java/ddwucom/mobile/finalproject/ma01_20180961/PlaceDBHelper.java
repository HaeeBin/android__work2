package ddwucom.mobile.finalproject.ma01_20180961;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PlaceDBHelper extends SQLiteOpenHelper {
    final static String TAG = "PlaceDBHelper";

    final static String DB_NAME ="place_db";
    public final static String TABLE_NAME = "place_table";

    public final static String ID = "_id";
    public final static String NAME = "name";
    public final static String ADDRESS = "address";
    public final static String PHONE = "phone";
    public final static String OPENING = "opening";
    public final static String RATING = "rating";
    public final static String SITE = "website";
    public final static String MEMO = "memo";

    public PlaceDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" + ID + " integer primary key autoincrement, "
                + NAME + " TEXT, " + ADDRESS + " TEXT, " + PHONE + " TEXT, " + OPENING + " TEXT, "
                + RATING + " TEXT, " + SITE + " TEXT, " + MEMO + " TEXT)";
        Log.d(TAG, sql);
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

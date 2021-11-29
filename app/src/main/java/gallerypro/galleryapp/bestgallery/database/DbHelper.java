package gallerypro.galleryapp.bestgallery.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import gallerypro.galleryapp.bestgallery.adapter.FavModel;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    private Context context;

    private static String DB_NAME = "FAVOURITE.DB";
    private static int DB_VERSION = 1;
    private String DB_TABLE = "FavouriteImage";
    private String COL_ID = "Id";

    private String CREATE_DB = "CREATE TABLE " + DB_TABLE + " (" + COL_ID + " TEXT PRIMARY KEY)";

    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /*-----------------------------------------------------------*/

    public void setFav(String imgId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ID, imgId);
        db.insert(DB_TABLE, null, cv);
    }

    public boolean getStatuss(String imgId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + DB_TABLE + " where " + COL_ID + "=?", new String[]{imgId}, null);
        if (cursor != null) {

            if (cursor.moveToFirst()) {

                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    public boolean removeFav(String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(DB_TABLE, COL_ID + "=?", new String[]{path}) > 0;
    }

    public ArrayList<String> getFavList() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> favModelList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + DB_TABLE, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {
                favModelList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        } else {
            favModelList = null;
        }

        return favModelList;

    }

}

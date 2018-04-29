package id.suksesit.pensil;

/*
 * Created by regopantes_apps on 24/04/18.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SoalDB extends SQLiteOpenHelper {

    // ===================================== TAMBAH KOMODITAS LAHAN ================================
    private static final String DATABASE_NAME = "kuisioner.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAMA_SOAL = "soal_kuisioner";
    private static final String ID = "id_soal";
    private static final String KATEGORI = "kategori";
    private static final String PERTANYAAN = "pertanyaan";
    private static final String STATUS = "status";
    private Context context;

    public SoalDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAMA_SOAL + " (" + ID +
                    " INTEGER PRIMARY KEY, " + KATEGORI + " TEXT null, " + PERTANYAAN + " TEXT null, "+STATUS+" TEXT null);";
            Log.d("DataKomoditi ", "onCreate: " + sql);
            db.execSQL(sql);

        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAMA_SOAL);
        onCreate(db);
    }

    public void dropTable() {
        try {
            SQLiteDatabase database = getWritableDatabase();
            String updateQuery = "DROP TABLE IF EXISTS " + TABLE_NAMA_SOAL;
            database.execSQL(updateQuery);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertSoal(int idSoal, String kategori, String pertanyaan, String status) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            String sql = "INSERT INTO " + TABLE_NAMA_SOAL + " (" + ID + ", " + KATEGORI + ", " + PERTANYAAN + ", "+STATUS+") VALUES ("
                    + idSoal + ", '" + kategori + "', '" + pertanyaan + "', '"+status+"');";
            db.execSQL(sql);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public void updateSoal(int id) {
        try {
            SQLiteDatabase db = getWritableDatabase();
            String sql = "UPDATE " + TABLE_NAMA_SOAL + " SET " + STATUS + " = 0 WHERE " + ID + "="+id+";";
            db.execSQL(sql);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public void updateSoalAll() {
        try {
            SQLiteDatabase db = getWritableDatabase();
            String sql = "UPDATE " + TABLE_NAMA_SOAL + " SET " + STATUS + " = '1';";
            db.execSQL(sql);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public void showAllSoal() {
        SQLiteDatabase db = getWritableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("SELECT "+PERTANYAAN+","+KATEGORI+" FROM " + TABLE_NAMA_SOAL + " WHERE "+STATUS+"=1 ORDER BY RANDOM() LIMIT 1;", null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0) != null) {
                    modelSoal ms = new modelSoal();
                    ms.setPertanyaan(cursor.getString(0));
                    ms.setKategori(cursor.getString(1));
                    HomeActivity.arraySoal.add(ms);
                    SoalActivity.arraySoal.add(ms);
                }
            } while (cursor.moveToNext());
        }
        db.close();
    }
}

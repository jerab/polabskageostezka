package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class InitDB {
    // DB NAME, VERSION
    static final String DATABASE_NAME = "GeoStezka";
    static final int DATABASE_VERSION = 1;
    // TABLE NAMES
    static final String TABLE_MAIN = "tabMain";
    // id, typ, status, cas splneni
    static final String TABLE_CAMTASK = "tabCamTask";
    static final String TABLE_DDTASK = "tabDDTask";
    static final String TABLE_QTASK = "tabQTask";
    // COMMON COLUMN NAMES
    static final String KEY_ID = "id";
    //static final String KEY_TASK_STATUS = "taskStatusNumber";
    //static final String KEY_DATUM_CAS_SPLNENI = "taskCompletedDateTime";
    // TABLE MAIN COLUMNS
    static final String KEY_TASK_ID_FK = "taskNumber";
    static final String KEY_TASK_TYPE = "taskType";
    static final String KEY_TASK_STATUS = "taskStatus";
    static final String KEY_TASK_CAS_SPLNENI = "taskStatus";
    // TABLE MAINtoTASK
    // TABLE CAMTASK COLUMNS

    // TABLE DDTASK COLUMNS

    // TABLE QTASK COLUMNS

    // CREATE STRING TABLE MAIN
    //private static final String CREATE_TABLE_MAIN = "CREATE TABLE" + TABLE_MAIN ;
    // CREATE STRING TABLE CAMTASK

    private static final String CREATE_TABLE_CAMTASK = "CREATE TABLE" + TABLE_CAMTASK
            + " (" + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_TASK_STATUS +" INTEGER);";
    // CREATE STRING TABLE DDTASK
    private static final String CREATE_TABLE_DDTASK = "CREATE TABLE" + TABLE_DDTASK;
    // CREATE STRING TABLE QTASK
    private static final String CREATE_TABLE_QTASK = "CREATE TABLE" + TABLE_QTASK;

    //private static final String DATABASE_DROP_ENTRIES = "DROP TABLE IF EXISTS " + DATABASE_TABLE;

    final Context context;
    InitDB.DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public InitDB(Context ctx)
    {
        this.context = ctx;
        DBHelper = new InitDB.DatabaseHelper(this.context);
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqlDB) {
            sqlDB.execSQL(CREATE_TABLE_CAMTASK);
            //sqlDB.execSQL(CREATE_TABLE_DDTASK);
            //sqlDB.execSQL(CREATE_TABLE_QTASK);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqlDB, int i, int i2) {
            /// provedení potřebných změn///
        }
    }

    //--- otevření DB ---
    public InitDB open()
    {
        try {
            db = DBHelper.getWritableDatabase();
        } catch (SQLException e){
            Log.d("GEO exception: ", e.toString());
        }
        return this;
    }
    //--- zavření DB ---
    public void close()
    {
        DBHelper.close();
    }

/*
    public CamTask getCamTask(long camTaskId) {
        String selectQuery = "SELECT  * FROM " + TABLE_CAMTASK + " WHERE "
                + KEY_ID + " = " + camTaskId;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        CamTask ct = new CamTask(c.getColumnIndex(KEY_ID));
        int pocet = (c.getColumnIndex(KEY_EFECTIVE_ROWS)/2);
        StringBuilder sb = new StringBuilder();
        String[] pole = new String[pocet];

        ct.setPocetPolozek(pocet);

        for (int i = 0; i<pocet;i++)
        {
            sb.setLength(0);
            sb.append("KEY_U");
            sb.append(i);
            sb.append("_RESULT");
            pole[i] = c.getString(c.getColumnIndex(sb.toString()));
        }
        ct.setVysledky(pole);
        return ct;
    }*/
}


package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratPocetUloh;
import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratUlohuPodleID;


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
    static final String KEY_TASK_TYP = "taskType";
    static final String KEY_TASK_STATUS = "taskStatus";
    // 0 - uzamcena // 1 - odemcena k plneni // 2 - splnena
    static final String KEY_TASK_CAS_SPLNENI = "taskCompletedTime";
    //cas v milisekundach
    // TABLE MAINtoTASK

    // TABLE CAMTASK COLUMNS
    // zapis cilu ukolu
    static final String KEY_TASK_ID = "id";
    static final String KEY_TARGET = "camTaskTarget";
    static final String KEY_STEP = "camTaskStep";
    static final String KEY_TIME = "camTaskTime";
    // TABLE DDTASK COLUMNS

    // TABLE QTASK COLUMNS
    static final String KEY_Q_NUMBER = "questionNumber";
    static final String KEY_Q_STATUS = "questionStatus";
    // 0 neodpovezena , 1 hotovo , 2 spatne
    // CREATE STRING TABLE MAIN
    //private static final String CREATE_TABLE_MAIN = "CREATE TABLE" + TABLE_MAIN ;
    // CREATE STRING TABLE CAMTASK
    private static final String CREATE_TABLE_MAIN = "CREATE TABLE IF NOT EXISTS " + TABLE_MAIN
            + " (" + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_TASK_TYP + " INTEGER, "
            + KEY_TASK_STATUS +" INTEGER, "
            + KEY_TASK_CAS_SPLNENI + " INTEGER);";

    private static final String CREATE_TABLE_CAMTASK = "CREATE TABLE IF NOT EXISTS " + TABLE_CAMTASK
            + " (" + KEY_TASK_ID + " INTEGER, "
            + KEY_STEP + " INTEGER, "
            + KEY_TARGET +" INTEGER, "
            //cas v milisekundach
            + KEY_TIME + " INTEGER, "
            + "PRIMARY KEY(" + KEY_TASK_ID + ", " + KEY_STEP + ")"
            +");";
    // CREATE STRING TABLE DDTASK
    private static final String CREATE_TABLE_DDTASK = "CREATE TABLE " + TABLE_DDTASK;
    // CREATE STRING TABLE QTASK
    private static final String CREATE_TABLE_QTASK = "CREATE TABLE IF NOT EXISTS " + TABLE_QTASK
            + " (" + KEY_TASK_ID + " INTEGER, "
            + KEY_Q_NUMBER + " INTEGER, "
            + KEY_Q_STATUS + " INTEGER, "
            + "PRIMARY KEY(" + KEY_TASK_ID + ", " + KEY_Q_NUMBER +")"
            +"); ";

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
            sqlDB.execSQL(CREATE_TABLE_MAIN);
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
            Log.d("GEO InitDB: ", e.toString());
        }
        return this;
    }
    //--- zavření DB ---
    public void close()
    {
        DBHelper.close();
    }

    public int vratStavUlohy (int id)
    {
        if(db == null)
            this.open();
        int stat = 0;
        Cursor c = db.query(TABLE_MAIN, new String[] {KEY_ID, KEY_TASK_STATUS}, KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null);
        if (c != null && (c.getCount() > 0))
        {
            c.moveToFirst();
            stat = Integer.valueOf(c.getString(1));
            c.close();
        } else {
            Log.d("GEO InitDB", "Task not found");
        }

        return stat;
    }
    public void odemkniUlohu(int id) {
        if(db == null)
            this.open();
        String STRING_UPDATE_TASK = "UPDATE " + TABLE_MAIN +
                " SET " + KEY_TASK_STATUS + " = 1 " +
                " WHERE " + KEY_ID + " = " + id +";";
        db.execSQL(STRING_UPDATE_TASK);
        Log.d("GEO InitDB: ","Task updated");
    }

    public long zapisTaskDoDatabaze(int id, int typ){
        if(db == null)
            this.open();
        int status = 0;
        /*String STRING_INSERT_TASK = "INSERT INTO " + TABLE_MAIN +
                "("+ KEY_TASK_ID +
                ", " + KEY_TASK_TYP +
                ", " + KEY_TASK_STATUS +
                ") VALUES (" + id +
                ", " + typ +
                ", " + status +
                ");";
        db.execSQL(STRING_INSERT_TASK);*/
        ContentValues cv = new ContentValues();
        cv.put(KEY_ID, id);
        cv.put(KEY_TASK_TYP, typ);
        cv.put(KEY_TASK_STATUS, status);
        return db.insert(TABLE_MAIN, null, cv);
    }
    public void zapisTaskDoDatabaze (int id, int typ, int status){
        // incoming status
        switch (status) {
            case 1:
                String STRING_UPDATE_TASK = "UPDATE " + TABLE_MAIN +
                        " SET " + KEY_TASK_STATUS + " = " + status +
                        " WHERE " + KEY_ID + " = " + id +";";
                db.execSQL(STRING_UPDATE_TASK);
                break;
            default:
                Log.d("GEO InitDB: ","Spatne volani do DB");
        }
    }
    public void zapisTaskDoDatabaze (int id, long cas){
        if(db == null)
            this.open();
        // incoming time
        // over jestli uz neni zapsana
        boolean done = false;
        Cursor c = db.query(TABLE_MAIN, new String[] {KEY_ID, KEY_TASK_CAS_SPLNENI}, KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null);
        if (c != null && (c.getCount() > 0))
        {
            c.moveToFirst();
            if (c.getString(1) != null) done = true;
            c.close();
        } else {
            Log.d("GEO InitDB", "Task not found");
        }
        if (!done)
        {
            int time = (int)cas;
            String STRING_UPDATE_TASK = "UPDATE " + TABLE_MAIN +
                    " SET " + KEY_TASK_STATUS + " = 2, " + KEY_TASK_CAS_SPLNENI + " = " + time +
                    " WHERE " + KEY_ID + " = " + id +";";
            db.execSQL(STRING_UPDATE_TASK);
            Log.d("GEO InitDB: ","Task updated");
        }


    }
    public long zapisCamTaskTarget (int id, int target, int cas) {
        if(db == null)
            this.open();

        int step = vratPosledniStepCamTask(id);
        step++;
        ContentValues cv = new ContentValues();
        cv.put(KEY_TASK_ID, id);
        cv.put(KEY_TARGET, target);
        cv.put(KEY_STEP, step);
        cv.put(KEY_TIME, cas);
        return db.insert(TABLE_CAMTASK, null, cv);
    }
    public int[] vratVsechnyTargetyCamTaskPodleId (int id)
    {
        if(db == null) {
            this.open();
        }
        /*String STRING_SELECT = "SELECT  " + KEY_STEP + " FROM " + TABLE_CAMTASK + " WHERE "
                + KEY_ID + " = " + id + " DESC";*/
        int[] step;
        Cursor c = db.query(TABLE_CAMTASK, new String[] {KEY_ID, KEY_STEP, KEY_TARGET}, KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, KEY_TARGET+" ASC");
        if (c != null)
        {
            c.moveToFirst();
            step= new int[c.getCount()];
                for (int i = 0; i<step.length;i++) {
                    step[i] = Integer.parseInt(c.getString(2));
                    c.moveToNext();
                }
            c.close();
            return step;
        } else {
            try {
                c.close();
            } catch (Exception e) {
                Log.d("GEO InitDB","Something broke");
            }
            return null;
        }
    }
    //  navrat stepu jednotlivym camtask
    private int vratPosledniStepCamTask (int id)
    {
        if(db == null) {
            this.open();
        }
        /*String STRING_SELECT = "SELECT  " + KEY_STEP + " FROM " + TABLE_CAMTASK + " WHERE "
                + KEY_ID + " = " + id + " DESC";*/
        int step = 0;
        Cursor c = db.query(TABLE_CAMTASK, new String[] {KEY_ID, KEY_STEP}, KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, KEY_STEP+" DESC");
        if (c != null && (c.getCount() > 0))
        {
            c.moveToFirst();
            step = Integer.parseInt(c.getString(1));
            c.close();
            return step;
        } else {
            Log.d("GEO InitDB", "Zatim zadne stepy");
            return -1;
        }
    }
}


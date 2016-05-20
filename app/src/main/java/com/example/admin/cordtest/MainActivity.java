package com.example.admin.cordtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";


    String name[] = {"Китай", "США", "Бразилия", "Россия", "Япония", "Германия",
            "Египет", "Италия", "Франция", "Канада"};
    int people[] = {1400, 311, 195, 142, 128, 82, 80, 60, 66, 35};
    String region[] = {"Азия", "Америка", "Америка", "Европа", "Азия",
            "Европа", "Африка", "Европа", "Европа", "Америка"};

    Button btnAll, btnFunc, btnPeople, btnSort, btnGroup, btnHaving;
    EditText etFunc, etPeople, etRegionPeople;
    RadioGroup rgSort;

    DBHelper dbHelper;
    SQLiteDatabase db;


    @Override
    protected void onPause() {
        super.onPause();
        dbHelper.close();
        Log.d(LOG_TAG,"onPause used!!!");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        btnAll = (Button) findViewById(R.id.btnAll);
        btnFunc = (Button) findViewById(R.id.btnFunc);
        btnPeople = (Button) findViewById(R.id.btnPeople);
        btnSort = (Button) findViewById(R.id.btnSort);
        btnGroup = (Button) findViewById(R.id.btnGroup);
        btnHaving = (Button) findViewById(R.id.btnHaving);

        etFunc = (EditText) findViewById(R.id.etFunc);
        etPeople = (EditText) findViewById(R.id.etPeople);
        etRegionPeople = (EditText) findViewById(R.id.etRegionPeople);

        rgSort = (RadioGroup) findViewById(R.id.rgSort);


        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAll();
            }
        });

        btnFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFunction();
            }
        });

        btnPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPeople();
            }
        });

        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSort();
            }
        });

        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGroup();
            }
        });

        btnHaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getHaving();
            }
        });


        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        Cursor c = db.query("country", null, null, null, null, null, null);
        if(c.getCount() == 0) {
            ContentValues cv = new ContentValues();

            for(int i = 0; i < 10; i++) {
                cv.put("name", name[i]);
                cv.put("people", people[i]);
                cv.put("region", region[i]);
                Log.d(LOG_TAG, "id = " + db.insert("country", null, cv));
            }
        }



    }

    // Все записи
    ////////////////////////////////////////////////////////////////////////////////////////////////
    void getAll() {

        Log.d(LOG_TAG, "--- Все записи ---");
        Cursor c = db.query("country", null, null, null, null, null, null);

        printLog(c);

    }

    // Функция
    ////////////////////////////////////////////////////////////////////////////////////////////////
    void getFunction() {

        String funcQuery = etFunc.getText().toString();

        if(etFunc.getText().length() != 0 ) {

            if(funcQuery.equals("*") || funcQuery.equals("name") || funcQuery.equals("people") || funcQuery.equals("region") ){

                String[] sFunc = funcQuery.split(" ");

                Log.d(LOG_TAG, "--- Функция " + sFunc + " ---");
                String[] columns = sFunc;
                Cursor c = db.query("country", columns, null, null, null, null, null);

                printLog(c);
                c.close();
            } else {
                Toast.makeText(MainActivity.this, "*, name, people, region", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "ведите названия столбца", Toast.LENGTH_SHORT).show();
        }
    }

    // Население больше, чем
    ////////////////////////////////////////////////////////////////////////////////////////////////
    void getPeople() {

        String sPeople = etPeople.getText().toString();

        Log.d(LOG_TAG, "--- Население больше " + sPeople + " ---");
        String selection = "people > ?";
        String[] selectionArgs = new String[] { sPeople };
        Cursor c = db.query("country", null, selection, selectionArgs, null, null,
                null);

        printLog(c);



    }

    // Население по региону
    ////////////////////////////////////////////////////////////////////////////////////////////////
    void getGroup() {

        Log.d(LOG_TAG, "--- Население по региону ---");
        String[] columns = new String[] { "region", "sum(people) as people" };
        String groupBy = "region";
        Cursor c = db.query("country", columns, null, null, groupBy, null, null);

        printLog(c);


    }

    // Население по региону больше чем
    ////////////////////////////////////////////////////////////////////////////////////////////////
    void getHaving() {

        String sRegionPeople = etRegionPeople.getText().toString();

        Log.d(LOG_TAG, "--- Регионы с населением больше " + sRegionPeople
                + " ---");
        String[] columns = new String[] { "region", "sum(people) as people" };
        String groupBy = "region";
        String having = "sum(people) > " + sRegionPeople;
        Cursor c = db.query("country", columns, null, null, groupBy , having , "DESC", null);



        printLog(c);


    }

    // Сортировка
    ////////////////////////////////////////////////////////////////////////////////////////////////
    void getSort() {
        String orderBy = null;


        switch (rgSort.getCheckedRadioButtonId()) {
            // наименование
            case R.id.rName:
                Log.d(LOG_TAG, "--- Сортировка по наименованию ---");
                orderBy = "name";
                break;
            // население
            case R.id.rPeople:
                Log.d(LOG_TAG, "--- Сортировка по населению ---");
                orderBy = "people";
                break;
            // регион
            case R.id.rRegion:
                Log.d(LOG_TAG, "--- Сортировка по региону ---");
                orderBy = "region";
                break;
        }
        Cursor c = db.query("country", null, null, null, null, null, orderBy);

        printLog(c);


    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    void printLog(Cursor c) {

        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = str.concat(cn + " = "
                                + c.getString(c.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(LOG_TAG, str);

                } while (c.moveToNext());
            }
            c.close();
        } else
            Log.d(LOG_TAG, "Cursor is null");
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper (Context context){
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");

            db.execSQL("create table country ("
                    + "id integer primary key autoincrement," + "name text,"
                    + "people integer," + "region text" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}

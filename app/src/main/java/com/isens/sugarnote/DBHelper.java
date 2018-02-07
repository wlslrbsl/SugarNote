package com.isens.sugarnote;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jeongjick on 2017-06-30.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "GLUCOSEDATA.db";
    public static final String CARS_TABLE_NAME = "GLUCOSEDATA";
    public static final String CARS_COLUMN_ID = "id";
    public static final String CARS_COLUMN_DATE = "create_at";
    public static final String CARS_COLUMN_GLSVAL = "glucose_val";
    public static final String CARS_COLUMN_MEAL = "meal";


    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);


    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL("create table  " +           CARS_TABLE_NAME+
                "(_id integer primary key AUTOINCREMENT NOT NULL,"+
                CARS_COLUMN_DATE+                " Text,"+
                CARS_COLUMN_GLSVAL+              " INTEGER,"+
                CARS_COLUMN_MEAL+                " Text)"
        );


    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_NAME);
        onCreate(db);
    }

    public void insert(String create_at, int glucose_val, String meal) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO GLUCOSEDATA VALUES(null, '" + create_at + "', " + glucose_val + ", '" + meal + "');");
        db.close();
    }

    public void update(int glucose_val, String meal) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE GLUCOSEDATA SET glucose_val=" + glucose_val + " WHERE meal='" + meal + "';");
        db.close();
    }

    public void delete(int glucose_val) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM GLUCOSEDATA WHERE glucose_val='" + glucose_val + "';");
        db.close();
    }

    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM GLUCOSEDATA", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(0)
                    + " : "
                    + cursor.getString(1)
                    + " | "
                    + cursor.getInt(2)
                    + " mg/dL "
                    + cursor.getString(3)
                    + "\n";
        }

        return result;
    }

    public void clear_db() {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("delete from GLUCOSEDATA");
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'GLUCOSEDATA'");


        db.close();
    }


}


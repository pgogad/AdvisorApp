package com.advisor.app.db;

import java.math.BigDecimal;
import java.math.RoundingMode;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AdvisorDB extends SQLiteOpenHelper
{
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "Advisor.db";

	String CREATE_TABLE_SQL = "CREATE TABLE TIME_TRACKER ( ID INTEGER, AMOUNT TEXT, START_TIME  TEXT, END_TIME TEXT );";

	public AdvisorDB(Context context)
	{
		super( context, DATABASE_NAME, null, DATABASE_VERSION );
	}

	@Override
	public void onCreate( SQLiteDatabase db )
	{
		db.execSQL( CREATE_TABLE_SQL );
	}

	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
	{
	}

	public void insertRecord( String minutes, long startTime, long endTime )
	{
		Cursor cursor = getReadOnlyCursor();
		SQLiteDatabase WRITER = this.getWritableDatabase();

		if( null == cursor || cursor.getCount() == 0 )
		{
			String insertSQL = "INSERT INTO TIME_TRACKER (ID , AMOUNT, START_TIME , END_TIME ) VALUES(1,'" + String.valueOf( minutes ) + "','0','0')";
			WRITER.execSQL( insertSQL );
		}
		else
		{
			Log.d("DB","Minutes = " + minutes);
			
			BigDecimal amount = new BigDecimal( minutes ).setScale( 5, RoundingMode.HALF_DOWN );
			if(amount.compareTo( BigDecimal.ZERO ) <= 0 )
			{
				amount = BigDecimal.ZERO.setScale( 5, RoundingMode.HALF_DOWN );
			}
			String updateSql = "UPDATE TIME_TRACKER SET AMOUNT='" + amount.toString() + "' , START_TIME='" + String.valueOf( startTime ) + "' , END_TIME='"
					+ String.valueOf( endTime ) + "' WHERE ID=1";

			WRITER.execSQL( updateSql );
		}

		WRITER.close();
		cursor.close();
	}

	public BigDecimal getAvailableMinutes()
	{
		Cursor cursor = getReadOnlyCursor();

		if( null == cursor || cursor.getCount() == 0 )
		{
			cursor.close();
			return BigDecimal.ZERO;
		}
		else
		{
			cursor.moveToFirst();
			BigDecimal temp = new BigDecimal( cursor.getString( 1 ) );
			cursor.close();
			return temp.setScale( 5, BigDecimal.ROUND_FLOOR );
		}

	}

	private Cursor getReadOnlyCursor()
	{
		String countQuery = "SELECT ID , AMOUNT, START_TIME , END_TIME FROM TIME_TRACKER";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery( countQuery, null );
		return cursor;
	}
}

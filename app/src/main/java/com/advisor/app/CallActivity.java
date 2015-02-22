package com.advisor.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.advisor.app.db.AdvisorDB;
import com.advisor.app.login.SigninActivity;
import com.advisor.app.phone.AsyncHelper;
import com.advisor.app.phone.Constants;
import com.advisor.app.phone.PhoneHelper;
import com.advisor.app.util.UtilHelper;
import com.twilio.client.Twilio;

public class CallActivity extends Activity
{
	private PhoneHelper phone;
	private AsyncHelper asyncHelper;
	private AdvisorDB dataBase;
	private ProgressDialog progress;
	private SharedPreferences sharedPref;
	private String[] shared = new String[Constants.SP_ARRAY_COUNT];

	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.call_activity );
		dataBase = new AdvisorDB( this );
		progress = new ProgressDialog( this );
		progress.setMessage( "Loading..." );
		progress.setCancelable( false );

		asyncHelper = new AsyncHelper( progress );

		try
		{
			phone = new PhoneHelper( CallActivity.this, asyncHelper.execute( "call", dataBase.getAvailableMinutes().toString() ).get() );
		}
		catch( Exception ex )
		{
			Log.e( "CallActivity", "Error occured while executing async task" );
		}

		sharedPref = getSharedPreferences( Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE );
		shared = UtilHelper.sharedPrefExpand( sharedPref.getString( Constants.EDITOR_EMAIL, Constants.SP_DEFAULT ) );
	}

	public void hangupCall( View view )
	{
		try
		{
			phone.disconnect();
			super.onBackPressed();
			overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
		}
		catch( Exception ex )
		{
			Log.e( "CallActivity", "Problem while executing hangup", ex );
		}
	}

	public void placeCall( View view )
	{
		try
		{
			if( !shared[Constants.SP_EMAIL].equals( Constants.SP_BLANK ) )
			{
				if( null != phone )
				{
					phone.connect();
				}
				else
				{
					phone = new PhoneHelper( CallActivity.this, asyncHelper.execute( "call", dataBase.getAvailableMinutes().toString() ).get() );
					phone.connect();
				}
			}
			else
			{
				Toast.makeText( getApplicationContext(), "Please log in", Toast.LENGTH_LONG ).show();
				Intent login = new Intent( this, SigninActivity.class );
				login.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
				startActivity( login );
				overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
				fileList();
			}

		}
		catch( Exception e )
		{
			Toast.makeText( getApplicationContext(), "Call Failed!!", Toast.LENGTH_LONG ).show();
		}
	}

	public void onDestroy()
	{
		super.onDestroy();
		phone = null;
		if( Twilio.isInitialized() )
		{
			Twilio.shutdown();
		}
	}

	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
		finish();
	}
}

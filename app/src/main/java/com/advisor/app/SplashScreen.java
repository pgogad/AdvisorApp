package com.advisor.app;

import com.advisor.app.phone.AsyncHelper;
import com.advisor.app.phone.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;

public class SplashScreen extends Activity
{
	private final int SPLASH_DISPLAY_LENGTH = 10000;
	private String rate;
	
	AsyncHelper async;
	
	@Override
	public void onCreate( Bundle icicle )
	{
		super.onCreate( icicle );
		setContentView( R.layout.splash_screen );

		TextView text = (TextView) findViewById( R.id.splash_text );
		text.setGravity( Gravity.CENTER );
		
		
		/*
		 * New Handler to start the Menu-Activity and close this Splash-Screen
		 * after some seconds.
		 */
		async = new AsyncHelper( null );
		getRate();
		
		new Handler().postDelayed( new Runnable()
		{
			@Override
			public void run()
			{
				/* Create an Intent that will start the Menu-Activity. */
				Intent mainIntent = new Intent( SplashScreen.this, MainLanding.class );
				mainIntent.putExtra( "rate", rate );
				SplashScreen.this.startActivity( mainIntent );
				SplashScreen.this.finish();
			}
		}, SPLASH_DISPLAY_LENGTH );
	}

	public void getRate()
	{
		try
		{
			if( rate == null )
			{
				String[] result = async.execute( "mainpage" ).get();
				rate = result[Constants.RATE];
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

}

package com.advisor.app;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

public class SplashScreen extends Activity
{
	private final int SPLASH_DISPLAY_LENGTH = 10000;
    private AsyncHttpClient client = new AsyncHttpClient();
	
	@Override
	public void onCreate( Bundle icicle )
	{
		super.onCreate( icicle );
		setContentView( R.layout.splash_screen );

		TextView text = (TextView) findViewById( R.id.splash_text );
		text.setGravity( Gravity.CENTER );
		getRate();
	}

	public void getRate()
	{
		try
		{
            client.get(this.getApplicationContext(),"http://dry-dusk-8611.herokuapp.com/ping",new AsyncHttpResponseHandler()
            {
                    @Override
                    public void onSuccess( String response )
                    {
                        Log.d("HTTP", "onSuccess: " + response);
                        Intent mainIntent = new Intent( SplashScreen.this, MainLanding.class );
                        SplashScreen.this.startActivity( mainIntent );
                        SplashScreen.this.finish();
                        overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
                    }
            });
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}

package com.advisor.app.phone;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.advisor.app.db.AdvisorDB;
import com.advisor.app.util.UtilHelper;
import com.twilio.client.Connection;
import com.twilio.client.Connection.State;
import com.twilio.client.ConnectionListener;
import com.twilio.client.Device;
import com.twilio.client.Twilio;

public class PhoneHelper implements Twilio.InitListener, ConnectionListener
{
	private static final String TAG = "PhoneHelper";
	private Connection connection;
	private Device device;

	private Context context;

	private String capabilityToken = null;

	private long startTime;
	private long endTime;

	private AdvisorDB database;
	private String rates;
	private String phoneNumber;

	public PhoneHelper( Context context, String[] param )
	{
		this.database = new AdvisorDB( context );
		this.context = context;

		this.capabilityToken = param[Constants.CAPABILITY_TOKEN];
		this.rates = param[Constants.RATE];
		this.phoneNumber = param[Constants.PHONE_NUMBER];

		if( !Twilio.isInitialized() )
		{
			Twilio.initialize( context, this );
		}

		startTime = 0l;
		endTime = 0l;
	}

	@Override
	public void onError( Exception arg0 )
	{
		Log.e( TAG, "Twilio SDK couldn't start: " + arg0.getLocalizedMessage() );
	}

	public void connect()
	{

		if( !Twilio.isInitialized() )
		{
			Twilio.initialize( context, this );
		}

		if( null != connection )
		{
			State state = connection.getState();

			if( state.equals( State.CONNECTED ) )
			{
				connection.disconnect();
			}
		}

		Map<String, String> parameters = new HashMap<String, String>();
		int amount = UtilHelper.getMinutesRemaining( database.getAvailableMinutes(), rates );

		if( amount < 1 )
		{
			Toast.makeText( context, "Please buy minutes in order to call Adviser :-)!!", Toast.LENGTH_LONG ).show();
		}
		else
		{
			parameters.put( "PhoneNumber", phoneNumber );
			parameters.put( "timeLimit", String.valueOf( amount * 60 ) );

			if(null != device)
			{
				connection = device.connect( parameters, this );
			}
			else
			{
				device = Twilio.createDevice( capabilityToken, null );
				connection = device.connect( parameters, this );
			}
			if( null == connection )
			{
				Log.e( TAG, "Failed to initialize Twilio " );
			}
		}
	}

	public void disconnect()
	{
		onDisconnect();
	}

	@Override
	public void onInitialized()
	{
		try
		{
			if( null != capabilityToken )
			{
				device = Twilio.createDevice( capabilityToken, null );
			}
		}
		catch( Exception e )
		{
			Log.e( TAG, "Failed to get capability token", e );
		}
	}

	private void shutDown()
	{
		if( null != connection && connection.getState() == State.CONNECTED )
		{
			connection.disconnect();
			connection = null;
		}

		if( device != null )
		{
			device.release();
		}
	}

	@Override
	public void onConnected( Connection arg0 )
	{
		startTime = System.currentTimeMillis();
		Log.d( TAG, "Call connected" );
	}

	@Override
	public void onConnecting( Connection arg0 )
	{
		Log.d( TAG, "Inside on connecting" );
	}

	@Override
	public void onDisconnected( Connection arg0 )
	{
		Log.d( TAG, "Inside onDisconnected" );
		onDisconnect();
	}

	@Override
	public void onDisconnected( Connection arg0, int arg1, String arg2 )
	{
		Log.d( TAG, "Inside onDisconnected" );
		onDisconnect();
	}

	private void onDisconnect()
	{
		endTime = System.currentTimeMillis();
		long seconds = 0l;
		if( startTime != 0 )
		{
			seconds = (endTime - startTime) / 1000;
		}
		Log.d( TAG, "Call lasted " + seconds + " seconds" );

		int minutes = 0;

		if( seconds >= 30 && seconds <= 60 )
		{
			minutes = 1;
		}
		else
		{
			minutes = Long.valueOf( seconds ).intValue() / 60;
		}

		Log.d( TAG, "Minutes to be charged : " + minutes + " mins" );

		BigDecimal amount = database.getAvailableMinutes();
		BigDecimal rate = new BigDecimal( rates ).setScale( 5, BigDecimal.ROUND_FLOOR );

		for( int i = 0; i < minutes; i++ )
		{
			amount = amount.subtract( rate ).setScale( 5, BigDecimal.ROUND_FLOOR );
		}
		Log.d( TAG, "Amount remaining : " + amount.toString() );
		database.insertRecord( amount.toString(), startTime, endTime );
		shutDown();
	}
}

package com.advisor.app.payment;

import hirondelle.date4j.DateTime;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.advisor.app.R;
import com.advisor.app.db.AdvisorDB;
import com.advisor.app.login.SigninActivity;
import com.advisor.app.phone.Constants;
import com.advisor.app.util.UtilHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

public class PayPalActivity extends Activity
{
	private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
	private static final String CONFIG_CLIENT_ID = "AboZuBA-iaS7l3ii-ZyDTdkEdO5Eas9BCycr_HTiZ1-uTjICrUVs4mWARVG7";
	private static final int REQUEST_CODE_PAYMENT = 1;

	private AdvisorDB dataBase;

	private SharedPreferences sharedpreferences;
	private Editor editor;

	private RadioGroup amountRadio;
	private EditText amountText;
	private String[] sharedPrefStrings;
	private String amount = null;
	private BigDecimal total = BigDecimal.ZERO;
	private PaymentConfirmation confirm;

	private AsyncHttpClient client = new AsyncHttpClient();
	private static PayPalConfiguration config = new PayPalConfiguration().environment( CONFIG_ENVIRONMENT ).clientId( CONFIG_CLIENT_ID );

	private DateTime now = DateTime.now( TimeZone.getTimeZone( "UTC" ) );
	private String date = now.format( "YYYY-MM-DD-hh:mm:ss" );

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.pay_pal );

		sharedPrefStrings = new String[Constants.SP_ARRAY_COUNT];
		sharedpreferences = getSharedPreferences( Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE );

		amountRadio = (RadioGroup) findViewById( R.id.amount_options );

		TextView intro = (TextView) findViewById( R.id.textView_paypal );
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics( metrics );
		intro.setWidth( (int) metrics.widthPixels / 2 );
		intro.setHeight( 100 );
		intro.setText( "Other Amount : " );
		intro.setGravity( Gravity.LEFT );

		amountText = (EditText) findViewById( R.id.custom_amount_text );
		amountText.setGravity( Gravity.LEFT );
		amountText.setText( "0.00" );
		amountText.setWidth( (int) metrics.widthPixels / 2 );

		dataBase = new AdvisorDB( this );
		editor = sharedpreferences.edit();
		sharedPrefStrings = UtilHelper.sharedPrefExpand( sharedpreferences.getString( Constants.EDITOR_EMAIL, Constants.SP_DEFAULT ) );
		client.addHeader( "content-type", "application/json" );
	}

	public void onBuyPressed( View pressed )
	{
		if( sharedPrefStrings[Constants.SP_EMAIL].equalsIgnoreCase( Constants.SP_BLANK ) )
		{
			Intent login = new Intent( getApplicationContext(), SigninActivity.class );
			login.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			startActivity( login );
			finish();
			overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
			Toast.makeText( getApplicationContext(), "Please login...", Toast.LENGTH_LONG ).show();
		}
		else
		{

			Intent paypalService = new Intent( PayPalActivity.this, PayPalService.class );
			paypalService.putExtra( PayPalService.EXTRA_PAYPAL_CONFIGURATION, config );
			startService( paypalService );

			int id = pressed.getId();
			id = amountRadio.getCheckedRadioButtonId();
			RadioButton radioButton = (RadioButton) findViewById( id );

			String temp = (String) radioButton.getText();

			int i = temp.indexOf( "$" );
			if( i == 0 )
			{
				amount = temp.substring( i + 1 );
			}
			String customAmt = String.valueOf( amountText.getText() );

			if( null != customAmt && customAmt.length() > 0 )
			{
				if( !customAmt.equalsIgnoreCase( "0.00" ) )
					amount = customAmt;
			}

			total = new BigDecimal( amount.trim() ).setScale( 5, BigDecimal.ROUND_FLOOR );

			PayPalPayment thingToBuy = new PayPalPayment( total, "USD", "Adviser Fees", PayPalPayment.PAYMENT_INTENT_SALE );
			Intent intent = new Intent( PayPalActivity.this, PaymentActivity.class );
			intent.putExtra( PaymentActivity.EXTRA_PAYMENT, thingToBuy );
			startActivityForResult( intent, REQUEST_CODE_PAYMENT );
			overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
			amountText.setText( "0.00" );
		}
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		if( requestCode == REQUEST_CODE_PAYMENT )
		{
			if( resultCode == Activity.RESULT_OK )
			{
				confirm = data.getParcelableExtra( PaymentActivity.EXTRA_RESULT_CONFIRMATION );
				if( confirm != null )
				{
					try
					{
						Log.i( "Approval", confirm.toJSONObject().toString() );
						String url = null;

						// Check if previous transaction needs to be reported
						if( !sharedPrefStrings[Constants.SP_AMOUNT].equals( Constants.SP_BLANK )
								&& !sharedPrefStrings[Constants.SP_TRANS_EMAIL].equals( Constants.SP_BLANK ) )
						{
							url = "http://dry-dusk-8611.herokuapp.com/paypalapproval/" + URLEncoder.encode( sharedPrefStrings[Constants.SP_APPROVAL], "UTF-8" )
									+ "/" + URLEncoder.encode( sharedPrefStrings[Constants.SP_TRANS_EMAIL], "UTF-8" ) + "/"
									+ URLEncoder.encode( sharedPrefStrings[Constants.SP_AMOUNT], "UTF-8" ) + "/"
									+ URLEncoder.encode( sharedPrefStrings[Constants.SP_DATE], "UTF-8" );

							client.get( this.getApplicationContext(), url, new AsyncHttpResponseHandler()
							{
								@Override
								public void onSuccess( String response )
								{
									Log.d( "HTTP", "onSuccess: " + response );
									sharedPrefStrings[Constants.SP_APPROVAL] = Constants.SP_BLANK;
									sharedPrefStrings[Constants.SP_AMOUNT] = Constants.SP_BLANK;
									sharedPrefStrings[Constants.SP_DATE] = Constants.SP_BLANK;
									sharedPrefStrings[Constants.SP_TRANS_EMAIL] = Constants.SP_BLANK;

									editor.putString( Constants.EDITOR_EMAIL, UtilHelper.sharedPrefContract( sharedPrefStrings ) );
									editor.commit();
								}
							} );
						}

						BigDecimal bd = dataBase.getAvailableMinutes();
						bd = bd.add( total.setScale( 5, BigDecimal.ROUND_FLOOR ) );

						dataBase.insertRecord( bd.setScale( 5, BigDecimal.ROUND_FLOOR ).toString(), Long.valueOf( "0" ).longValue(), Long.valueOf( "0" )
								.longValue() );

						url = "http://dry-dusk-8611.herokuapp.com/paypalapproval/" + URLEncoder.encode( confirm.toJSONObject().toString(), "UTF-8" ) + "/"
								+ URLEncoder.encode( sharedPrefStrings[Constants.SP_EMAIL], "UTF-8" ) + "/"
								+ URLEncoder.encode( total.setScale( 5, BigDecimal.ROUND_FLOOR ).toString(), "UTF-8" ) + "/"
								+ URLEncoder.encode( date, "UTF-8" );

						client.get( this.getApplicationContext(), url, new AsyncHttpResponseHandler()
						{
							@Override
							public void onSuccess( String response )
							{
								Log.d( "HTTP", "onSuccess: " + response );

								if( response.equalsIgnoreCase( "Success" ) )
								{
									sharedPrefStrings[Constants.SP_APPROVAL] = Constants.SP_BLANK;
									sharedPrefStrings[Constants.SP_AMOUNT] = Constants.SP_BLANK;
									sharedPrefStrings[Constants.SP_DATE] = Constants.SP_BLANK;
									sharedPrefStrings[Constants.SP_TRANS_EMAIL] = Constants.SP_BLANK;
								}
								else
								{
									sharedPrefStrings[Constants.SP_AMOUNT] = total.setScale( 5, BigDecimal.ROUND_FLOOR ).toString();
									sharedPrefStrings[Constants.SP_DATE] = date;
									sharedPrefStrings[Constants.SP_TRANS_EMAIL] = sharedPrefStrings[Constants.SP_EMAIL];
									sharedPrefStrings[Constants.SP_APPROVAL] = confirm.toJSONObject().toString();

								}
								editor.putString( Constants.EDITOR_EMAIL, UtilHelper.sharedPrefContract( sharedPrefStrings ) );
								editor.commit();
							}

							// Save transaction info to shared pref we will try
							// later
							@Override
							public void onFailure( int statusCode, Throwable error, String content )
							{
								sharedPrefStrings[Constants.SP_AMOUNT] = total.setScale( 5, BigDecimal.ROUND_FLOOR ).toString();
								sharedPrefStrings[Constants.SP_DATE] = date;
								sharedPrefStrings[Constants.SP_TRANS_EMAIL] = sharedPrefStrings[Constants.SP_EMAIL];
								sharedPrefStrings[Constants.SP_APPROVAL] = confirm.toJSONObject().toString();
								editor.putString( Constants.EDITOR_EMAIL, UtilHelper.sharedPrefContract( sharedPrefStrings ) );
								editor.commit();
							}
						} );

						Toast.makeText( getApplicationContext(), "Payment processed Successfully!!", Toast.LENGTH_LONG ).show();
						overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
						finish();
					}
					catch( Exception e )
					{
						Log.e( "PayPalActivity", "an extremely unlikely failure occurred: ", e );
						Toast.makeText( getApplicationContext(), "Problem occured while processing payment!!", Toast.LENGTH_LONG ).show();
					}
				}
			}
			else if( resultCode == Activity.RESULT_CANCELED )
			{
				Log.i( "paymentExample", "The user canceled." );
			}
			else if( resultCode == PaymentActivity.RESULT_EXTRAS_INVALID )
			{
				Log.i( "paymentExample", "An invalid Payment was submitted. Please see the docs." );
			}
		}
		else if( resultCode == Activity.RESULT_CANCELED )
		{
			Log.i( "FuturePaymentExample", "The user canceled." );
		}
		else if( resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID )
		{
			Log.i( "FuturePaymentExample",
					"Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs." );
		}
	}

	@Override
	public void onDestroy()
	{
		stopService( new Intent( this, PayPalService.class ) );
		super.onDestroy();
	}

	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
		finish();
	}
}
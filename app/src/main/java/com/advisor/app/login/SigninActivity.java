package com.advisor.app.login;

import java.net.URLEncoder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.advisor.app.R;
import com.advisor.app.phone.Constants;
import com.advisor.app.util.UtilHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class SigninActivity extends Activity
{

	private EditText emailET;
	private EditText pwdET;
	private ProgressDialog prgDialog;
	private AsyncHttpClient client = new AsyncHttpClient();
	private SharedPreferences sharedPref;
	private Editor editor;
	private String email;
	private String[] sp = new String[Constants.SP_ARRAY_COUNT];

	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.login_screen );

		emailET = (EditText) findViewById( R.id.loginEmail );
		pwdET = (EditText) findViewById( R.id.loginPassword );
		prgDialog = new ProgressDialog( this );

		sharedPref = getSharedPreferences( Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE );
		editor = sharedPref.edit();
	}

	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
		finish();
	}

	public void doLogging( View view )
	{
		email = emailET.getText().toString().trim();
		String password = pwdET.getText().toString().trim();

		if( UtilHelper.isNotNull( email ) && UtilHelper.isNotNull( password ) )
		{
			if( UtilHelper.validate( email ) )
			{
				try
				{
					prgDialog.setMessage( "Loging in..." );
					prgDialog.setCancelable( false );
					prgDialog.show();

					client.addHeader( "content-type", "application/json" );
					String url = "http://dry-dusk-8611.herokuapp.com/dologin/" + URLEncoder.encode( email, "UTF-8" ) + "/"
							+ URLEncoder.encode( password, "UTF-8" );

					client.get( this.getApplicationContext(), url, new AsyncHttpResponseHandler()
					{
						@Override
						public void onSuccess( String response )
						{
							Log.d( "HTTP", "onSuccess: " + response );
							prgDialog.dismiss();
							if( response.equalsIgnoreCase( "Done" ) )
							{
								sp = UtilHelper.sharedPrefExpand( sharedPref.getString( Constants.EDITOR_EMAIL, Constants.SP_DEFAULT ) );
								sp[Constants.SP_EMAIL] = email;
								editor.putString( Constants.EDITOR_EMAIL, UtilHelper.sharedPrefContract( sp ) );
								editor.commit();
								Toast.makeText( getApplicationContext(), "Login Succesful", Toast.LENGTH_LONG ).show();
								overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
								finish();
							}
							else if( response.equalsIgnoreCase( "Email address or password did not match" ) )
							{
								Toast.makeText( getApplicationContext(), "Email or password did not match", Toast.LENGTH_LONG ).show();
							}
							else if( response.equalsIgnoreCase( "Invalid email address" ) )
							{
								Toast.makeText( getApplicationContext(), "Email address you entered does not exist", Toast.LENGTH_LONG ).show();
							}
						}

						@Override
						public void onFailure( int statusCode, Throwable error, String content )
						{
							prgDialog.dismiss();
							if( statusCode == 404 )
							{
								Toast.makeText( getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG ).show();
							}
							else if( statusCode == 500 )
							{
								Toast.makeText( getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG ).show();
							}
							else
							{
								Toast.makeText( getApplicationContext(), "Unexpected Error occcured! Please try again later", Toast.LENGTH_LONG ).show();
							}
						}

					} );
					prgDialog.dismiss();
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
			}
			else
			{
				Toast.makeText( getApplicationContext(), "Please enter valid email", Toast.LENGTH_LONG ).show();
			}
		}
		else
		{
			Toast.makeText( getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG ).show();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	public void resetPassowrd( View view )
	{
		Intent reset = new Intent( this, ForgotPassword.class );
		reset.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
		startActivity( reset );
		overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
	}
}

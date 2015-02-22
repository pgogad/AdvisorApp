package com.advisor.app.login;

import java.net.URLEncoder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.advisor.app.R;
import com.advisor.app.util.UtilHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class RegisterMe extends Activity
{

	private ProgressDialog prgDialog;
	private EditText emailET;
	private EditText pwdET;
	private EditText nameET;
	private EditText passwordET;

	private AsyncHttpClient client = new AsyncHttpClient();

	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.register_me );

		emailET = (EditText) findViewById( R.id.registerEmail );
		pwdET = (EditText) findViewById( R.id.registerPassword );
		nameET = (EditText) findViewById( R.id.registerName );
		passwordET = (EditText) findViewById( R.id.registerPasswordAgain );

		prgDialog = new ProgressDialog( this );
	}

	@SuppressLint( "DefaultLocale" )
	public void doRegistration( View view )
	{

		String email = emailET.getText().toString().trim();
		String password1 = pwdET.getText().toString().trim();
		String passwordAgain = passwordET.getText().toString().trim();
		String name = nameET.getText().toString().trim();

		if( UtilHelper.isNotNull( email ) && UtilHelper.isNotNull( password1 ) && UtilHelper.isNotNull( name ) && UtilHelper.isNotNull( passwordAgain ) )
		{
			if( UtilHelper.validate( email ) )
			{
				if( UtilHelper.validateName( name ) )
				{
					if( password1.equals( passwordAgain ) )
					{
						try
						{
							prgDialog = new ProgressDialog( this );
							prgDialog.setMessage( "Please wait..." );
							prgDialog.setCancelable( false );
							prgDialog.show();
							client.addHeader( "content-type", "application/json" );
							String url = "http://dry-dusk-8611.herokuapp.com/register/" + URLEncoder.encode( email, "UTF-8" ) + "/"
									+ URLEncoder.encode( name, "UTF-8" ) + "/" + URLEncoder.encode( password1, "UTF-8" );

							client.get( this.getApplicationContext(), url, new AsyncHttpResponseHandler()
							{
								@Override
								public void onSuccess( String response )
								{
									Log.d( "HTTP", "onSuccess: " + response );
									prgDialog.dismiss();

									if( response.equalsIgnoreCase( "duplicate" ) )
									{
										Toast.makeText( getApplicationContext(), "The email address is in use", Toast.LENGTH_LONG ).show();
									}
									else if( response.equalsIgnoreCase( "Success" ) )
									{
										Toast.makeText( getApplicationContext(), "You have been registered!!", Toast.LENGTH_LONG ).show();
										finish();
									}
									else
									{
										Toast.makeText( getApplicationContext(), "There was problem registering you", Toast.LENGTH_LONG ).show();
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
										Toast.makeText( getApplicationContext(), "Unexpected Error occcured! Please try again later", Toast.LENGTH_LONG )
												.show();
									}
								}

							} );
						}
						catch( Exception ex )
						{
							ex.printStackTrace();
						}
					}
					else
					{
						Toast.makeText( getApplicationContext(), "The passwords you have entered do not match", Toast.LENGTH_LONG ).show();
					}
				}
				else
				{
					Toast.makeText( getApplicationContext(),
							"Please enter valid name. Only characters are allowed, no special characters and white space is allowed", Toast.LENGTH_LONG )
							.show();
				}
			}
			else
			{
				Toast.makeText( getApplicationContext(), "Please enter valid email address", Toast.LENGTH_LONG ).show();
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

	
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition( R.anim.slide_in, R.anim.slide_out );
		finish();
	}

}

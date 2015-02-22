package com.advisor.app.phone;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.util.Log;

public class HttpHelper
{
	private static final String TAG = "HttpHelper";

	private static HttpClient httpClient;

	public static String getPhoneNumber()
	{
		try
		{
			HttpURLConnection urlConnection = null;
			URL urlToRequest = new URL( "http://dry-dusk-8611.herokuapp.com/phone" );
			urlConnection = (HttpURLConnection) urlToRequest.openConnection();
			urlConnection.setConnectTimeout( 1000 * 20 );
			urlConnection.setRequestMethod( "GET" );
			urlConnection.setReadTimeout( 1000 * 20 );
			int statusCode = urlConnection.getResponseCode();

			if( statusCode == HttpURLConnection.HTTP_UNAUTHORIZED )
			{
				// handle unauthorized (if service requires user login)
			}
			else if( statusCode != HttpURLConnection.HTTP_OK )
			{
				// handle any other errors, like 404, 500,..
			}

			InputStream in = new BufferedInputStream( urlConnection.getInputStream() );
			return stringFromInputStream( in );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static String postPayPalApproval( String approval )
	{
		try
		{
			String url = "http://dry-dusk-8611.herokuapp.com/paypalapproval/" + URLEncoder.encode( approval, "UTF-8" );
			return httpGet( url );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return null;
		}
	}

	public static String validateLogin( String username, String password )
	{
		try
		{
			String url = "http://dry-dusk-8611.herokuapp.com/dologin/" + URLEncoder.encode( username, "UTF-8" ) + "/" + URLEncoder.encode( password, "UTF-8" );
			return httpGet( url );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return null;
		}
	}

	public static String requestWebService( int minutes )
	{
		try
		{
			return httpGet( "http://dry-dusk-8611.herokuapp.com/rest/" + URLEncoder.encode( String.valueOf( minutes ), "UTF-8" ) );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static String getRates()
	{
		try
		{
			HttpURLConnection urlConnection = null;
			URL urlToRequest = new URL( "http://dry-dusk-8611.herokuapp.com/rates" );
			urlConnection = (HttpURLConnection) urlToRequest.openConnection();
			urlConnection.setConnectTimeout( 1000 * 20 );
			urlConnection.setRequestMethod( "GET" );
			urlConnection.setReadTimeout( 1000 * 20 );
			int statusCode = urlConnection.getResponseCode();

			if( statusCode == HttpURLConnection.HTTP_UNAUTHORIZED )
			{
				// handle unauthorized (if service requires user login)
			}
			else if( statusCode != HttpURLConnection.HTTP_OK )
			{
				// handle any other errors, like 404, 500,..
			}

			InputStream in = new BufferedInputStream( urlConnection.getInputStream() );
			return stringFromInputStream( in );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			return null;
		}
	}

	private static void ensureHttpClient()
	{
		if( httpClient != null )
			return;

		BasicHttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout( params, 45000 );
		HttpConnectionParams.setSoTimeout( params, 30000 );

		SchemeRegistry registry = new SchemeRegistry();
		registry.register( new Scheme( "http", new PlainSocketFactory(), 80 ) );
		try
		{
			registry.register( new Scheme( "https", SSLSocketFactory.getSocketFactory(), 443 ) );
		}
		catch( Exception e )
		{
			Log.w( TAG, "Unable to register HTTPS socket factory: " + e.getLocalizedMessage() );
		}

		ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager( params, registry );
		httpClient = new DefaultHttpClient( connManager, params );
	}

	private static String stringFromInputStream( InputStream is ) throws IOException
	{
		char[] buf = new char[1024];
		StringBuilder out = new StringBuilder();

		Reader in = new InputStreamReader( is, "UTF-8" );

		int bin;
		while( (bin = in.read( buf, 0, buf.length )) >= 0 )
		{
			out.append( buf, 0, bin );
		}
		is.close();
		return out.toString();
	}

	public static String httpGet( String url ) throws Exception
	{
		ensureHttpClient();

		HttpGet request = new HttpGet( url );
		HttpResponse response = httpClient.execute( request );
		if( response != null )
		{
			int statusCode = response.getStatusLine().getStatusCode();
			if( statusCode == 200 )
				return stringFromInputStream( response.getEntity().getContent() );
			else
				throw new Exception( "Got error code " + statusCode + " from server" );
		}

		throw new Exception( "Unable to connect to server" );
	}
}

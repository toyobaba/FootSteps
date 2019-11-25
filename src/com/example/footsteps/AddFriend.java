package com.example.footsteps;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AddFriend extends Activity{

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	Person person;
	boolean received = false;
	@SuppressLint({ "NewApi", "NewApi" })
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.add_friends); 
           
        IntentFilter filter = new IntentFilter("android.person");
        registerReceiver(receiver, filter);
 	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.addToDictionary:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }    
    
    private final BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("receiver", "received");
			person = new Person();
			person = intent.getParcelableExtra("person parcel");
			received = true;
		}
	};
	
    public void AddthisFriend(View view){
        	new Asyncactivity().execute("add");
    	findViewById(R.id.addFriendButton).setClickable(true);
    }
    
    private class Asyncactivity extends AsyncTask <String, Void, String> //1st - doinback, 2nd - progressupdate, 3rd - postexecute
    {
    	@SuppressWarnings("unused")
		String action = null;
    	
    	protected String getText(HttpEntity entity) throws
    	IllegalStateException, IOException 	{
    		
    		InputStream in  = entity.getContent();
    		StringBuffer out = new StringBuffer();
    		int n = 1;
    		while (n>0)	{
    			byte[]b = new byte[4096];
    			n = in.read(b);
    			if (n>0) out.append(new String (b, 0, n));
    		}
    		return out.toString();
    	}
    	
    	@Override
    	protected String doInBackground(String... params) {
    		String text = null;
    		HttpClient client = new DefaultHttpClient();
    		HttpPost poster = new HttpPost("http://ec2-23-22-156-79.compute-1.amazonaws.com/phpfile.php");
    		ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
    		action = params[0];
    		
    		if (((TextView)findViewById(R.id.friendname)).length() == 0)
    			return null;
    		if (!received) return null;
    	
    		{    			
    			NameValuePair user = new BasicNameValuePair("mainuser", person.mainuser);
    			NameValuePair friend = new BasicNameValuePair("username", ((TextView)findViewById(R.id.friendname)).getText().toString());
    			NameValuePair act = new BasicNameValuePair("action", params[0]);
    			list.add(user); list.add(friend); list.add(act);
    		}
    		
    		try	{
				poster.setEntity(new UrlEncodedFormEntity(list)); //unsupported encoding exception
				HttpResponse response = client.execute(poster);
				HttpEntity entity = response.getEntity();
				text = getText(entity);
			}
			catch (Exception e)	{
				Log.d("main activity", e.toString());
				return e.getLocalizedMessage();
			}
    		return text;
    	}	    	
    	
		protected void onPostExecute(String results)	{
			String message;
			String error;
			
			if (results!=null)	{
				try	{
    				//fetched objects should be context based; i.e. they should work based on action
    				//make action a global variable. 
					JSONObject json_data = new JSONObject(results);
	    			message = json_data.getString("message");
					error = json_data.getString("error");
					if (message.equals(""))
						Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
					else
						Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();					
				}
				catch(JSONException e)
				{
					Log.d("main activity", e.toString());
				}
				
				catch (ParseException e)
				{
					e.printStackTrace();
				}    			 
    		}
    		else 
    		{
    			if (!received) Toast.makeText(getApplicationContext(),"Server is having problems" ,Toast.LENGTH_LONG).show();
    			else Toast.makeText(getApplicationContext(),"Invalid username. Try again" ,Toast.LENGTH_LONG).show();
    		}
    		
    		findViewById(R.id.addFriendButton).setClickable(true);
    	  		
    	}
    }
}

package com.example.footsteps;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

 	public class Person implements Parcelable {
	
	String mainuser = "nuller"; 
	String[] requests = new String[0];
	String[] friendnames = new String[0];
	double[] latitudes ;
	double[] longitudes;
	String[] statuses;
	long synctime;//= 30000;
	static int counter = 0;
	int onstat;
	double latitude;
	double longitude;
	String status;// = "This is a test status"; //This must be obtained
	
	Timer t;
	Handler thandle = new Handler();
	
	public Person (Parcel in)
	{
		mainuser = in.readString();
		requests = in.createStringArray();
		friendnames = in.createStringArray();
		latitudes = in.createDoubleArray();
		longitudes = in.createDoubleArray();
		statuses = in.createStringArray();
		status = in.readString();
		latitude = in.readDouble();
		longitude = in.readDouble();
		t = new Timer();
		
	}
	
	public Person()
	{
		
	}
    
	public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>(){
		public Person createFromParcel(Parcel in)
		{
			return new Person(in);
		}
		public Person[] newArray(int size)
		{
			return new Person[size];
		}
		
	};
	
    public void signout(View arg0)
    {
    	t.cancel();
    	mainuser = "nuller"; //may not be necessary
    }
        
    public void startSync()
    {
    	t.cancel();
    	t = new Timer();
    	t.schedule(new Timertask(), 0, synctime);
    }
    
    protected class Timertask extends TimerTask
    {
    	@Override public void run()
    	{
    		thandle.post(syncrunner);
    	}
    }
    
    Runnable syncrunner = new Runnable()
    {
    	@Override public void run()
    	{
    		new Asyncactivity().execute("get");
    	}
    };
    
    
    
    //INCLUDE CODE FOR RESUME AND PAUSE
    
    private class Asyncactivity extends AsyncTask <String, Void, String> //1st - doinback, 2nd - progressupdate, 3rd - postexecute
    {
    	String action = null;
    	
    	protected String getText(HttpEntity entity) throws
    	IllegalStateException, IOException
    	{
    		
    		InputStream in  = entity.getContent();
    		StringBuffer out = new StringBuffer();
    		int n = 1;
    		while (n>0)
    		{
    			byte[]b = new byte[4096];
    			n = in.read(b);
    			if (n>0) out.append(new String (b, 0, n));
    		}
    		return out.toString();
    	}
    	
    	@Override
    	protected String doInBackground(String... params)
    	{
    		String text = null;
    		HttpClient client = new DefaultHttpClient();
    		HttpPost poster = new HttpPost("http://ec2-23-22-156-79.compute-1.amazonaws.com/phpfile.php");
    		ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
    		action = params[0];
    		
    		if (params[0] == "get")
    		{
    			
    			NameValuePair user = new BasicNameValuePair("mainuser", mainuser);
    			//ideally a getStatus function will be called before this next step
    			NameValuePair stat = new BasicNameValuePair("status", status);
    			NameValuePair act = new BasicNameValuePair("action", params[0]);
    			list.add(user); list.add(stat); list.add(act);
    			
    			NameValuePair lat = new BasicNameValuePair("latitude", ((Double)latitude).toString());
  				NameValuePair lon = new BasicNameValuePair("longitude", ((Double)longitude).toString());
  				NameValuePair ostat = new BasicNameValuePair("onstat", ((Integer)onstat).toString());
  				list.add(lat); list.add(lon); list.add(ostat);
  		
    			
    			Log.d("main activity", "executing sync");
    		}
    		
    		try
			{
				poster.setEntity(new UrlEncodedFormEntity(list)); //unsupported encoding exception
				HttpResponse response = client.execute(poster);
				HttpEntity entity = response.getEntity();
				text = getText(entity);
			}
			catch (Exception e)
			{
				Log.d("main activity", e.toString());
				return e.getLocalizedMessage();
			}
    		return text;
    	}
    	
    	
		protected void onPostExecute(String results)
    	{
			boolean getter = false;
			String username = null;
			
			if (results!=null)
    		{
				try
    			{
    				//fetched objects should be context based; i.e. they should work based on action
    				//make action a global variable. 
					if (action.equals("get"))
					{
						JSONObject data = new JSONObject(results);
						JSONArray reqs = data.getJSONArray("requests");
						int rlength = reqs.length();
						requests = new String[rlength];
						for (int i = 0; i<rlength; i++)
						{
							username = ((JSONObject)reqs.get(i)).getString("username");
							requests[i] = username;
						}						
						
						Log.d("main activity", "syncing");
						
						JSONArray frnds = data.getJSONArray("friends");
						int flength = frnds.length();
						friendnames = new String[flength];
						latitudes = new double[flength];
						longitudes = new double[flength];
						for (int i = 0; i<flength; i++)
						{
							username = ((JSONObject)frnds.get(i)).getString("username");
							double lat = ((JSONObject)frnds.get(i)).getDouble("latitude");
							double lon = ((JSONObject)frnds.get(i)).getDouble("longitude");
							String status = ((JSONObject)frnds.get(i)).getString("status");
							friendnames[i] = username;
							latitudes[i] = lat;
							longitudes[i] = lon;
						}
						counter++;
					
					}
						//et.setText(message + " " + error)
					
				}
				catch(JSONException e)
				{
					//et.setText(e.toString() + " " + action + " " + username);
					Log.d("main activity", e.toString());
				}
				
				catch (ParseException e)
				{
					e.printStackTrace();
				}

    			
    			//et.setText(id + " " + name + " " + error);   
    		}
			//((EditText)findViewById(R.id.my_edit)).setText("Invalid username. Try again");
    	   		
    		if (getter)
    		{
    			getter = false;
    			final Handler delayer = new Handler();
    			delayer.postDelayed(new Runnable() 
    			{
    			  @Override
    			  public void run() 
    			  {
    				  Log.d("main activity", "start sync called");
    				  startSync();
    			  }
    			}, 1000);
    		}
    			
    		//maybe a wait line before sync begins. 
    		//Log.d("main activity", "service started");
    		//startSync();
    		
    		
    	}
    }



	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeString(mainuser);
		arg0.writeStringArray(requests);
		arg0.writeStringArray(friendnames);
		arg0.writeDoubleArray(latitudes);
		arg0.writeDoubleArray(longitudes);
		arg0.writeStringArray(statuses);
		arg0.writeString(status);
		arg0.writeDouble(latitude);
		arg0.writeDouble(longitude);
	}
    
}


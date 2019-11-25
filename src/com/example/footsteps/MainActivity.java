package com.example.footsteps;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.maps.MapController;

public class MainActivity extends Activity  {	
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	Person person;
	Timer t = new Timer();
	Handler thandle = new Handler();
	long synctime = 3000;
	boolean first = true;
	boolean sfirst;
	String action;
	boolean finishTheFuckingLoop = false;
	private double longitude;
	private double latitude;
    private Criteria criteria = new Criteria();
    private String provider = new String();
    Location location;
    MapController mapController;
    Asyncactivity asynch;
	
	@SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        person = new Person();
        person = intent.getParcelableExtra("person parcel");
        action = intent.getStringExtra("act.action");
        asynch = new Asyncactivity();
        asynch.execute("get");       
        PreferenceManager.setDefaultValues(getApplicationContext(),R.xml.settings,true);       
        
	   	// Set the criteria for getting location (need to optimize this line area for power management)
        final LocationManager locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
    	criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);    	
        provider = locationManager.getBestProvider(criteria,true);	
	    
	    //Get location for the first time.
	    location = new Location(provider);
	    //location = locationManager.getLastKnownLocation(provider);	     
	    longitude =location.getLongitude();
	    latitude = location.getLatitude();	
	   	    
	    //Listens for changes in location
        LocationListener locationListener = new LocationListener() {
   		 	public synchronized void onLocationChanged(Location location) {  		 		
   		 	
   		 	longitude = location.getLongitude();
		 	latitude = location.getLatitude();	
			}   		
			public void onStatusChanged(String provider, int status, Bundle extras) {}
   		    public void onProviderEnabled(String provider) {}
   		    public void onProviderDisabled(String provider) {}
        };   		    		
   		// Register the listener with the Location Manager to receive location updates
        //int st = Integer.parseInt(prefs.getString("sync_time","10000"));
	   	locationManager.requestLocationUpdates(provider, 3000 , 1, locationListener);// Get changes in location every 1000 ms (1s).
	   	System.out.println("This is provider" + provider);
   }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }    
    
    @Override
    public void onDestroy() {
        finishTheFuckingLoop = true;
        asynch = new Asyncactivity();
        asynch.execute("get");
        super.onDestroy();
    }   
    
    private final BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("receiver", "received");
			person = new Person();
			person = intent.getParcelableExtra("person parcel");
			
		}	
	};

    public void showLocation_Map(View view){
    	Intent intent = new Intent(this, LocationMap.class);
    	startActivity(intent);    	
    }
   
    public void getFriends(View view){
	   	Intent intent = new Intent(this, FriendMenu.class);
		intent.putExtra("person parcel",person);
	  	startActivity(intent); 	   
    } 
    
    public void Settings(View view){
	   Intent intent = new Intent(this, Settings.class);
	   startActivity(intent);	   
    }   
    
    public void startSync()
    {
    	Log.d("main activity", "start sync cajyhg");
    	t.cancel();
    	t = new Timer();
    	t.schedule(new Timertask(), 0, synctime);
    	Log.d("main activity", "start sync cajyhg");
    }
     
    Runnable syncrunner = new Runnable()
    {
    	@Override public void run()
    	{
    		asynch = new Asyncactivity();
    		asynch.execute("get");
    	}
    };
    
    protected class Timertask extends TimerTask
    {
    	@Override public void run()
    	{
    		thandle.post(syncrunner);
    	}
    }
        
    private class Asyncactivity extends AsyncTask <String, Void, String>  //1st - doinback, 2nd - progressupdate, 3rd - postexecute
    {
    	SharedPreferences prefs;    	
    	    	
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
    	
    	@SuppressLint("NewApi")
		@Override
    	protected String doInBackground(String... params)
    	{
    		String text = null;
    		HttpClient client = new DefaultHttpClient();
    		HttpPost poster = new HttpPost("http://ec2-23-22-156-79.compute-1.amazonaws.com/phpfile.php");
    		ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
    	   		
    		System.out.println(action);
    		try
    		{
    			if (!action.equals("signin"))
        		{
        			prefs = getSharedPreferences("com.example.helloworld_preferences", MODE_PRIVATE); 
        			if (action.equals("signup"))
            		{
        				System.out.println("This is action " + action);
            			person.status = "";
            			person.onstat = 1;
            			person.synctime = 10000;
            			sfirst = true;
            			Editor e = prefs.edit();
            			e.putBoolean("visibility",person.onstat == 1 ? true:false);
            			e.putString("status",person.status);
            			e.putString("sync_time", ((Long)person.synctime).toString());
            			e.commit();
            		}
            		else if (action.equals("null"))
            		{
            			
            			System.out.println("this is null");
            			prefs = getSharedPreferences("com.example.helloworld_preferences", MODE_PRIVATE);
            			person.status = prefs.getString("status","");
            			boolean b = (prefs.getBoolean("visibility",true));
            			person.onstat = b ? 1: 0;
            			System.out.println(person.status);
            			person.synctime = Long.parseLong(prefs.getString("sync_time", "10000"));
            		}
            		
        			System.out.println("this is latitude" + latitude);
        			if (finishTheFuckingLoop)
        			{
        				latitude = 0;
        				longitude = 0;
        			}
            		//ideally a getStatus function will be called before this next step
            		NameValuePair stat = new BasicNameValuePair("status", person.status);
            			       
            		NameValuePair lat = new BasicNameValuePair("latitude", ((Double)latitude).toString());
        			NameValuePair lon = new BasicNameValuePair("longitude", ((Double)longitude).toString());
        			NameValuePair onstat = new BasicNameValuePair("onstat", ((Integer)person.onstat).toString());
        			NameValuePair synctime = new BasicNameValuePair("synctime", ((Long)person.synctime).toString());
        			
        			list.add(stat); list.add(synctime);
            		list.add(lat); list.add(lon); list.add(onstat);
        		}
        		else sfirst = true;
        		NameValuePair user = new BasicNameValuePair("mainuser", person.mainuser);
        		NameValuePair act = new BasicNameValuePair("action", params[0]);
        		list.add(user); list.add(act); 
        		
        		Log.d("main activity", "executing sync");
        		System.out.println("executing sync");
        		    		
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
    		}
    		catch (NullPointerException e)
    		{
    			System.out.println("App is finished");
    		}
    		
    		return text;
    	}  	
    	
		protected void onPostExecute(String results)
    	{
			String username = null;
			
			if (results!=null)
    		{
				System.out.println(results);
				try
    			{
    				//fetched objects should be context based; i.e. they should work based on action
    				//make action a global variable. 
					System.out.println("in post execute");
					JSONObject data = new JSONObject(results);
					JSONArray reqs = data.getJSONArray("requests");
					int rlength = reqs.length();
					person.requests = new String[rlength];
					for (int i = 0; i<rlength; i++)
					{
						username = ((JSONObject)reqs.get(i)).getString("username");
						person.requests[i] = username;
					}						
						
					Log.d("main activity", "syncing");
					
					JSONArray frnds = data.getJSONArray("friends");
					int flength = frnds.length();
					
					person.friendnames = new String[flength];
					person.latitudes = new double[flength];
					person.longitudes = new double[flength];
					person.statuses = new String[flength];
					for (int i = 0; i<flength; i++)
					{
						username = ((JSONObject)frnds.get(i)).getString("username");
						double lat = ((JSONObject)frnds.get(i)).getDouble("latitude");
						double lon = ((JSONObject)frnds.get(i)).getDouble("longitude");
						String status = ((JSONObject)frnds.get(i)).getString("status");
						
						person.friendnames[i] = username;
						person.latitudes[i] = lat;
						person.longitudes[i] = lon;
						person.statuses[i] = status;
						
					}
				
					String pstatus = data.getString("pstatus");
					int onstat = data.getInt("onstat");
					long synctime = data.getInt("synctime");
					person.status = pstatus;
					person.onstat = onstat;
					person.synctime = synctime;
				}
				catch(Exception e)
				{
					Log.d("main activity", e.toString());
				}

    		}
    		
			if (!finishTheFuckingLoop)
			{
				person.latitude = latitude;
		        person.longitude = longitude;
				Intent intent = new Intent("android.person");
				intent.putExtra("person parcel",person);
				sendBroadcast(intent);
				if (sfirst)
				{
					if (action.equals("signin"))
					{
						prefs = getSharedPreferences("com.example.helloworld_preferences", MODE_PRIVATE);
						Editor e = prefs.edit();
	        			e.putBoolean("visibility",person.onstat == 1 ? true:false);
	        			e.putString("status",person.status);
	        			e.putString("sync_time", ((Long)person.synctime).toString());
	        			e.commit();
					}
					action = "null";
					sfirst = false;
				}
				if (first)
				{
					first = false;
					Log.d("main activity", "start sync called");
					startSync();
				}
			}
			else person = null;

    	}
    }

	
}

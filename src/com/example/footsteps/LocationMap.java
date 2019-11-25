package com.example.footsteps;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class LocationMap extends MapActivity {
	private double longitude;
	private double latitude;	
    private Criteria criteria = new Criteria();
    private String provider = new String();
    Location location;
    MapController mapController;
    long synctime;
	Person person;
	MapView mapView;
	List<Overlay> mapOverlays;
	OverlayItem overlayitemt;
	OverlayItem overlayitem2;
	Drawable drawable;
	Drawable drawable2;
	HelloItemizedOverlay itemizedoverlay;
	HelloItemizedOverlay itemizedoverlay2;
	
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);
        
        IntentFilter filter = new IntentFilter("android.person");
        registerReceiver(receiver, filter);
        mapView = (MapView) findViewById(R.id.mapview);
    	mapOverlays = mapView.getOverlays(); 	
    	overlayitemt = new OverlayItem(new GeoPoint(12, 14), "empty","empty");
    	overlayitem2 = new OverlayItem(new GeoPoint(12, 14), "empty","empty");
    	drawable = this.getResources().getDrawable(R.drawable.map_marker);
    	drawable2 = this.getResources().getDrawable(R.drawable.map_marker_self);
        itemizedoverlay = new HelloItemizedOverlay(drawable, this);
        itemizedoverlay2 = new HelloItemizedOverlay(drawable2, this);
        itemizedoverlay.addOverlay(overlayitemt);
        itemizedoverlay2.addOverlay(overlayitem2);
        mapOverlays.add(itemizedoverlay);
        mapOverlays.add(itemizedoverlay2);
      } 
    
    private final BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("receiver", "received");
			person = new Person();
			person = intent.getParcelableExtra("person parcel");
			int siz = itemizedoverlay.size();
			for(int i = 0; i<siz; i++){
				itemizedoverlay.removeOverlay(0);
			}
			int siz2 = itemizedoverlay2.size();
			for(int i = 0; i<siz2; i++){
				itemizedoverlay2.removeOverlay(0);
			}
			System.out.println("Before Display on Map");
		   	displayOnMap();
		   	
		}	
	};
    
    //Actual reciever method.
    public void displayOnMap(){    	
    	
    	System.out.println("Inside Display on Map");
        mapView.setBuiltInZoomControls(true);
        int len = person.friendnames.length +1;
        double longitudein; double latitudein;
        
        String[] statuses = new String[len];
        String[] friends = new String[len];
        double[] latitudes = new double[len];
        double[] longitudes = new double[len];
        System.out.println("This is  the length" + len);
        
        
        for(int i =0; i<len-1; i++){
        	statuses[i] = person.statuses[i];
        	friends[i] = person.friendnames[i];
        	latitudes[i] = person.latitudes[i];
        	longitudes[i] = person.longitudes[i];        	
        }
        
        statuses[len-1] = person.status;
        friends[len- 1] = "Me";
        latitudes[len - 1] = person.latitude;
        longitudes[len-1] = person.longitude;
        System.out.println("latitude"  + person.latitude);
        
        GeoPoint point;    	    
    	 mapController = mapView.getController();         
         double maxLatitude = latitudes[0]; double minLatitude =latitudes[0];
         double maxLongitude = longitudes[0]; double minLongitude = longitudes[0];
         
		    if (mapController != null) {
		    	
		    	for (int i = 0; i<len; i++){
		    		System.out.println("Outside Outside Outside Outside");
		    		
		    		if(longitudes[i] != 0.0){
		    					    			
		    			System.out.println("Inside inside inside inside");
		    			longitudein = longitudes[i]; 
				    	latitudein = latitudes[i];
				    	point = new GeoPoint((int)(latitudein*1e6), (int)(longitudein*1e6));
			    		OverlayItem overlayitem = new OverlayItem(point, friends[i],statuses[i]);
		    			
		    			if(friends[i].equals("Me")){
		    				OverlayItem overlayitem2 = new OverlayItem(point, friends[i],statuses[i]);
		    				itemizedoverlay2.addOverlay(overlayitem2);
		    				
		    			}
		    			else
		    				itemizedoverlay.addOverlay(overlayitem);
		    			
		    			if(minLongitude>longitudes[i] || minLongitude == 0) {
		    			minLongitude = longitudes[i];		    			
		    			}
		    			if(minLatitude>latitudes[i]|| minLatitude == 0) {
		    			minLatitude = latitudes[i];		    			
		    			}
		    			if(maxLongitude<longitudes[i] || maxLatitude == 0) {
		    			maxLongitude = longitudes[i];		    			
		    			}
		    			if(maxLatitude<latitudes[i] || maxLatitude == 0) {
		    			maxLatitude = latitudes[i];	
		    			}
		    		}
		    	}
		    	
		    	mapOverlays.add(itemizedoverlay);
		    	mapOverlays.add(itemizedoverlay2);
		    	mapController.zoomToSpan(((int)((maxLatitude - minLatitude)*1e6)), ((int)((maxLongitude - minLongitude)*1e6)));
		    	mapController.animateTo(new GeoPoint((int)((maxLatitude + minLatitude)/2*1e6) ,((int)((maxLongitude + minLongitude)/2 *1e6))));
		    	mapView.postInvalidate();
		    }
		    
    }   

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_location_map, menu);
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

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected void onStop() {
	    super.onStop();
	}
	
	@Override
	protected void onStart() {
	    super.onStart();
	}
}

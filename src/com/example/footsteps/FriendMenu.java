package com.example.footsteps;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class FriendMenu extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	Person person;
	boolean received = false;

	@SuppressLint({ "NewApi", "NewApi" })
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.friends_menu); 
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
	
	@Override
	protected void onResume(){
			super.onResume();
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
    
    public void getFriends(View view){
 	   Intent intent = new Intent(this, FriendsList.class);
 	   String message = "Hksfmf ksfps";
 	   intent.putExtra(EXTRA_MESSAGE, message);
 	   startActivity(intent); 	   
    }    
    
    public void getAddFriend(View view){
  	   Intent intent = new Intent(this, AddFriend.class);
  	   intent.putExtra("person parcel",person);
  	   startActivity(intent); 	   
    }  
    
    public void Requests(View view){
   	   Intent intent = new Intent(this, FriendRequests.class);
   	   intent.putExtra("person parcel",person);
   	   startActivity(intent); 	   
    }  
}
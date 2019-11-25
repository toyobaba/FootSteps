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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SignInPage extends Activity {
	
	ImageButton button;
	ImageButton button2;
	EditText u;
	EditText p;
	Person person = new Person();
	String message;
	String error;
	String action = null;
	
	public String singninStatus = "Successful";
	SharedPreferences prefs;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sign_in);
        u = (EditText)findViewById(R.id.username);
    	p = (EditText)findViewById(R.id.password);
    	p.setTypeface(Typeface.DEFAULT);
    	p.setTransformationMethod(new PasswordTransformationMethod());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void signInside(View view){
    	button = (ImageButton)findViewById(R.id.Signinbutton);
    	button.setClickable(false);
    	button2 = (ImageButton)findViewById(R.id.Signupbutton);
    	button2.setClickable(false);
    	new Asyncactivity().execute("signin");
	}
  
    public void signup(View view){
    	button = (ImageButton)findViewById(R.id.Signinbutton);
    	button.setClickable(false);
    	button2 = (ImageButton)findViewById(R.id.Signupbutton);
    	button2.setClickable(false);
    	new Asyncactivity().execute("signup");
    }	
  
    private class Asyncactivity extends AsyncTask <String, Void, String> //1st - doinback, 2nd - progressupdate, 3rd - postexecute
    {  	
  		protected String getText(HttpEntity entity) throws	IllegalStateException, IOException{
  			InputStream in  = entity.getContent();
  			StringBuffer out = new StringBuffer();
  			int n = 1;
  			while (n>0){
  				byte[]b = new byte[4096];
  				n = in.read(b);
  				if (n>0) out.append(new String (b, 0, n));
  			}
  		return out.toString();
  		}
  	
  		@Override
  		protected String doInBackground(String... params){
  		String text = null;
  		HttpClient client = new DefaultHttpClient();
  		HttpPost poster = new HttpPost("http://ec2-23-22-156-79.compute-1.amazonaws.com/phpfile.php");
  		ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
  		action = params[0];
  		
  		if (params[0] == "signup" || params[0] == "signin")
  		{
  			if (u.getText().length() == 0 || p.getText().length() == 0)
  				return null;
  			
  			NameValuePair user = new BasicNameValuePair("username", u.getText().toString());
  			NameValuePair pass = new BasicNameValuePair("password", p.getText().toString());
  			NameValuePair act = new BasicNameValuePair("action", params[0]);
  			list.add(user); list.add(pass); list.add(act);			
  		}
  		
  		try
  		{
  			poster.setEntity(new UrlEncodedFormEntity(list)); //unsupported encoding exception
  			HttpResponse response = client.execute(poster);
  			HttpEntity entity = response.getEntity();
  			text = getText(entity);
  		}
		catch (Exception e){
			Log.d("main activity", e.toString());
			return e.getLocalizedMessage();
		}
  		return text;
  	}  	
  	
  	protected void onPostExecute(String results){
  		boolean getter = false;	
  		
		if (results!=null)
		{
			try
			{				
				JSONObject json_data = new JSONObject(results);
				message = json_data.getString("message");
				error = json_data.getString("error");
				
				if ((action == "signup" || action == "signin") && error.equals("")){
					person.mainuser = u.getText().toString();
					System.out.println(person.mainuser);
					//counter = 0;
					
					if (action == "signin")
						getter = true;
					
					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					intent.putExtra("person parcel",person);
					intent.putExtra("act.action", action);
				  	startActivity(intent);
				  	finish();
				}
				
				if (message.equals(""))
					Toast.makeText(getApplicationContext(),error ,Toast.LENGTH_LONG).show();
  			}
			
			catch(JSONException e){
				Log.d("main activity", e.toString());
			}
				
			catch (ParseException e){
				e.printStackTrace();
			}
  		}	
  		else Toast.makeText(getApplicationContext(),"Invalid username. Try again" ,Toast.LENGTH_LONG).show();
		button.setClickable(true);
		button.setClickable(true); 		
  	}
  }
}

package org.cneo.hotornotapp;

import org.cneo.hotornotapp.AboutDialog.onSubmitListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements onSubmitListener {
	private static String nickname;
	private String HOTURL = "http://www.hotornot.de/index.php";
	private TextView textView;
	
	final Context context = this;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	onClickMaybe();
	        	return true;
	        case R.id.check_female:
	       		HOTURL = "http://www.hotornot.de/index.php/?changegender=w";
	       		item.setChecked(true);
	       		Toast.makeText(getApplicationContext(),
	       				"Female Selected",
	       				Toast.LENGTH_LONG).show();
	       		return true;
	        case R.id.check_male:
	            HOTURL = "http://www.hotornot.de/index.php/?changegender=m";
	            item.setChecked(true);
	        	Toast.makeText(getApplicationContext(),
	        			"Male Selected",
	        			Toast.LENGTH_LONG).show();
	        	return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView) findViewById(R.id.textView1);

        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(new String[] { HOTURL });
    }

    public String cutBack(String txt, String teil, int number) {
        for (int i = 0; i < number; i++) {
            txt = txt.substring(0, txt.lastIndexOf(teil));
        }
        return txt;
    }
    
    public String cutFront(String txt, String teil, int number) {
        for (int i = 0; i < number; i++) {
            txt = txt.substring(txt.indexOf(teil) + 1, txt.length());
        }
        return txt;
    } 
    
    private class PostAsyncTask extends AsyncTask<String, Integer, Double>{
    	@Override
    	protected Double doInBackground(String... params) {
    		// TODO Auto-generated method stub
    		postData(params[0]);
    		return null;
    	}
    	
    	protected void onPostExecute(Double result){
//    		pb.setVisibility(View.GONE);
    		Toast.makeText(getApplicationContext(), "successfully logged", Toast.LENGTH_LONG).show();
    	}
    	
    	@SuppressWarnings("unused")
		public void postData(String valueIWantToSend) {
    		HttpClient httpclient = new DefaultHttpClient();
    		// specify the URL you want to post to
    		HttpPost httppost = new HttpPost("http://webershandwick.de/receiver.php");
    		try {
    			// create a list to store HTTP variables and their values
    			List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
    			// add an HTTP variable and value pair
    			nameValuePairs.add(new BasicNameValuePair("myHttpData", valueIWantToSend));
    			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    			// send the variable and value, in other words post, to the URL
    			HttpResponse response = httpclient.execute(httppost);
    		} catch (ClientProtocolException e) {
    			// process exception
    		} catch (IOException e) {
    			// process exception
    		}
    	}
    }
    
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    	ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            for (String url : urls) {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(
                            new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }        

        @Override
        protected void onPostExecute(String result) {
        	String pix_url = "";
        	nickname = "";
        	
            Pattern p = Pattern.compile("(<title.*?>)(.+?)(</title>)");
            Matcher m = p.matcher(result);
            
            if (m.find())
            {
            	String codeGroup = m.group();
            	
            	nickname = nickname + cutFront(codeGroup, "u", 1);
            	nickname = cutBack(nickname, "?", 1);
            }
            
            p = Pattern.compile("<div style=\"(.+?)\">");
            m = p.matcher(result);
            
            if (m.find())
            {
            	String codeGroup = m.group();
            	
            	pix_url = pix_url + cutFront(codeGroup, "'", 1);
            	pix_url = cutBack(pix_url, "'", 1);
            	
            	textView.setText("Nickname:" + nickname);
            	
            	new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
            	.execute(pix_url);
            }            
        }
    }

    public void readWebpage(View view) {
    	DownloadWebPageTask task = new DownloadWebPageTask();
    	task.execute(new String[] { HOTURL });
    	
    	Timestamp timestamp = new Timestamp(new Date().getTime());
    	
    	new PostAsyncTask().execute(timestamp.toString() + "," + nickname + "\n");
    }
    
    public void onClickMaybe() {  
        AboutDialog fragment1 = new AboutDialog();   
        fragment1.mListener = MainActivity.this;  
        fragment1.show(getFragmentManager(), "");
    }

	@Override
	public void setOnSubmitListener(String arg) {
		// TODO Auto-generated method stub
		
	} 
	
}
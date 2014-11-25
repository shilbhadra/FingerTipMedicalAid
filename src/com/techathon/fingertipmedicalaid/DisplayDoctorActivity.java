package com.techathon.fingertipmedicalaid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DisplayDoctorActivity extends Activity implements LocationListener{
	
	String[] nodeXMLValue;
	LocationManager locationManager;
	ProgressDialog progressDialog;
	String strLatitude;
    String strLongitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_doctor);
		
		// Get the location manager
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    // Define the criteria how to select the location provider -> use default
	    Criteria criteria = new Criteria();
	    String provider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);
	    
	    double latitude;
    	double longitude;
	    
	    if(location != null) {
	    	latitude = location.getLatitude();
	    	longitude = location.getLongitude();
	    }
	    else
	    {
	    	latitude = (double)22.5115645;
	    	longitude = (double)88.4108479;
	    }
	    
	    strLatitude = Double.toString(latitude);
	    strLongitude = Double.toString(longitude);
	    
	    ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	    
	    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
	    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	    
	    if(!isConnected) {
	    	AlertDialog alertDialog = new AlertDialog.Builder(DisplayDoctorActivity.this).create();
	    	alertDialog.setTitle("Internet");
	    	alertDialog.setMessage("Please check your Internet connection.");
	    	alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
	    	    new DialogInterface.OnClickListener() {
	    	        public void onClick(DialogInterface dialog, int which) {
	    	            dialog.dismiss();
	    	            finish();
	    	        }
	    	    });
	    	
	    	alertDialog.show();
	    }
	    
	    String url = "http://fingertipserver.mybluemix.net/FingerTipMedicalAidServlet?medical=doctors&lattitude=" + latitude + "&longitude=" + longitude;
	    //String url = "http://fingertipserver.mybluemix.net/FingerTipMedicalAidServlet?medical=doctors&lattitude=22.5115645&longitude=88.4108479";
	    
	    if(isConnected) {
	    	GetXMLTask task = new GetXMLTask();
	    	task.execute(new String[] {url});
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_doctor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onResume() {
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, this);
	    super.onResume();
	}

	@Override
	public void onPause() {
	    locationManager.removeUpdates(this);
	    super.onPause();
	}
	
	private static String getValue(String tag, Element element) {  
		NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();  
		Node node = (Node) nodeList.item(0);  
		return node.getNodeValue();  
	}
	
	private class GetXMLTask extends AsyncTask<String, Void, String> {
		@Override
	    protected String doInBackground(String... urls) {
			BufferedReader bufferedReader = null;
		    String fetchedXML = null;
			
			try {
				HttpClient httpClient = new DefaultHttpClient();
				String url = urls[0];
				
				System.out.println("URL -> " + url);
				
				// uri = new URI(url);
				HttpGet request = new HttpGet(url);
		        //request.setURI(uri);
		        HttpResponse response = httpClient.execute(request);
		        int statusCode = response.getStatusLine().getStatusCode();
		        
		        System.out.println("Response Code -> " + statusCode);
		        
		        bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		        StringBuffer stringBuffer = new StringBuffer("");
		        String l = "";
		        String nl = System.getProperty("line.separator");
		        while ((l = bufferedReader.readLine()) != null) {
		            stringBuffer.append(l + nl);
		        }
		        
		        bufferedReader.close();
		        fetchedXML = stringBuffer.toString();
		        
		        System.out.println("fetchedXML -> " + fetchedXML);
			}
			catch(Exception e) {
				System.out.println(e.getMessage());
				System.out.println(e.toString());
			}
			finally {
				try {
					bufferedReader.close();
				}
				catch(Exception e) {
					System.out.println(e.getMessage());
				}
			}
			
			//String fetchedXML = "<ambulancedetails><ambulance><id>100</id><name>Seva Trust</name><distance>2.2</distance><phone>9230475959</phone><address>22 Rajarhat Road</address></ambulance><ambulance><id>101</id><name>Mother Mission</name><distance>3.2</distance><phone>9832673217</phone><address>47/2 Kolutola Road</address></ambulance></ambulancedetails>";
			return fetchedXML;
		}
		
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(DisplayDoctorActivity.this,"","Loading",false);
		}
		
		@Override
	    protected void onPostExecute(String fetchedXML) {
			
			if(fetchedXML == null) {
		    	AlertDialog alertDialog = new AlertDialog.Builder(DisplayDoctorActivity.this).create();
		    	alertDialog.setTitle("Internet");
		    	alertDialog.setMessage("Please check your Internet connection.");
		    	alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
		    	    new DialogInterface.OnClickListener() {
		    	        public void onClick(DialogInterface dialog, int which) {
		    	            dialog.dismiss();
		    	            DisplayDoctorActivity.this.finish();
		    	        }
		    	    });
		    	
		    	alertDialog.show();
		    }
			else {
				//XML Parsing
				Document document = null;
				try {
					DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					InputSource inputSource = new InputSource();
					inputSource.setCharacterStream(new StringReader(fetchedXML));
					document = documentBuilder.parse(inputSource);
				}
				catch(Exception e) {
					System.out.println(e.getMessage());
				}
				
				Element element = document.getDocumentElement();
				element.normalize();
				
				NodeList nodeList = document.getElementsByTagName("doctor");
				
				String[] values = new String[nodeList.getLength()];
				nodeXMLValue = new String[nodeList.getLength()];
				
				for(int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					if(node.getNodeType() == Node.ELEMENT_NODE){
						Element nodeElement = (Element)node;
						
						String listValue = getValue("name", nodeElement);
						listValue += " (" + getValue("distance", nodeElement) + " km)";
						values[i] = listValue;
						
						String listArrayValue = "<doctor>";
						listArrayValue += "<id>" + getValue("id", nodeElement) + "</id>";
						listArrayValue += "<name>" + getValue("name", nodeElement) + "</name>";
						listArrayValue += "<distance>" + getValue("distance", nodeElement) + "</distance>";
						listArrayValue += "<phone>" + getValue("phone", nodeElement) + "</phone>";
						listArrayValue += "<address>" + getValue("address", nodeElement) + "</address>";
						listArrayValue += "<availability>" + getValue("availability", nodeElement) + "</availability>";
						listArrayValue += "<tolongitude>" + getValue("longitude", nodeElement) + "</tolongitude>";
						listArrayValue += "<tolatitude>" + getValue("latitude", nodeElement) + "</tolatitude>";
						listArrayValue += "<fromlongitude>" + strLongitude + "</fromlongitude>";
						listArrayValue += "<fromlatitude>" + strLatitude + "</fromlatitude>";
						listArrayValue += "</doctor>";
						nodeXMLValue[i] = listArrayValue;
					}
				}
				
				// Get ListView object from xml
		        ListView listView = (ListView) findViewById(R.id.listView1);
		        
		        // Define a new Adapter
		        // First parameter - Context
		        // Second parameter - Layout for the row
		        // Third parameter - ID of the TextView to which the data is written
		        // Forth - the Array of data
	
		        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DisplayDoctorActivity.this,
		          android.R.layout.simple_list_item_1, android.R.id.text1, values);
		        
		        // Assign adapter to ListView
		        listView.setAdapter(adapter);
		        
		        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		        	@Override
		        	public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
		        		Intent intent = new Intent(DisplayDoctorActivity.this, DoctorDetailsActivity.class);
		        		intent.putExtra("ListItemXML", DisplayDoctorActivity.this.nodeXMLValue[position]);
		        		startActivity(intent);
		        	} 
				});
			}

	        progressDialog.dismiss();
	    }
	}
}

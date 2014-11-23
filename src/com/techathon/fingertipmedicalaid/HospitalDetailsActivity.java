package com.techathon.fingertipmedicalaid;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class HospitalDetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hospital_details);
		
		Intent intent = getIntent();
		String listItemXML = intent.getStringExtra("ListItemXML");
		
		TextView textView1 = (TextView)findViewById(R.id.textView1);
		TextView textView2 = (TextView)findViewById(R.id.textView2);
		TextView textView3 = (TextView)findViewById(R.id.textView3);
		TextView textView4 = (TextView)findViewById(R.id.textView4);
		
		//XML Parsing
		Document document = null;
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource inputSource = new InputSource();
			inputSource.setCharacterStream(new StringReader(listItemXML));
			document = documentBuilder.parse(inputSource);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		Element element = document.getDocumentElement();
		element.normalize();
		
		NodeList nodeList = document.getElementsByTagName("hospital");
		
		for(int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				Element nodeElement = (Element)node;
				
				textView1.setText(getValue("name", nodeElement));
				textView2.setText(getValue("address", nodeElement));
				textView3.setText(getValue("phone", nodeElement));
				textView4.setText("Departments: " + getValue("department", nodeElement));
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hospital_details, menu);
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
	
	private static String getValue(String tag, Element element) {  
		NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();  
		Node node = (Node) nodeList.item(0);  
		return node.getNodeValue();  
	}
}

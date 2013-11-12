package com.newthinktank.stockquotes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

public class StockInfoActivity extends Activity {
	
	// Used to identify the app in the LogCat, so I
	// can output messages and debug the program
	
	private static final String TAG = "STOCKQUOTE";
	
	// Define the TextViews I use in activity_stock_info.xml
	
	TextView companyNameTextView;
	TextView yearLowTextView;
	TextView yearHighTextView;
	TextView daysLowTextView;
	TextView daysHighTextView;
	TextView lastTradePriceOnlyTextView;
	TextView changeTextView;
	TextView daysRangeTextView;
	
	// XML node keys
	static final String KEY_ITEM = "quote"; // parent node
	static final String KEY_NAME = "Name";
	static final String KEY_YEAR_LOW = "YearLow";
	static final String KEY_YEAR_HIGH = "YearHigh";
	static final String KEY_DAYS_LOW = "DaysLow";
	static final String KEY_DAYS_HIGH = "DaysHigh";
	static final String KEY_LAST_TRADE_PRICE = "LastTradePriceOnly";
	static final String KEY_CHANGE = "Change";
	static final String KEY_DAYS_RANGE = "DaysRange";
	
	// XML Data to Retrieve
	String name = "";
	String yearLow = "";
	String yearHigh = "";
	String daysLow = "";
	String daysHigh = "";
	String lastTradePriceOnly = "";
	String change = "";
	String daysRange = "";
	
	// Used to make the URL to call for XML data
	String yahooURLFirst = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22";
	String yahooURLSecond = "%22)&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
	
	// NEW STUFF
	
	// Holds values pulled from the XML document using XmlPullParser
	String[][] xmlPullParserArray = {{"AverageDailyVolume", "0"}, {"Change", "0"}, {"DaysLow", "0"},
			{"DaysHigh", "0"}, {"YearLow", "0"}, {"YearHigh", "0"},
			{"MarketCapitalization", "0"}, {"LastTradePriceOnly", "0"}, {"DaysRange", "0"},
			{"Name", "0"}, {"Symbol", "0"}, {"Volume", "0"},
			{"StockExchange", "0"}};
			
	int parserArrayIncrement = 0;
			
	// END OF NEW STUFF
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Creates the window used for the UI
		setContentView(R.layout.activity_stock_info);
		
		// Get the message from the intent that has the stock symbol
		Intent intent = getIntent();
		String stockSymbol = intent.getStringExtra(MainActivity.STOCK_SYMBOL);
		
		// Initialize TextViews
		companyNameTextView = (TextView) findViewById(R.id.companyNameTextView);
		yearLowTextView = (TextView) findViewById(R.id.yearLowTextView);
		yearHighTextView = (TextView) findViewById(R.id.yearHighTextView);
		daysLowTextView = (TextView) findViewById(R.id.daysLowTextView);
		daysHighTextView = (TextView) findViewById(R.id.daysHighTextView);
		lastTradePriceOnlyTextView = (TextView) findViewById(R.id.lastTradePriceOnlyTextView);
		changeTextView = (TextView) findViewById(R.id.changeTextView);
		daysRangeTextView = (TextView) findViewById(R.id.daysRangeTextView);
		
		// Sends a message to the LogCat
		Log.d(TAG, "Before URL Creation " + stockSymbol);
		
		// Create the YQL query
		final String yqlURL = yahooURLFirst + stockSymbol + yahooURLSecond;
		
		// The Android UI toolkit is not thread safe and must always be 
		// manipulated on the UI thread. This means if I want to perform
		// any network operations like grabbing xml data, I have to do it
		// in its own thread. The problem is that you can't write to the
		// GUI from outside the main activity. AsyncTask solves those problems
		
		new MyAsyncTask().execute(yqlURL);

	}
	
	// Use AsyncTask if you need to perform background tasks, but also need
	// to change components on the GUI. Put the background operations in
	// doInBackground. Put the GUI manipulation code in onPostExecute
	
	private class MyAsyncTask extends AsyncTask<String, String, String>{
		
		// String... arg0 is the same as String[] args
		protected String doInBackground(String... args) {
			
			// NEW STUFF
			
			try{
				
				Log.d("test","In XmlPullParser");
				
				// It is recommended to use the XmlPullParser because
				// it is faster and requires less memory then the DOM API
				
				// XmlPullParserFactory provides you with the ability to 
				// create pull parsers that parse XML documents
				
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

				// Parser supports XML namespaces
				  factory.setNamespaceAware(true);

				  // Provides the methods needed to parse XML documents
				  XmlPullParser parser = factory.newPullParser(); 

				  // InputStreamReader converts bytes of data into a stream
				  // of characters
				  
				  parser.setInput(new InputStreamReader(getUrlData(args[0])));  

				  // Passes the parser and the first tag in the XML document
				  // for processing
				  
				  beginDocument(parser,"query");

				  // Get the currently targeted event type, which starts
				  // as START_DOCUMENT
				  
				  int eventType = parser.getEventType();

				  do{
					  
					// Cycles through elements in the XML document while
					// neither a start or end tag are found

					  nextElement(parser);

					  // Switch to the next element
					  
					  parser.next();
					  
					  // Get the current event type

					  eventType = parser.getEventType();

					  // Check if a value was found between 2 tags
					  
					  if(eventType == XmlPullParser.TEXT){
								  
						  // Get the text from between the tags
						  
						  String valueFromXML = parser.getText();
						  
						  // Store it in an array with the corresponding tag
						  // value
						  
						  xmlPullParserArray[parserArrayIncrement++][1] = valueFromXML;

					  }

				  } while (eventType != XmlPullParser.END_DOCUMENT) ;  
				
			}
			
			catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			
			finally {
		    }
			
			// END OF NEW STUFF
			
			return null;
		}
		
		// NEW STUFF
		
		public InputStream getUrlData(String url) throws URISyntaxException, 
        ClientProtocolException, IOException {

			// Used to get access to HTTP resources
			
			DefaultHttpClient client = new DefaultHttpClient();
			
			// Retrieves information from the URL
			
			HttpGet method = new HttpGet(new URI(url));
			
			// Gets a response from the client on whether the 
			// connection is stable
			
			HttpResponse res = client.execute(method);
			
			// An HTTPEntity may be returned using getEntity() which tells 
			// the system where the content is coming from
			
			return res.getEntity().getContent();
		}
		
		public final void beginDocument(XmlPullParser parser, String firstElementName) throws XmlPullParserException, IOException
	    {
			int type;
			
			// next() advances to the next element in the XML
			// document being a starting or ending tag, or a value
			// or the END_DOCUMENT
			
		    while ((type=parser.next()) != parser.START_TAG
		                   && type != parser.END_DOCUMENT) {
		            ;
		    }
		
		    // Throw an error if a start tag isn't found
		    
		    if (type != parser.START_TAG) {
		    	throw new XmlPullParserException("No start tag found");
		    }
		
		    // Verify that the tag passed in is the first tag in the XML
		    // document
		    
		    if (!parser.getName().equals(firstElementName)) {
		            throw new XmlPullParserException("Unexpected start tag: found " + parser.getName() +
		                    ", expected " + firstElementName);
		    }
	    }
		
		public final void nextElement(XmlPullParser parser) throws XmlPullParserException, IOException
		{
			int type;
			
			// Cycles through elements in the XML document while
			// neither a start or end tag are found
			
			while ((type=parser.next()) != parser.START_TAG
		                   && type != parser.END_DOCUMENT) {
		            ;
			}
		}
		
		// END OF NEW STUFF
		
		// Changes the values for a bunch of TextViews on the GUI
		protected void onPostExecute(String result){
			
			companyNameTextView.setText(xmlPullParserArray[9][1]);
			yearLowTextView.setText("Year Low: " + xmlPullParserArray[4][1]);
			yearHighTextView.setText("Year High: " + xmlPullParserArray[5][1]);
			daysLowTextView.setText("Days Low: " + xmlPullParserArray[2][1]);
			daysHighTextView.setText("Days High: " + xmlPullParserArray[3][1]);
			lastTradePriceOnlyTextView.setText("Last Price: " + xmlPullParserArray[7][1]);
			changeTextView.setText("Change: " + xmlPullParserArray[1][1]);
			daysRangeTextView.setText("Daily Price Range: " + xmlPullParserArray[8][1]);
			
		}

	}

}

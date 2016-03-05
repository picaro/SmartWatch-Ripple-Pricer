package com.op.ripplepricer.advancedwidget;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;

/**
 * The simple text control shows a user configured text on the accessory. The
 * text is stored in shared preferences.
 * This sample also includes multiple widgets.
 */
public class AdvancedWidgetControl extends ControlExtension {

    public static final String RIPPLE_PRICE_URL = "http://data.ripple.com/v2/exchanges/XRP/USD+rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B?limit=1&descending=true";

	
    private final SharedPreferences mPreferences;

    private final OnSharedPreferenceChangeListener mPreferenceListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (TextUtils.equals(mContext.getString(R.string.preference_widget_text_key), key)) {
                showStoredText();
            }
        }
    };

    AdvancedWidgetControl(Context context, String hostAppPackageName) {
        super(context, hostAppPackageName);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public void onResume() {
        // Control is now visible. Start listening for changes.
        mPreferences.registerOnSharedPreferenceChangeListener(mPreferenceListener);

        // Send a UI when the extension becomes visible.
        showStoredText();
    }

    @Override
    public void onPause() {
        // Control is no longer visible. Stop listening for changes.
        mPreferences.unregisterOnSharedPreferenceChangeListener(mPreferenceListener);
    }

    private void showStoredText() {
        String widgetText = retrieveOldPrice();

        //NetworkOnMainThreadException fix - async
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
		try {
			widgetText = tryToGetNewPrice(widgetText);
	    } catch(Exception ex) {
	    	   ex.printStackTrace(); 
		}
		
        
        // Prepare a bundle with the string and the resource id in the layout
        // that shall be updated.
        Bundle bundle = new Bundle();
        bundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.control_text_box_view);
        bundle.putString(Control.Intents.EXTRA_TEXT, widgetText);
        Bundle[] layoutData = new Bundle[1];
        layoutData[0] = bundle;

        showLayout(R.layout.control_text_box, layoutData);
    }

	private String tryToGetNewPrice(String widgetText) throws IOException, JSONException {
			return String.format("$%.4f", readXRPPriceFromJson());
    }

	private String retrieveOldPrice() {
		String widgetText = mPreferences.getString(
        mContext.getString(R.string.preference_widget_text_key),
        mContext.getString(R.string.preference_widget_text_default));
		return widgetText;
	}
	
    private double readXRPPriceFromJson() throws IOException, JSONException {
        String json = getJsonFromRest();
        JSONObject jObject = new JSONObject(json);
        JSONArray jsonArray = jObject.getJSONArray("exchanges");
        JSONObject so = (JSONObject) jsonArray.get(0);
        return so.getDouble("rate");
    }

    private String getJsonFromRest() throws IOException {
        URL url = new URL(RIPPLE_PRICE_URL);
        InputStream is = url.openStream();
        String json = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return json;
    }

}

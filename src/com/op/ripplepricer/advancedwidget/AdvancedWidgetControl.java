/*
Copyright (c) 2014, Sony Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Mobile Communications AB nor the names
 of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.op.ripplepricer.advancedwidget;

import java.io.IOException;
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

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;

/**
 * The simple text control shows a user configured text on the accessory. The
 * text is stored in shared preferences.
 * This sample also includes multiple widgets.
 */
public class AdvancedWidgetControl extends ControlExtension {

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
        String widgetText = mPreferences.getString(
        mContext.getString(R.string.preference_widget_text_key),
       mContext.getString(R.string.preference_widget_text_default));

        //NetworkOnMainThreadException fix - async
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
        try {
        	Log.d("RIPPLE","aaaaaa");
        	URL url = new URL("http://rippleprice.com/wp-content/themes/ripple/priceinfo.txt");
        	   Scanner s = new Scanner(url.openStream());
        	   widgetText = "XRP " + s.next();
        	   // read from your scanner
           	Log.e("RIPPLE","price:" + widgetText);
        }
    	catch(IOException ex) {
    	   ex.printStackTrace(); // for now, simply output it.
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

}

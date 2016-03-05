package com.op.ripplepricer.advancedwidget;

import java.util.Random;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.widget.Widget;
import com.sonyericsson.extras.liveware.extension.util.widget.BaseWidget;


class SmallTextWidget extends BaseWidget {

    public static final int WIDTH_SMALL = 86;
    public static final int HEIGHT_SMALL = 34;

    public static final int WIDTH_LARGE = 215;
    public static final int HEIGHT_LARGE = 68;

    private SharedPreferences mPreferences;

    private final OnSharedPreferenceChangeListener mPreferenceListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (TextUtils.equals(mContext.getString(R.string.preference_widget_text_key), key)) {
                String widgetText = mPreferences.getString(
                        mContext.getString(R.string.preference_widget_text_key),
                        mContext.getString(R.string.preference_widget_text_default));
                Random rand = new Random();
                int randomNum = rand.nextInt((50 - 1) + 1) + 1;
                widgetText = "z" + randomNum;;
                        showStoredText(widgetText);
            }
        }
    };

    /**
     * Widget constructor. Must be public. initialisation done in onCreate()
     * 
     * @param bundle
     */
    public SmallTextWidget(WidgetBundle bundle) {
        super(bundle);
    }

    @Override
    public void onCreate() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public void onStartRefresh() {
        // Widget is now visible. Start listening for changes.
        mPreferences.registerOnSharedPreferenceChangeListener(mPreferenceListener);

        // Show the text that is saved in the preferences.
        String widgetText = mPreferences.getString(
                mContext.getString(R.string.preference_widget_text_key),
                mContext.getString(R.string.preference_widget_text_default));
        Random rand = new Random();
        int randomNum = rand.nextInt((50 - 1) + 1) + 1;
        widgetText = "" + randomNum;
        
        showStoredText(widgetText);
    }

    @Override
    public void onStopRefresh() {
        // Widget is no longer visible. Stop listening for changes.
        mPreferences.unregisterOnSharedPreferenceChangeListener(mPreferenceListener);
    }

    @Override
    public void onTouch(int type, int x, int y) {
        // User has tapped the widget. Open the control part of the extension.
        Intent intent = new Intent(Control.Intents.CONTROL_START_REQUEST_INTENT);
        intent.putExtra(Control.Intents.EXTRA_AEA_PACKAGE_NAME, mContext.getPackageName());
        sendToHostApp(intent);
    }

    protected void showStoredText(String text) {
        // Prepare a bundle with the string and the resource id in the layout
        // that shall be updated.
        Log.d(AdvancedWidgetExtensionService.LOG_TAG, "Showing small layout " + this.toString());
        Bundle bundle = new Bundle();
        bundle.putInt(Widget.Intents.EXTRA_LAYOUT_REFERENCE,
                R.id.widget_text_box_view);
        bundle.putString(Control.Intents.EXTRA_TEXT, text);
        Bundle[] layoutData = new Bundle[1];
        layoutData[0] = bundle;
        showLayout(getLayoutId(), layoutData);
    }

    protected int getLayoutId() {
        return R.layout.widget_text_box;
    }

    @Override
    public int getWidth() {
        return WIDTH_SMALL;
    }

    @Override
    public int getHeight() {
        return HEIGHT_SMALL;
    }

    @Override
    public int getPreviewUri() {
        return R.drawable.preview_small;
    }

    @Override
    public int getName() {
        return R.string.widget_name_1_2;
    }

}

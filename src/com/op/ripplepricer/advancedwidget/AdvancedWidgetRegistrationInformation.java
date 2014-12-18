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

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;
import com.sonyericsson.extras.liveware.extension.util.registration.WidgetContainer;

import java.util.UUID;

/**
 * The simple text registration information is used to determine if the
 * extension can run on different accessory. The simple text extension has two
 * widgets that require widget API version 3 and one control that require
 * control API version 2.
 */
public class AdvancedWidgetRegistrationInformation extends RegistrationInformation {

    final Context mContext;
    private String extensionKey;
    private static final String EXTENSION_KEY_PREF = "EXTENSION_KEY_PREF";


    protected AdvancedWidgetRegistrationInformation(Context context) {
        mContext = context;
    }

    @Override
    public int getRequiredWidgetApiVersion() {
        // This extension includes multiple widgets, which is supported from
        // Widget API level 3.
        return 3;
    }

    @Override
    public int getRequiredControlApiVersion() {
        // This sample requires at least Control API level 2.
        return 2;
    }

    @Override
    public int getRequiredNotificationApiVersion() {
        return API_NOT_REQUIRED;
    }

    @Override
    public int getRequiredSensorApiVersion() {
        return API_NOT_REQUIRED;
    }

    @Override
    public boolean isDisplaySizeSupported(int width, int height) {
        // The control part uses layouts and can adapt to any width and height.
        return true;
    }

    @Override
    public boolean isWidgetSizeSupported(final int width, final int height) {
        // Check that at least the 1 by 2 widget fits.
        return (width >= SmallTextWidget.WIDTH_SMALL && height >= SmallTextWidget.HEIGHT_SMALL);
    }

    @Override
    protected WidgetClassList getWidgetClasses(Context context, String hostAppPackageName,
            WidgetContainer widgetContainer) {

        // Create a list of all widgets to register.
        WidgetClassList list = new WidgetClassList();
        // If the 1 by 2 widget fits then register it.
        if (widgetContainer.getMaxWidth() >= SmallTextWidget.WIDTH_SMALL
                && widgetContainer.getMaxHeight() >= SmallTextWidget.HEIGHT_SMALL) {
            list.add(SmallTextWidget.class);
        }

        // If the 2 by 5 widget fits then register it.
        if (widgetContainer.getMaxWidth() >= SmallTextWidget.WIDTH_LARGE
                && widgetContainer.getMaxHeight() >= SmallTextWidget.HEIGHT_LARGE) {
            list.add(LargeTextWidget.class);
        }

        return list;
    }

    @Override
    public ContentValues getExtensionRegistrationConfiguration() {
        String iconHostapp = ExtensionUtils.getUriString(mContext,
                R.drawable.icon);
        String iconExtension = ExtensionUtils.getUriString(mContext,
                R.drawable.icon_extension);

        ContentValues values = new ContentValues();

        values.put(Registration.ExtensionColumns.CONFIGURATION_ACTIVITY,
                AdvancedWidgetPreferenceActivity.class.getName());
        values.put(Registration.ExtensionColumns.CONFIGURATION_TEXT,
                mContext.getString(R.string.configuration_text));
        values.put(Registration.ExtensionColumns.NAME,
                mContext.getString(R.string.extension_name));
        values.put(Registration.ExtensionColumns.EXTENSION_KEY,
                getExtensionKey());
        values.put(Registration.ExtensionColumns.HOST_APP_ICON_URI, iconHostapp);
        values.put(Registration.ExtensionColumns.EXTENSION_ICON_URI, iconExtension);
        values.put(Registration.ExtensionColumns.NOTIFICATION_API_VERSION,
                getRequiredNotificationApiVersion());
        values.put(Registration.ExtensionColumns.PACKAGE_NAME, mContext.getPackageName());

        return values;
    }

    /**
     * A basic implementation of getExtensionKey
     * Returns and saves a random string based on UUID.randomUUID()
     *
     * Note that this implementation doesn't guarantee random numbers on Android 4.3 and older. See <a href="https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html">https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html</a>
     *
     * @return A saved key if it exists, otherwise a randomly generated one.
     * @see com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation#getExtensionKey()
     */
    @Override
    public synchronized String getExtensionKey() {
        if (TextUtils.isEmpty(extensionKey)) {
            // Retrieve key from preferences
            SharedPreferences pref = mContext.getSharedPreferences(EXTENSION_KEY_PREF,
                    Context.MODE_PRIVATE);
            extensionKey = pref.getString(EXTENSION_KEY_PREF, null);
            if (TextUtils.isEmpty(extensionKey)) {
                // Generate a random key if not found
                extensionKey = UUID.randomUUID().toString();
                pref.edit().putString(EXTENSION_KEY_PREF, extensionKey).commit();
            }
        }
        return extensionKey;
    }

}

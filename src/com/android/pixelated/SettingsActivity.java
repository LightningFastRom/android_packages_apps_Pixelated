/*
 * Copyright (C) 2015 The Android Open Source Project
 * Copyright (C) 2017 Paranoid Android
 * Copyright (C) 2017 CypherOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.pixelated;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.net.Uri;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.text.TextUtils;

/**
 * Settings activity for Launcher. Currently implements the following setting: Allow rotation
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new LauncherSettingsFragment())
                .commit();
    }

    /**
     * This fragment shows the launcher preferences.
     */
    public static class LauncherSettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

        private String mDefaultIconPack;
        private SystemDisplayRotationLockObserver mRotationLockObserver;
        
		private PreferenceCategory mGerinalCategory;
		private PreferenceCategory mlayoutCategory;
		private PreferenceCategory mApprenceCategory;
		private PreferenceCategory mBehaviorCategory;
		
		private SwitchPreference mShowGoogleApp;
        private Preference mNotificationBadges;

        private IconsHandler mIconsHandler;
        private PackageManager mPackageManager;
        private Preference mIconPack;
		
		private SwitchPreference mShowQsb;
        private ListPreference mSelectLayout;
        private SwitchPreference mShowAllAppIcon;
		private SwitchPreference mToggleLightStausbar;
        private SwitchPreference mToggleLegacyFolderIcon;
        private SwitchPreference mSetDockOpacity;
		private SwitchPreference mTogglePulldownSearch;
        private SwitchPreference mHapticFeedback;
		
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesName(LauncherFiles.SHARED_PREFERENCES_KEY);
            addPreferencesFromResource(R.xml.launcher_preferences);

            PreferenceScreen preference = getPreferenceScreen();

            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .registerOnSharedPreferenceChangeListener(this);
            mPackageManager = getActivity().getPackageManager();
			
			/*Prefrences*/
			/*General Prefrences*/
			mGerinalCategory = (PreferenceCategory) findPreference(Utilities.KEY_GENERAL_PREFRENCE_CATEGORY);
			/*Notification Badge Prefrence*/
			/* Todo: need fixing */
            mNotificationBadges = (Preference) findPreference(Utilities.KEY_NOTIFICATION_BADGES);
            // Load the switch preference if the service isn't enabled in notification access settings.
            /*if (isNotificationBadgeEnabled()) {
                if (mNotificationBadges != null) {
                    // If the service is enabled, remove the preference.
                    preference.removePreference(mNotificationBadges);
                }
            }*/
			
			/*Layout Prefrences*/
			mlayoutCategory = (PreferenceCategory) findPreference(Utilities.KEY_LAYOUT_PREFRENCE_CATEGORY);
			/*Quick Layout Select*/
			/* Todo: need implementing */
			mSelectLayout = (ListPreference) findPreference(Utilities.KEY_SELECT_LAYOUT);
			mSelectLayout.setEnabled(false);
			/*Google Now Prefrence*/
			// Disable Google Now as it broken Todo: Need fixing
            //boolean state = Utilities.getPrefs(getActivity()).getBoolean(
            //        Utilities.ACTION_LEFT_PAGE_CHANGED, true);
            mShowGoogleApp = (SwitchPreference) findPreference(Utilities.KEY_SHOW_GOOGLE_APP);
            //mShowGoogleApp.setChecked(state);
			mShowGoogleApp.setEnabled(false);
			mShowGoogleApp.setChecked(false);
			/*Show QSB Prefrence*/
			/* Todo: need implementing */
			mShowQsb= (SwitchPreference) findPreference(Utilities.KEY_SHOW_QSB);
			mShowQsb.setEnabled(false);
			mShowQsb.setChecked(false);
			/*Show All App Icon Prefrence*/
			/* Todo: need implementing */
			mShowAllAppIcon = (SwitchPreference) findPreference(Utilities.KEY_SHOW_ALL_APP_ICON);
			mShowAllAppIcon.setEnabled(false);
			mShowAllAppIcon.setChecked(false);
			
			/*Apprence Prefrences*/
			mApprenceCategory = (PreferenceCategory) findPreference(Utilities.KEY_APPRENCE_PREFRENCE_CATEGORY);
			/*Icon Pack Prefrences*/
            mDefaultIconPack = getString(R.string.default_iconpack_title);
            mIconsHandler = IconCache.getIconsHandler(getActivity().getApplicationContext());
            mIconPack = (Preference) findPreference(Utilities.KEY_ICON_PACK);
			/*Ststusbar Prefrences*/
			/* Todo: need implementing */
			mToggleLightStausbar = (SwitchPreference) findPreference(Utilities.KEY_TOGGLE_LIGHT_STATUSBAR);
			mToggleLightStausbar.setEnabled(false);
			mToggleLightStausbar.setChecked(false);
			/*Legacy Folder Icon Prefrences*/
			/* Todo: need implementing */
			mToggleLegacyFolderIcon = (SwitchPreference) findPreference(Utilities.KEY_TOGGLE_LEGACY_FOLDER_ICON);
			mToggleLegacyFolderIcon.setEnabled(false);
			mToggleLegacyFolderIcon.setChecked(false);
			/*Dock Opacity Prefrences*/
			/* Todo: need implementing */
			mSetDockOpacity = (SwitchPreference) findPreference(Utilities.KEY_SET_DOCK_OPACITY);
			mSetDockOpacity.setEnabled(false);
			mSetDockOpacity.setChecked(false);
			
			/*Behavior Prefrences*/
			mBehaviorCategory = (PreferenceCategory) findPreference(Utilities.KEY_BEHAVIOR_PREFRENCE_CATEGORY);
			/*Pulldown Search Prefrences*/
			/* Todo: need implementing */
			mTogglePulldownSearch = (SwitchPreference) findPreference(Utilities.KEY_TOGGLE_PULLDOWN_SEARCH);
			mTogglePulldownSearch.setEnabled(false);
			mTogglePulldownSearch.setChecked(false);
			/*Haptic Feedback Prefrences*/
			/* Todo: need implementing */
			mHapticFeedback = (SwitchPreference) findPreference(Utilities.KEY_PREF_HAPTIC_FEEDBACK);
			ContentResolver mContentResolver = getActivity().getApplicationContext().getContentResolver();
			int val = Settings.System.getInt(mContentResolver, Settings.System.HAPTIC_FEEDBACK_ENABLED, 0);
			// Load the switch preference if the service isn't enabled in notification access settings.
			if (val != 0) {
				mHapticFeedback.setEnabled(true);
			}else{
				mHapticFeedback.setEnabled(false);
				mHapticFeedback.setChecked(false);
				mBehaviorCategory.removePreference(mHapticFeedback);
			}
            
			// Setup allow rotation preference
            Preference rotationPref = findPreference(Utilities.ALLOW_ROTATION_PREFERENCE_KEY);
            if (getResources().getBoolean(R.bool.allow_rotation)) {
                // Launcher supports rotation by default. No need to show this setting.
                getPreferenceScreen().removePreference(rotationPref);
            } else {
                ContentResolver resolver = getActivity().getContentResolver();
                mRotationLockObserver = new SystemDisplayRotationLockObserver(rotationPref, resolver);

                // Register a content observer to listen for system setting changes while
                // this UI is active.
                resolver.registerContentObserver(
                        Settings.System.getUriFor(System.ACCELEROMETER_ROTATION),
                        false, mRotationLockObserver);

                // Initialize the UI once
                mRotationLockObserver.onChange(true);
                rotationPref.setDefaultValue(Utilities.getAllowRotationDefaultValue(getActivity()));
            }
            reloadIconPackSummary();
            reloadIconPackAppIcon();
        }

        @Override
        public void onPause() {
            super.onPause();
            mIconsHandler.hideDialog();
        }

        @Override
        public void onDestroy() {
            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .unregisterOnSharedPreferenceChangeListener(this);
            if (mRotationLockObserver != null) {
                getActivity().getContentResolver().unregisterContentObserver(mRotationLockObserver);
                mRotationLockObserver = null;
            }
            super.onDestroy();
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference pref) {
            if (pref == mIconPack) {
                mIconsHandler.showDialog(getActivity());
                return true;
            }
			if (pref == mNotificationBadges) {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                return true;
            }
            /*if (pref == mShowQsb) {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                return true;
            }
			if (pref == mSelectLayout) {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                return true;
            }
			if (pref == mShowAllAppIcon) {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                return true;
            }
			if (pref == mSetDockOpacity) {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                return true;
            }
			if (pref == mTogglePulldownSearch) {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                return true;
            }
			if (pref == mHapticFeedback) {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                return true;
            }*/
            // Disable Google Now as it broken Todo: Need fixing
            /*if (pref == mShowGoogleApp) {
				if (!Settings.canDrawOverlays(this)) {
    				/*Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, 
					Uri.parse("package:" + getActivity().getApplicationContext().getPackageName()));
    				startActivityForResult(intent, 0);
				} else {
                	boolean state = Utilities.getPrefs(getActivity()).getBoolean(
                        	Utilities.ACTION_LEFT_PAGE_CHANGED, true);
                	Utilities.getPrefs(getActivity()).edit().putBoolean(
                        	Utilities.ACTION_LEFT_PAGE_CHANGED, !state).commit();
                	Intent intent = new Intent(Utilities.ACTION_LEFT_PAGE_CHANGED);
                	getActivity().sendBroadcast(intent);
				}
				return true;
            }*/
            return false;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            reloadIconPackSummary();
			reloadIconPackAppIcon();
        }

        private void reloadIconPackSummary() {
            ApplicationInfo info = null;
            String iconPack = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getString(Utilities.KEY_ICON_PACK, mDefaultIconPack);
            if (!mIconsHandler.isDefaultIconPack()) {
                try {
                    info = mPackageManager.getApplicationInfo(iconPack, 0);
                } catch (PackageManager.NameNotFoundException e) {
                }
                if (info != null) {
                    iconPack = mPackageManager.getApplicationLabel(info).toString();
                }
            }
            mIconPack.setSummary(iconPack);
        }
		
		private void reloadIconPackAppIcon() {
            ApplicationInfo info = null;
			Drawable mIconPackAppIcon = null;
            String iconPack = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getString(Utilities.KEY_ICON_PACK, mDefaultIconPack);
            if (!mIconsHandler.isDefaultIconPack()) {
                try {
                    info = mPackageManager.getApplicationInfo(iconPack, 0);
                } catch (PackageManager.NameNotFoundException e) {
                }
                if (info != null) {
                    mIconPackAppIcon = mPackageManager.getApplicationIcon(info);
                }
            }
			mIconPack.setIcon(mIconPackAppIcon);
        }
		
		/**
         * Checks whether device has Vibrator Service.
         * @return True if enabled, false otherwise.
         */
		 private boolean isHapticFeedbackService(){
			ContentResolver mContentResolver = getActivity().getApplicationContext().getContentResolver();
			int val = Settings.System.getInt(mContentResolver, Settings.System.HAPTIC_FEEDBACK_ENABLED, 0);
			//mSettingEnabled = val != 0;
			if(val != 0){
				//no haptic feedback is possible
				return true;
			}else{
				//haptic feedback is a possible feature
				return false;
			}
		 }
		 
        /**
         * Checks whether the notification badge service is enabled.
         * @return True if enabled, false otherwise.
         */
		// Enable NotificationBadges Todo: Need fixing
       /*private boolean isNotificationBadgeEnabled(){
            ContentResolver resolver = getActivity().getApplicationContext().getContentResolver();
            String packageName = getActivity().getApplicationContext().getPackageName();
            final String setting = Settings.Secure.getString(resolver,
                    Settings.Secure.ENABLED_NOTIFICATION_LISTENERS);
            if (!TextUtils.isEmpty(setting)) {
                final String[] names = setting.split(":");
                for (int i = 0; i < names.length; i++) {
                    final ComponentName componentName = ComponentName.unflattenFromString(names[i]);
                    if (componentName != null) {
                        if (TextUtils.equals(packageName, componentName.getPackageName())) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }*/
    }

    /**
     * Content observer which listens for system auto-rotate setting changes, and enables/disables
     * the launcher rotation setting accordingly.
     */
    private static class SystemDisplayRotationLockObserver extends ContentObserver {

        private final Preference mRotationPref;
        private final ContentResolver mResolver;

        public SystemDisplayRotationLockObserver(
                Preference rotationPref, ContentResolver resolver) {
            super(new Handler());
            mRotationPref = rotationPref;
            mResolver = resolver;
        }

        @Override
        public void onChange(boolean selfChange) {
            boolean enabled = Settings.System.getInt(mResolver,
                    Settings.System.ACCELEROMETER_ROTATION, 1) == 1;
            mRotationPref.setEnabled(enabled);
            mRotationPref.setSummary(enabled
                    ? R.string.allow_rotation_desc : R.string.allow_rotation_blocked_desc);
        }
    }
}

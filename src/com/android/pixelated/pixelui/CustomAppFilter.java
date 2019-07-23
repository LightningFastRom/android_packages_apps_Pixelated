package com.android.pixelated.pixelui;

import android.content.ComponentName;
import java.util.HashSet;

import com.android.pixelated.AppFilter;

public class CustomAppFilter extends AppFilter
{
    private final HashSet mHide;

    public CustomAppFilter() {
        mHide = new HashSet();
        mHide.add(ComponentName.unflattenFromString("com.google.android.googlequicksearchbox/.VoiceSearchActivity"));
        mHide.add(ComponentName.unflattenFromString("com.google.android.apps.wallpaper/.picker.CategoryPickerActivity"));
    }

    public boolean shouldShowApp(final ComponentName componentName) {
        return !mHide.contains(componentName);
    }
}

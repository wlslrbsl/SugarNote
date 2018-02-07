package com.isens.sugarnote;

import android.graphics.drawable.Drawable;

/**
 * Created by chong on 2017-12-17.
 */

public class CustomAdapterSettingItem {
    private Drawable icon;
    private String name;


    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

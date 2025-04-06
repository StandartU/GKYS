package com.example.walkiepaws.backend;

import android.annotation.SuppressLint;
import android.content.Context;

public class Utils {
    @SuppressLint("DiscouragedApi")
    public int getDrawableIdByName(Context context, String fileName) {
        return context.getResources().getIdentifier(fileName, "drawable", context.getPackageName());
    }

}

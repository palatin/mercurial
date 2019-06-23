package com.palatin.mercurial.util

import android.content.Context
import android.graphics.Bitmap
import com.palatin.mercurial.R

class Util {
    companion object {
        init {
            System.loadLibrary("image_rotate")
        }

        fun setTheme(context: Context, theme: Theme) {
            context.getSharedPreferences("pref", Context.MODE_PRIVATE)
                .edit().putString("theme", theme.name).commit()
        }

        fun getTheme(context: Context): Int =
            if(context.getSharedPreferences("pref", Context.MODE_PRIVATE).getString("theme", Theme.LIGHT.name) == Theme.LIGHT.name)
                R.style.AppThemeLight else R.style.AppThemeDark
    }

    external fun rotateImageBy90(bitmap: Bitmap, width: Int, height: Int): Bitmap
}

enum class Theme {
    LIGHT,
    DARK
}


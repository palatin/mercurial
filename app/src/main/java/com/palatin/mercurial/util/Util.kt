package com.palatin.mercurial.util

import android.graphics.Bitmap

class Util {
    companion object {
        init {
            System.loadLibrary("image_rotate")
        }


    }

    external fun rotateImageBy90(bitmap: Bitmap, width: Int, height: Int): Bitmap
}


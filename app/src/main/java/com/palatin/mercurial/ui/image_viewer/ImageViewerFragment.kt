package com.palatin.mercurial.ui.image_viewer


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.palatin.mercurial.R
import com.palatin.mercurial.util.Util
import kotlinx.android.synthetic.main.fragment_image_viewer.*


class ImageViewerFragment : DialogFragment() {

    private var currentImage: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_image_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("image")?.let { setImage(it) }
    }

    private fun setImage(filePath: String) {
        currentImage = BitmapFactory.decodeFile(filePath, BitmapFactory.Options().apply {
            inPreferredConfig = Bitmap.Config.ARGB_8888
        })

        iv_image.setImageBitmap(currentImage)
        iv_image.setOnClickListener {
            iv_image.setImageBitmap(null)
            currentImage = Util().rotateImageBy90(currentImage!!, currentImage!!.width, currentImage!!.height)
            iv_image.setImageBitmap(currentImage)
        }
    }


}

package com.orbits.queuesystem.helper

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.provider.Settings
import android.text.SpannableString
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.orbits.queuesystem.R
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Random


@SuppressLint("StaticFieldLeak")
object Global {

    fun getDimension(activity: Activity, size: Double): Int {
        return if (Constants.DEVICE_DENSITY > 0) {
            //density saved in constant calculated on first time in splash if in case its 0 then calculate again
            (Constants.DEVICE_DENSITY * size).toInt()
        } else {
            ((getDeviceWidthInDouble(activity) / 320) * size).toInt()

        }
    }

    fun getDeviceWidthInDouble(activity: Activity): Double {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels.toDouble()
    }



    fun ImageView.loadImagesUsingCoil(
        strUrl: String?,
        errorImage: Int? = null,
    ) {
        this.load(strUrl) {
            crossfade(true)
            if (errorImage != null) {
                error(errorImage)
            }
            allowConversionToBitmap(true)
            bitmapConfig(Bitmap.Config.ARGB_8888)
            allowHardware(true)
            listener(object : ImageRequest.Listener {
                override fun onError(request: ImageRequest, result: ErrorResult) {
                }
            })
        }
    }


    fun setFontSize(activity: Activity, value: Float): Float {
        return value / (activity.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    @SuppressLint("NewApi")
    fun getTypeFace(context: Context, fontStyle: String): Typeface {
        return when (fontStyle) {
            Constants.fontRegular -> context.resources.getFont(R.font.font_regular)
            Constants.fontBold -> context.resources.getFont(R.font.font_bold)
            Constants.fontMedium -> context.resources.getFont(R.font.font_medium)
            Constants.fontRegularRev -> context.resources.getFont(R.font.font_regular_rev)
            else -> context.resources.getFont(R.font.font_regular)
        }
    }

}

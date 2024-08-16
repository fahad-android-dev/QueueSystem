package com.orbits.queuesystem.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.InputFilter
import android.text.format.DateUtils
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

object Extensions {


    fun String.printLog(tag: String = "") {
        println("$tag ::: $this ")
    }

    fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    /* fun Context.showCustomSnackBar(
         layoutSnackBar: LayoutSnackBarBinding,
         strMsg: String,
         showBelowStatus: Boolean = false,
         duration:Int = 2000, //pass this true where the theme is fullscreen
         gravity : Int = Gravity.TOP
     ) {

         val snackbar = Snackbar.make(layoutSnackBar.root, strMsg, Snackbar.LENGTH_LONG)
         val layoutParams = ActionBar.LayoutParams(snackbar.view.layoutParams)
         layoutParams.gravity = gravity
         snackbar.view.setPadding(0, 10, 0, 10)

         if (showBelowStatus) {//If true, adding top margin to show it below status bar,
             // because in fullscreen theme statusBar is overlapping snackBar
             var statusBarHeight = 0
             val resourceId: Int =
                 resources.getIdentifier("status_bar_height", "dimen", "android")
             if (resourceId > 0) {
                 statusBarHeight = resources.getDimensionPixelSize(resourceId)
             }
             layoutParams.topMargin = statusBarHeight
         }
         snackbar.view.layoutParams = layoutParams
         snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
         val context = snackbar.context
         layoutSnackBar.txtSnackBarText.text = strMsg
         val snackBarLayout = snackbar.view as Snackbar.SnackbarLayout
         snackBarLayout.addView(layoutSnackBar.root)
         snackbar.setBackgroundTint(
             ContextCompat.getColor(
                 context,
                 R.color.black
             )
         )
         snackbar.duration = duration
         snackbar.show()
     }*/

    fun String.capitalizeFirstChar(): String {
        return this.replaceFirstChar { it.uppercase() }
    }

    fun String.capitaliseEachWord(): String {
        val value = lowercase()
        return if (value.isNotEmpty()) {
            try {
                value.split(' ').joinToString(" ") { string ->
                    string.replaceFirstChar { char ->
                        if (char.isLowerCase())
                            char.titlecase(Locale.getDefault())
                        else
                            char.toString()
                    }
                }
            } catch (e: Exception) {
                value
            }
        } else {
            ""
        }
    }

    /*For Recyler view*/
    fun RecyclerView.disableItemAnimator() {
        (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
    }

    fun RecyclerView.removeItemDecorations() {
        while (this.itemDecorationCount > 0) {
            this.removeItemDecorationAt(0)
        }
    }

    /*For View Pager*/
    fun ViewPager2.removeItemDecorations() {
        while (this.itemDecorationCount > 0) {
            this.removeItemDecorationAt(0)
        }
    }

    fun RecyclerView.removeItemDecoration() {
        while (this.itemDecorationCount > 0) {
            this.removeItemDecorationAt(0)
        }
    }

    fun Uri.getOriginalFileName(context: Context): String? {
        return context.contentResolver.query(this, null, null, null, null)?.use {
            val nameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameColumnIndex)
        }
    }


    fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }


    fun EditText.applyMaxLength(maxLength: Int) {
        filters = arrayOf(InputFilter.LengthFilter(maxLength))
    }

    fun String?.appendComma(): String {
        if (!isNullOrEmpty()) {
            return "$this, "
        }
        return ""
    }

    fun String?.concat(char: String): String {
        if (!isNullOrEmpty()) {
            return "$this$char"
        }
        return ""
    }

    fun EditText.showHidePassword() {
        if (transformationMethod == null) {
            transformationMethod = PasswordTransformationMethod()
            setSelection(text?.length ?: 0)
        } else {
            transformationMethod = null
            setSelection(text?.length ?: 0)
        }
    }

    fun ViewPager2.setAutoScroll() {
        val position = currentItem
        try {
            if (position < (adapter?.itemCount ?: 0) - 1)
                currentItem = position + 1
            else
                setCurrentItem(0, true)
        } catch (e: Exception) {
        }
    }

    fun handler(delay: Long, block: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            block()
        }, delay)
    }

    fun Activity.getGalleryIntent(
        mediaType: ActivityResultContracts.PickVisualMedia.VisualMediaType = ActivityResultContracts.PickVisualMedia.ImageOnly,
        block: (request: PickVisualMediaRequest) -> Unit
    ) {
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(mediaType)
            .build()
        block(request)
    }

    fun Activity.getCameraIntent(isCaptureVideo: Boolean = false, block: (i: Intent, outputPath: String?, outputUri: Uri?) -> Unit) {
        val takePictureIntent = if (isCaptureVideo) Intent(MediaStore.ACTION_VIDEO_CAPTURE) else Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            var filePath = ""
            try {
                photoFile = createImageFile(this, isCaptureVideo).apply { filePath = absolutePath }
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(this, packageName, it)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                block(takePictureIntent, filePath, photoURI)
            }
        }
    }


    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(activity: Activity, isCaptureVideo: Boolean): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mFileName = if (isCaptureVideo) "VIDEO" else "JPEG_" + timeStamp + "_"
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(mFileName, if (isCaptureVideo) ".mp4" else ".jpg", storageDir)
    }

    fun Activity.getFilePickerIntent(block: (i: Intent) -> Unit) {
        val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
        pdfIntent.type = "application/pdf"
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
        if (pdfIntent.resolveActivity(packageManager) != null) {
            block(pdfIntent)
        }
    }



    fun String?.getExifRotationInDegree(): Bitmap {
        val bitmap = BitmapFactory.decodeFile(this)
        val exif = this?.let { ExifInterface(it) }
        val rotation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        val degree = when (rotation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                90f
            }

            ExifInterface.ORIENTATION_ROTATE_180 -> {
                180f
            }

            ExifInterface.ORIENTATION_ROTATE_270 -> {
                270f
            }

            else -> 0f
        }

        return bitmap.rotate(degree)
    }

    private fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }


    fun Dialog?.dismissIfShowing() {
        try {
            if (this?.isShowing == true) {
                this.dismiss()
            }
        } catch (e: Exception) {
            return
        }
    }


    fun Date.isToday(): Boolean = DateUtils.isToday(this.time)

    private var searchJob: Job? = null
    fun EditText.search(lifecycleScope: LifecycleCoroutineScope, block: (query: String) -> Unit) {
        doAfterTextChanged {
            searchJob?.cancel()
            /** cancel previous job when user enters new letter */
            searchJob = lifecycleScope.launch {
                /** add some delay before search, this function checks if coroutine is canceled, if it is canceled it won't continue execution*/
                delay(500)
                block(it.toString())
            }
        }
    }

    fun NestedScrollView.scrollToPosition(view: View) {
        smoothScrollTo(0, view.y.toInt())
    }

    fun ViewPager2.reduceDragSensitivity() {
        val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true
        val recyclerView = recyclerViewField.get(this) as RecyclerView
        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true
        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop * 6)       // "8" was obtained experimentally
    }

    fun ViewPager2.setCarouselPagerTransformation(nextItemVisiblePx: Float, currentItemHorizontalMarginPx: Float) {
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            page.scaleY = 1 - (0.25f * kotlin.math.abs(position))
            //page.alpha = 0.75f + (1 - kotlin.math.abs(position))
        }
        setPageTransformer(pageTransformer)
    }


    fun String?.asDouble(): Double {
        if (!this.isNullOrEmpty()) {
            return try {
                if (this.contains(",")) {
                    this.replace(",", "")
                }
                this.toDouble()
            } catch (e: Exception) {
                0.0
            }
        }
        return 0.0
    }

    fun Int?.asDouble(): Double {
        if (this != null) {
            return try {
                this.toDouble()
            } catch (e: Exception) {
                0.0
            }
        }
        return 0.0
    }

    fun Int?.asBoolean(): Boolean {
        if (this != null) {
            return try {
                this == 1
            } catch (e: Exception) {
                false
            }
        }
        return false
    }

    fun String?.asInt(): Int {
        if (!this.isNullOrEmpty()) {
            return try {
                this.toInt()
            } catch (e: Exception) {
                0
            }
        }
        return 0
    }

    fun String?.asFloat(): Float {
        if (!this.isNullOrEmpty()) {
            return try {
                this.toFloat()
            } catch (e: Exception) {
                0f
            }
        }
        return 0f
    }

    fun Double?.asInt(): Int {
        if (this != null) {
            return try {
                this.toInt()
            } catch (e: Exception) {
                0
            }
        }
        return 0
    }

    fun Double?.asString(): String {
        if (this != null) {
            return try {
                this.toString()
            } catch (e: Exception) {
                "0.0"
            }
        }
        return "0.0"
    }

    fun Int?.asString(): String {
        if (this != null) {
            return try {
                this.toString()
            } catch (e: Exception) {
                "0"
            }
        }
        return "0"
    }


    fun Float?.asString(): String {
        if (this != null) {
            return try {
                this.toString()
            } catch (e: Exception) {
                ""
            }
        }
        return ""
    }

    fun Float?.asInt(): Int {
        if (this != null) {
            return try {
                this.toInt()
            } catch (e: Exception) {
                0
            }
        }
        return 0
    }

    fun String?.setPhoneCodeWithPlus(): String {
        //while showing adding plus from our side
        return if (!this.isNullOrEmpty()) {
            "+$this"
        } else {
            ""
        }
    }

    fun String?.getPhoneCodeWithoutPlus(): String {
        //while sending to server, remove plus
        return if (!this.isNullOrEmpty()) {
            this.replace("+", "")
        } else {
            ""
        }
    }


    fun Boolean.then(block: () -> Unit) {
        if (this) {
            block()
        } else {
            return
        }
    }


    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }


    fun Context.openWhatsapp(number: String) {
        var url = "https://api.whatsapp.com/send?phone=${number}"
        try {
            val i = Intent(Intent.ACTION_VIEW)
            i.setPackage("com.whatsapp")
            i.data = Uri.parse(url)
            startActivity(i)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            try {
                url = "https://play.google.com/store/apps/details?id=com.whatsapp"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        } catch (e: ActivityNotFoundException) {
            url = "https://play.google.com/store/apps/details?id=com.whatsapp"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }


    fun <T : Serializable?> Intent.getSerializable(key: String, m_class: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            this.getSerializableExtra(key, m_class)!!
        else
            this.getSerializableExtra(key) as T
    }

    fun Context.isVisibleInvisible(condition: Boolean): Int {
        return if (condition) View.VISIBLE else View.INVISIBLE
    }

    fun getCurrentDate(inputFormat: String): String {
        val sdf = SimpleDateFormat(inputFormat, Locale.ENGLISH)
        return sdf.format(Date())
    }

    fun getStringToDate(inputFormat: String, date: String): Date? {
        return try {
            val simpleDateFormat = SimpleDateFormat(inputFormat, Locale.ENGLISH)
            simpleDateFormat.parse(date)
        } catch (e: Exception) {
            val simpleDateFormat = SimpleDateFormat(inputFormat, Locale.ENGLISH)
            simpleDateFormat.parse(getCurrentDate(inputFormat))
        }
    }



    fun <T : Serializable?> Bundle.getSerializableViaArgument(key: String, m_class: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            this.getSerializable(key, m_class)!!
        else
            this.getSerializable(key) as T
    }

    inline fun <reified T : Serializable> Bundle.serializableList(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> this.getSerializable(
            key,
            T::class.java
        )

        else -> @Suppress("DEPRECATION") getSerializable(key) as T?
    }



    fun Fragment.isFragmentResumed(block: () -> Unit) {
        if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
            block()
        } else {
            return
        }
    }

    fun getDiscountedPrice(finalPrice: String?, regularPrice: String?): Int {
        return try {//if final & regular price non empty then calculating discount else 0
            if (!finalPrice.isNullOrEmpty() && !regularPrice.isNullOrEmpty()) {
                (100 - ceil(finalPrice.asDouble() / regularPrice.asDouble() * 100)).toInt()
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }


    fun View.setWeight(float: Float) {
        val params = layoutParams as LinearLayout.LayoutParams
        params.weight = float
        layoutParams = params
    }

    fun ImageView.rotateIf(filterTypeSelected: Boolean?) {
        rotation = if (filterTypeSelected == true) 180f else 0f
    }


    fun isIbanValid(iban: String): Boolean {
        val IBAN_MIN_SIZE = 15
        val IBAN_MAX_SIZE = 34
        val IBAN_MAX: Long = 999999999
        val IBAN_MODULUS: Long = 97
        val trimmed = iban.trim { it <= ' ' }
        if (trimmed.length < IBAN_MIN_SIZE || trimmed.length > IBAN_MAX_SIZE) {
            return false
        }
        val reformat = trimmed.substring(4) + trimmed.substring(0, 4)
        var total: Long = 0
        for (i in 0 until reformat.length) {
            val charValue = Character.getNumericValue(reformat[i])
            if (charValue < 0 || charValue > 35) {
                return false
            }
            total = (if (charValue > 9) total * 100 else total * 10) + charValue
            if (total > IBAN_MAX) {
                total %= IBAN_MODULUS
            }
        }
        return total % IBAN_MODULUS == 1L
    }

}
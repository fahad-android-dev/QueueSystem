package com.orbits.queuesystem.helper

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.orbits.queuesystem.R
import com.orbits.queuesystem.databinding.LayoutToolbarBinding
import java.util.*


open class BaseActivity : AppCompatActivity() {
    private var progressDialog: Dialog? = null
    private var layoutToolbarBinding: LayoutToolbarBinding? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           /* val config = newBase.resources.configuration
            config.setLocale(Locale(this.getAppConfig()?.lang ?: "en"))
            applyOverrideConfiguration(config)*/
        }
    }

   /* fun showProgressDialog(type: String = "") {
        if (NetworkUtil.getConnectivityStatus(this)) {
            if (progressDialog != null && progressDialog?.isShowing == true) {
                progressDialog?.dismiss()
            }
            val v: View = when (type) {
                "B" -> {
                    //for bottom tab
                    layoutInflater.inflate(R.layout.dialog_loading_bottom_bar, null)
                }
                "F" -> {
                    //for full screen
                    layoutInflater.inflate(R.layout.dialog_loading_full_screen, null)
                }
                else -> {
                    //for toolbar
                    layoutInflater.inflate(R.layout.dialog_loading, null)
                }
            }
            val imageview: ImageView = v.findViewById(R.id.imgLoading)
            //imageview.loadGifUsingCoil(this, R.drawable.loading_gif)

            progressDialog = Dialog(this, R.style.MyDialogTheme)
            progressDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            progressDialog?.setContentView(v)


            progressDialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            if (progressDialog != null && progressDialog?.isShowing == false && !isFinishing) {
                progressDialog?.show()
                progressDialog?.setCancelable(false)
                progressDialog?.setCanceledOnTouchOutside(false)
            }
        }
    }*/

    fun hideProgressDialog() {
        if (progressDialog != null && progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }

    fun setUpToolbar(
        binding: LayoutToolbarBinding,
        title: String = "",
        iconTwo: Int = 0,
        iconMenu: Int = 0,
        isBackArrow: Boolean = true,
        toolbarClickListener: CommonInterfaceClickEvent? = null
    ) {
        layoutToolbarBinding = binding

        if (isBackArrow)  layoutToolbarBinding?.conIconOne?.isVisible = false

        /*if (isBackArrow) layoutToolbarBinding?.conIconOne?.visibility = isVisibleInvisible(iconOne != 0)
        else layoutToolbarBinding?.conIconOne?.isVisible = iconOne != 0*/


        layoutToolbarBinding?.conIconTwo?.isVisible = iconTwo != 0
        if (layoutToolbarBinding?.conIconTwo?.isVisible == true)
            layoutToolbarBinding?.ivIconTwo?.setImageResource(iconTwo)

        layoutToolbarBinding?.linBackArrow?.isVisible = isBackArrow && iconMenu == 0

        if (iconMenu != 0){
            layoutToolbarBinding?.linBackArrow?.isVisible = true
            layoutToolbarBinding?.ivArrow?.setImageResource(iconMenu)
            layoutToolbarBinding?.linBackArrow?.setOnClickListener {
                toolbarClickListener?.onToolBarListener(Constants.TOOLBAR_ICON_MENU)
            }
        }else {
            layoutToolbarBinding?.ivArrow?.setImageResource(R.drawable.ic_back_arrow)
            layoutToolbarBinding?.linBackArrow?.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        layoutToolbarBinding?.conIconOne?.setOnClickListener {
            toolbarClickListener?.onToolBarListener(Constants.TOOLBAR_ICON_ONE)
        }

        layoutToolbarBinding?.conIconTwo?.setOnClickListener {
            toolbarClickListener?.onToolBarListener(Constants.TOOLBAR_ICON_TWO)
        }

        layoutToolbarBinding?.txtToolbarHeader?.text = title
    }
}
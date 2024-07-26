package com.orbits.queuesystem.helper

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.orbits.queuesystem.R
import com.orbits.queuesystem.databinding.LayoutAddServiceDialogBinding
import com.orbits.queuesystem.helper.Global.getDimension

object Dialogs {

    var addServiceDialog: Dialog? = null

    fun showAddServiceDialog(
        activity: Context,
        isCancellable: Boolean? = true,
        alertDialogInterface: AlertDialogInterface,
    ) {
        try {
            addServiceDialog = Dialog(activity)
            addServiceDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            addServiceDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding: LayoutAddServiceDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.layout_add_service_dialog, null, false
            )
            addServiceDialog?.setContentView(binding.root)
            val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
            lp.copyFrom(addServiceDialog?.window?.attributes)
            lp.width = getDimension(activity as Activity, 300.00)
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            addServiceDialog?.window?.attributes = lp
            addServiceDialog?.setCanceledOnTouchOutside(isCancellable ?: true)
            addServiceDialog?.setCancelable(isCancellable ?: true)

            binding.btnAlertPositive.text = activity.getString(R.string.confirm)

            binding.ivCancel.setOnClickListener {
                addServiceDialog?.dismiss()
            }

            binding.btnAlertPositive.setOnClickListener {
                addServiceDialog?.dismiss()
                alertDialogInterface.onYesClick()
            }
            addServiceDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /*fun showCustomAlert(
        activity: Context,
        title: String = "",
        msg: String = "",
        yesBtn: String,
        noBtn: String,
        singleBtn: Boolean = false,
        isCancellable: Boolean? = true,
        alertDialogInterface: AlertDialogInterface,
    ) {
        try {
            customDialog = Dialog(activity)
            customDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            customDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding: LayoutCustomAlertBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.layout_custom_alert, null, false
            )
            customDialog?.setContentView(binding.root)
            val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
            lp.copyFrom(customDialog?.window?.attributes)
            lp.width = getDimension(activity as Activity, 300.00)
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            customDialog?.window?.attributes = lp
            customDialog?.setCanceledOnTouchOutside(isCancellable ?: true)
            customDialog?.setCancelable(isCancellable ?: true)


            binding.txtAlertTitle.text = title
            binding.txtAlertMessage.text = msg
            binding.btnAlertNegative.text = noBtn
            binding.btnAlertPositive.text = yesBtn

            binding.btnAlertNegative.visibility = if (singleBtn) View.GONE else View.VISIBLE
            binding.btnAlertNegative.setOnClickListener {
                customDialog?.dismiss()
                alertDialogInterface.onNoClick()
            }
            binding.btnAlertPositive.setOnClickListener {
                customDialog?.dismiss()
                alertDialogInterface.onYesClick()
            }
            customDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    fun addSpacesBetweenLetters(input: String): String {
        // Convert the string to a list of characters, join them with spaces, and convert back to string
        return input.toCharArray().joinToString("   ")
    }


}

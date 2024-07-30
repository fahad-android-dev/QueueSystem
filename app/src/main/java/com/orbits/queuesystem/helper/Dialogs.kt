package com.orbits.queuesystem.helper

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.orbits.queuesystem.R
import com.orbits.queuesystem.databinding.LayoutAddCounterDialogBinding
import com.orbits.queuesystem.databinding.LayoutAddServiceDialogBinding
import com.orbits.queuesystem.databinding.LayoutCustomAlertBinding
import com.orbits.queuesystem.helper.Global.getDimension
import com.orbits.queuesystem.helper.Global.getTypeFace
import com.orbits.queuesystem.helper.database.LocalDB.getAllServiceFromDB
import com.orbits.queuesystem.mvvm.counters.model.CounterListDataModel
import com.orbits.queuesystem.mvvm.main.model.ServiceListDataModel

object Dialogs {

    var addServiceDialog: Dialog? = null
    var addCounterDialog: Dialog? = null
    var customDialog: Dialog? = null

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
                alertDialogInterface.onAddService(
                    model = ServiceListDataModel(
                        serviceId = binding.edtServiceId.text.toString(),
                        name = binding.edtServiceName.text.toString(),
                        nameAr = binding.edtServiceNameAr.text.toString(),
                        tokenStart = binding.edtTokenStart.text.toString(),
                        tokenEnd = binding.edtTokenEnd.text.toString(),
                        currentToken = binding.edtTokenStart.text.toString(),
                    )

                )
            }
            addServiceDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showAddCounterDialog(
        activity: Context,
        isCancellable: Boolean? = true,
        alertDialogInterface: AlertDialogInterface,
    ) {
        try {
            addCounterDialog = Dialog(activity)
            addCounterDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            addCounterDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding: LayoutAddCounterDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.layout_add_counter_dialog, null, false
            )
            addCounterDialog?.setContentView(binding.root)
            val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
            lp.copyFrom(addCounterDialog?.window?.attributes)
            lp.width = getDimension(activity as Activity, 300.00)
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            addCounterDialog?.window?.attributes = lp
            addCounterDialog?.setCanceledOnTouchOutside(isCancellable ?: true)
            addCounterDialog?.setCancelable(isCancellable ?: true)

            binding.btnAlertPositive.text = activity.getString(R.string.confirm)

            binding.ivCancel.setOnClickListener {
                addCounterDialog?.dismiss()
            }

            binding.edtCounterType.setOnClickListener {
                showWheelView(
                    activity,
                    arrayListData = activity.getAllServiceFromDB()?.map { it?.serviceName } as ArrayList<String>
                ) { value ->
                    activity.getAllServiceFromDB()?.forEach { it?.isSelected = false }
                    activity.getAllServiceFromDB()?.get(value)?.isSelected = true
                    binding.edtCounterType.setText(activity.getAllServiceFromDB()?.get(value)?.serviceName)
                }
            }

            binding.btnAlertPositive.setOnClickListener {
                addCounterDialog?.dismiss()
                alertDialogInterface.onAddCounter(
                    model = CounterListDataModel(
                        id = binding.edtCounterId.text.toString(),
                        counterId = binding.edtCounterId.text.toString(),
                        name = binding.edtCounterName.text.toString(),
                        nameAr = binding.edtCounterNameAr.text.toString(),
                        counterType = binding.edtCounterType.text.toString()

                    )

                )
            }
            addCounterDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun showWheelView(
        activity: Activity,
        arrayListData: ArrayList<String>,
        listener: WheelViewEvent
    ) {
        val parentView = activity.layoutInflater.inflate(R.layout.layout_bottom_sheet_picker, null)
        val bottomSheerDialog = BottomSheetDialog(activity)
        bottomSheerDialog.setContentView(parentView)
        bottomSheerDialog.setCanceledOnTouchOutside(false)
        bottomSheerDialog.setCancelable(false)
        val wheelView = parentView.findViewById(R.id.wheelView) as WheelView
        val txtDone = parentView.findViewById(R.id.txtDone) as TextView
        val txtCancel = parentView.findViewById(R.id.txtCancel) as TextView
        txtCancel.typeface = getTypeFace(activity, Constants.fontMedium)
        txtDone.typeface = getTypeFace(activity, Constants.fontMedium)

        try {
            if (arrayListData.isNotEmpty()) {
                wheelView.setItems(arrayListData)
                txtCancel.setOnClickListener {
                    bottomSheerDialog.dismiss()
                }
                txtDone.setOnClickListener {
                    bottomSheerDialog.dismiss()
                    listener.onDoneClicked(wheelView.seletedIndex)
                }
                bottomSheerDialog.show()
            }
        } catch (e: Exception) {
        }
    }

    fun showCustomAlert(
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
    }

    fun addSpacesBetweenLetters(input: String): String {
        // Convert the string to a list of characters, join them with spaces, and convert back to string
        return input.toCharArray().joinToString("   ")
    }


}

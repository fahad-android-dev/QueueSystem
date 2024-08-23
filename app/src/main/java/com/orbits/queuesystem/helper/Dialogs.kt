package com.orbits.queuesystem.helper

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.orbits.queuesystem.R
import com.orbits.queuesystem.databinding.LayoutAddCounterDialogBinding
import com.orbits.queuesystem.databinding.LayoutAddServiceDialogBinding
import com.orbits.queuesystem.databinding.LayoutAddUserDialogBinding
import com.orbits.queuesystem.databinding.LayoutCustomAlertBinding
import com.orbits.queuesystem.helper.Extensions.asString
import com.orbits.queuesystem.helper.Global.getDimension
import com.orbits.queuesystem.helper.Global.getTypeFace
import com.orbits.queuesystem.helper.database.LocalDB.getAllServiceFromDB
import com.orbits.queuesystem.mvvm.counters.model.CounterListDataModel
import com.orbits.queuesystem.mvvm.main.model.ServiceListDataModel
import com.orbits.queuesystem.mvvm.users.model.UserListDataModel

object Dialogs {

    var addServiceDialog: Dialog? = null
    var addCounterDialog: Dialog? = null
    var addUserDialog: Dialog? = null
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
                        tokenNo = binding.edtTokenStart.text.toString(),
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
            var serviceId = ""
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
                    serviceId = activity.getAllServiceFromDB()?.get(value)?.id.asString()
                }
            }

            binding.btnAlertPositive.setOnClickListener {
                when {
                    binding.edtCounterId.text.toString().isEmpty() -> {
                        Toast.makeText(activity, "Please enter counter id", Toast.LENGTH_SHORT).show()
                    }
                    binding.edtCounterName.text.toString().isEmpty() -> {
                        Toast.makeText(activity, "Please enter counter name", Toast.LENGTH_SHORT).show()
                    }
                    binding.edtCounterNameAr.text.toString().isEmpty() -> {
                        Toast.makeText(activity, "Please enter counter name in arabic", Toast.LENGTH_SHORT).show()
                    }
                    binding.edtCounterType.text.toString().isEmpty() -> {
                        Toast.makeText(activity, "Please select counter type", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        addCounterDialog?.dismiss()
                        alertDialogInterface.onAddCounter(
                            model = CounterListDataModel(
                                id = binding.edtCounterId.text.toString(),
                                counterId = binding.edtCounterId.text.toString(),
                                name = binding.edtCounterName.text.toString(),
                                nameAr = binding.edtCounterNameAr.text.toString(),
                                counterType = binding.edtCounterType.text.toString(),
                                serviceId = serviceId
                            )
                        )
                    }
                }
            }
            addCounterDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun showAddUserDialog(
        activity: Context,
        isCancellable: Boolean? = true,
        alertDialogInterface: AlertDialogInterface,
    ) {
        try {
            addUserDialog = Dialog(activity)
            addUserDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            addUserDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding: LayoutAddUserDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.layout_add_user_dialog, null, false
            )
            addUserDialog?.setContentView(binding.root)
            val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
            lp.copyFrom(addUserDialog?.window?.attributes)
            lp.width = getDimension(activity as Activity, 300.00)
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            addUserDialog?.window?.attributes = lp
            addUserDialog?.setCanceledOnTouchOutside(isCancellable ?: true)
            addUserDialog?.setCancelable(isCancellable ?: true)

            binding.btnAlertPositive.text = activity.getString(R.string.confirm)

            binding.ivCancel.setOnClickListener {
                addUserDialog?.dismiss()
            }

            binding.ivPasswordEye.setOnClickListener {
                if (binding.edtPassword.transformationMethod == null) {
                    binding.edtPassword.transformationMethod = PasswordTransformationMethod()
                    binding.edtPassword.setSelection(binding.edtPassword.text?.length ?: 0)
                    binding.ivPasswordEye.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_eye))
                } else {
                    binding.edtPassword.transformationMethod = null
                    binding.edtPassword.setSelection(binding.edtPassword.text?.length ?: 0)
                    binding.ivPasswordEye.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_eye_closed))
                }
            }

            binding.ivConfirmPasswordEye.setOnClickListener {
                if (binding.edtConfirmPassword.transformationMethod == null) {
                    binding.edtConfirmPassword.transformationMethod = PasswordTransformationMethod()
                    binding.edtConfirmPassword.setSelection(binding.edtConfirmPassword.text?.length ?: 0)
                    binding.ivConfirmPasswordEye.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_eye))
                } else {
                    binding.edtConfirmPassword.transformationMethod = null
                    binding.edtConfirmPassword.setSelection(binding.edtConfirmPassword.text?.length ?: 0)
                    binding.ivConfirmPasswordEye.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_eye_closed))
                }
            }

            binding.btnAlertPositive.setOnClickListener {
                when {
                    binding.edtUserId.text.toString().isEmpty() -> {
                        Toast.makeText(activity, "Please enter user id", Toast.LENGTH_SHORT).show()
                    }
                    binding.edtUserName.text.toString().isEmpty() -> {
                        Toast.makeText(activity, "Please enter user name", Toast.LENGTH_SHORT).show()
                    }
                    binding.edtPassword.text.toString().isEmpty() -> {
                        Toast.makeText(activity, "Please enter password", Toast.LENGTH_SHORT).show()
                    }
                    binding.edtConfirmPassword.text.toString().isEmpty() -> {
                        Toast.makeText(activity, "Please enter confirm password", Toast.LENGTH_SHORT).show()
                    }
                    binding.edtConfirmPassword.text.toString() != binding.edtPassword.text.toString() -> {
                        Toast.makeText(activity, "Both password does not match", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        addUserDialog?.dismiss()
                        alertDialogInterface.onAddUser(
                            model = UserListDataModel(
                                id = binding.edtUserId.text.toString(),
                                userId = binding.edtUserId.text.toString(),
                                userName = binding.edtUserName.text.toString(),
                                password = binding.edtPassword.text.toString(),
                            )
                        )
                    }
                }

            }
            addUserDialog?.show()
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

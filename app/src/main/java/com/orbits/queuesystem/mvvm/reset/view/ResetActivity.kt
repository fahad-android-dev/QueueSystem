package com.orbits.queuesystem.mvvm.reset.view

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.orbits.queuesystem.R
import com.orbits.queuesystem.databinding.ActivityResetBinding
import com.orbits.queuesystem.helper.AlertDialogInterface
import com.orbits.queuesystem.helper.BaseActivity
import com.orbits.queuesystem.helper.CommonInterfaceClickEvent
import com.orbits.queuesystem.helper.Constants
import com.orbits.queuesystem.helper.Dialogs
import com.orbits.queuesystem.helper.Extensions.getFormattedDateTime
import com.orbits.queuesystem.helper.database.LocalDB.addResetData
import com.orbits.queuesystem.helper.database.LocalDB.getAllResetData
import com.orbits.queuesystem.helper.database.LocalDB.getAllTransactionFromDB
import com.orbits.queuesystem.helper.database.LocalDB.resetAllTransactionInDb
import com.orbits.queuesystem.helper.database.LocalDB.updateResetDateTime
import com.orbits.queuesystem.helper.database.ResetDataDbModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ResetActivity : BaseActivity() {
    private lateinit var binding: ActivityResetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset)

        initializeToolbar()
        initializeFields()
        onClickListeners()
    }

    private fun initializeToolbar() {
        setUpToolbar(
            binding.layoutToolbar,
            title = getString(R.string.reset),
            isBackArrow = true,
            toolbarClickListener = object : CommonInterfaceClickEvent {
                override fun onToolBarListener(type: String) {
                    if (type == Constants.TOOLBAR_ICON_ONE) {

                    }
                }
            }
        )
    }


    private fun initializeFields() {
        if (getAllResetData()?.isNotEmpty() == true){
            binding.txtResetTime.text = getAllResetData()?.get(0)?.resetDateTime?.substringAfter(" ") ?: ""
        }else {
            binding.txtResetTime.text = "00:00"
        }

    }

    private fun onClickListeners(){
        binding.btnSetReset.setOnClickListener {
            Dialogs.showTimePicker(this,object : AlertDialogInterface {
                override fun onTimeSelected(hour: String, minute: String) {
                    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
                    val formattedDateTime = getFormattedDateTime(currentDate, "$hour:$minute")
                    binding.txtResetTime.text = hour + ":" + minute
                    if(getAllResetData()?.isEmpty() == true){
                        println("here is reset data 000 ${getAllResetData()}")
                        addResetData(
                            data = ResetDataDbModel(
                                id = 0,
                                resetDateTime = formattedDateTime,
                                currentDateTime = "",
                                lastDateTime = "2024-08-26 20:20"
                            )
                        )
                    }else {
                        println("here is reset data 111 ${getAllResetData()}")
                        updateResetDateTime(formattedDateTime)

                        println("here is reset data 222 ${getAllResetData()}")
                    }
                }
            })
        }

        binding.btnResetNow.setOnClickListener {
            println("here is all transactions ${getAllTransactionFromDB()}")
            Dialogs.showCustomAlert(
                activity = this@ResetActivity,
                msg = getString(R.string.are_you_sure_you_want_to_reset_queue_now),
                yesBtn = getString(R.string.yes),
                noBtn = getString(R.string.no),
                alertDialogInterface = object : AlertDialogInterface {
                    override fun onYesClick() {
                        resetAllTransactionInDb()
                        Toast.makeText(this@ResetActivity,
                            getString(R.string.queue_reset_successfully), Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }


}
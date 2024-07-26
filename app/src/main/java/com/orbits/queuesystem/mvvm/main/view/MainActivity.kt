package com.orbits.queuesystem.mvvm.main.view

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.orbits.queuesystem.R
import com.orbits.queuesystem.databinding.ActivityMainBinding
import com.orbits.queuesystem.helper.AlertDialogInterface
import com.orbits.queuesystem.helper.BaseActivity
import com.orbits.queuesystem.helper.CommonInterfaceClickEvent
import com.orbits.queuesystem.helper.Constants
import com.orbits.queuesystem.helper.Dialogs

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initializeToolbar()
        onClickListeners()

    }

    private fun initializeToolbar() {
        setUpToolbar(
            binding.layoutToolbar,
            title = getString(R.string.app_name),
            isBackArrow = false,
            toolbarClickListener = object : CommonInterfaceClickEvent {
                override fun onToolBarListener(type: String) {
                    if (type == Constants.TOOLBAR_ICON_ONE) {

                    }
                }
            }
        )
    }

    private fun onClickListeners(){
        binding.btnAddService.setOnClickListener {
            Dialogs.showAddServiceDialog(this, true, object : AlertDialogInterface {
                override fun onYesClick() {
                    super.onYesClick()
                }
            })
        }
    }
}
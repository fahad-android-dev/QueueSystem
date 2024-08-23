package com.orbits.queuesystem.mvvm.users.view

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.orbits.queuesystem.R
import com.orbits.queuesystem.databinding.ActivityCounterListBinding
import com.orbits.queuesystem.databinding.ActivityUserListBinding
import com.orbits.queuesystem.helper.AlertDialogInterface
import com.orbits.queuesystem.helper.BaseActivity
import com.orbits.queuesystem.helper.CommonInterfaceClickEvent
import com.orbits.queuesystem.helper.Constants
import com.orbits.queuesystem.helper.CounterConfig.parseInCounterDbModel
import com.orbits.queuesystem.helper.CounterConfig.parseInCounterModelArraylist
import com.orbits.queuesystem.helper.Dialogs
import com.orbits.queuesystem.helper.UserConfig.parseInUserDbModel
import com.orbits.queuesystem.helper.UserConfig.parseInUserModelArraylist
import com.orbits.queuesystem.helper.database.LocalDB.addCounterInDB
import com.orbits.queuesystem.helper.database.LocalDB.addUserInDB
import com.orbits.queuesystem.helper.database.LocalDB.deleteCounterInDb
import com.orbits.queuesystem.helper.database.LocalDB.deleteServiceInDb
import com.orbits.queuesystem.helper.database.LocalDB.getAllCounterFromDB
import com.orbits.queuesystem.helper.database.LocalDB.getAllUserFromDB
import com.orbits.queuesystem.mvvm.counters.adapter.CounterListAdapter
import com.orbits.queuesystem.mvvm.counters.model.CounterListDataModel
import com.orbits.queuesystem.mvvm.users.adapter.UserListAdapter
import com.orbits.queuesystem.mvvm.users.model.UserListDataModel

class UserListActivity : BaseActivity() {
    private lateinit var binding: ActivityUserListBinding
    private var adapter = UserListAdapter()
    private var arrListCounter = ArrayList<UserListDataModel?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_list)

        initializeToolbar()
        initializeFields()
        onClickListeners()
    }

    private fun initializeToolbar() {
        setUpToolbar(
            binding.layoutToolbar,
            title = getString(R.string.user_list),
            isBackArrow = true,
            toolbarClickListener = object : CommonInterfaceClickEvent {
                override fun onToolBarListener(type: String) {
                    if (type == Constants.TOOLBAR_ICON_ONE) {

                    }
                }
            }
        )
    }

    private fun initializeFields(){
        binding.rvUserList.adapter = adapter
        setData(parseInUserModelArraylist(getAllUserFromDB()))
    }

    private fun setData(data: ArrayList<UserListDataModel?>) {
        arrListCounter.clear()
        arrListCounter.addAll(data)
        adapter.onClickEvent = object : CommonInterfaceClickEvent {
            override fun onItemClick(type: String, position: Int) {
                if(type == "deleteCounter"){
                    Dialogs.showCustomAlert(
                        activity = this@UserListActivity,
                        msg = getString(R.string.are_you_sure_you_want_to_delete_this_counter),
                        yesBtn = getString(R.string.yes),
                        noBtn = getString(R.string.no),
                        alertDialogInterface = object : AlertDialogInterface {
                            override fun onYesClick() {
                                deleteCounterInDb(arrListCounter[position]?.id)
                                arrListCounter.removeAt(position)
                                adapter.setData(arrListCounter)
                            }
                        }
                    )

                }
            }
        }
        adapter.setData(arrListCounter)
    }

    private fun onClickListeners(){
        binding.btnAddUser.setOnClickListener {
            Dialogs.showAddUserDialog(this, true, object : AlertDialogInterface {
                override fun onAddUser(model: UserListDataModel) {
                    val dbModel = parseInUserDbModel(model, model.id ?: "")
                    addUserInDB(dbModel)
                    setData(parseInUserModelArraylist(getAllUserFromDB()))
                }
            })
        }
    }
}
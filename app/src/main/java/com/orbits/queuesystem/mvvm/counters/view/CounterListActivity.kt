package com.orbits.queuesystem.mvvm.counters.view

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.orbits.queuesystem.R
import com.orbits.queuesystem.databinding.ActivityCounterListBinding
import com.orbits.queuesystem.helper.interfaces.AlertDialogInterface
import com.orbits.queuesystem.helper.BaseActivity
import com.orbits.queuesystem.helper.interfaces.CommonInterfaceClickEvent
import com.orbits.queuesystem.helper.Constants
import com.orbits.queuesystem.helper.configs.CounterConfig.parseInCounterDbModel
import com.orbits.queuesystem.helper.configs.CounterConfig.parseInCounterModelArraylist
import com.orbits.queuesystem.helper.Dialogs
import com.orbits.queuesystem.helper.database.LocalDB.addCounterInDB
import com.orbits.queuesystem.helper.database.LocalDB.getAllCounterFromDB
import com.orbits.queuesystem.mvvm.counters.adapter.CounterListAdapter
import com.orbits.queuesystem.mvvm.counters.model.CounterListDataModel

class CounterListActivity : BaseActivity() {
    private lateinit var binding: ActivityCounterListBinding
    private var adapter = CounterListAdapter()
    private var arrListCounter = ArrayList<CounterListDataModel?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_counter_list)

        initializeToolbar()
        initializeFields()
        onClickListeners()
    }

    private fun initializeToolbar() {
        setUpToolbar(
            binding.layoutToolbar,
            title = getString(R.string.counter_list),
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
        binding.rvCounterList.adapter = adapter
        setData(parseInCounterModelArraylist(getAllCounterFromDB()))
    }

    private fun setData(data: ArrayList<CounterListDataModel?>) {
        arrListCounter.clear()
        arrListCounter.addAll(data)
        adapter.onClickEvent = object : CommonInterfaceClickEvent {
            override fun onItemClick(type: String, position: Int) {
                if(type == "editCounter"){
                    Dialogs.showAddCounterDialog(
                        this@CounterListActivity,
                        editCounterModel = data[position],
                        object : AlertDialogInterface {
                        override fun onUpdateCounter(model: CounterListDataModel) {
                            println("here is counter service id ${model.serviceId}")
                            val dbModel = parseInCounterDbModel(model, model.counterId ?: "")
                            addCounterInDB(dbModel)
                            setData(parseInCounterModelArraylist(getAllCounterFromDB()))
                            println("here is counter all counters ${getAllCounterFromDB()}")
                        }
                    })

                }
            }
        }
        adapter.setData(arrListCounter)
    }

    private fun onClickListeners(){
        binding.btnAddCounter.setOnClickListener {
            Dialogs.showAddCounterDialog(
                this,
                editCounterModel = null,
                object : AlertDialogInterface {
                override fun onAddCounter(model: CounterListDataModel) {
                    val dbModel = parseInCounterDbModel(model, model.counterId ?: "")
                    addCounterInDB(dbModel)
                    setData(parseInCounterModelArraylist(getAllCounterFromDB()))
                }
            })
        }
    }
}
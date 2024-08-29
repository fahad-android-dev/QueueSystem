package com.orbits.queuesystem.mvvm.counters.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.orbits.queuesystem.R
import com.orbits.queuesystem.databinding.LvItemCounterListBinding
import com.orbits.queuesystem.helper.interfaces.CommonInterfaceClickEvent
import com.orbits.queuesystem.mvvm.counters.model.CounterListDataModel
import me.thanel.swipeactionview.SwipeActionView
import me.thanel.swipeactionview.SwipeGestureListener

class CounterListAdapter : RecyclerView.Adapter<CounterListAdapter.MyViewHolder>() {
    var arrListData : ArrayList<CounterListDataModel?> = ArrayList()
    var onClickEvent : CommonInterfaceClickEvent?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: LvItemCounterListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.lv_item_counter_list,
            parent,
            false
        )
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = arrListData.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val a = arrListData[position]
        val context = holder.itemView.context

        holder.binding.txtCounterId.text = "Counter Id : ${a?.id}"
        holder.binding.txtCounterName.text = "Counter Name : ${a?.name}"
        holder.binding.txtServiceType.text = "Counter Type : ${a?.counterType}"
        holder.binding.txtServiceId.text = "Service Id : ${a?.serviceId}"

        holder.binding.swipeLayout.swipeGestureListener = object : SwipeGestureListener {
            override fun onSwipedLeft(swipeActionView: SwipeActionView): Boolean {
                return false
            }

            override fun onSwipedRight(swipeActionView: SwipeActionView): Boolean {
                return false
            }
        }


        holder.binding.rootLayout.setOnClickListener {
            onClickEvent?.onItemClick("counterClicked",position)
        }

        holder.binding.linEdit.setOnClickListener {
            onClickEvent?.onItemClick("editCounter", position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: ArrayList<CounterListDataModel?>) {
        if (data.isEmpty()) {
            arrListData = ArrayList()
        }
        arrListData = data
        notifyDataSetChanged()
    }

    class MyViewHolder(var binding: LvItemCounterListBinding) :
        RecyclerView.ViewHolder(binding.root)
}
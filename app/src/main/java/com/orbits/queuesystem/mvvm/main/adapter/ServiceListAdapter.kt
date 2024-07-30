package com.orbits.queuesystem.mvvm.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.orbits.queuesystem.R
import com.orbits.queuesystem.databinding.LvItemServiceListBinding
import com.orbits.queuesystem.helper.CommonInterfaceClickEvent
import com.orbits.queuesystem.mvvm.main.model.ServiceListDataModel
import me.thanel.swipeactionview.SwipeActionView
import me.thanel.swipeactionview.SwipeGestureListener

class ServiceListAdapter : RecyclerView.Adapter<ServiceListAdapter.MyViewHolder>() {
    var arrListData : ArrayList<ServiceListDataModel?> = ArrayList()
    var onClickEvent : CommonInterfaceClickEvent?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: LvItemServiceListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.lv_item_service_list,
            parent,
            false
        )
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = arrListData.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val a = arrListData[position]
        val context = holder.itemView.context

        holder.binding.txtServiceId.text = "Service Id : ${a?.id}"
        holder.binding.txtServiceName.text = "Service Name : ${a?.name}"
        holder.binding.txtStartToken.text = "Start Token : ${a?.tokenStart}"
        holder.binding.txtEndToken.text = "End Token : ${a?.tokenEnd}"


        holder.binding.swipeLayout.swipeGestureListener = object : SwipeGestureListener {
            override fun onSwipedLeft(swipeActionView: SwipeActionView): Boolean {
                return false
            }

            override fun onSwipedRight(swipeActionView: SwipeActionView): Boolean {
                return false
            }
        }


        holder.binding.rootLayout.setOnClickListener {
            onClickEvent?.onItemClick("serviceClicked",position)
        }

        holder.binding.linDelete.setOnClickListener {
            onClickEvent?.onItemClick("deleteService", position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: ArrayList<ServiceListDataModel?>) {
        if (data.isEmpty()) {
            arrListData = ArrayList()
        }
        arrListData = data
        notifyDataSetChanged()
    }

    class MyViewHolder(var binding: LvItemServiceListBinding) :
        RecyclerView.ViewHolder(binding.root)
}
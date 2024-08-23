package com.orbits.queuesystem.mvvm.users.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.orbits.queuesystem.R
import com.orbits.queuesystem.databinding.LvItemCounterListBinding
import com.orbits.queuesystem.databinding.LvItemUserListBinding
import com.orbits.queuesystem.helper.CommonInterfaceClickEvent
import com.orbits.queuesystem.mvvm.counters.model.CounterListDataModel
import com.orbits.queuesystem.mvvm.users.model.UserListDataModel
import me.thanel.swipeactionview.SwipeActionView
import me.thanel.swipeactionview.SwipeGestureListener

class UserListAdapter : RecyclerView.Adapter<UserListAdapter.MyViewHolder>() {
    var arrListData : ArrayList<UserListDataModel?> = ArrayList()
    var onClickEvent : CommonInterfaceClickEvent?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: LvItemUserListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.lv_item_user_list,
            parent,
            false
        )
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = arrListData.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val a = arrListData[position]
        val context = holder.itemView.context

        holder.binding.txtUserId.text = "User Id : ${a?.id}"
        holder.binding.txtName.text = "User Name : ${a?.userName}"
        holder.binding.txtPassword.text = "Password : ${a?.password}"

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

        holder.binding.linDelete.setOnClickListener {
            onClickEvent?.onItemClick("deleteCounter", position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: ArrayList<UserListDataModel?>) {
        if (data.isEmpty()) {
            arrListData = ArrayList()
        }
        arrListData = data
        notifyDataSetChanged()
    }

    class MyViewHolder(var binding: LvItemUserListBinding) :
        RecyclerView.ViewHolder(binding.root)
}
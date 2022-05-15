package com.sit708.coupledApp.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sit708.coupledApp.databinding.AdapterMessageOneBinding
import com.sit708.coupledApp.models.Message

class ChatAdapter(private val messageList: List<Message>,private val  context: Context) :
    RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AdapterMessageOneBinding.inflate(LayoutInflater.from(context), parent, false)
          return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message: String? = messageList[position].message
        val isReceived: Boolean = messageList[position].isReceived
        if (isReceived) {
            holder.messageReceive.visibility = View.VISIBLE
            holder.messageSend.visibility = View.GONE
            holder.messageReceive.text = message
        } else {
            holder.messageSend.visibility = View.VISIBLE
            holder.messageReceive.visibility = View.GONE
            holder.messageSend.text = message
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class MyViewHolder(itemView: AdapterMessageOneBinding) : RecyclerView.ViewHolder(itemView.root) {
        var messageSend: TextView = itemView.messageSend
        var messageReceive: TextView= itemView.messageReceive

    }
}
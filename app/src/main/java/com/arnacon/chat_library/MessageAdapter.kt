package com.arnacon.chat_library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arnacon.chat_library.R


class MessagesAdapter(private val messages: MutableList<DisplayedMessage>, private val currentUser: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_MY_MESSAGE = 1
        private const val VIEW_TYPE_OTHER_MESSAGE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sender == currentUser) VIEW_TYPE_MY_MESSAGE else VIEW_TYPE_OTHER_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_MY_MESSAGE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.messagelayout, parent, false)
            MyMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.othermessage, parent, false)
            OtherMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is MyMessageViewHolder) {
            holder.bind(message)
        } else if (holder is OtherMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount() = messages.size

    fun addMessage(message: DisplayedMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    class MyMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageText: TextView = view.findViewById(R.id.text_gchat_message_me)

        fun bind(message: DisplayedMessage) {
            when (message) {
                is DisplayedMessage.TextMessage -> messageText.text = message.text
                is DisplayedMessage.FileMessage -> messageText.text = "File: ${message.filename}"
            }
        }
    }

    class OtherMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageText: TextView = view.findViewById(R.id.text_gchat_message_other)

        fun bind(message: DisplayedMessage) {
            when (message) {
                is DisplayedMessage.TextMessage -> messageText.text = message.text
                is DisplayedMessage.FileMessage -> messageText.text = "File: ${message.filename}"
            }
        }
    }
}

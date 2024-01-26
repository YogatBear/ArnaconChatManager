package com.arnacon.chat_library

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessagesAdapter(private val messages: MutableList<DisplayedMessage>, private val currentUser: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_MY_MESSAGE = 1
        private const val VIEW_TYPE_OTHER_MESSAGE = 2
    }

    override fun getItemViewType(position: Int): Int {
        val viewType = if (messages[position].sender == currentUser) {
            VIEW_TYPE_MY_MESSAGE
        } else {
            VIEW_TYPE_OTHER_MESSAGE
        }
        Log.d("MessagesAdapter", "getItemViewType - Position: $position, ViewType: $viewType, Sender: ${messages[position].sender}, CurrentUser: $currentUser")
        return viewType
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
        Log.d("MessagesAdapter", "onBindViewHolder - Position: $position, Message: $message")
        if (holder is MyMessageViewHolder) {
            holder.bind(message as DisplayedMessage.TextMessage)
        } else if (holder is OtherMessageViewHolder) {
            holder.bind(message as DisplayedMessage.TextMessage) // Assuming all messages are text messages
        }
    }

    override fun getItemCount() = messages.size

    fun addMessage(message: DisplayedMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    class MyMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageText: TextView = view.findViewById(R.id.text_gchat_message_me)

        fun bind(message: DisplayedMessage.TextMessage) {
            messageText.text = message.text
        }
    }

    class OtherMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageText: TextView = view.findViewById(R.id.text_gchat_message_other)
        private val userNameText: TextView = view.findViewById(R.id.text_gchat_user_other)

        fun bind(message: DisplayedMessage.TextMessage) {
            messageText.text = message.text
            userNameText.text = message.sender  // Set the sender's name dynamically
        }
    }
}

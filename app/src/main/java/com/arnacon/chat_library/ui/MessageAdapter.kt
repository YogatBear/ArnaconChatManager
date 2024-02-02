package com.arnacon.chat_library

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(
    private val messages: MutableList<DisplayedMessage>,
    private val currentUser: String,
    private val onFileClick: (Uri) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_MY_TEXT_MESSAGE = 1
        private const val VIEW_TYPE_OTHER_TEXT_MESSAGE = 2
        private const val VIEW_TYPE_MY_FILE_MESSAGE = 3
        private const val VIEW_TYPE_OTHER_FILE_MESSAGE = 4
        private const val VIEW_TYPE_MY_IMAGE_MESSAGE = 5
        private const val VIEW_TYPE_OTHER_IMAGE_MESSAGE = 6
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return when (message) {
            is DisplayedMessage.TextMessage -> {
                if (message.sender == currentUser) VIEW_TYPE_MY_TEXT_MESSAGE else VIEW_TYPE_OTHER_TEXT_MESSAGE
            }
            is DisplayedMessage.FileMessage -> {
                if (isImageFile(message.filename)) {
                    if (message.sender == currentUser) VIEW_TYPE_MY_IMAGE_MESSAGE else VIEW_TYPE_OTHER_IMAGE_MESSAGE
                } else {
                    if (message.sender == currentUser) VIEW_TYPE_MY_FILE_MESSAGE else VIEW_TYPE_OTHER_FILE_MESSAGE
                }
            }
        }
    }

    private fun isImageFile(filename: String): Boolean {
        return listOf(".jpeg", ".jpg", ".png").any { filename.toLowerCase().endsWith(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MY_TEXT_MESSAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.my_text, parent, false)
                MyMessageViewHolder(view)
            }
            VIEW_TYPE_OTHER_TEXT_MESSAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.other_text, parent, false)
                OtherMessageViewHolder(view)
            }
            VIEW_TYPE_MY_FILE_MESSAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.my_file, parent, false)
                MyFileMessageViewHolder(view, onFileClick)
            }
            VIEW_TYPE_OTHER_FILE_MESSAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.other_file, parent, false)
                OtherFileMessageViewHolder(view, onFileClick)
            }
            VIEW_TYPE_MY_IMAGE_MESSAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.my_image, parent, false)
                MyImageMessageViewHolder(view, onFileClick)
            }
            VIEW_TYPE_OTHER_IMAGE_MESSAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.other_image, parent, false)
                OtherImageMessageViewHolder(view, onFileClick)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        val showDate = position == 0 || message.formattedDate != messages[position - 1].formattedDate

        when (holder) {
            is MyMessageViewHolder -> holder.bind(message as DisplayedMessage.TextMessage, showDate)
            is OtherMessageViewHolder -> holder.bind(message as DisplayedMessage.TextMessage, showDate)
            is MyFileMessageViewHolder -> holder.bind(message as DisplayedMessage.FileMessage, showDate)
            is OtherFileMessageViewHolder -> holder.bind(message as DisplayedMessage.FileMessage, showDate)
            is MyImageMessageViewHolder -> holder.bind(message as DisplayedMessage.FileMessage, showDate)
            is OtherImageMessageViewHolder -> holder.bind(message as DisplayedMessage.FileMessage, showDate)
        }
    }

    override fun getItemCount() = messages.size

    fun addMessage(message: DisplayedMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    class MyMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageText: TextView = view.findViewById(R.id.text_gchat_message_me)
        private val dateText: TextView = view.findViewById(R.id.text_gchat_date_me)
        private val timeText: TextView = view.findViewById(R.id.text_gchat_timestamp_me)

        fun bind(message: DisplayedMessage.TextMessage, showDate: Boolean) {
            dateText.visibility = if (showDate) View.VISIBLE else View.GONE
            messageText.text = message.text
            dateText.text = message.formattedDate
            timeText.text = message.formattedTime
        }
    }

    class OtherMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageText: TextView = view.findViewById(R.id.text_gchat_message_other)
        private val userNameText: TextView = view.findViewById(R.id.text_gchat_user_other)
        private val dateText: TextView = view.findViewById(R.id.text_gchat_date_other)
        private val timeText: TextView = view.findViewById(R.id.text_gchat_timestamp_other)

        fun bind(message: DisplayedMessage.TextMessage, showDate: Boolean) {
            dateText.visibility = if (showDate) View.VISIBLE else View.GONE
            messageText.text = message.text
            userNameText.text = message.sender
            dateText.text = message.formattedDate
            timeText.text = message.formattedTime
        }
    }

    class MyFileMessageViewHolder(view: View, private val onFileClick: (Uri) -> Unit) : RecyclerView.ViewHolder(view) {
        private val fileNameText: TextView = view.findViewById(R.id.text_file_name_me)
        private val fileSizeText: TextView = view.findViewById(R.id.text_file_size_me)
        private val dateText: TextView = view.findViewById(R.id.text_gchat_date_other)
        private val timeText: TextView = view.findViewById(R.id.text_gchat_timestamp_other)

        fun bind(message: DisplayedMessage.FileMessage, showDate: Boolean) {
            dateText.visibility = if (showDate) View.VISIBLE else View.GONE
            fileNameText.text = message.filename
            fileSizeText.text = message.filesize.toString()
            dateText.text = message.formattedDate
            timeText.text = message.formattedTime

            itemView.setOnClickListener { onFileClick(message.fileUri) }
        }
    }

    class OtherFileMessageViewHolder(view: View, private val onFileClick: (Uri) -> Unit) : RecyclerView.ViewHolder(view) {
        private val fileNameText: TextView = view.findViewById(R.id.text_file_name_other)
        private val fileSizeText: TextView = view.findViewById(R.id.text_file_size_other)
        private val dateText: TextView = view.findViewById(R.id.text_image_date)
        private val timeText: TextView = view.findViewById(R.id.text_image_timestamp)

        fun bind(message: DisplayedMessage.FileMessage, showDate: Boolean) {
            dateText.visibility = if (showDate) View.VISIBLE else View.GONE
            fileNameText.text = message.filename
            fileSizeText.text = message.filesize.toString()
            dateText.text = message.formattedDate
            timeText.text = message.formattedTime

            itemView.setOnClickListener { onFileClick(message.fileUri) }
        }
    }

    class MyImageMessageViewHolder(view: View, private val onFileClick: (Uri) -> Unit) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.image_message)
        private val dateText: TextView = view.findViewById(R.id.text_image_date)
        private val timeText: TextView = view.findViewById(R.id.text_image_timestamp)

        fun bind(message: DisplayedMessage.FileMessage, showDate: Boolean) {
            dateText.visibility = if (showDate) View.VISIBLE else View.GONE
            imageView.setImageURI(message.fileUri)
            dateText.text = message.formattedDate
            timeText.text = message.formattedTime
        }
    }

    class OtherImageMessageViewHolder(view: View, private val onFileClick: (Uri) -> Unit) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.image_message_other)
        private val senderNameText: TextView = view.findViewById(R.id.text_gchat_user_other)
        private val dateText: TextView = view.findViewById(R.id.text_gchat_date_other)
        private val timeText: TextView = view.findViewById(R.id.text_gchat_timestamp_other)

        fun bind(message: DisplayedMessage.FileMessage, showDate: Boolean) {
            dateText.visibility = if (showDate) View.VISIBLE else View.GONE
            imageView.setImageURI(message.fileUri)
            dateText.text = message.formattedDate
            timeText.text = message.formattedTime
            senderNameText.text = message.sender
        }
    }

    fun setMessages(newMessages: List<DisplayedMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }
}

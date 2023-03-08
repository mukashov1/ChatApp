package com.example.chatapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class MessageAdapter(
    context: Context,
    resource: Int,
    messages: MutableList<Message>
) : ArrayAdapter<Message>(context, resource, messages) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val conView = convertView
            ?: LayoutInflater.from(context)
                .inflate(R.layout.message_item, parent, false)
        val photoImageView: ImageView = conView.findViewById(R.id.photoImageView)
        val textTextView: TextView = conView.findViewById(R.id.textTextView)
        val nameTextView: TextView = conView.findViewById(R.id.nameTextView)
        val message = getItem(position)
        nameTextView.text = message?.name



        if (message?.imageUrl == null) {
            textTextView.visibility = View.VISIBLE
            photoImageView.visibility = View.GONE
            textTextView.text = message?.text
        } else {
            textTextView.visibility = View.GONE
            photoImageView.visibility = View.VISIBLE
            Glide.with(photoImageView.context)
                .load(message.imageUrl)
                .into(photoImageView)
        }


        return conView
    }
}
package com.example.practico4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practico4.models.Contact
import com.example.practico4.R

class ContactAdapter(
    private var contacts: List<Contact>,
    private val onEdit: (Contact) -> Unit,
    private val onDelete: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    fun setContacts(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.contactName.text = "${contact.name} ${contact.last_name}"
        holder.contactInfo.text = contact.company ?: "Sin compañía"

        Glide.with(holder.itemView.context)
            .load(contact.profile_picture)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.contactImage)

        holder.editButton.setOnClickListener { onEdit(contact) }
        holder.deleteButton.setOnClickListener { onDelete(contact) }
    }

    override fun getItemCount(): Int = contacts.size

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactImage: ImageView = view.findViewById(R.id.contact_image)
        val contactName: TextView = view.findViewById(R.id.contact_name)
        val contactInfo: TextView = view.findViewById(R.id.contact_info)
        val editButton: Button = view.findViewById(R.id.edit_button)
        val deleteButton: Button = view.findViewById(R.id.delete_button)
    }
}

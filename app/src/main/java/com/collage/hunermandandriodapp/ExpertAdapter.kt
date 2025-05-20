package com.collage.hunermandandriodapp

import com.bumptech.glide.Glide
import Expert_Dataclass
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.collage.hunermandandriodapp.databinding.ActivityExpertsBinding
import com.collage.hunermandandriodapp.databinding.ItemExpertsBinding

class ExpertAdapter(private var expertList: List<Expert_Dataclass>) : RecyclerView.Adapter<ExpertAdapter.ExpertViewHolder>() {

    inner class ExpertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name_expertsItem)
        val address: TextView = view.findViewById(R.id.tv_address_expertsItem)
        val skills: TextView = view.findViewById(R.id.tvSkillsExpertsItem)
        val gender: TextView = view.findViewById(R.id.tv_gender)
        val education: TextView = view.findViewById(R.id.tv_education)
        val experience: TextView = view.findViewById(R.id.tv_experience)
        val description: TextView = view.findViewById(R.id.tv_description)
        val profileImage : ImageView = view.findViewById(R.id.iv_profile_image)
        val expandIcon: ImageView = view.findViewById(R.id.iv_expand_icon)
        val callIcon: ImageView = view.findViewById(R.id.iv_call_icon) // Call icon
        val messagIcon : ImageView = view.findViewById(R.id.iv_message_icon)
        val whattsappIcon : ImageView = view.findViewById(R.id.iv_whattsapp_icon)
        val expandableLayout: View = view.findViewById(R.id.expandableLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpertViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_experts, parent, false)
        return ExpertViewHolder(view)
    }

    override fun getItemCount(): Int = expertList.size

    override fun onBindViewHolder(holder: ExpertViewHolder, position: Int) {
        val currentItem = expertList[position]
        holder.name.text = currentItem.name
        holder.address.text = currentItem.address
        holder.skills.text = currentItem.skills
        holder.gender.text = currentItem.gender
        holder.education.text = currentItem.certificate
        holder.experience.text = currentItem.experience
        holder.description.text = currentItem.description

        // Set initial visibility of expandable layout to GONE
        holder.expandableLayout.visibility = View.GONE

        // Toggle expand/collapse on click
        holder.itemView.setOnClickListener {
            if (holder.expandableLayout.visibility == View.GONE) {
                holder.expandableLayout.visibility = View.VISIBLE
                rotateIcon(holder.expandIcon, 0f, 180f)
            } else {
                holder.expandableLayout.visibility = View.GONE
                rotateIcon(holder.expandIcon, 180f, 0f)
            }
        }

        // Set up call icon click to initiate a phone call
        holder.callIcon.setOnClickListener {
            val context = holder.itemView.context
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${currentItem.ph_no}")
            }
            context.startActivity(dialIntent)
        }

        // Message icon to send SMS
        holder.messagIcon.setOnClickListener {
            val context = holder.itemView.context
            val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:${currentItem.ph_no}")
            }
            context.startActivity(smsIntent)
        }

        // Message icon to send SMS
        holder.whattsappIcon.setOnClickListener {
            val context = holder.itemView.context
            val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/${currentItem.ph_no}")
            }
            context.startActivity(smsIntent)
        }

        // Load the profile image using Glide
        Glide.with(holder.itemView.context)
            .load(currentItem.imageUrl)
            .placeholder(R.drawable.profile_image)
            .into(holder.profileImage)
    }

    // Rotate the expand/collapse icon
    private fun rotateIcon(icon: ImageView, from: Float, to: Float) {
        val rotate = RotateAnimation(
            from, to,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 300
        rotate.fillAfter = true
        icon.startAnimation(rotate)
    }
}

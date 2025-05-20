package com.collage.hunermandandriodapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.collage.hunermandandriodapp.databinding.ItemJobsBinding
import com.google.firebase.database.FirebaseDatabase
import android.widget.LinearLayout

class JobsAdapter(
    private val JobsList: MutableList<Jobs_Dataclass>,
    private val isProfileFragment: Boolean
) : RecyclerView.Adapter<JobsAdapter.JobViewHolder>() {

    inner class JobViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val jobTitle: TextView = view.findViewById(R.id.tv_jobtitle)
        val location: TextView = view.findViewById(R.id.tv_address)
        val salary: TextView = view.findViewById(R.id.tv_salary)
        val jobType: TextView = view.findViewById(R.id.tv_jobtype)
        val gender: TextView = view.findViewById(R.id.tv_gender)
        val education: TextView = view.findViewById(R.id.tv_education)
        val experience: TextView = view.findViewById(R.id.tv_experience)
        val description: TextView = view.findViewById(R.id.tv_description)
        val contactPeronName : TextView = view.findViewById(R.id.tv_contacPersonName)
        val expandableLayout: View = view.findViewById(R.id.expendablelayout)
        val expandIcon: ImageView = view.findViewById(R.id.iv_expand_icon)
        val callIcon: ImageView = view.findViewById(R.id.iv_Jcall_icon)
        val messageIcon: ImageView = view.findViewById(R.id.iv_Jmessage_icon)
        val whatsappIcon: ImageView = view.findViewById(R.id.iv_Jwhattsapp_icon)
        val deleteButton: ImageView = view.findViewById(R.id.btnDeleteJob)
        val progressbar : ProgressBar = view.findViewById(R.id.progressBar)

        val llfooter :LinearLayout = view.findViewById(R.id.ll_footer)

        fun bind(job: Jobs_Dataclass, position: Int) {
            jobTitle.text = job.jobtitle
            location.text = job.location
            salary.text = job.sallry
            jobType.text = job.jobtype
            gender.text = job.gender
            education.text = job.education
            experience.text = job.experience
            description.text = job.description
            contactPeronName.text = job.contact_person_name

            // Show delete button only in ProfileFragment
            deleteButton.visibility = if (isProfileFragment) View.VISIBLE else View.GONE
            llfooter.visibility = if(isProfileFragment) View.GONE else View.VISIBLE

            deleteButton.setOnClickListener {
                job.key?.let { key ->

                    val Context = itemView.context

                    AlertDialog.Builder(Context)
                        .setTitle("Delete Job")
                        .setMessage("Are you sure, you want to delete your Job?")
                        .setPositiveButton("Yes") { _, _ ->
                            deleteJob(key, position)
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            }
        }

        private fun deleteJob(jobKey: String, position: Int) {

            progressbar.visibility = View.VISIBLE

            val jobsRef = FirebaseDatabase.getInstance().getReference("jobs")
            jobsRef.child(jobKey).removeValue().addOnSuccessListener {
                // Remove job from the list and notify adapter
                JobsList.removeAt(position)
                notifyItemRemoved(position)

                progressbar.visibility = View.GONE

                Toast.makeText(itemView.context, "Job deleted", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(itemView.context, "Failed to delete job", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_jobs, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = JobsList[position]
        holder.bind(job, position)

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
                data = Uri.parse("tel:${job.phon_no}")
            }
            context.startActivity(dialIntent)
        }

        // Message icon to send SMS
        holder.messageIcon.setOnClickListener {
            val context = holder.itemView.context
            val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:${job.phon_no}")
            }
            context.startActivity(smsIntent)
        }

        // WhatsApp icon to send message
        holder.whatsappIcon.setOnClickListener {
            val context = holder.itemView.context
            val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/${job.phon_no}")
            }
            context.startActivity(whatsappIntent)
        }
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

    override fun getItemCount(): Int = JobsList.size
}

package com.collage.hunermandandriodapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.collage.hunermandandriodapp.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.expertbtn.setOnClickListener {
            val expertIntent = Intent(this, Experts::class.java)
            startActivity(expertIntent)
            finish()
        }

        binding.loginbtn.setOnClickListener {
            val loginIntent = Intent(this, SignIn_cnic::class.java)
            startActivity(loginIntent)
        }

        binding.searchViewContainer.setOnClickListener {
            val searchIntent = Intent(this, Jobs::class.java)
            startActivity(searchIntent)
            finish()
        }

        binding.searchIcon.setOnClickListener {
            val searchIntent = Intent(this, Jobs::class.java)
            startActivity(searchIntent)
            finish()
        }
    }
}

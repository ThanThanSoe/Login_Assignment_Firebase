package com.padc.login_assignment_firebase.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.padc.login_assignment_firebase.R
import kotlinx.android.synthetic.main.content_main.*

class ContentActivity:BaseActivity() {
    companion object{

        private const val USER_NAME = "User name"
        private const val USER_EMAIL = "User email"

        fun newIntent(context: Context, userName : String, userEmail : String): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(USER_NAME,userName)
            intent.putExtra(USER_EMAIL,userEmail)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)

        val userName = intent.getStringExtra(USER_NAME)
        val userEmail = intent.getStringExtra(USER_EMAIL)

        if(userName!=null){
            tv_username.text = "User name - $userName"
        }
        if(userEmail!=null){
            tv_email.text = "Email - $userEmail"
        }

        btn_ok.setOnClickListener {
            finish()
        }
    }
}
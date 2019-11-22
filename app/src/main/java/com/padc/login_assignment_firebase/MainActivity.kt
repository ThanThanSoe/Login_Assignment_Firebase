package com.padc.login_assignment_firebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.padc.login_assignment_firebase.Utils.RC_SIGN_IN
import com.padc.login_assignment_firebase.activities.BaseActivity

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private var googleSignInClient: GoogleSignInClient? = null
    val callbackManager = CallbackManager.Factory.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FacebookSdk.sdkInitialize(applicationContext)

        f_login_button.setReadPermissions("email")

        f_login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                handleFacebookAccessToken(result!!.accessToken)
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {

            }

        })
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        g_sign_in_button.setSize(SignInButton.SIZE_STANDARD)
        g_sign_in_button.setOnClickListener {
            signIn()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode,resultCode,data)

        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e:ApiException){
                Log.w(this.toString(),"Google sign in failed",e)
            }
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken){
        Log.d(this.toString(),"handleFaceAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful){
                    Log.d(this.toString(),"signInWithCredential:success")
                    val user = auth.currentUser
                }else {
                    Log.w(this.toString(),"signInwithCredential:failure",task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun signIn(){
        val signInIntent = googleSignInClient?.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount){
        Log.d(this.toString(), "FirebaseAuthWithGoogle: "+account.id!!)

        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(this.toString(), "signInWithCredential:success")
                    val user = auth.currentUser
                } else {
                    Log.w(this.toString(), "signInWithCredential:failure", task.exception)
                }
            }
    }
}

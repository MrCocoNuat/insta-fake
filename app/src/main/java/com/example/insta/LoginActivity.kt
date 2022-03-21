package com.example.insta

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.parse.ParseUser
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.Charset

class LoginActivity : AppCompatActivity() {

    private lateinit var actionBar : ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        actionBar = supportActionBar!!
        actionBar.title = "Sign In or Sign Up"

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val buttonSignIn = findViewById<Button>(R.id.buttonSignIn)
        val buttonSignUp = findViewById<Button>(R.id.buttonSignUp)

        buttonSignIn.setOnClickListener{
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            //try a login
            ParseUser.logInInBackground(username,password){ user, _ ->
                if (user != null){
                    // good! save credentials to a cache, report an OK and finish
                    saveCredentials(username,password)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                else{
                    // no good
                    Toast.makeText(this,"Incorrect credentials",Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonSignUp.setOnClickListener{
            val newUser = ParseUser()
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            newUser.username = username
            newUser.setPassword(password)

            //try a sign up
            newUser.signUpInBackground { e ->
                if (e == null){
                    // good! save credentials to a cache, report an OK and finish
                    saveCredentials(username,password)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                else{
                    // no good
                    Toast.makeText(this,"Sign up failed. Duplicate usernames?",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun getUserFile(): File {
        //not exposed to user's other apps unless rooted
        return File(filesDir,"user.dat")
    }
    private fun saveCredentials(username:String, password:String){
        Log.i(TAG, "Cached credentials for $username")
        val file = getUserFile()
        FileUtils.write(file,username+"\n", Charset.defaultCharset()) //overwrite
        FileUtils.write(file,password, Charset.defaultCharset(),true) //append
    }

    companion object{
        private const val TAG = "LoginActivity"
    }

}
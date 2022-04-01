package com.example.insta

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentTransaction
import com.parse.ParseFile
import com.parse.ParseQuery
import com.parse.ParseUser
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {

    private lateinit var iLoginActivity : Intent
    private lateinit var actionBar : ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iLoginActivity = Intent(this, LoginActivity::class.java)
        actionBar = supportActionBar!!
        actionBar.title = "Your Pictures"

        // check for presence of logged-in user in file
        val credentials = loadCredentials()
        //login now
        if (credentials == null) {
            //no cached user, onto LoginActivity
            Log.i(TAG, "No cached user. Redirecting to new login")
            doLogin()
        } else ParseUser.logInInBackground(credentials[0], credentials[1]) { user, _ ->
            if (user != null) {
                Log.i(TAG, "Auto-login as ${user.username} success!")
                //good! onto MainActivity proper
                mainActivityForReal(user)
            } else {
                Log.e(TAG, "Auto-login failed! Redirecting to new login")
                //bad! onto LoginActivity
                doLogin()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu):Boolean{
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menuLogout -> {
                Log.i(TAG,"Logging out")
                deleteUserFile()
                doLogin()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun doLogin(){
        getLoginActivityResult.launch(iLoginActivity)
    }

    private val getLoginActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            //login was successful,
            // onto MainActivity proper
            mainActivityForReal(ParseUser.getCurrentUser())
        }
        else{
            // no good! must be completed with OK to proceed
            doLogin()
        }
    }

    fun mainActivityForReal(user : ParseUser){
        Log.i(TAG, "Now logged in as ${user.username}")

        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_UPLOADEDBY)

        // no condition = find all
        query.findInBackground() { posts, e ->
            if (e == null) {
                for (post in posts){
                    Log.i(TAG,"Post: ${post.getCaption()} by ${post.getUploadedBy()?.username}")
                }
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.replace(R.id.placeholder, FeedFragment(posts))
                ft.commit()
            } else {
                Log.e(TAG, "Failed to load posts from server")
            }
        }

//        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
//        ft.replace(R.id.placeholder, CaptureFragment())
//        ft.commit()
    }








    private fun getUserFile(): File {
        //not exposed to user's other apps unless rooted
        return File(filesDir,"user.dat")
    }

    // returns null if no saved user
    private fun loadCredentials(): List<String>? {
        val file = getUserFile()
        try{
            val credentials = mutableListOf<String>()
            FileUtils.readLines(file, Charset.defaultCharset()).forEach { s : String ->
                credentials.add(s)
            }
            if (credentials.size == 2) {
                Log.i(TAG, "successfully loaded credentials from file")
                return credentials //success!
            }
            if (credentials.size == 0) {
                Log.i(TAG, "no credentials found, ready to create anew")
                return null
            }
            Log.e(TAG, "Credentials file corrupted! Deleting")
            file.delete()
            return null
        } catch(ioException: IOException){
            ioException.printStackTrace() //uh oh
            return null
        }
    }

    // uncache user credentials
    private fun deleteUserFile(){
        getUserFile().delete()
    }

    companion object {
        const val TAG = "MainActivity"

    }
}
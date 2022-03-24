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
            } else {
                Log.e(TAG, "Failed to load posts from server")
            }
        }

        findViewById<Button>(R.id.buttonPicture).setOnClickListener{
            onLaunchCamera()
        }
        findViewById<Button>(R.id.buttonSubmit).setOnClickListener{
            val caption = findViewById<EditText>(R.id.etCaption).text.toString()
            if (photoFile != null) {
                submitPost(caption, photoFile)
            }
            else{
                Toast.makeText(this, "Must include an image!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun submitPost(caption : String, image : File?){
        val post = Post()
        post.setCaption(caption)
        if (image != null) {
            post.setImage(ParseFile(photoFile))
        }
        post.setUploadedBy(ParseUser.getCurrentUser())

        post.saveInBackground(){ e ->
            if (e == null) {
                Log.i(TAG, "Successfully saved post")
                Toast.makeText(this, "Post submitted!", Toast.LENGTH_SHORT).show()
                findViewById<EditText>(R.id.etCaption).text.clear()
                findViewById<ImageView>(R.id.ivImage).setImageDrawable(null)
            }
            else Log.e(TAG, "Could not save post: ${e}")
        }
    }

    private val APP_TAG = "Insta"
    private val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    private val photoFileName = "photo.jpg"
    var photoFile: File? = null

    fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }
    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                val ivPreview: ImageView = findViewById(R.id.ivImage)
                ivPreview.setImageBitmap(takenImage)
            } else { // Result was a failure
                Log.e(TAG, "No picture taken!")
            }
        }
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
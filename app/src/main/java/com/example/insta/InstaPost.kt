package com.example.insta

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser
import java.io.File

@ParseClassName("Post")
class Post : ParseObject(){
    fun getCaption() : String = getString(KEY_CAPTION)!!
    fun setCaption(caption: String) = put(KEY_CAPTION, caption)
    fun getImage() : ParseFile? = getParseFile(KEY_IMAGE)
    fun setImage(image: ParseFile) = put(KEY_IMAGE, image)
    fun getUploadedBy() : ParseUser? = getParseUser(KEY_UPLOADEDBY)
    fun setUploadedBy(user: ParseUser) = put(KEY_UPLOADEDBY, user)
    companion object {
        const val KEY_CAPTION = "caption"
        const val KEY_UPLOADEDBY = "uploadedBy"
        const val KEY_IMAGE = "image"
    }
}
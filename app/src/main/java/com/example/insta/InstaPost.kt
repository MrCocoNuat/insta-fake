package com.example.insta

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@ParseClassName("Post")
class Post : ParseObject(){
    fun getCaption() : String = getString(KEY_CAPTION)!!
    fun setCaption(caption: String) = put(KEY_CAPTION, caption)
    fun getImage() : ParseFile? = getParseFile(KEY_IMAGE)
    fun setImage(image: ParseFile) = put(KEY_IMAGE, image)
    fun getUploadedBy() : ParseUser? = getParseUser(KEY_UPLOADEDBY)
    fun setUploadedBy(user: ParseUser) = put(KEY_UPLOADEDBY, user)
    fun getUploadedAt() : Date? = getDate(KEY_UPLOADEDAT) //doesn't work
    companion object {
        const val KEY_CAPTION = "caption"
        const val KEY_UPLOADEDBY = "uploadedBy"
        const val KEY_UPLOADEDAT = "createdAt"
        const val KEY_IMAGE = "image"

        @RequiresApi(Build.VERSION_CODES.O)
        fun relativeTimestamp(createdTime: String): String {
            val forwardFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss xx uuuu")
            val createdDateTime : LocalDateTime = LocalDateTime.parse(createdTime, forwardFormatter) //local

            val now : LocalDateTime = LocalDateTime.now(ZoneId.of("+0000")) //match timezones, yes this is hardcoded

            val secondsAgo = createdDateTime.until(now, ChronoUnit.SECONDS)
            if (secondsAgo < 0){
                Log.w("Tweet","Encountered a tweet from the future! Timezone error?")
            }
            if (secondsAgo < 5){
                return "now"
            }
            if (secondsAgo < 60){
                return "${secondsAgo}s"
            }
            if (secondsAgo < 60*60){
                return "${secondsAgo/60}m"
            }
            if (secondsAgo < 60*60*24){
                return "${secondsAgo/60/60}h"
            }
            if (secondsAgo < 60*60*24*30){
                return "${secondsAgo/60/60/24}d"
            }
            val reverseFormatter = DateTimeFormatter.ofPattern("d MMM uu")
            return createdTime.format(reverseFormatter)
        }
    }
}
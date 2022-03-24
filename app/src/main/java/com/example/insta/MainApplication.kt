package com.example.insta

import android.app.Application
import android.content.Intent
import org.apache.commons.io.FileUtils

import android.util.Log
import com.parse.Parse
import com.parse.ParseAnonymousUtils
import com.parse.ParseObject
import com.parse.ParseUser
import java.io.File
import java.io.IOException
import java.nio.charset.Charset


class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        ParseObject.registerSubclass(Post::class.java)

        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("eSAsvs3gSrZIplOXJyxhIfZYtUWa0frvTrCLAELz")
                .clientKey("Pb9wsO4ViWMqggrrOVovhWrwSewusblhVkbI2RyF")
                .server("https://parseapi.back4app.com")
                .build())


    }

    companion object {
        private const val TAG = "MainApplication"
    }


}
package com.chat.boot

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.chat.boot.utils.Base64Converter
import com.chat.boot.utils.RequestUtils
import java.io.ByteArrayOutputStream


class ChatActivity : AppCompatActivity() {

    private var fileEncoded : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Getting extra data
        val displayName:String = intent.getStringExtra("name").toString()
        this.title = "Hello $displayName"
        Log.d("Name to display", displayName)

        val btnSend = findViewById<Button>(R.id.button_send)
        val btnFile = findViewById<Button>(R.id.button_file)
        val btnCamera = findViewById<Button>(R.id.button_camera)

        val chatEntry = findViewById<EditText>(R.id.user_input)
        val chatInteractionContainer = findViewById<LinearLayout>(R.id.interactionContainer)



        // Camera selection
        btnCamera.setOnClickListener{
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 112)
        }

        // File selection
        btnFile.setOnClickListener{
            val intent = Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }

        // Send button
        btnSend.setOnClickListener {
            Log.d("Chat", chatEntry.text.toString())

            // Request to the rasa server
            val requestObject = RequestUtils()
            requestObject.sendMessage(this, chatEntry.text.toString(), chatInteractionContainer,
                this.fileEncoded
            )

            // Ready for the next interaction
            chatEntry.text.clear()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("requestCode", requestCode.toString())
        Log.d("resultCode", resultCode.toString())

        if (requestCode == 112 && resultCode == RESULT_OK && data != null){
            val bitmap: Bitmap = data.extras?.get("data") as Bitmap
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val byteArray: ByteArray = outputStream.toByteArray()
            Base64Converter.convertToBase64(byteArray).also { fileEncoded = it }
            Log.d("encoded from camera", fileEncoded.toString())
        }

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data ?: return
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedFile)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val byteArray: ByteArray = outputStream.toByteArray()
            Base64Converter.convertToBase64(byteArray).also { fileEncoded = it }
            Log.d("encoded", fileEncoded.toString())

        }
    }

}
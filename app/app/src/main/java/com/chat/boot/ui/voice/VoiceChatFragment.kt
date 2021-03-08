package com.chat.boot.ui.voice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chat.boot.R
import java.util.*

class VoiceChatFragment : Fragment(), RecognitionListener {

    private lateinit var notificationsViewModel: VoiceChatViewModel
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent
    private val permission = 100
    private lateinit var progressBar: ProgressBar
    private lateinit var returnedText: TextView
    private  lateinit var toggleButton: ToggleButton

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
                ViewModelProvider(this).get(VoiceChatViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_voice_chat, container, false)

        // Configuring voice chat elements
        toggleButton = root.findViewById(R.id.toggleVoiceButton)
        progressBar = root.findViewById(R.id.progressBar)
        returnedText = root.findViewById(R.id.speech_to_text)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.context);
        speechRecognizer.setRecognitionListener(this)

        progressBar.visibility = View.VISIBLE

        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            Log.i("Button pressed", isChecked.toString())
            if (isChecked) {
                progressBar.visibility = View.VISIBLE
                progressBar.isIndeterminate = true
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),
                        permission
                )

            } else {
                progressBar.isIndeterminate = false
                progressBar.visibility = View.VISIBLE
                speechRecognizer.stopListening()

                // TODO: Capture text and send it to server.
            }
        }

        return root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            permission ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager
                                .PERMISSION_GRANTED) {
                    Log.d("inside", permission.toString())
                    speechRecognizer.startListening(speechRecognizerIntent)
                } else {
                    Log.d("onRequestPermissionsResult", permission.toString())
                    Toast.makeText(this.context, "Permission Denied!",
                            Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onRmsChanged(rmsdB: Float) {
        progressBar.progress = rmsdB.toInt()
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Log.d("Ready", "I am ready for this")
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        TODO("Not yet implemented")
    }
    override fun onPartialResults(partialResults: Bundle?) {
        TODO("Not yet implemented")
    }
    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.d("lalala", "onEvent")
    }
    override fun onBeginningOfSpeech() {
        Log.i("logTag", "onBeginningOfSpeech")
        progressBar.isIndeterminate = false
        progressBar.max = 10

    }
    override fun onEndOfSpeech() {
        progressBar.isIndeterminate = true
        toggleButton.isChecked = false
    }

    override fun onError(error: Int) {
        val errorMessage: String = getErrorText(error)
        Log.d("logTag", "FAILED $errorMessage")
        returnedText.text = errorMessage
        toggleButton.isChecked = false
    }

    private fun getErrorText(error: Int): String {
        var message = ""
        message = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Didn't understand, please try again."
        }
        return message
    }

    override fun onResults(results: Bundle?) {
        Log.i("logTag", "onResults")
        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = ""
        if (matches != null) {
            for (result in matches) text = """
          $result
          """.trimIndent()
        }
        Log.d("text", text)
        returnedText.text = text
    }
}
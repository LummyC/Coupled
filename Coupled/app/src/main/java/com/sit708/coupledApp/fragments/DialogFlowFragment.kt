package com.sit708.coupledApp.fragments
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.dialogflow.v2.*
import com.google.common.collect.Lists
import com.sit708.coupledApp.R
import com.sit708.coupledApp.activities.MainCallback
import com.sit708.coupledApp.adapters.ChatAdapter
import com.sit708.coupledApp.databinding.FragmentDialogflowBinding
import com.sit708.coupledApp.helpers.SendMessageInBg
import com.sit708.coupledApp.models.Message
import java.util.*

class DialogFlowFragment : Fragment() {

    private var _binding: FragmentDialogflowBinding? = null
    private val binding get() = _binding!!
    private var callback: MainCallback? = null

    var chatAdapter: ChatAdapter? = null
    var messageList: MutableList<Message> = ArrayList()

    /*DialogFlow*/
    private var sessionsClient: SessionsClient? = null
    private var sessionName: SessionName? = null
    private val uuid = UUID.randomUUID().toString()
    private val TAG = "DialogFlowFragment"

    fun setCallback(callback: MainCallback) {
        this.callback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogflowBinding.inflate(layoutInflater, container, false)
        chatAdapter = context?.let { ChatAdapter(messageList, it) }

        binding.messagesView.apply {
            adapter = chatAdapter
            layoutManager=LinearLayoutManager(context)
        }

        binding.btSend.setOnClickListener {
            val message = binding.inputText.text.toString()
            if (message.isNotEmpty()) {
                messageList.add(Message(message, false))
                binding.inputText.setText("")
                binding.messagesView.adapter?.notifyDataSetChanged()
                binding.messagesView.layoutManager?.scrollToPosition(messageList.size - 1)
                sendMessageToBot(message)
            } else {
                Toast.makeText(activity, "Please enter text!", Toast.LENGTH_SHORT).show()
            }
        }
        setUpBot()
        return binding.root
    }

    private fun setUpBot() {
        try {
            val stream = this.resources.openRawResource(R.raw.credentials_json)
            val credentials = GoogleCredentials.fromStream(stream)
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"))
            val projectId = (credentials as ServiceAccountCredentials).projectId
            val settingsBuilder = SessionsSettings.newBuilder()
            val sessionsSettings = settingsBuilder.setCredentialsProvider(
                FixedCredentialsProvider.create(credentials)
            ).build()
            sessionsClient = SessionsClient.create(sessionsSettings)
            sessionName = SessionName.of(projectId, uuid)
            Log.d(TAG, "projectId : $projectId")
        } catch (e: Exception) {
            Log.d(TAG, "setUpBot: " + e.message)
        }
    }

    private fun sendMessageToBot(message: String) {
        val input = QueryInput.newBuilder()
            .setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build()
        SendMessageInBg(this, sessionName!!, sessionsClient!!, input).execute()
    }

    /*Indicates that Lint should ignore the specified warnings for the annotated element.*/
    @SuppressLint("NotifyDataSetChanged")
    fun callback(returnResponse: DetectIntentResponse?) {
        if (returnResponse != null) {
            val botReply = returnResponse.queryResult.fulfillmentText
            if (botReply.isNotEmpty()) {
                messageList.add(Message(botReply, true))
                chatAdapter?.notifyDataSetChanged()
                binding.messagesView.layoutManager?.scrollToPosition(messageList.size - 1)

            } else {
                Toast.makeText(activity, "something went wrong", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(activity, "failed to connect!", Toast.LENGTH_SHORT).show()
        }
    }
}

/*The Android lint tool is a static code analysis tool that checks your Android project source files for
potential bugs and optimization improvements for correctness, security, performance, usability, accessibility,
and internationalization.*/

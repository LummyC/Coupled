package com.sit708.coupledApp.helpers
import android.os.AsyncTask
import android.util.Log
import com.google.cloud.dialogflow.v2.*
import com.sit708.coupledApp.fragments.DialogFlowFragment

class SendMessageInBg(
    private val botReply: DialogFlowFragment,
    private val session: SessionName,
    private val sessionsClient: SessionsClient,
    private val queryInput: QueryInput
) : AsyncTask<Void?, Void?, DetectIntentResponse?>() {

    private val tag = "async"
    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg voids: Void?): DetectIntentResponse? {
        try {
            val detectIntentRequest = DetectIntentRequest.newBuilder()
                .setSession(session.toString())
                .setQueryInput(queryInput)
                .build()
            return sessionsClient.detectIntent(detectIntentRequest)
        } catch (e: Exception) {
            Log.d(tag, "doInBackground: " + e.message)
            e.printStackTrace()
        }
        return null
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(response: DetectIntentResponse?) {
        //handle return response here
        Log.d(tag, "onPostExecute:queryResult ${response?.queryResult} ")
        botReply.callback(response)
    }

}
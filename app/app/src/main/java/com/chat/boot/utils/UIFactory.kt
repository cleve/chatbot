package com.chat.boot.utils

import android.content.Context
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.chat.boot.R
import org.json.JSONArray
import org.json.JSONObject

class UIFactory {
    companion object{

        private fun  buttonCreator(ctx: Context, title: String, options: JSONArray): MutableMap<String, Any?>{
            val buttonContainer: MutableMap<String, Any?> = mutableMapOf()
            val buttons = mutableListOf<MutableMap<String, Any>>()
            val customLayout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            val mainLinearLayout = LinearLayout(ctx)
            val buttonsLinearLayout = LinearLayout(ctx)
            val titleView = TextView(ctx)
            customLayout.setMargins(40.dp, 10.dp, 10.dp, 5.dp)
            customLayout.gravity = Gravity.RIGHT
            mainLinearLayout.setPadding(10.dp, 10.dp, 10.dp, 10.dp)
            mainLinearLayout.setBackgroundResource(R.drawable.bot_responses)
            mainLinearLayout.orientation = LinearLayout.VERTICAL
            buttonsLinearLayout.orientation = LinearLayout.HORIZONTAL

            // Style
            mainLinearLayout.layoutParams = customLayout
            titleView.text = title
            mainLinearLayout.addView(titleView)

            // Exploring and adding the content
            for (i in 0 until options.length()) {
                val optionButton = Button(ctx)
                val item: JSONObject = options.getJSONObject(i)
                val option: JSONObject = item.get("value") as JSONObject
                val buttonContent: MutableMap<String, Any> = mutableMapOf()
                optionButton.text = option.getString("option")
                // References
                buttonContent["option"] = option.getString("option")
                buttonContent["action"] = option.getString("payload")
                buttonContent["object"] = optionButton
                buttons.add(buttonContent)
                buttonsLinearLayout.addView(optionButton)
                // Your code here
            }
            // Building the holder
            mainLinearLayout.addView(buttonsLinearLayout)
            buttonContainer["layout"] = mainLinearLayout
            buttonContainer["objects"] = buttons
            return buttonContainer
        }

        /*
         Object for buttons:
           "custom":{"type":"ui","layout":"buttons","title":"Make a decision",
           "data":[{"value":{"option":"yes","payload":"\/affirm"}},{"value":{"option":"no","payload":"\/deny"}}]}
         */
        fun elementCreator(ctx: Context, jsonObject: JSONObject): MutableMap<String, Any?> {
            // Check for UI
            val jsonElement: JSONObject = jsonObject.get("custom") as JSONObject
            if (!jsonElement.get("type").equals("ui")) {
                return mutableMapOf()
            }
            return if (jsonElement.get("layout").equals("buttons")) {
                val title = jsonElement.get("title") as String
                val jsonArray = jsonElement.get("data") as JSONArray
                return buttonCreator(ctx, title, options = jsonArray)
            } else {
                return mutableMapOf()
            }
        }
    }
}
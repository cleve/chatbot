package com.chat.boot.utils

import android.content.Context
import android.view.Gravity
import android.widget.*
import com.chat.boot.R
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject

class UIFactory {
    companion object{

        private fun imagesCreator(ctx: Context, title: String, options: JSONArray): MutableMap<String, Any?> {
            val imageContainer: MutableMap<String, Any?> = mutableMapOf()
            val images = mutableListOf<MutableMap<String, Any>>()

            // Layouts
            val scroll = HorizontalScrollView(ctx)
            scroll.isClickable = true
            val scrollParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
            scroll.layoutParams = scrollParams
            val imagesContainer = LinearLayout(ctx)
            val customLayout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
            val paramsImages = LinearLayout.LayoutParams(
                100.dp,
                100.dp
            )
            scroll.isHorizontalScrollBarEnabled = false
            paramsImages.setMargins(5.dp,10.dp,5.dp,10.dp)
            imagesContainer.orientation = LinearLayout.HORIZONTAL
            imagesContainer.layoutParams = customLayout
            imagesContainer.isClickable = true

            for (i in 0 until options.length()) {
                val imageContent: MutableMap<String, Any> = mutableMapOf()
                val imageViewContainer = ImageView(ctx)
                imageViewContainer.layoutParams = paramsImages
                imageViewContainer.isClickable = true
                imageViewContainer.setBackgroundResource(R.drawable.image_selector)
                imageViewContainer.setPadding(5.dp,5.dp,5.dp,5.dp)
                val item: JSONObject = options.getJSONObject(i)
                val url: String = item.getString("url")
                Picasso.get().load(url).into(imageViewContainer);
                imagesContainer.addView(imageViewContainer)

                // References
                imageContent["option"] = item.getString("id")
                imageContent["action"] = item.getString("name")
                imageContent["object"] = imageViewContainer
                images.add(imageContent)
            }
            // Building the holder
            scroll.addView(imagesContainer)
            imageContainer["layout"] = scroll
            imageContainer["objects"] = images
            return imageContainer
        }

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
                val buttonContent: MutableMap<String, Any> = mutableMapOf()

                // Set text
                optionButton.text = item.getString("option")

                // References
                buttonContent["option"] = item.getString("option")
                buttonContent["action"] = item.getString("payload")
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
            return when {
                jsonElement.get("layout").equals("buttons") -> {
                    val title = jsonElement.get("title") as String
                    val jsonArray = jsonElement.get("data") as JSONArray
                    buttonCreator(ctx, title, options = jsonArray)
                }
                jsonElement.get("layout").equals("image_selector") -> {
                    val title = jsonElement.get("title") as String
                    val jsonArray = jsonElement.get("data") as JSONArray
                    imagesCreator(ctx, title, options = jsonArray)
                }
                else -> {
                    mutableMapOf()
                }
            }
        }
    }
}
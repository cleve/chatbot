package com.chat.boot.utils

import android.util.Base64

class Base64Converter {

    companion object Factory {
        fun convertToBase64(attachment: ByteArray): String? {
            return Base64.encodeToString(attachment, Base64.NO_WRAP)
        }
    }
}
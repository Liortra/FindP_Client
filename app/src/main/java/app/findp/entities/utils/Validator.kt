package app.findp.entities.utils

import android.text.TextUtils
import android.util.Patterns

object Validator {
    @JvmStatic
    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    @JvmStatic
    fun isValidUserName(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target)
    }

    @JvmStatic
    fun isValidAvatarUrl(target: CharSequence?): Boolean {
        return target == null || !TextUtils.isEmpty(target)
    }
}
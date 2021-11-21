package com.zoup.android.note.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

object ViewUtils {
    var View.active: Boolean
        get() = isActivated
        set(newValue) {
            isActivated = newValue
            (this as? ViewGroup)?.children?.forEach { it.active=newValue }
        }

}
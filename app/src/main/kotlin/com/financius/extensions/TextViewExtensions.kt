package com.financius.extensions

import android.widget.EditText
import android.widget.TextView

fun TextView.setTextIfChanged(text: String?) {
    if (!this.text.toString().equals(text, ignoreCase = false)) {
        setText(text)
        (this as? EditText)?.setSelection(text?.length ?: 0)
    }
}
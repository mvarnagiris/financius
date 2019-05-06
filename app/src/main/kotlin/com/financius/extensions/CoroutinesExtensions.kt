package com.financius.extensions

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

fun <T> openPresenterChannel(sendBlock: SendChannel<T>.() -> Unit): ReceiveChannel<T> {
    val channel = presenterChannel<T>()
    sendBlock(channel)
    return channel
}

fun <T> presenterChannel(): Channel<T> {
    return Channel(Channel.UNLIMITED)
}

fun View.clicks(): ReceiveChannel<Unit> = openPresenterChannel {
    setOnClickListener { offer(Unit) }
    invokeOnClose { setOnClickListener(null) }
}

fun TextView.textChanges(): ReceiveChannel<String> = openPresenterChannel {
    val textChangeListener = TextChangeListener(this)
    addTextChangedListener(textChangeListener)
    this.invokeOnClose { removeTextChangedListener(textChangeListener) }
}

private class TextChangeListener(private val sendChannel: SendChannel<String>) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable?) {
        sendChannel.offer(s?.toString().orEmpty())
    }
}
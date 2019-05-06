package com.financius.data.models

interface Media {
    val uri: Uri
}

interface Image : Media

object NoImage : Image {
    override val uri: Uri get() = Uri("")
}
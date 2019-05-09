package com.financius.models

interface Media {
    val uri: Uri
}

interface Image : Media
data class RemoteImage(override val uri: Uri) : Image

object NoImage : Image {
    override val uri: Uri get() = Uri("")
}
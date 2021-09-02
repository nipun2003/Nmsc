package com.nipunapps.nmsc.other

import java.text.SimpleDateFormat
import java.util.*

fun setCurPlayerTimeToTextView(ms : Long) : String{
    val dataToConvert = (ms-(30*60*1000))
    val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    return dateFormat.format(dataToConvert)
}
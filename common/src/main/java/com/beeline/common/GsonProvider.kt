package com.beeline.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder


object GsonProvider {

    val gson = Gson()

    val gsonWithExpose = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()!!
}
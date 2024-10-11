package com.eminsasmaz.otoworldd.model

data class Vehicle(
    val licensePlate:String="",
    val brand:String="",
    val model:String="",
    val year:String="",
    var documentId: String = "",
    var selected:Boolean=false
)

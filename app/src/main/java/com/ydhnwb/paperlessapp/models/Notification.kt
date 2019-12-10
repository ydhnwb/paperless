package com.ydhnwb.paperlessapp.models

data class Notification(var id : Int, var from : String, var title : String, var description : String, var date : String, var image : String? = null)
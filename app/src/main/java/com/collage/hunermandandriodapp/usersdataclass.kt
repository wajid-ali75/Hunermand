package com.collage.hunermandandriodapp

data class Usersdataclass(
    val Cnic:String, val Name:String? = null, val Email:String? = null,
    val Ph_no: String? = null, val Gender: String? = null, val Address: String? = null,
    val Password:String?, val ConfirmPasswrd:String? )

data class ExpertUserDataclass(
    val Cnic:String, val Name:String? = null, val Email:String? = null,
    val Ph_no: String? = null, val Gender: String? = null, val Address: String? = null,
    val Password:String?,val confirmpassword:String?, val skills:String? = null,val experience:String? =null,
    val certificate:String? = null,val description:String?=null ,  val imageUrl: String,  )

data class PostJobDataclass(
    val jobtitle : String? = null,val jobtype:String? = null,
    val experience: String? = null, val gender:String? = null, val sallry:String? = null,
    val education:String? = null, val contact_person_name:String? = null,val phon_no:String? = null,
    val description: String? = null, val location:String?= null, val cnic : String? = null

)

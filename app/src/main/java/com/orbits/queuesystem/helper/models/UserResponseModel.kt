package com.orbits.queuesystem.helper.models

import kotlinx.serialization.Serializable

@Serializable
data class UserResponseModel(
    val `data`: UserDataModel? = null,
    var voice_selected: String? = null,
    var msg_en: String? = null,
    var msg_ar: String? = null,
    var voice_gender: String? = null,
)

@Serializable
data class UserDataModel(
    val appVersion: String? = null,
    val createdAt: String? = null,
    val deviceModel: String? = null,
    val deviceToken: String? = null,
    val deviceType: String? = null,
    val dob: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val id: Int? = null,
    val isCodeVerified: Boolean? = false,
    val osVersion: String? = null,
    var connection_code : String = ""
)

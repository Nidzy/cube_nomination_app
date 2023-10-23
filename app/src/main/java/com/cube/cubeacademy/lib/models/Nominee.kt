package com.cube.cubeacademy.lib.models

import com.google.gson.annotations.SerializedName

data class Nominee(
	@SerializedName("nominee_id") val nomineeId: String,
	@SerializedName("first_name") val firstName: String,
	@SerializedName("last_name") val lastName: String
)

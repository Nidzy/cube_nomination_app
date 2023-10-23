package com.cube.cubeacademy.lib.api

import com.cube.cubeacademy.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthTokenInterceptor : Interceptor {
	override fun intercept(chain: Interceptor.Chain): Response {
		var request = chain.request()

		if (request.headers("No-Authentication").isEmpty()) {
			val finalToken = "Bearer ${BuildConfig.AUTH_TOKEN}"
			request = request.newBuilder().addHeader("Authorization", finalToken).build()
		}
		return chain.proceed(request)
	}
}
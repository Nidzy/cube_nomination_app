package com.cube.cubeacademy.lib.di

import com.cube.cubeacademy.lib.api.ApiService
import javax.inject.Inject

class Repository @Inject constructor(val api: ApiService) {
	// TODO: Add additional code if you need it

	/*
	*  Updated getAllNomination, getAllNominees and createNomination functions and its return type and handled API call through ViewModel
	*  modified return type and moved api call logic to viewmodel in order to handle API response with success method and livedata
	*
	* */
		// TODO: Write the code to fetch the list nominations from the api
	suspend fun getAllNominations() = api.getAllNominations()


		// TODO: Write the code to fetch list of all nominees from the api
	suspend fun getAllNominees() = api.getAllNominees()

	// TODO: Write the code to create a new nomination using the api
	suspend fun createNomination(nomineeId: String, reason: String, process: String) = api.createNomination(nomineeId, reason, process)


}
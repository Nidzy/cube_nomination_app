package com.cube.cubeacademy.lib.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cube.cubeacademy.lib.api.ApiService
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.models.DataWrapper
import com.cube.cubeacademy.lib.models.Nomination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreateNominationViewModel @Inject constructor(val api: ApiService) : ViewModel() {

    private val repository = Repository(api)
    private val apiResponse = MutableLiveData<DataWrapper<Nomination>>()
    val apiResponseGet: LiveData<DataWrapper<Nomination>> get() = apiResponse

    // Function to create a new nomination using the provided nominee ID, reason, and process
// The function uses coroutines to handle the asynchronous API call
    fun createNomination(nomineeId: String, reason: String, process: String) {

        // Launch a new coroutine on the viewModelScope
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                // Call the repository to create the nomination with the provided parameters
                repository.createNomination(nomineeId, reason, process)
            }
            // Set the result of the API call in the live data variable for observation
            apiResponse.postValue(result)

        }
    }
}
package com.cube.cubeacademy.lib.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.cube.cubeacademy.lib.api.ApiService
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.models.DataWrapper
import com.cube.cubeacademy.lib.models.Nomination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Inject

@HiltViewModel
class NominationViewModel @Inject constructor(val api: ApiService) : ViewModel() {

    private val repository = Repository(api)
    private val job = SupervisorJob()

    private val coroutineContext = Dispatchers.IO + job

    // Function to retrieve all nominations as LiveData wrapped in DataWrapper
    fun getAllNomination(): LiveData<DataWrapper<List<Nomination>>> {

        // Return a LiveData object within a coroutine context
        return liveData(context = coroutineContext) {
            // Call the repository to fetch the list of all nominations
            val response = repository.getAllNominations()
            // Check if the response is successful and the body is not null
            if (response.isSuccessful && response.body() != null) {
                // Emit the list of nominations wrapped in DataWrapper to observers
                emit(response.body()!!)
            } else {
                Log.d("TAG", "Failed")
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancel()
    }
}
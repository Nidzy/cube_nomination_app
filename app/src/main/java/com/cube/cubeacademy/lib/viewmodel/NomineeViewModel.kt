package com.cube.cubeacademy.lib.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.cube.cubeacademy.lib.api.ApiService
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.models.DataWrapper
import com.cube.cubeacademy.lib.models.Nominee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Inject

@HiltViewModel
class NomineeViewModel @Inject constructor(val api: ApiService) : ViewModel() {

    private val repository = Repository(api)
    private val job = SupervisorJob()

    private val coroutineContext = Dispatchers.IO + job

    // Function to fetch the list of nominees as LiveData wrapped in DataWrapper
    fun getNomineeList() : LiveData<DataWrapper<List<Nominee>>> {

        // Return a LiveData object within a coroutine context
        return liveData(context = coroutineContext) {
            // Call the repository to get the list of all nominees
            val response = repository.getAllNominees()
            // Check if the response is successful and the body is not null
            if (response.isSuccessful && response.body() != null) {
                // Emit the list of nominees wrapped in DataWrapper to observers
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
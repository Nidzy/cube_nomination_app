package com.cube.cubeacademy.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cube.cubeacademy.R
import com.cube.cubeacademy.databinding.ActivityMainBinding
import com.cube.cubeacademy.lib.adapters.NominationsRecyclerViewAdapter
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.util.NetworkUtils
import com.cube.cubeacademy.lib.util.ToastUtil
import com.cube.cubeacademy.lib.viewmodel.NominationViewModel
import com.cube.cubeacademy.lib.viewmodel.NomineeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var repository: Repository
    lateinit var nominationViewModel: NominationViewModel
    lateinit var nomineeViewModel: NomineeViewModel
    lateinit var adapter: NominationsRecyclerViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        populateUI()
    }

    override fun onResume() {
        super.onResume()
        // Initialize nomination data
        initNominationData()

    }

    // Function to populate the UI with data and set up button actions
    private fun populateUI() {
        /**
         * TODO: Populate the UI with data in this function
         * 		 You need to fetch the list of user's nominations from the api and put the data in the recycler view
         * 		 And also add action to the "Create new nomination" button to go to the CreateNominationActivity
         */

        // Initialize ViewModel instances
        nominationViewModel = ViewModelProvider(this).get(NominationViewModel::class.java)
        nomineeViewModel = ViewModelProvider(this).get(NomineeViewModel::class.java)
        // Set up RecyclerView
        binding.nominationsList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            isNestedScrollingEnabled = true
            setHasFixedSize(true)
            setItemViewCacheSize(0)
        }
        adapter = NominationsRecyclerViewAdapter()
        binding.nominationsList.adapter = adapter

        binding.createButton.setOnClickListener {
            val intent = Intent(this, CreateNominationActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to initialize nomination data and set up the observer
    @SuppressLint("NotifyDataSetChanged")
    private fun initNominationData() {

        if (NetworkUtils.isNetworkAvailable(this)) {
            nominationViewModel.getAllNomination()
                .observe(this, Observer { nominationData ->
                    if (nominationData.data.isEmpty()) {
                        binding.emptyContainer.visibility = View.VISIBLE
                        binding.nominationsList.visibility = View.GONE
                    } else {
                        binding.emptyContainer.visibility = View.GONE
                        binding.nominationsList.visibility = View.VISIBLE
                        adapter.addData(nominationData.data)
                        adapter.notifyDataSetChanged()
                        nomineeViewModel.getNomineeList().observe(this, Observer { nomineeData ->
                            if (nomineeData != null) {
                                adapter.setNomineesList(nomineeData.data)
                                adapter.notifyDataSetChanged()
                            } else {
                                Log.d("nomineeDataInMain", "Null Data Received")
                            }
                        })


                    }
                })
        } else {
            ToastUtil.showToast(this, getString(R.string.error_connect_internet))

        }

    }

}
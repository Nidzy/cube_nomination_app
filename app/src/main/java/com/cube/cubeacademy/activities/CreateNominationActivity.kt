package com.cube.cubeacademy.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cube.cubeacademy.R
import com.cube.cubeacademy.databinding.ActivityCreateNominationBinding
import com.cube.cubeacademy.lib.di.Repository
import com.cube.cubeacademy.lib.models.Nominee
import com.cube.cubeacademy.lib.util.NetworkUtils
import com.cube.cubeacademy.lib.util.ToastUtil
import com.cube.cubeacademy.lib.viewmodel.CreateNominationViewModel
import com.cube.cubeacademy.lib.viewmodel.NomineeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateNominationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNominationBinding

    @Inject
    lateinit var repository: Repository

    private var nomineeId: String = ""
    private var reason: String = ""
    private var process: String = ""
    private lateinit var nomineeList: List<Nominee>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateNominationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        populateUI()
    }

    private fun populateUI() {
        /**
         * TODO: Populate the form after having added the views to the xml file (Look for TODO comments in the xml file)
         * 		 Add the logic for the views and at the end, add the logic to create the new nomination using the api
         * 		 The nominees drop down list items should come from the api (By fetching the nominee list)
         */

        val nomineeViewModel = ViewModelProvider(this)[NomineeViewModel::class.java]
        val createNominationViewModel =
            ViewModelProvider(this)[CreateNominationViewModel::class.java]

        if (NetworkUtils.isNetworkAvailable(this)) {
            // Fetch data for the nominee list from the ViewModel
            nomineeViewModel.getNomineeList()
                .observe(this, Observer { nomineeData ->
                    if (nomineeData != null) {
                        // Load the data into the spinner
                        loadDataIntoSpinner(nomineeData.data)
                    }
                })

        } else {
            ToastUtil.showToast(this, getString(R.string.error_connect_internet))

        }

        // Perform operations when the user selects an item from the spinner
        binding.spinnerCube.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedNomineeName =
                    binding.spinnerCube.selectedItem as String
                nomineeId = getNomineeIdFromName(selectedNomineeName) // Function to find the ID
                enableSubmitButton()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case where nothing is selected
                nomineeId = ""
            }
        }
        binding.radioProcess.setOnCheckedChangeListener { group, checkedId ->
            enableSubmitButton()
            when (checkedId) {
                R.id.radio_very_unfair -> {
                    process = "very_unfair"
                }

                R.id.radio_unfair -> {
                    process = "unfair"
                }

                R.id.radio_fair -> {
                    process = "fair"
                }

                R.id.radio_not_sure -> {
                    process = "not_sure"
                }

                R.id.radio_very_fair -> {
                    process = "very_fair"
                }

            }
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                when {
                    s!!.isNotEmpty() -> {
                        enableSubmitButton()
                    }

                    else -> {
                        binding.submitButton.isEnabled = false
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }
        }
        binding.etReason.addTextChangedListener(textWatcher)

        binding.submitButton.setOnClickListener {
            val reasonText = binding.etReason
            reason = reasonText.text.toString()


            if (NetworkUtils.isNetworkAvailable(this)) {
                lifecycleScope.launch {
                    createNominationViewModel.createNomination(nomineeId, reason, process)
                }
            } else {
                ToastUtil.showToast(this, getString(R.string.error_connect_internet))
            }

        }
        // Observe the API response for the new nomination creation
        createNominationViewModel.apiResponseGet.observe(this) {
            when {
                it.data != null -> {
                    // API call was successful
                    // Redirect the user to the next screen
                    val intent = Intent(this, NominationSubmittedActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }

                else -> {
                    // API call failed
                    // error case
                    ToastUtil.showToast(this, getString(R.string.error_in_creating_nomination))
                }
            }
        }
        binding.backButton.setOnClickListener {

            val isEditTextFilled = binding.etReason.text.toString().isNotEmpty()
            val isSpinnerSelected = binding.spinnerCube.selectedItemPosition != 0
            val isRadioButtonChecked = binding.radioProcess.checkedRadioButtonId != -1


            // Show a bottom sheet dialog to confirm leaving when user has already started to fill
            if (isEditTextFilled || isSpinnerSelected || isRadioButtonChecked) {
                val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_view, null)
                val dialog = BottomSheetDialog(this)
                dialog.setContentView(bottomSheetView)
                dialog.window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    ?.setBackgroundResource(android.R.color.transparent)
                dialog.show()

                val leaveButton: AppCompatButton = bottomSheetView.findViewById(R.id.sure_button)
                val cancelButton: AppCompatButton = bottomSheetView.findViewById(R.id.cancel_button)
                leaveButton.setOnClickListener {
                    dialog.dismiss() // Close the bottom sheet on button click
                    navigateToMain()
                }
                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
            } else {
                navigateToMain()
            }

        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun loadDataIntoSpinner(data: List<Nominee>) {
        nomineeList = data
        val nomineeNames = data.map { it.firstName + " " + it.lastName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nomineeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Adding label "Select Option" to the first item
        val defaultOption = "Select Option"
        val itemListWithDefault = listOf(defaultOption) + nomineeNames

        binding.spinnerCube.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            itemListWithDefault
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    /*
    * returns id based on nominee name to pass for createnomination API
    * */
    private fun getNomineeIdFromName(name: String): String {
        for (nominee in nomineeList) {
            if (nominee.firstName + " " + nominee.lastName == name) {
                return nominee.nomineeId
            }
        }
        return "0"
    }

    /*
    * Enables the submit button based on the conditions
    * Disables the submit button if any of the conditions are not met
    * */
    private fun enableSubmitButton() {

        val isEditTextFilled = binding.etReason.text.toString().isNotEmpty()
        val isSpinnerSelected = binding.spinnerCube.selectedItemPosition != 0
        val isRadioButtonChecked = binding.radioProcess.checkedRadioButtonId != -1

        binding.submitButton.isEnabled =
            isEditTextFilled && isSpinnerSelected && isRadioButtonChecked
    }
}
package com.cube.cubeacademy.lib.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cube.cubeacademy.databinding.ViewNominationListItemBinding
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee

class NominationsRecyclerViewAdapter(var nominationsList: MutableList<Nomination> = mutableListOf()) :
    ListAdapter<Nomination, NominationsRecyclerViewAdapter.ViewHolder>(DIFF_CALLBACK) {

    // Local list to store nominee data
    private var nomineesList: List<Nominee> = mutableListOf()

    class ViewHolder(val binding: ViewNominationListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewNominationListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*
        *
        * TODO: This should show the nominee name instead of their id! Where can you get their name from?
        * */
        if (nominationsList.size > 0) {
            with(holder) {
                with(nominationsList[position]) {
                    binding.name.text = fetchNomineeName(nomineeId)
                    binding.reason.text = reason
                }
            }

        }
    }

    override fun getItemCount(): Int {
        // Return the size of the nominationsList
        return nominationsList.size
    }


    fun addData(listOfNomination: List<Nomination>) {
        // clear existing and add all the items from the new list
        nominationsList = mutableListOf()
        nominationsList.addAll(listOfNomination)
    }

    /*
    * assign nominee data to local nominee list
    * */
    fun setNomineesList(nominees: List<Nominee>) {
        this.nomineesList = nominees
    }

    /*
    * fetch nominee name based on its id
    * */
    private fun fetchNomineeName(nomineeId: String): String {
        // Find the nominee based on the provided nomineeId
        val nominee = nomineesList.find { it.nomineeId == nomineeId }
        // Return the full name of the nominee if found, otherwise return a default message
        return nominee?.let { "${it.firstName} ${it.lastName}" } ?: "Nominee Not Found"
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Nomination>() {
            override fun areItemsTheSame(oldItem: Nomination, newItem: Nomination) =
                oldItem.nominationId == newItem.nominationId

            override fun areContentsTheSame(oldItem: Nomination, newItem: Nomination) =
                oldItem == newItem
        }
    }
}
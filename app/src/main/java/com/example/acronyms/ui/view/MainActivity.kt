package com.example.acronyms.ui.view

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acronyms.R
import com.example.acronyms.data.api.ApiHelper
import com.example.acronyms.data.api.RetrofitBuilder
import com.example.acronyms.data.model.AcronymsResponse
import com.example.acronyms.databinding.ActivityMainBinding
import com.example.acronyms.ui.adapter.AcronymsAdapter
import com.example.acronyms.ui.base.ViewModelFactory
import com.example.acronyms.ui.viewmodel.MainViewModel
import com.example.acronyms.utils.AcronymsUtil
import com.example.acronyms.utils.Logger
import com.example.acronyms.utils.Status
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel : MainViewModel
    private lateinit var adapter : AcronymsAdapter
    val filtered = mutableListOf<String>()
    internal var activityMainBinding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupViewModel()
        setupUI()
    }

    /**
     * Set viewModel
     */
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MainViewModel::class.java)
    }

    private fun setupUI() {
        activityMainBinding?.recyclerView?.layoutManager = LinearLayoutManager(this)
        adapter = AcronymsAdapter(arrayListOf())
        activityMainBinding?.recyclerView?.addItemDecoration(
            DividerItemDecoration(
                activityMainBinding?.recyclerView?.context,
                (activityMainBinding?.recyclerView?.layoutManager as LinearLayoutManager).orientation
            )
        )
        activityMainBinding?.recyclerView?.adapter = adapter

        activityMainBinding?.editText?.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun afterTextChanged(s : Editable?) {
                if (AcronymsUtil.isNetworkAvailable(this@MainActivity)) {
                    setupObservers(s.toString())
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Network connection is not available",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    resetAdapter(filtered)
                }
            }

            override fun beforeTextChanged(
                s : CharSequence?,
                start : Int,
                count : Int,
                after : Int
            ) {
            }

            override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {

            }
        })
    }

    private fun setupObservers(acronym_input : String?) {
        if (acronym_input != null) {
            viewModel.getAcronyms(acronym_input).observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            Logger.info("MainActivity", "service is getting success")
                            activityMainBinding?.recyclerView?.visibility = View.VISIBLE
                            activityMainBinding?.progressBar?.visibility = View.GONE
                            updateAdapter(resource.data)
                        }
                        Status.ERROR -> {
                            Logger.info("MainActivity", "service is getting error")
                            activityMainBinding?.recyclerView?.visibility = View.VISIBLE
                            activityMainBinding?.progressBar?.visibility = View.GONE
                            Logger.info(
                                "MainActivity",
                                "Message - ${it.message} , Status - ${it.status}"
                            )
                            resetAdapter(filtered)
                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                        }
                        Status.LOADING -> {
                            Logger.info(
                                "MainActivity",
                                "service is getting response, loading stage"
                            )
                            activityMainBinding?.progressBar?.visibility = View.VISIBLE
                            activityMainBinding?.recyclerView?.visibility = View.GONE
                        }
                    }
                }
            })
        }
    }

    private fun updateAdapter(acronymsResponse : List<AcronymsResponse>?) {
        val filtered = getAdapterAcronymValue(acronymsResponse)
        if (filtered.size == 0) {
            Toast.makeText(this, "No Result found", Toast.LENGTH_LONG).show()
        }
        resetAdapter(filtered)
    }

    /**
     *get abbreviation value from service response
     */
    private fun getAdapterAcronymValue(acronymsResponse : List<AcronymsResponse>?) : MutableList<String> {
        val filtered = mutableListOf<String>()
        if (acronymsResponse != null && acronymsResponse.size > 0) {
            acronymsResponse.get(0).lfs.forEach { element -> element.lf.let { filtered.add(it) } }
            Logger.info("MainActivity", Gson().toJson(filtered))
        }
        return filtered
    }

    /**
     * Reset adapter when error occurred & no result found
     */
    private fun resetAdapter(filtered : MutableList<String>) {
        adapter.apply {
            addAcronym(filtered)
            notifyDataSetChanged()
        }
    }
}

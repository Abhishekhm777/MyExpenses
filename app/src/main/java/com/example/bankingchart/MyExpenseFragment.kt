package com.example.bankingchart

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androiddata.ui.main.RecyclerAdapter
import com.example.bankingchart.databinding.FragmentMyExpenseBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.sms_layout.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyExpenseFragment : Fragment() {
    private val TAG = "MyExpenseFragment"
    lateinit var binding: FragmentMyExpenseBinding
    private lateinit var adapter: RecyclerAdapter
    private val PERMISSION_REQUEST_CODE = 1
    private val viewModel by viewModel<MainViewModel>()
    private var checkedItem = 0
    private lateinit var navcontroler: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyExpenseBinding.inflate(inflater, container, false)
        binding.swipe.isRefreshing = true

        if (checkPergmission()) {
            getAllSms()
        } else {
            requestPermission()
        }
        binding.swipe.also {
            it.setOnRefreshListener {
                getAllSms()
            }
        }

        navcontroler = Navigation.findNavController(
            requireActivity(), R.id.nav_host
        )

        return binding.root
    }

    private fun getAllSms() {
        viewModel.allsms.observe(this, Observer {
            binding.swipe.isRefreshing = false
            adapter = RecyclerAdapter(it)
            Log.e(TAG, "getAllSms: "+it.size )
            binding.apply {
                this.recyclerView.adapter = adapter
            }
        })
    }

    private fun requestPermission() {
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(Manifest.permission.READ_SMS),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkPergmission(): Boolean {
        val result = activity?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.READ_SMS
            )
        }
        return result == PackageManager.PERMISSION_GRANTED
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAllSms()
            } else {
                Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_menu -> {
                showFilterDialog()
            }

            android.R.id.home -> {
                navcontroler.navigateUp()
            }

        }
        return true
    }

    private fun showFilterDialog() {
        val filterItems = arrayOf("All", "Income", "Expense")

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.filter_by)
            .setPositiveButton(R.string.ok) { _, _ ->
                if (checkedItem != 0) adapter.filter.filter(filterItems[checkedItem])
                else adapter.filter.filter("")

            }
            .setSingleChoiceItems(filterItems, checkedItem) { _, which ->
                checkedItem = which
            }

            .show()
    }


}
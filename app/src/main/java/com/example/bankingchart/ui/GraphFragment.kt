package com.example.bankingchart.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.bankingchart.R
import com.example.bankingchart.data.SharedViewModel
import com.example.bankingchart.databinding.FragmentGraphBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.fragment_graph.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class GraphFragment : Fragment() {
    lateinit var binding: FragmentGraphBinding
    private val viewModel by viewModel<SharedViewModel>()
    private lateinit var navcontroler: NavController
    private val PERMISSION_REQUEST_CODE = 1
    private val TAG = "GraphFragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGraphBinding.inflate(inflater, container, false)
        binding.swipe?.isRefreshing = true
        binding.swipe?.setOnRefreshListener {
            binding.swipe?.isRefreshing = false
        }
        activity?.title = R.string.home_title.toString()
        if (checkPermission()) {
            getData()
        } else {
            requestPermission()
        }

        navcontroler = Navigation.findNavController(
            requireActivity(), R.id.nav_host
        )

        binding.fab.setOnClickListener {
            navcontroler.navigate(R.id.myExpenseFragment2)
        }

        return binding.root
    }

    private fun getData() {
        viewModel.getSms().observe(this, Observer {
            val incomeList = mutableListOf<Double>()
            val expenseList = mutableListOf<Double>()
            val totalList = mutableListOf<Double>()
            it.map { model ->
                model.amount?.let { it1 -> totalList.add(it1) }
                when (model.type) {
                    "Income" -> model.amount?.let { it1 -> incomeList.add(it1) }
                    else -> model.amount?.let { it1 -> expenseList.add(it1) }
                }
            }

            val income = incomeList.sumByDouble { it }.toFloat()
            val expense = expenseList.sumByDouble { it }.toFloat()
            val total = totalList.sumByDouble { it }.toFloat()

            setUpChart(income, expense, total)

        })


    }


    private fun setUpChart(income: Float, expense: Float, total: Float) {
        binding.swipe?.isRefreshing = false
        val pieEntries = ArrayList<PieEntry>()
        pieEntries.add(PieEntry(total, "Total transactions"))
        pieEntries.add(PieEntry(income, "Income"))
        pieEntries.add(PieEntry(expense, "Expenses"))

        val l: Legend = binding.pieChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        val colors = java.util.ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)

        colors.add(ColorTemplate.getHoloBlue())

        val pieDataSet = PieDataSet(pieEntries, "")

        val pieData = PieData(pieDataSet)
        pieDataSet.valueTextColor = Color.BLUE
        pieDataSet.valueTextSize = 10f
        pieDataSet.colors = colors
        pieDataSet.sliceSpace = 2f
        binding.pieChart.setDrawCenterText(true)
        binding.pieChart.centerText = "All transactions"
        binding.pieChart.data = pieData
        binding.pieChart.highlightValues(null)
        binding.pieChart.animateY(1400, Easing.EaseInOutQuad)
        binding.pieChart.setEntryLabelColor(Color.GRAY)
        binding.pieChart.invalidate()

    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.READ_SMS),
            PERMISSION_REQUEST_CODE
        )

    }

    private fun checkPermission(): Boolean {
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
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getData()
            } else {
                binding.swipe?.isRefreshing = true
                Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}



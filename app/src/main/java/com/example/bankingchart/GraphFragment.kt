package com.example.bankingchart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.bankingchart.databinding.FragmentGraphBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.String.format


class GraphFragment : Fragment() {
    lateinit var binding: FragmentGraphBinding
    lateinit var pieEntries: ArrayList<PieEntry>
   private val viewModel by viewModel<MainViewModel>()
    private lateinit var navcontroler: NavController
    private  val TAG = "GraphFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGraphBinding.inflate(inflater, container, false)
        activity?.title = R.string.home_title.toString()

        GlobalScope.launch(Dispatchers.Main){ // creates worker thread

                getEntries()

        }



        // add a lot of colors


        navcontroler = Navigation.findNavController(
            requireActivity(), R.id.nav_host
        )

        binding.fab.setOnClickListener {
            navcontroler.navigate(R.id.myExpenseFragment2)
        }

        return binding.root
    }

    private suspend fun getEntries() {
        viewModel.allsms.observe(this, Observer {
          val incomeList = mutableListOf<Double>()
            val expenseList = mutableListOf<Double>()
            val totalList = mutableListOf<Double>()
            it.map {model ->
                if(model.type.equals("Income")){
                    model.amount?.let { it1 -> incomeList.add(it1) }

                }
                if(model.type.equals("Expense")){
                    model.amount?.let { it1 -> expenseList.add(it1) }

                }
                model.amount?.let { it1 -> totalList.add(it1) }
            }

            val  income= format("%.3f",incomeList.sumByDouble { it }).toFloat()
            val expense= format("%.3f",expenseList.sumByDouble { it }).toFloat()
          val   total= format("%.3f",totalList.sumByDouble { it }).toFloat()

            setUpChart(income,expense,total)

        })


    }


    fun setUpChart(income: Float, expense: Float, total: Float) {
        pieEntries = ArrayList<PieEntry>()
        pieEntries.add(PieEntry(total, "Total Transactions"))
        pieEntries.add(PieEntry(income, "Income"))
        pieEntries.add(PieEntry(expense, "Expenses"))


        val colors = java.util.ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)

        colors.add(ColorTemplate.getHoloBlue())

        val pieDataSet = PieDataSet(pieEntries, "")
        val pieData = PieData(pieDataSet)
        binding.pieChart.setData(pieData)
        pieDataSet.setColors(colors)
        pieDataSet.sliceSpace = 2f
        binding.pieChart.setDrawCenterText(true)
        binding.pieChart.centerText = "All transactions"
        pieDataSet.valueTextColor = Color.BLUE
        pieDataSet.valueTextSize = 10f
        pieDataSet.sliceSpace = 5f
        binding.pieChart.invalidate()

    }
}



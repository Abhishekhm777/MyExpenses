package com.example.androiddata.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bankingchart.R
import com.example.bankingchart.SmsModel


class RecyclerAdapter(
                      val contents:List<SmsModel>
                     ):
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(),Filterable {

    var searchableList: ArrayList<SmsModel> = contents as ArrayList<SmsModel>
    private var onNothingFound: (() -> Unit)? = null
    override fun getItemCount() = searchableList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.sms_layout, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val content = searchableList[position]
        with(holder) {
            title.text = content.type
            subtitle.text = content.body
        }

    }


    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
        val subtitle = itemView.findViewById<TextView>(R.id.subtitle)

    }



    override fun getFilter(): Filter {
        return object : Filter() {
            private val filterResults = FilterResults()
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                if (constraint.isNullOrBlank()) {
                  searchableList = contents as ArrayList<SmsModel>
                } else {
                    val filtered =ArrayList<SmsModel>()
                    val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }
                    for (item in contents) {
                        if(item.type?.contains(filterPattern,true)!!){
                            filtered.add(item)
                        }
                    }
                   searchableList  = filtered
                }
                return filterResults.also {
                    it.values = searchableList
                }
            }
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
               if(results?.values!=null) {
                   searchableList = results?.values as ArrayList<SmsModel>
                   notifyDataSetChanged()
               }


            }
        }

    }
}
package com.example.bankingchart.data

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.example.bankingchart.model.SmsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern


class Repository(private val app: Application) {
    private  val TAG = "Repository"
    val allsms = MutableLiveData<List<SmsModel>>()

    init {
        Log.e(TAG, "init: " )
        CoroutineScope(Dispatchers.IO).launch {
            getAllSms()
        }

    }

    @WorkerThread
   suspend fun  getAllSms() {
        Log.e(TAG, "getAllSms: " )
        val listOfSms: MutableList<SmsModel> = ArrayList()
        val message: Uri = Uri.parse("content://sms/")
        val cr: ContentResolver = app.contentResolver
        val c: Cursor? = cr.query(message, null, null, null, null)
        val regexForAccount: Pattern = Pattern.compile("[0-9]*[Xx\\*]*[0-9]*[Xx\\*]+[0-9]{3,}")

        val regexForAmount =
            Pattern.compile("[rR][sS][^\"]*[,\\d]+\\.?\\d{0,2}|[iI][nN][rR]\\.?\\s*[,\\d]+\\.?\\d{0,2}")
        val price = Pattern.compile("[0-9]{1,13}(\\.[0-9]*)?")
        val totalSMS: Int = c?.count ?: 0
        if (c?.moveToFirst() == true) {
            for (i in 0 until totalSMS) {

                val body = c?.getString(c.getColumnIndexOrThrow("body"))
                val m: Matcher = regexForAccount.matcher(body)

                if (m.find()) {
                    val amm: Matcher = regexForAmount.matcher(body)
                    if (amm.find()) {
                        val amount = amm.group(0)
                        Log.e(TAG, "getAllSms: "+amount )
                        if (body?.contains("deposited", true)!!
                            || body?.contains("credited", true)
                        ) {
                            val priceMatcher = price.matcher(amount.replace(",", ""))
                            if (priceMatcher.find()) {
                                val st = priceMatcher.group(0)
                                listOfSms.add(
                                    SmsModel(
                                        "Income",
                                        body,
                                        st.toDoubleOrNull()
                                    )
                                )
                            }


                        } else if (body?.contains("withdrawn", true)!!
                            || body?.contains("debited", true)!!
                            || body?.contains("spent", true)!!
                        ) {

                            val priceMatcher = price.matcher(amount.replace(",", ""))
                            if (priceMatcher.find()) {
                                val st = priceMatcher.group(0)
                                listOfSms.add(
                                    SmsModel(
                                        "Expense",
                                        body,
                                        st.toDoubleOrNull()
                                    )
                                )
                            }
                        }
                    }
                }
                c?.moveToNext()
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c?.close()
        allsms.postValue(listOfSms)


    }

}
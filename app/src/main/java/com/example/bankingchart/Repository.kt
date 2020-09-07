package com.example.bankingchart

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern


class Repository(private val app: Application) {

    val allsms = MutableLiveData<List<SmsModel>>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            getAllSms()
        }

    }

    @WorkerThread
    fun getAllSms() {
        val lstSms: MutableList<SmsModel> = ArrayList()
        val message: Uri = Uri.parse("content://sms/")
        val cr: ContentResolver = app.getContentResolver()
        val c: Cursor? = cr.query(message, null, null, null, null)
        // app.startManagingCursor(c)
        var p: Pattern = Pattern.compile("[0-9]*[Xx\\*]*[0-9]*[Xx\\*]+[0-9]{3,}")
      //  val ammont = Pattern.compile("(?i)(?:(?:RS|INR|MRP)\\.?\\s?)(\\d+(:?\\,\\d+)?(\\,\\d+)?(\\.\\d{1,2})?)")
 val ammont = Pattern.compile("[rR][sS]\\.?\\s[,\\d]+\\.?\\d{0,2}|[iI][nN][rR]\\.?\\s*[,\\d]+\\.?\\d{0,2}")
 val price =Pattern.compile("[0-9]{1,13}(\\.[0-9]*)?")
        val totalSMS: Int = c?.getCount() ?: 0
        if (c?.moveToFirst() == true) {
            for (i in 0 until totalSMS) {

                val id = c?.getString(c.getColumnIndexOrThrow("_id"))

                c?.getString(
                    c
                        .getColumnIndexOrThrow("address")
                )

                val body = c?.getString(c.getColumnIndexOrThrow("body"))
                val date = c?.getString(c.getColumnIndexOrThrow("date"))
                val m: Matcher = p.matcher(body)

                if (m.find()) {

                    val amm: Matcher = ammont.matcher(body)
                    if (amm.find()) {
                        val message = amm.group(0)
                        if (body?.contains("deposited", true)!!
                            || body?.contains("credited", true)!!
                        ) {
                            val lprice =price.matcher(message.replace(",",""))
                            if(lprice.find()){
                                val st=  lprice.group(0)
                                lstSms.add(SmsModel("Income", body, st.toDoubleOrNull()))
                            }



                        } else if (body?.contains("withdrawn", true)!!
                            || body?.contains("debited", true)!!
                            || body?.contains("spent", true)!!
                        ) {

                            val lprice =price.matcher(message.replace(",",""))
                            if(lprice.find()){
                                val st=  lprice.group(0)
                                lstSms.add(SmsModel("Expense", body, st.toDoubleOrNull()))
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
        allsms.postValue(lstSms)


    }

}
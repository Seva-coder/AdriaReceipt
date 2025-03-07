package mne.seva.mnereceipt.presentation

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import java.time.LocalDateTime
import java.util.Calendar

abstract class BaseDatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    abstract val viewModel: ViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
        val date = LocalDateTime.of(year, month+1, day, 0,0, 0)
        setDate(date)
    }

    abstract fun setDate(date: LocalDateTime)
}

package ru.netology.nmedia.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import ru.netology.nmedia.R

import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.netologyandroidhomework1.AndroidUtils
import com.example.netologyandroidhomework1.RevealAnimationSetting
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.databinding.FragmentSaveEventBinding
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.model.ErrorCallback
import ru.netology.nmedia.requests.EventCreateRequest
import ru.netology.nmedia.viewModel.EventViewModel
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class SaveEventFragment : DialogFragment() {
    @Inject
    lateinit var errorCallback:ErrorCallback
    lateinit var saveEventFragmentBinding:FragmentSaveEventBinding
    val date: Date = Calendar.getInstance().time
    val dateFormatOutTimeText: android.icu.text.DateFormat = SimpleDateFormat("HH:mm")
    val dateFormatOutDateText: android.icu.text.DateFormat =SimpleDateFormat("dd MMM yyyy")
    private var editedPost:Event? = null
    private var revealAnimationSetting:RevealAnimationSetting? = null
    private val eventViewModel: EventViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            editedPost = it.getSerializable("event") as Event?
            revealAnimationSetting = it.getSerializable("revealAnimationSetting") as RevealAnimationSetting
        }
        setStyle(
            DialogFragment.STYLE_NORMAL,
            R.style.FullScreenDialogStyle
        );
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        saveEventFragmentBinding = FragmentSaveEventBinding.inflate(layoutInflater)
        eventViewModel.dataState.observe(viewLifecycleOwner) { feedModel ->
            feedModel.run {
                saveEventFragmentBinding.apply {
                    progressBar.isVisible = loading
                }
                error?.let {
                    errorCallback.onError(it.reason, it.onRetryListener)
                }
                if(isSaved){
                    findNavController().popBackStack()
                }
            }
        }
        val contentEditText = saveEventFragmentBinding.contentEditText.apply {
            text.clear()
        }
        val button = saveEventFragmentBinding.save
        val cancelButton = saveEventFragmentBinding.cancelButton
        if (editedPost == null) {
            button.setIconResource(R.drawable.baseline_create_24)
            button.setText("CREATE")
            saveEventFragmentBinding.linearLayoutUpdate.visibility = View.GONE
            button.setOnClickListener {
                if (contentEditText.text.isBlank())
                    Snackbar.make(saveEventFragmentBinding.root,"Контент не может быть пустым",Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok){
                    }.show()
                else {
                    eventViewModel.saveEvent(EventCreateRequest(content = contentEditText.text.toString(), datetime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date)))
                }
            }
        } else {
            editedPost?.let {event->
                button.setIconResource(R.drawable.baseline_save_as_24)
                button.text = "EDIT"
                contentEditText.setText(event.content)
                saveEventFragmentBinding.linearLayoutUpdate.visibility = View.VISIBLE
                button.setOnClickListener {
                    if (contentEditText.text.isBlank()) {
                        Snackbar.make(
                            saveEventFragmentBinding.root,
                            "Контент не может быть пустым",
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction(android.R.string.ok) {
                        }.show()
                    } else {
                        eventViewModel.saveEvent(EventCreateRequest(event.copy(content = contentEditText.text.toString(), datetime = date)))
                    }
                }
                cancelButton.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }
        setTextTime()
        saveEventFragmentBinding.textTime.setOnClickListener {
            showTimePickerDialog()
        }
        setTextDate()
        saveEventFragmentBinding.textDate.setOnClickListener {
            showDatePickerDialog()
        }
        AndroidUtils.registerCircularRevealAnimation(requireContext(),saveEventFragmentBinding.root,revealAnimationSetting!!,
            requireContext().getColor(R.color.black),requireContext().getColor(R.color.white),
        )
        return saveEventFragmentBinding.root
    }
    private fun showTimePickerDialog(){
        TimePickerDialog(requireContext(),
            { _, hourOfDay, minute ->
                date.apply {
                    hours = hourOfDay
                    minutes = minute
                }
                setTextTime()
            }, date.hours, date.minutes, true).show()
    }
    private fun showDatePickerDialog(){
        DatePickerDialog(requireContext(),
            { _, year, month, dayOfMonth ->
                date.apply {
                    date = dayOfMonth
                    this.month = month
                    this.year = year-1900
                }
                setTextDate()
            },date.year + 1900 , date.month,date.day ).show()
    }
    private fun setTextTime(){
        saveEventFragmentBinding.textTime.text= dateFormatOutTimeText.format(date)
    }
    private fun setTextDate(){
        saveEventFragmentBinding.textDate.text= dateFormatOutDateText.format(date)
    }
}
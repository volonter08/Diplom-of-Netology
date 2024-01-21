package ru.netology.nmedia.fragments

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import ru.netology.nmedia.R

import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.entity.utills.AndroidUtils
import ru.netology.nmedia.entity.utills.RevealAnimationSetting
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import ru.netology.nmedia.databinding.FragmentSaveJobBinding
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.model.ErrorCallback
import ru.netology.nmedia.requests.JobCreateRequest
import ru.netology.nmedia.viewModel.JobViewModel
import ru.netology.nmedia.viewModel.JobViewModelFactory
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class SaveJobFragment : DialogFragment() {
    @Inject
    lateinit var errorCallback: ErrorCallback
    private lateinit var saveJobFragmentBinding: FragmentSaveJobBinding
    private var editedJob: Job? = null
    private var startDate: Date = Calendar.getInstance().time
    private var finishDate: Date? = Calendar.getInstance().time
    private val dateFormatOutDateText: android.icu.text.DateFormat = SimpleDateFormat("dd MMM yyyy")
    private var revealAnimationSetting: RevealAnimationSetting? = null
    private val jobViewModel: JobViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<
                    JobViewModelFactory> { factory ->
                factory.create(0)
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            revealAnimationSetting =
                it.getSerializable("revealAnimationSetting") as RevealAnimationSetting
            editedJob =
                it.getSerializable("job") as Job?
        }
        setStyle(
            STYLE_NORMAL,
            R.style.FullScreenDialogStyle
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        saveJobFragmentBinding = FragmentSaveJobBinding.inflate(layoutInflater)
        jobViewModel.dataState.observe(viewLifecycleOwner) { feedModel ->
            feedModel.run {
                saveJobFragmentBinding.apply {
                    progressBar.isVisible = loading
                }
                error?.let {
                    errorCallback.onError(it.reason, it.onRetryListener)
                }
                if (isSaved) {
                    findNavController().popBackStack()
                }
            }
        }
        val nameEditText = saveJobFragmentBinding.nameEditText.apply {
            text.clear()
        }
        val positionEditText = saveJobFragmentBinding.positionEditText.apply {
            text.clear()
        }
        val linkEditText = saveJobFragmentBinding.linkEditText.apply {
            text.clear()
        }
        val saveButton = saveJobFragmentBinding.save
        val cancelButton = saveJobFragmentBinding.cancelButton
        if (editedJob == null) {
            saveButton.setIconResource(R.drawable.baseline_create_24)
            saveButton.text = getString(R.string.create)
            saveJobFragmentBinding.linearLayoutUpdate.visibility = View.GONE
            saveButton.setOnClickListener {
                if (nameEditText.text.isBlank())
                    Snackbar.make(
                        saveJobFragmentBinding.root,
                        getString(R.string.empty_name_message),
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(android.R.string.ok) {
                    }.show()
                if (positionEditText.text.isBlank())
                    Snackbar.make(
                        saveJobFragmentBinding.root,
                        getString(R.string.empty_position_message),
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(android.R.string.ok) {
                    }.show()

                else {
                    jobViewModel.saveJob(
                        JobCreateRequest(
                            id = 0,
                            name = nameEditText.text.toString(),
                            position = positionEditText.text.toString(),
                            link = linkEditText.text.toString().ifBlank { null },
                            start = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(startDate),
                            finish = finishDate?.let {
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(it)
                            }
                        )
                    )
                }
            }
        } else {
            editedJob?.let { job ->
                saveButton.setIconResource(R.drawable.baseline_save_as_24)
                saveButton.text = getString(R.string.edit)
                nameEditText.setText(job.name)
                positionEditText.setText(job.position)
                linkEditText.setText(job.position)
                startDate = job.start
                finishDate = job.finish
                cancelButton.setOnClickListener {
                    findNavController().popBackStack()
                }
                saveButton.setOnClickListener {
                    if (nameEditText.text.isBlank())
                        Snackbar.make(
                            saveJobFragmentBinding.root,
                            "Контент не может быть пустым",
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction(android.R.string.ok) {
                        }.show()
                    else {
                        jobViewModel.saveJob(
                            JobCreateRequest(
                                job.copy(
                                    name = nameEditText.text.toString(),
                                    position = positionEditText.text.toString(),
                                    link = linkEditText.text.toString().ifBlank { null },
                                    start = startDate,
                                    finish = finishDate
                                )
                            )
                        )
                    }
                }
            }
        }
        setTextStartDate()
        saveJobFragmentBinding.textStartDate.setOnClickListener {
            showStartDatePickerDialog()
        }
        setTextFinishDate()
        saveJobFragmentBinding.textFinishDate.setOnClickListener { view ->
            PopupMenu(view.context, view).apply {
                inflate(R.menu.finish_date_options)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.to_present_day -> {
                            saveJobFragmentBinding.textFinishDate.text =
                                getString(R.string.to_present_day)
                            finishDate = null
                            true
                        }

                        R.id.pick_date_of_finish -> {
                            showFinishDatePickerDialog()
                            true
                        }

                        else -> false

                    }
                }
            }.show()
        }
        AndroidUtils.registerCircularRevealAnimation(
            saveJobFragmentBinding.root, revealAnimationSetting!!
        )
        return saveJobFragmentBinding.root
    }

    private fun showStartDatePickerDialog() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                startDate.apply {
                    this.date = dayOfMonth
                    this.month = month
                    this.year = year - 1900
                }
                setTextStartDate()
            }, startDate.year + 1900, startDate.month, startDate.day
        ).show()
    }

    private fun showFinishDatePickerDialog() {
        if (finishDate == null)
            finishDate = Date()
        finishDate?.apply {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    this.date = dayOfMonth
                    this.month = month
                    this.year = year - 1900
                    setTextFinishDate()
                },
                year + 1900,
                month,
                date
            ).show()
        }
    }

    private fun setTextStartDate() {
        saveJobFragmentBinding.textStartDate.text = dateFormatOutDateText.format(startDate)
    }

    private fun setTextFinishDate() {
        if (finishDate == null) {
            saveJobFragmentBinding.textFinishDate.text = getString(R.string.to_present_day)
        } else {
            saveJobFragmentBinding.textFinishDate.text = dateFormatOutDateText.format(finishDate)
        }
    }
}
package com.devspace.taskbeats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isVisible
import com.devspace.taskbeats.database.CategoryEntity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class CreateOrUpdateTaskBottomSheet(
    private val categoryList: List<CategoryEntity>,
    private val task: TaskUiData? = null,
    private val preSelectedCategory: String? = null,
    private val onCreateClicked: (TaskUiData) -> Unit,
    private val onUpdateClicked: (TaskUiData) -> Unit,
    private val onDeleteClicked: (TaskUiData) -> Unit
) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("DEBUG", "Task: $task | PreSelectedCategory: $preSelectedCategory")
        val view = inflater.inflate(
            R.layout.create_or_update_task_bottom_sheet,
            container,
            false
        )
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val tietaskName = view.findViewById<TextInputEditText>(R.id.tie_task_text)
        val btncreateOrUpdateTask = view.findViewById<Button>(R.id.btn_task_create_or_update)
        val btndeleteTask = view.findViewById<Button>(R.id.btn_task_delete)
        val spinner = view.findViewById<Spinner>(R.id.spinner_categories)
        var taskCategory: String? = null

        val categoryNames = categoryList.map { it.name }
        val categoryString: List<String>
        val initialSelectionIndex: Int

        val categoryToSelect = task?.category ?: preSelectedCategory
        val hasValidCategory = categoryToSelect?.isNotEmpty() == true &&
                categoryNames.contains(categoryToSelect)

        if (hasValidCategory) {
            categoryString = categoryNames
            val currentCategory = categoryToSelect!!
            val index = categoryNames.indexOf(currentCategory)
            initialSelectionIndex = if (index >= 0) index else 0
        } else {
            categoryString = listOf("Select a category") + categoryNames
            initialSelectionIndex = 0
        }


        ArrayAdapter(
            requireContext(),
            R.layout.spinner_selected_item,
            categoryString.toList()
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.setSelection(initialSelectionIndex, false)
            taskCategory =
                if (initialSelectionIndex == 0 && categoryString[0] == "Select a category") {
                    null
                } else {
                    categoryString[initialSelectionIndex]
                }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                taskCategory = if (task == null && position == 0) {
                    null
                } else {
                    categoryString[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                taskCategory = null
            }
        }

        if (task == null) {
            btndeleteTask.isVisible = false
            tvTitle.setText(R.string.create_task_title)
            btncreateOrUpdateTask.setText(R.string.create_task)
        } else {
            tvTitle.setText(R.string.update_task_title)
            btncreateOrUpdateTask.setText(R.string.update)
            tietaskName.setText(task.name)
            btndeleteTask.isVisible = true

            val currentCategory = categoryList.first { it.name == task.category }
            val index = categoryList.indexOf(currentCategory)
            spinner.setSelection(index)


        }

        btndeleteTask.setOnClickListener {
            if (task != null) {
                onDeleteClicked.invoke(task)
                dismiss()
            } else {
                Log.d("CreateOrUpdateTaskBottomSheet", "task not found")
            }
        }

        btncreateOrUpdateTask.setOnClickListener {
            val taskName = tietaskName.text.toString().trim()
            if (taskCategory != null && taskName.trim().isNotEmpty()) {
                if (task == null) {
                    onCreateClicked.invoke(
                        TaskUiData(
                            id = 0,
                            name = taskName,
                            category = requireNotNull(taskCategory)
                        )
                    )
                } else {
                    onUpdateClicked.invoke(
                        TaskUiData(
                            id = task.id,
                            name = taskName,
                            category = requireNotNull(taskCategory)
                        )
                    )
                }
                dismiss()
            } else {
                Snackbar.make(
                    btncreateOrUpdateTask,
                    getString(R.string.snackbar_invalid_category),
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }
        }
        return view
    }
}
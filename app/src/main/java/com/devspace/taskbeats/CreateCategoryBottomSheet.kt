package com.devspace.taskbeats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText


class CreateCategoryBottomSheet(
    private val onCreateClicked: (String) -> Unit
) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.create_category_bottom_sheet,
            container,
            false
        )
        val btnCreate = view.findViewById<Button>(R.id.btn_category_create)
        val tieCategoryName = view.findViewById<TextInputEditText>(R.id.tie_category_name)
        btnCreate.setOnClickListener {
            val name = tieCategoryName.text.toString()
            if (name.trim() != "+") {
                if (name.trim().isEmpty()) {
                    Snackbar.make(
                        btnCreate,
                        getString(R.string.invalid_category_name),
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    onCreateClicked.invoke(name)
                    dismiss()
                }
            } else {
                Snackbar.make(
                    btnCreate,
                    getString(R.string.invalid_category),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
        return view
    }
}



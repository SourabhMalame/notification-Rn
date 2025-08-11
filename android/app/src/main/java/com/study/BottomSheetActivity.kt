package com.study

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater

class BottomSheetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Show the bottom sheet immediately when activity starts
        showBottomSheet()
    }

    private fun showBottomSheet() {
        // Inflate the bottom sheet layout
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, null)

        // Create BottomSheetDialog
        val dialog = BottomSheetDialog(this).apply {
            setContentView(view)
            setCancelable(false) // Prevent dismissing by tapping outside
        }

        // Handle approve button click
        view.findViewById<Button>(R.id.approveBtn).setOnClickListener {
            // Close the dialog
            dialog.dismiss()
            // Launch main app activity
            startActivity(Intent(this@BottomSheetActivity, MainActivity::class.java))
            // Finish current activity if needed
            finish()
        }

        // Handle confirm button (if you still want this functionality)
        view.findViewById<Button>(R.id.confirmBtn).setOnClickListener {
            // Add your confirm logic here
            dialog.dismiss()
        }

        // Handle cancel button click
        view.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            // Simply dismiss the dialog
            dialog.dismiss()
            // Finish the activity to close everything
            finish()
        }

        // Show the dialog
        dialog.show()
        
        // Optional: Handle dialog dismissal (when swiped down or back pressed)
        dialog.setOnDismissListener {
            finish() // Close the activity when dialog is dismissed
        }
    }
}
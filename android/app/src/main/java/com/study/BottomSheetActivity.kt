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

        view.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            dialog.dismiss()
            finish()
        }


        dialog.show()
        
        dialog.setOnDismissListener {
            finish() 
        }
    }
}
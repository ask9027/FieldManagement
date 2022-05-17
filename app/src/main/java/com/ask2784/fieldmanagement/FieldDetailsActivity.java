package com.ask2784.fieldmanagement;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ask2784.fieldmanagement.databinding.ActivityFieldDetailsBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FieldDetailsActivity extends AppCompatActivity {
    private CollectionReference collectionReference;
    private String fieldId;
    private ActivityFieldDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFieldDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.include1.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Field Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mainMethod();
    }

    private void mainMethod() {
        fieldId = getIntent().getStringExtra("fieldId");
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        collectionReference = fireStore.collection("Fields");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit_field) {
            editField();
            return true;
        }
        if (item.getItemId() == R.id.delete_field) {
            deleteField();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editField() {
        Toast.makeText(this, "Edited", Toast.LENGTH_SHORT).show();

    }

    private void deleteField() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Delete")
                .setMessage("Do You want to delete this Field?")
                .setPositiveButton("Yes", (dialogInterface, i) -> collectionReference.document(fieldId)
                        .delete().addOnCompleteListener(task -> {
                            if (task.isCanceled()) {
                                Toast.makeText(this, "Canceled " + task.getResult(), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (task.isComplete()) {
                                Toast.makeText(this, "Deleted ", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            dialogInterface.dismiss();
                        }))
                .setNegativeButton("No", null);

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

}
package com.ask2784.fieldmanagement;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ask2784.fieldmanagement.databases.FieldDetailsAdapter;
import com.ask2784.fieldmanagement.databases.Fields;
import com.ask2784.fieldmanagement.databases.OnClickListener;
import com.ask2784.fieldmanagement.databinding.ActivityFieldDetailsBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class FieldDetailsActivity extends AppCompatActivity implements OnClickListener {
    private CollectionReference collectionReference;
    private String fieldId;
    private ActivityFieldDetailsBinding binding;
    private ArrayList<Fields> fieldArrayList;
    private FieldDetailsAdapter fieldDetailsAdapter;
    private EventListener<DocumentSnapshot> eventListener;
    private ListenerRegistration listenerRegistration;

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
        fieldArrayList = new ArrayList<>();
        fieldId = getIntent().getStringExtra("fieldId");
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        collectionReference = fireStore.collection("Fields");
        initRecyclerView();
        getFieldData();
        addDetails();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getFieldData() {
        eventListener = (value, error) -> {
            if (error != null) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                SpannableString spannableString = new SpannableString(error.getLocalizedMessage());
                Linkify.addLinks(spannableString, Linkify.ALL);
                TextView errorView = new TextView(this);
                errorView.setPadding(40, 0, 40, 0);
                errorView.setMovementMethod(LinkMovementMethod.getInstance());
                errorView.setText(spannableString);
                builder.setTitle("Getting Error on Data")
                        .setView(errorView)
                        .setPositiveButton("Okay", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }

            if (value != null) {
                fieldArrayList.clear();
                Fields field = value.toObject(Fields.class);
                fieldArrayList.add(field);
                fieldDetailsAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        };

        if (listenerRegistration == null) {
            setCollectionReference();
        }
    }

    private void setCollectionReference() {
        listenerRegistration = collectionReference.document(fieldId).addSnapshotListener(eventListener);
    }

    private void initRecyclerView() {
        binding.detailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fieldDetailsAdapter = new FieldDetailsAdapter(fieldArrayList, this);
        binding.detailsRecyclerView.setAdapter(fieldDetailsAdapter);
    }

    private void addDetails() {
        binding.addFabDetails.setOnClickListener(v -> Snackbar.make(binding.getRoot(), "Add Details", Snackbar.LENGTH_SHORT).show());
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
        Snackbar.make(binding.getRoot(), "Edits", Snackbar.LENGTH_SHORT).show();
    }

    private void deleteField() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Delete")
                .setMessage("Do You want to delete `" + fieldArrayList.get(0).getName() + "` Field?")
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onViewClick(int position) {
        Snackbar.make(binding.getRoot(), "Clicked on " + position, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setCollectionReference();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}

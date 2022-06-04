package com.ask2784.fieldmanagement;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ask2784.fieldmanagement.databases.OnClickListener;
import com.ask2784.fieldmanagement.databases.adapters.FieldDetailsAdapter;
import com.ask2784.fieldmanagement.databases.models.FieldDetails;
import com.ask2784.fieldmanagement.databases.models.Fields;
import com.ask2784.fieldmanagement.databinding.ActivityFieldDetailsBinding;
import com.ask2784.fieldmanagement.databinding.AddFieldsBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class FieldDetailsActivity extends AppCompatActivity implements OnClickListener {
    private CollectionReference collectionReference;
    private String fieldId;
    private ActivityFieldDetailsBinding binding;
    private ArrayList<Fields> fieldArrayList;
    private ArrayList<FieldDetails> fieldDetailsList;
    private ArrayList<String> fieldDetailsIdList;
    private FieldDetailsAdapter fieldDetailsAdapter;
    private EventListener<DocumentSnapshot> eventListener;
    private EventListener<QuerySnapshot> detailsEventListener;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFieldDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.include1.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mainMethod();
    }

    private void mainMethod() {
        fieldArrayList = new ArrayList<>();
        fieldDetailsList = new ArrayList<>();
        fieldDetailsIdList = new ArrayList<>();
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
                fieldArrayList.add(value.toObject(Fields.class));
                Objects.requireNonNull(getSupportActionBar()).setTitle(value.get("name") + " Field");
                fieldDetailsAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        };

        detailsEventListener = (value, error) -> {
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
                fieldDetailsIdList.clear();
                fieldDetailsList.clear();
                for (QueryDocumentSnapshot snapshot : value) {
                    if (snapshot != null) {
                        fieldDetailsIdList.add(snapshot.getId());
                        FieldDetails details = snapshot.toObject(FieldDetails.class);
                        fieldDetailsList.add(details);
                    } else Toast.makeText(this, "Data Not Available", Toast.LENGTH_SHORT).show();
                }
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
        listenerRegistration = collectionReference.document(fieldId).collection("FieldDetails").orderBy("year", Query.Direction.DESCENDING).addSnapshotListener(detailsEventListener);
    }

    private void initRecyclerView() {
        binding.detailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fieldDetailsAdapter = new FieldDetailsAdapter(fieldArrayList, this, fieldDetailsList);
        binding.detailsRecyclerView.setAdapter(fieldDetailsAdapter);
    }

    private void addDetails() {
        binding.addFabDetails.setOnClickListener(v -> {
            MaterialAlertDialogBuilder addBuilder = new MaterialAlertDialogBuilder(this);
            Spinner yearsSpinner = new AppCompatSpinner(this);
            ArrayList<String> years = new ArrayList<>();
            int thisYear = Calendar.getInstance().get(Calendar.YEAR);
            for (int i = 1900; i <= thisYear; thisYear--) {
                years.add(String.valueOf(thisYear));
            }
            ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
            yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            yearsSpinner.setAdapter(yearsAdapter);
            addBuilder.setTitle("Years")
                    .setView(yearsSpinner)
                    .setPositiveButton("Okay", (dialogInterface, i) -> {
                        FieldDetails fieldDetails = new FieldDetails(yearsSpinner.getSelectedItem().toString());
                        collectionReference
                                .document(fieldId)
                                .collection("FieldDetails")
                                .add(fieldDetails)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(FieldDetailsActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    if (task.isCanceled()) {
                                        Toast.makeText(FieldDetailsActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                                    }
                                    dialogInterface.dismiss();
                                });
                    })
                    .create()
                    .show();

        });
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
        AddFieldsBinding editFieldsBinding;
        editFieldsBinding = AddFieldsBinding.inflate(getLayoutInflater());

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder
                .setTitle("Edit Field")
                .setView(editFieldsBinding.getRoot())
                .setPositiveButton("Done", null)
                .setNegativeButton("Cancel", null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            String[] fieldType = {"Bigha", "Hectare", "Acer"};
            ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fieldType);
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            editFieldsBinding.fieldSpinner.setAdapter(typeAdapter);
            Fields field = fieldArrayList.get(0);
            String[] area = field.getArea().split("(?<=\\d)(\\s+)(?=\\D)");

            editFieldsBinding.addFiledName.setText(field.getName());
            editFieldsBinding.addFieldArea.setText(area[0]);
            editFieldsBinding.fieldSpinner.setSelection(typeAdapter.getPosition(area[1]));
            editFieldsBinding.addFieldCurrentCrop.setText(field.getCurrentCrop());

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(view -> {
                        if (editFieldsBinding.addFiledName.getText() != null && editFieldsBinding.addFiledName.getText().toString().isEmpty()) {
                            editFieldsBinding.addFiledName.setError("Enter Field Name");
                            editFieldsBinding.addFiledName.requestFocus();
                        } else if (editFieldsBinding.addFieldArea.getText() != null && editFieldsBinding.addFieldArea.getText().toString().isEmpty()) {
                            editFieldsBinding.addFieldArea.setError("Enter Field Area");
                            editFieldsBinding.addFieldArea.requestFocus();
                        } else if (editFieldsBinding.addFieldCurrentCrop.getText() != null && editFieldsBinding.addFieldCurrentCrop.getText().toString().isEmpty()) {
                            editFieldsBinding.addFieldCurrentCrop.setError("Enter Current Crop");
                            editFieldsBinding.addFieldCurrentCrop.requestFocus();
                        } else {
                            Fields fields = new Fields(field.getUId(),
                                    editFieldsBinding.addFiledName.getText().toString().trim(),
                                    editFieldsBinding.addFieldArea.getText().toString().trim() + " " + editFieldsBinding.fieldSpinner.getSelectedItem().toString(),
                                    editFieldsBinding.addFieldCurrentCrop.getText().toString().trim());
                            collectionReference.document(fieldId)
                                    .set(fields).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(FieldDetailsActivity.this, "Edited Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                        if (task.isCanceled()) {
                                            Toast.makeText(FieldDetailsActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            alertDialog.dismiss();
                        }
                    });
        });
        alertDialog.show();
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
        onBackPressed();
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

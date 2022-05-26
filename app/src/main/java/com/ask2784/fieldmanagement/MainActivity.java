package com.ask2784.fieldmanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ask2784.fieldmanagement.databases.Fields;
import com.ask2784.fieldmanagement.databases.FieldsAdapter;
import com.ask2784.fieldmanagement.databases.OnClickListener;
import com.ask2784.fieldmanagement.databinding.ActivityMainBinding;
import com.ask2784.fieldmanagement.databinding.AddFieldsBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, OnClickListener {
    private ArrayList<Fields> fieldsList;
    private ArrayList<String> fieldsIdList;
    private long pressedTime;
    private String uId;
    private ActivityMainBinding binding;
    private ListenerRegistration listenerRegistration;
    private CollectionReference collectionReference;
    private EventListener<QuerySnapshot> eventListener;
    private FieldsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.include.toolbar);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            uId = mAuth.getCurrentUser().getUid();
            mainMethod();
            addField();
        }
    }

    private void mainMethod() {
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        fieldsList = new ArrayList<>();
        fieldsIdList = new ArrayList<>();
        initRecyclerView();
        collectionReference = fireStore.collection("Fields");
        getFirestoreData();
    }

    private void initRecyclerView() {
        binding.mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FieldsAdapter(fieldsList, this);
        binding.mainRecyclerView.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getFirestoreData() {
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
                fieldsIdList.clear();
                fieldsList.clear();
                for (QueryDocumentSnapshot snapshot : value) {
                    if (snapshot != null) {
                        fieldsIdList.add(snapshot.getId());
                        Fields fields = snapshot.toObject(Fields.class);
                        fieldsList.add(fields);
                    } else Toast.makeText(this, "Data Not Available", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
                binding.mainRecyclerView.setVisibility(fieldsList.isEmpty() ? View.GONE : View.VISIBLE);
                binding.empty.setVisibility(fieldsList.isEmpty() ? View.VISIBLE : View.GONE);
            } else {
                Toast.makeText(MainActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        };

        if (listenerRegistration == null) {
            setCollectionReference();
        }

    }

    private void addField() {
        binding.addFab.setOnClickListener(v -> {
            AddFieldsBinding addFieldsBinding;
            addFieldsBinding = AddFieldsBinding.inflate(getLayoutInflater());
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
            alertDialogBuilder
                    .setTitle("Add New Field")
                    .setView(addFieldsBinding.getRoot())
                    .setPositiveButton("Save", null)
                    .setNegativeButton("Cancel", null);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setOnShowListener(dialogInterface -> {
                String[] fieldType = {"Bigha", "Hectare", "Acer"};
                ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fieldType);
                typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                addFieldsBinding.fieldSpinner.setAdapter(typeAdapter);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(view -> {
                            if (addFieldsBinding.addFiledName.getText() != null && addFieldsBinding.addFiledName.getText().toString().isEmpty()) {
                                addFieldsBinding.addFiledName.setError("Enter Field Name");
                                addFieldsBinding.addFiledName.requestFocus();
                            } else if (addFieldsBinding.addFieldArea.getText() != null && addFieldsBinding.addFieldArea.getText().toString().isEmpty()) {
                                addFieldsBinding.addFieldArea.setError("Enter Field Area");
                                addFieldsBinding.addFieldArea.requestFocus();
                            } else if (addFieldsBinding.addFieldCurrentCrop.getText() != null && addFieldsBinding.addFieldCurrentCrop.getText().toString().isEmpty()) {
                                addFieldsBinding.addFieldCurrentCrop.setError("Enter Current Crop");
                                addFieldsBinding.addFieldCurrentCrop.requestFocus();
                            } else {
                                Fields fields = new Fields(uId,
                                        addFieldsBinding.addFiledName.getText().toString().trim(),
                                        addFieldsBinding.addFieldArea.getText().toString().trim() + " " + addFieldsBinding.fieldSpinner.getSelectedItem().toString(),
                                        addFieldsBinding.addFieldCurrentCrop.getText().toString().trim());
                                collectionReference.add(fields).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    if (task.isCanceled()) {
                                        Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                alertDialog.dismiss();
                            }
                        });
            });
            alertDialog.show();
        });
    }

    private void setCollectionReference() {
        listenerRegistration = collectionReference.whereEqualTo("uid", uId).orderBy("name").addSnapshotListener(eventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            AuthUI.getInstance().signOut(this);
            return true;
        } else if (item.getItemId() == R.id.exit) {
            System.exit(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            System.exit(0);
        } else {
            Toast.makeText(MainActivity.this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
        setCollectionReference();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    @Override
    public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onViewClick(int position) {
        Intent intent = new Intent(MainActivity.this, FieldDetailsActivity.class);
        intent.putExtra("fieldId", fieldsIdList.get(position));
        startActivity(intent);
    }
}

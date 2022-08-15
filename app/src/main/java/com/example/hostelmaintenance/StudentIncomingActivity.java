package com.example.hostelmaintenance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class StudentIncomingActivity extends AppCompatActivity implements  LeaveVerifyAdapter.OnLeaveListener{
    RecyclerView recyclerviewIncoming;
    LeaveVerifyAdapter lVA;
    ArrayList<GetLeaveData> incominglist;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_incoming);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data.....");
        progressDialog.show();
        incominglist = new ArrayList<>();
        int num = 1;
        recyclerviewIncoming= findViewById(R.id.incoming_recycle);
        recyclerviewIncoming.setHasFixedSize(true);
        recyclerviewIncoming.setLayoutManager(new LinearLayoutManager(this));
        FirebaseFirestore.getInstance().collection("Student_Leaves").whereEqualTo("Verified_HW", num)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentChange ds: queryDocumentSnapshots.getDocumentChanges()){
                            GetLeaveData getIncoming = ds.getDocument().toObject(GetLeaveData.class);
                            getIncoming.setId(ds.getDocument().getId());
                            incominglist.add(getIncoming);

                            lVA = new LeaveVerifyAdapter(getApplicationContext(),incominglist,StudentIncomingActivity.this);
                            recyclerviewIncoming.setAdapter(lVA);
                            lVA.notifyDataSetChanged();
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();

                            }

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Log.d(">>>>>>>>>", e.getMessage());
                    }
                });

    }

    @Override
    public void OnLeaveClick(int position) {
        Intent i = new Intent(this,FinalIncomingActivity.class);
        i.putExtra("Incoming_Item",incominglist.get(position));
        startActivity(i);
        finish();
    }
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_item, menu);
        MenuItem menuItem = menu.findItem(R.id.searchaction);
        android.widget.SearchView searchView = (android.widget.SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search here");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                lVA.getFilter().filter(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
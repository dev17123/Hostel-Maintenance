package com.example.hostelmaintenance.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.example.hostelmaintenance.GetComplaintData;
import com.example.hostelmaintenance.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ComplaintStatusActivity extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseAuth auth;
    RecyclerView rvlist;
    DataAdapter dataAdapter;
    ProgressDialog progressDialog;


    ArrayList<GetComplaintData> complainlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_status);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();
        auth=FirebaseAuth.getInstance();
        user =auth.getCurrentUser();
        String email= user.getEmail();
        complainlist = new ArrayList<>();
        dataAdapter= new DataAdapter(this,complainlist);
        rvlist=findViewById(R.id.rec1);
        rvlist.setHasFixedSize(true);
        rvlist.setLayoutManager(new LinearLayoutManager(this));
        rvlist.setAdapter(dataAdapter);

        FirebaseFirestore.getInstance().collection("Complaints")
                .whereEqualTo("Email",email)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(DocumentChange ds: task.getResult().getDocumentChanges()){
                           GetComplaintData getComplaintData = ds.getDocument().
                                   toObject(GetComplaintData.class);
                           complainlist.add(getComplaintData);
                           dataAdapter.notifyDataSetChanged();
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();

                        }
                    }
                });

    }
}
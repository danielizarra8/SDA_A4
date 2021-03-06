package com.example.sdaassign4_2021;


import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * Images used are sourced from Public Domain Day 2019.
 * by Duke Law School's Center for the Study of the Public Domain
 * is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * A simple {@link Fragment} subclass.
 * @author Chris Coughlan
 * @author Edited by Rafael Izarra 2022
 */
public class BookList extends Fragment {
    private static final String TAG1 = "retrieveImg";
    private static final String TAG2 = "retrievebooks";

    //declare variables, database and widgets

    private ArrayList<Book> mBook = new ArrayList<>();

    StorageReference imageRef;
    StorageReference storageReference;
    LibraryViewAdapter recyclerViewAdapter;

    String author, title, id, url, url1;
    boolean isAvailable;

    FirebaseFirestore dbRef;

    public BookList() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_book_list, container, false);

        //instantiate the database and call the getBookData() method to retireve book's data
        dbRef = FirebaseFirestore.getInstance();
        getBookData();


        RecyclerView recyclerView = root.findViewById(R.id.bookView_view);
        recyclerViewAdapter = new LibraryViewAdapter(getContext(), mBook);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return root;
    }

    public void getBookData() {
        // get the storage reference from firebase where images are stored
        storageReference = FirebaseStorage.getInstance().getReference();

        //point to the right collection in the database
        dbRef.collection("books").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    //loop through each ducument retrieved
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        url = document.getString("url");
                        // set the right path
                        imageRef = storageReference.child("/images/"+url);
                        // download the image url
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // pass the values and create a new Book object
                                url1 = uri.toString();
                                title = (String) (document.getString("Title"));
                                author = (String) (document.getString("Author"));
                                isAvailable = document.getBoolean("Availability");
                                id = document.getId();
                                mBook.add(new Book(author,title,id,url1,isAvailable));
                                // update the recyclerview adapater to reflect the changes, otherwise it won't display the data
                                recyclerViewAdapter.notifyDataSetChanged();
                            }
                        });

                    } // end of loop

                } // end of if statement
            }
        });

    }

}
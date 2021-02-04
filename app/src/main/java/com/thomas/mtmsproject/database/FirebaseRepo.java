package com.thomas.mtmsproject.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseRepo {
    private static FirebaseFirestore firebaseDB;
    private static FirebaseRepo firebaseRepo;
    private static String SOURCE_COLLECTION = "Source";
    private static String DRIVERS_COLLECTION = "Drivers";

    public static FirebaseRepo getInstance() {
        if (firebaseRepo == null) {
            firebaseRepo = new FirebaseRepo();
            firebaseDB = FirebaseFirestore.getInstance();
        }
        return firebaseRepo;
    }


    public Task<QuerySnapshot> getSource() {
        return firebaseDB.collection(SOURCE_COLLECTION).get();

    }

    public Task<QuerySnapshot> getDrivers() {
        return firebaseDB.collection(DRIVERS_COLLECTION).get();
    }

}

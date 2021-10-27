package com.qadr.clouddatabasesystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Utility {
    public static ProgressDialog progressDialog;


    public static void showDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public static void cancelDialog(){
        progressDialog.dismiss();
    }

    public static boolean uploadFile (Context context, Uri uri, String fileExtension, Map<String, Object> thesisData){
        AtomicLong time = new AtomicLong(new Date().getTime());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String uploadName = thesisData.get("author_matric") + "_" + new Date().getTime() + fileExtension;
        Log.i( "uploadFile: ", uploadName);
        Utility.showDialog(context);
        AtomicBoolean ret = new AtomicBoolean(true);
        StorageReference documentsRef = storage.getReference().child("thesis_documents/"+uploadName);
        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("application/*")
                .build();
        UploadTask uploadTask = documentsRef.putFile(uri, metadata);
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            ret.set(false);
            Log.e("ERROR DOWNLOAD URI", "uploadFile: ", exception);
        }).addOnSuccessListener(taskSnapshot -> {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
            // ...
            thesisData.put("uri", uploadName);
            createThesisRecord(thesisData).addOnCompleteListener(task1 -> {
                if(task1.isSuccessful()){
                    Toast.makeText(context, "Thesis Registered successfully", Toast.LENGTH_SHORT).show();
                    time.set(new Date().getTime() - time.get());
//                    Toast.makeText(context, "TIME_TAKEN = " + time, Toast.LENGTH_SHORT).show();

                } else {
                    ret.set(false);
                    Log.e("Error", "error while creating thesis record: ", task1.getException());
                }
            });
            Utility.cancelDialog();
        });
        return ret.get();
    }

    private static Task<String> createThesisRecord (Map<String, Object> data){
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        return (Task<String>) functions.getHttpsCallable("createThesisRecord").call(data)
                .continueWith(task -> {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.

                    Map<String, Object> returnData = (Map<String, Object>) task.getResult().getData();
                    return (String) returnData.get("message");
                });
    }

    public static Task<Map<String, Object>> getThesis (Map<String, Object> data){
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        return (Task<Map<String, Object>>) functions.getHttpsCallable("getThesis").call(data)
                .continueWith(task -> {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.

                    return (Map<String, Object>) task.getResult().getData();
                });
    }

    public static void downloadFile (Context context, String url) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference documentRef = storageReference.child(url);
        String suffix = url.substring(url.length() - 4);
        showDialog(context);
        File localFile = null;
        try {
            localFile = File.createTempFile("thesis"+ new Date().getTime(), suffix, context.getExternalFilesDir(null));
            Log.d("FILE LOCATION",  localFile.getAbsolutePath());
        } catch (IOException e) {
            cancelDialog();
            e.printStackTrace();
        }

        documentRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            // Local temp file has been created
            Toast.makeText(context, "Document downloaded successfully", Toast.LENGTH_SHORT).show();
            cancelDialog();
        }).addOnFailureListener(exception -> {
                Log.e("ERROR", "error downloading document", exception);
                cancelDialog();
        });
    }
}

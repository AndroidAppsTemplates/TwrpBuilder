package github.grace5921.TwrpBuilder.Fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import github.grace5921.TwrpBuilder.R;
import github.grace5921.TwrpBuilder.util.User;

/**
 * Created by androidlover5842 on 19/1/18.
 */

public class DevsBuildRunningFragment extends Fragment {
    private FirebaseListAdapter<User> adapter;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ListView mListView;
    private Query query;
    private DatabaseReference mUploader;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;
    DevsBuildRunningFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_devs_inqueue, container, false);
        storage = FirebaseStorage.getInstance();
        storageRef=storage.getReference();
        mListView = (ListView) view.findViewById(R.id.Lv_devs);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mUploader = mFirebaseInstance.getReference("Builds");
        userId = mUploader.push().getKey();
        query = FirebaseDatabase.getInstance()
                .getReference("RunningBuild");

        FirebaseListOptions<User> options = new FirebaseListOptions.Builder<User>()
                .setLayout(R.layout.list_developer_stuff)
                .setQuery(query,User.class)
                .build();

        adapter = new FirebaseListAdapter<User>(options) {
            @Override
            protected void populateView(View v, final User model, int position) {
                TextView tvEmail = v.findViewById(R.id.list_user_email);
                TextView tvDevice = v.findViewById(R.id.list_user_device);
                TextView tvBoard = v.findViewById(R.id.list_user_board);
                TextView tvDate= v.findViewById(R.id.list_user_date);
                TextView tvBrand = v.findViewById(R.id.list_user_brand);
                Button btFiles=v.findViewById(R.id.BtFile);
                final Button btStartBuild=v.findViewById(R.id.bt_start_build);
                final Button btBuildDone=v.findViewById(R.id.bt_build_done);
                tvDate.setText("Date : "+model.WtDate());
                tvEmail.setText("Email : "+model.WEmail());
                tvDevice.setText("Model : " + model.WModel());
                tvBoard.setText("Board : "+model.WBoard());
                tvBrand.setText("Brand : " +model.WBrand());
                btStartBuild.setVisibility(View.GONE);
                btBuildDone.setVisibility(View.VISIBLE);

                btFiles.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        storageRef.child("queue/" + model.WBrand() + "/" + model.WBoard() + "/" + model.WModel() + "/TwrpBuilderRecoveryBackup.tar" ).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(getContext().DOWNLOAD_SERVICE);

                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                String fileName=model.WModel()+"-"+model.WBoard()+"-"+model.WEmail()+".tar";
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                Long reference = downloadManager.enqueue(request);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                            }
                        });


                        Toast.makeText(getContext(),model.WModel(),Toast.LENGTH_SHORT).show();
                    }
                });

                /*btStartBuild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btBuildDone.setVisibility(View.VISIBLE);
                        btStartBuild.setVisibility(View.GONE);
                        mFirebaseInstance.getReference("RunningBuild").addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                                            child.getRef().removeValue();
                                        }
                                    }


                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w("TodoApp", "getUser:onCancelled", databaseError.toException());
                                    }
                                });

                        User user = new User(model.WBrand(),model.WBoard(),model.WModel(),model.WEmail(),model.WUid(),model.WFmcToken(),model.WtDate());
                        mUploader.child(userId).setValue(user);
                        System.out.println(model.WBrand()+model.WBoard()+model.WModel()+model.WEmail()+model.WtDate());
                    }
                });*/


            }
        };
        mListView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
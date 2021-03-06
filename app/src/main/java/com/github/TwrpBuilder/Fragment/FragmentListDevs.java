package com.github.TwrpBuilder.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.holder.ListContributorsHolder;
import com.github.TwrpBuilder.model.Developer;
import com.github.TwrpBuilder.util.FirebaseProgressBar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


/**
 * Created by androidlover5842 on 12.3.2018.
 */

public class FragmentListDevs extends Fragment implements View.OnClickListener {
    @Nullable
    private FirebaseRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_list_developers, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.rv_list_devs);
        Query query = FirebaseDatabase.getInstance()
                .getReference("Developers");
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder()
                .setQuery(query, Developer.class)
                .build();
        query.keepSynced(true);

        String s = null;

        adapter = new FirebaseRecyclerAdapter<Developer, ListContributorsHolder>(options) {
            @NonNull
            @Override
            public ListContributorsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_developers, parent, false);
                return new ListContributorsHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ListContributorsHolder holder, int position, @NonNull Developer dev) {
                String key = getRef(position).getKey();
                holder.bind(
                        dev.getEmail(),
                        dev.getName(),
                        dev.getPhotoUrl(),
                        dev.getXdaUrl(),
                        dev.getGitId(),
                        dev.getDonationUrl(),
                        dev.getDescription(),
                        key
                );
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                ProgressBar progressBar = v.findViewById(R.id.pb_builds);
                TextView textView = v.findViewById(R.id.tv_no_build);
                new FirebaseProgressBar(progressBar, textView, adapter, "Developers");
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ProgressBar progressBar = v.findViewById(R.id.pb_builds);
        TextView textView = v.findViewById(R.id.tv_no_build);
        new FirebaseProgressBar(progressBar, textView, adapter, "Developers");

        return v;
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

    @Override
    public void onClick(@NonNull View view) {
        int id = view.getId();

    }
}

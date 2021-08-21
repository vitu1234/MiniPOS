package com.example.minipos.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minipos.R;
import com.example.minipos.adapters.SettingsAdapter;
import com.example.minipos.models.StaticGeneralModel;

import java.util.ArrayList;
import java.util.List;


public class SettingsFragment extends Fragment {
    RecyclerView recyclerViewSettings;
    List<StaticGeneralModel> staticGeneralModelList;
    SettingsAdapter settingsAdapter;

    public SettingsFragment() {
        // Required empty public constructor
    }


    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        recyclerViewSettings = view.findViewById(R.id.settings_recycler_list);

        //add values to model
        staticGeneralModelList = new ArrayList<>();
        staticGeneralModelList.add(new StaticGeneralModel(1, R.drawable.profile_icon, "User Settings", "Display Picture, Username, Email, etc."));
        staticGeneralModelList.add(new StaticGeneralModel(2, R.drawable.store_icon, "Store Settings", "Store name, address, contacts and tax"));
        staticGeneralModelList.add(new StaticGeneralModel(3, R.drawable.settings_icon_24, "App Settings", "App customization and tweaks"));
        staticGeneralModelList.add(new StaticGeneralModel(4, R.drawable.printer_icon_24, "Printer Settings", "Paper size, Receipt layout"));
        staticGeneralModelList.add(new StaticGeneralModel(5, R.drawable.storage_icon_24, "Database Management", "Backup/Restore, Import/Export database, Clear data"));
        staticGeneralModelList.add(new StaticGeneralModel(6, R.drawable.info_icon_24, "About App", "Feedback, Community, Support, Check for update"));
        setRecycler();
        return view;
    }

    public void setRecycler() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewSettings.setLayoutManager(layoutManager);
        recyclerViewSettings.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));//line between items
        settingsAdapter = new SettingsAdapter(getContext(), staticGeneralModelList);
        recyclerViewSettings.setAdapter(settingsAdapter);
    }
}
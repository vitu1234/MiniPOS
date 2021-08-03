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
import com.example.minipos.adapters.ManagementsRecyclerAdapter;
import com.example.minipos.models.Management;

import java.util.ArrayList;
import java.util.List;


public class ManagementsFragment extends Fragment {

    RecyclerView recyclerViewManagement;
    List<Management> managementList;
    ManagementsRecyclerAdapter managementsRecyclerAdapter;

    public ManagementsFragment() {
        // Required empty public constructor
    }


    public static ManagementsFragment newInstance(String param1, String param2) {
        ManagementsFragment fragment = new ManagementsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_managements, container, false);
        recyclerViewManagement = view.findViewById(R.id.managements_recycler_list);

        //add values to model
        managementList = new ArrayList<>();
        managementList.add(new Management(1, R.drawable.product_24_icon, "Products", "View list, create, edit or delete products"));
        managementList.add(new Management(2, R.drawable.category_icon, "Categories", "View list, create, edit or delete product categories"));
        managementList.add(new Management(3, R.drawable.people_groups_icon, "Customers", "View list, create, edit or delete customers"));
        managementList.add(new Management(4, R.drawable.supplier_24_icon, "Suppliers", "View list, create, edit or delete suppliers"));
        setRecycler();
        return view;
    }

    public void setRecycler() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewManagement.setLayoutManager(layoutManager);
        recyclerViewManagement.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));//line between items
        ManagementsRecyclerAdapter managementsRecyclerAdapter = new ManagementsRecyclerAdapter(getContext(), managementList);
        recyclerViewManagement.setAdapter(managementsRecyclerAdapter);
    }
}
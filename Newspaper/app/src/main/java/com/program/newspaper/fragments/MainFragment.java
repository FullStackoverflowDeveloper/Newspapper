package com.program.newspaper.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.program.newspaper.R;
import com.program.newspaper.model.Item;
import com.program.newspaper.model.ItemAdapter;
import com.program.newspaper.presenters.MainPresenter;
import com.program.newspaper.view.MainView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainFragment extends MvpAppCompatFragment implements MainView {
    @InjectPresenter
    public MainPresenter presenter;

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private ProgressBar spinner;
    private TextView listIsEmpty;
    private FloatingActionButton fabAdd;
    private SearchView search;
    private Button btn_restart;
    private final List<Item> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        search = requireActivity().findViewById(R.id.search_line);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.searchItems(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        spinner = requireActivity().findViewById(R.id.progressBar);
        listIsEmpty = requireActivity().findViewById(R.id.nothing);
        btn_restart = requireActivity().findViewById(R.id.btn_again);
        fabAdd = requireActivity().findViewById(R.id.fab_add);

        fabAdd.setOnClickListener(v -> {
            fabAdd.setEnabled(false);
            Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_detailFragment);
        });

        btn_restart.setOnClickListener(view -> {
            startLoad();
            presenter.loadData();
        });

        recyclerView = requireActivity().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ItemAdapter(items);

        adapter.setClickListener(new ItemAdapter.ClickListener() {
            @Override
            public void itemClick(int position, View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("key", items.get(position).getId());
                Log.d("Newspaper", v.toString());
                Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_detailFragment, bundle);
            }

            @Override
            public void deleteClick(int position, View v) {
                presenter.deleteItem(items.get(position).getId());
                items.remove(items.get(position));
                adapter.notifyItemRemoved(position);
            }
        });
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.loadData();
    }

    @Override
    public void startLoad() {
        spinner.setVisibility(View.VISIBLE);
        listIsEmpty.setVisibility(View.GONE);
        btn_restart.setVisibility(View.GONE);
        Log.d("TAG", String.valueOf(items.size()));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void updateData(List<Item> data) {
        Log.d("TAG", String.valueOf(items.size()));
        if (data.size() == 0){
            showEmpty();
            items.clear();
            adapter.notifyDataSetChanged();
        }else{
            spinner.setVisibility(View.GONE);
            listIsEmpty.setVisibility(View.GONE);
            btn_restart.setVisibility(View.GONE);

            items.clear();
            items.addAll(data);
            adapter.notifyDataSetChanged();
        }
        Log.d("TAG", String.valueOf(items.size()));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void showError(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
        items.clear();
        adapter.notifyDataSetChanged();
        showEmpty();
    }

    @Override
    public void showEmpty() {
        spinner.setVisibility(View.GONE);
        listIsEmpty.setVisibility(View.VISIBLE);
        btn_restart.setVisibility(View.VISIBLE);
        Log.d("TAG", String.valueOf(items.size()));
    }
}
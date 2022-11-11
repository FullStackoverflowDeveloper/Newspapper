package com.program.newspaper.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.program.newspaper.R;
import com.program.newspaper.model.Item;
import com.program.newspaper.presenters.DetailPresenter;
import com.program.newspaper.view.DetailView;

import java.util.NavigableSet;
import java.util.Objects;

public class DetailFragment extends MvpAppCompatFragment implements DetailView {
    @InjectPresenter
    public DetailPresenter presenter;
    private ProgressBar spinner;
    private TextView title;
    private TextView text;
    private FloatingActionButton fab_save, fab_edit;
    private int oldID = 0;
    private boolean isNew = false;
    private boolean isEditable = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        spinner = requireActivity().findViewById(R.id.progressBarDetail);
        text = requireActivity().findViewById(R.id.edittext_text);
        title = requireActivity().findViewById(R.id.edittext_title);
        fab_save = requireActivity().findViewById(R.id.fab_save);
        fab_edit = requireActivity().findViewById(R.id.fab_edit);

        fab_save.setOnClickListener(v -> {
            if(text.getText().toString().isEmpty() || title.getText().toString().isEmpty()){
                showError("Заполните пустые поля");
            }else {
                fab_save.setEnabled(false);
                saving();
                Navigation.findNavController(v).navigateUp();
            }
        });

        fab_edit.setOnClickListener(v -> {
            isEditable = !isEditable;
            setEdit();
        });

        if (getArguments() != null) {
            oldID = getArguments().getInt("key");
            presenter.loadItem(oldID);
        }else{
            isNew = true;
            isEditable = true;
            dataLoaded(new Item("", ""));
        }
        setEdit();
    }

    @SuppressLint("ResourceAsColor")
    private void setEdit(){
        if (!isEditable){
            InputMethodManager manager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(fab_save.getWindowToken(), 0);
            text.setInputType(InputType.TYPE_NULL);
            title.setInputType(InputType.TYPE_NULL);
            showError("Режим редактирования выключен");
        }else{
            text.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            title.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            showError("Режим редактирования включен");
        }
    }

    @Override
    public void startLoad() {
        spinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void dataLoaded(Item item) {
        spinner.setVisibility(View.GONE);

        text.setText(item.getText());
        title.setText(item.getTitle());
    }

    @Override
    public void showError(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void saving(){
        Item item = new Item(title.getText().toString(), text.getText().toString());
        if (isNew){
            presenter.saveItem(item);
        }else {
            presenter.updateItem(oldID, item);
        }
    }
}

package com.program.newspaper.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.firebase.database.DatabaseError;
import com.program.newspaper.model.Item;
import com.program.newspaper.model.Model;
import com.program.newspaper.view.MainView;

import java.util.List;


@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {
    private final Model model;

    public MainPresenter(){
        model = new Model();
    }

    public void loadData(){
        getViewState().startLoad();
        model.checkDataExists(this);
        model.loadItemsFromDbRx(this);
    }

    public void dataNotExists(){
        getViewState().showEmpty();
    }

    public void deleteItem(int id){
        model.deleteItem(id);
    }

    public void searchItems(String title){
        model.searchItemsFromDbRx(title, this);
    }

    public void updateData(List<Item> data){
        getViewState().updateData(data);
    }

    public void showErrorMsg(Throwable error){
        getViewState().showError(error.getMessage());
    }

    public void showErrorMsg(DatabaseError error){
        getViewState().showError(error.getMessage());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        model.destroy();
    }
}

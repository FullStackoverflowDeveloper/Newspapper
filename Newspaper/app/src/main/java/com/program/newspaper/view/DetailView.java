package com.program.newspaper.view;

import com.arellomobile.mvp.MvpView;
import com.program.newspaper.model.Item;

public interface DetailView extends MvpView {
    void startLoad();
    void dataLoaded(Item item);
    void showError(String msg);
}

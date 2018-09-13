package com.kotlintraining.merseyside.kotlintraining.base.view;

public interface IActivityView extends IView {

    void hideKeyboard();

    void onBackPressed();

    void showBottomDialog();

    void showBottomDialog(String title, String content, String negativeText, String positiveText);

    void updateLanguage();
}

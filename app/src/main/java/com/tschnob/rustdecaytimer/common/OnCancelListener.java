package com.tschnob.rustdecaytimer.common;

public interface OnCancelListener {

    /**
     * Called when a cancel happens
     * @param position The position of the item in the
     *                 list that the cancel happened to
     */
    void onCancel(int position);
}

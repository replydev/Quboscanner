package me.replydev.qubo.gui;


import me.replydev.qubo.QuboInstance;
import me.replydev.utils.Log;

import javax.swing.*;

class ProgressBarRunnable implements Runnable {

    private final JProgressBar progressBar;
    private final QuboInstance quboInstance;

    public ProgressBarRunnable(JProgressBar progressBar, QuboInstance quboInstance) {
        this.progressBar = progressBar;
        this.quboInstance = quboInstance;
    }

    @Override
    public void run() {
        int percentage = (int) quboInstance.getPercentage();
        progressBar.setString(percentage + "%");
        progressBar.setValue(percentage);
        if (progressBar.getValue() == 100) return;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.log_to_file(e.toString(), "log.txt");
        }
    }

}

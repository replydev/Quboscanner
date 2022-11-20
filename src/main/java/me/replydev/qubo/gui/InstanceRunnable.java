package me.replydev.qubo.gui;

import me.replydev.qubo.QuboInstance;
import me.replydev.utils.Log;

class InstanceRunnable implements Runnable {


    private final QuboInstance quboInstance;
    private final MainWindow window;

    public InstanceRunnable(QuboInstance quboInstance, MainWindow window) { //window è un "puntatore" alla finestra principale
        this.quboInstance = quboInstance;
        this.window = window;
    }

    public void stop() {
        this.quboInstance.stop();
    }

    @Override
    public void run() {
        try {
            quboInstance.run();
        } catch (NumberFormatException e) {
            if (Confirm.requestConfirm("Check threads or timeout fields and relaunch program, would you like to see an example configuration?"))
                window.exampleConf();
        }
        window.idle();
        Log.logln("Stopped");
    }
}

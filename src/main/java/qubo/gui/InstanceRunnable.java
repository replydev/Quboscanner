package qubo.gui;

import qubo.QuboInstance;
import utils.Log;

class InstanceRunnable implements Runnable {


    private final QuboInstance quboInstance;
    private final MainWindow window;

    public void stop(){
        this.quboInstance.stop();
    }

    public InstanceRunnable(QuboInstance quboInstance, MainWindow window){ //window è un "puntatore" alla finestra principale
        this.quboInstance = quboInstance;
        this.window = window;
    }

    @Override
    public void run() {
        quboInstance.run();
        window.idle();
        Log.logln("Stopped");
    }
}

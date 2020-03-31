package utils;

import qubo.Info;

import javax.swing.*;

public class Confirm {

    public static boolean getConfirm(String text){
        if(Info.gui){
            return JOptionPane.showConfirmDialog(null, text, "Confirm",  JOptionPane.YES_NO_OPTION) == 0;
        }
        else{
            return Keyboard.s(text).equalsIgnoreCase("s");
        }
    }
}

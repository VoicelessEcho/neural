package com.neural;

import com.neural.main.Controller;

/**
 * Created by Virgis on 2017.06.24.
 */
public class Main {
    public static void main(String[] args){
        Controller controller = new Controller();
        String fName = null;
        String number = null;
        if (args.length != 0){
            fName = args[0];
            number = args[1];
        }
        controller.start(fName, number);
        return;
    }
}

package edu.developmentaid;

import edu.developmentaid.util.Printer;
import edu.developmentaid.service.Service;

public class Main {
    public static void main(String[] args) {
        Printer.printToStdout(new Service().parse());
    }
}
package org.Utilities;
import org.Utilities.Generic;
public class Runner {
    public static void main(String[] args) throws Exception {


        Generic gen = new Generic();

        System.out.println("Execution started..........");

        gen.compareXmlFiles();
        System.out.println("Execution Completed........");
    }
}
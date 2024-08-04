package com.github.bigsheykh.javacodesforlastproject;

import java.io.File;
// import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        File pomFile = new File(CustomModelling.getBaseDir(), "pom.xml");
        new CustomModelling(pomFile);
    }
}

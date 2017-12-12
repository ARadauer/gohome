package com.radauer.gohome;

/**
 * Created by Andreas on 30.08.2017.
 */
public class Settings {

    public static int worDayDurationMinutes = (int) (10.5 * 60);
    public static int warningBoundary = 5;
    public static int infoBoundary = 30;

    public static String taskFilePath = "d:/tasks.txt";
    public static int taskIntervallMinutes = 30;
    public static int serverCheckIntervallSecounds = 10;
    public static String[][] apps = {
            {"https://cc.porscheinformatik.com/cc-at/health", "CC AT"},
            {"https://cc.porscheinformatik.com/cc-hu/health", "CC HU"},
            {"https://cc.porscheinformatik.com/cc-ro/health", "CC RO"}
    };
}

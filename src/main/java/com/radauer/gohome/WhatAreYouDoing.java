package com.radauer.gohome;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javax.swing.JOptionPane;


import static com.radauer.gohome.Settings.taskFilePath;
import static com.radauer.gohome.Settings.taskIntervallMinutes;

/**
 * Created by Andreas on 30.08.2017.
 */
public class WhatAreYouDoing implements Runnable {

    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    private static DateTimeFormatter timeFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private File file = new File(taskFilePath);
    private LocalDateTime lastTaskStart;
    private String lastTask;

    public WhatAreYouDoing() {
        new Thread(this).start();
    }


    @Override
    public void run() {
        System.out.println("What are you doing?");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while (true) {
            askForTask();
            try {
                Thread.sleep(1000 * taskIntervallMinutes * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void askForTask() {
        String task = JOptionPane.showInputDialog("Was machst du gerade?", lastTask);

        if (lastTask == null || !lastTask.equals(task)) {
            handleNewTask(task);

        }
    }

    private void handleNewTask(String task) {
        System.out.println("neuer task " + task);
        LocalDateTime newTaskStart = LocalDateTime.now();
        if (lastTaskStart != null) {
            Duration duration = Duration.between(lastTaskStart, newTaskStart);
            System.out.println("Letzter Task hat " + printDurration(duration) + " gedauert");
            write(";" + printDurration(duration));
        }
        write("\r\n");
        System.out.println("starte Task " + task);
        lastTask = task;
        lastTaskStart = newTaskStart;
        write(newTaskStart.format(dateFormat) + ";" + newTaskStart.format(timeFormat) + ";" + task);
    }

    private void write(String text) {
        try {
            Files.write(file.toPath(), text.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String printDurration(Duration duration) {
        return duration.toMinutes() + ":" + (duration.getSeconds() % 60);
    }
}

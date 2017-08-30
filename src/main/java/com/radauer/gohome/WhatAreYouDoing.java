package com.radauer.gohome;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Andreas on 30.08.2017.
 */
public class WhatAreYouDoing implements Runnable {


    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
    private static long intervallSec = 30 * 60;
    private File file = new File("d:/tasks.txt");
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
                Thread.sleep(1000 * intervallSec);
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
            write(";" + printDurration(duration) + "\n");
        }
        write("\r\n");
        System.out.println("starte Task " + task);
        lastTask = task;
        lastTaskStart = newTaskStart;
        write(newTaskStart.format(dateTimeFormatter) + ";" + task);
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

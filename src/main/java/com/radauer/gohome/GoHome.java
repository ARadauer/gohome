package com.radauer.gohome;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Created by Andreas on 06.07.2017.
 */
public class GoHome implements Runnable {

    private WhatAreYouDoing whatAreYouDoing;

    Image green = ImageIO.read(ClassLoader.getSystemResource("green.png"));
    Image blue = ImageIO.read(ClassLoader.getSystemResource("blue.png"));
    Image red = ImageIO.read(ClassLoader.getSystemResource("red.png"));
    Image yellow = ImageIO.read(ClassLoader.getSystemResource("yellow.png"));

    int workDayDuration = (int) (10.5*60);

    private LocalDateTime lastRefresh;
    private LocalTime startWorkDay;
    private LocalTime endWorkDay;

    boolean yellowWarning = false;
    boolean redWarning = false;


    private TrayIcon trayIcon;

    public GoHome() throws IOException, AWTException {
        //System.out.println(image.getGraphics().getClip().getBounds().width);
        final PopupMenu popup = new PopupMenu();
        trayIcon = new TrayIcon(blue, "");
        trayIcon.setImageAutoSize(true);
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a pop-up menu components
        MenuItem refreshItem = new MenuItem("Arbeitsbeginn neu laden");
        refreshItem.addActionListener(e -> refreshStartTime());
        MenuItem newTaskItem = new MenuItem("Neuer Task");
        newTaskItem.addActionListener(e -> whatAreYouDoing.askForTask());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(-1));
        popup.add(refreshItem);
        popup.add(newTaskItem);
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);
        tray.add(trayIcon);
        new Thread(this).start();
        whatAreYouDoing = new WhatAreYouDoing();

    }


    public static void main(String[] args) throws Exception {

        new GoHome();


    }

    private void updateTrayIcon() {

        checkForUpdate();
        System.out.println("Update");

        Duration workDuration = Duration.between(startWorkDay, LocalTime.now());
        System.out.println("work Duration " + workDuration);
        int minutesLeft = (int) (workDayDuration - (workDuration.getSeconds() / 60));

        if (minutesLeft < 5) {
            if (!redWarning) {
                trayIcon.displayMessage("Zeitüberschreitung", "Noch 5 Minuten!", TrayIcon.MessageType.ERROR);
                redWarning = true;
            }
            System.out.println("Set red");
            trayIcon.setImage(red);
        } else if (minutesLeft <= 30) {
            if (!yellowWarning) {
                trayIcon.displayMessage("Zeitüberschreitung", "Noch 30 Minuten!", TrayIcon.MessageType.WARNING);
                yellowWarning = true;
            }
            trayIcon.setImage(yellow);
        } else {
            trayIcon.setImage(green);
        }
        int hoursLeft = minutesLeft / 60;
        minutesLeft = minutesLeft % 60;
        System.out.println("minutes Left " + minutesLeft);

        trayIcon.setToolTip("Noch " + hoursLeft + ":" + ((minutesLeft < 10) ? "0" : "") + minutesLeft + " bis " + endWorkDay);

    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            updateTrayIcon();
        }

    }

    private void checkForUpdate() {
        LocalDateTime now = LocalDateTime.now();

        if (lastRefresh == null || now.isAfter(lastRefresh.plusDays(1))) {
            refreshStartTime();

        }

    }

    private void refreshStartTime() {
        String time = JOptionPane.showInputDialog("Wann hast du heute zu arbeiten begonnen?");
        if (time == null) {
            return;
        }
        System.out.println(time);
        lastRefresh = LocalDateTime.now();
        String[] timeparts = time.split(":");
        startWorkDay = LocalTime.of(Integer.parseInt(timeparts[0]), Integer.parseInt(timeparts[1]));
        endWorkDay = startWorkDay.plusMinutes((long) (workDayDuration));
        System.out.println("Started work day on: " + startWorkDay);
        System.out.println("End work day on: " + endWorkDay);
        yellowWarning = false;
        redWarning = false;
    }
}

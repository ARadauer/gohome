package com.radauer.gohome;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.radauer.gohome.Settings.*;

/**
 * Created by Andreas on 06.07.2017.
 */
public class GoHome implements Runnable {

    private WhatAreYouDoing whatAreYouDoing;
    private ServerChecker serverChecker;

    public static Image green;

    public static Image blue;
    public static Image red;
    public static Image yellow;
    public static Icon upIcon;
    public static Icon downIcon;



    private LocalDateTime lastRefresh;
    private LocalTime startWorkDay;
    private LocalTime endWorkDay;

    boolean yellowWarning = false;
    boolean redWarning = false;


    private TrayIcon trayIcon;

    public GoHome() throws IOException, AWTException {
        //System.out.println(image.getGraphics().getClip().getBounds().width);
        final JPopupMenu popup = new JPopupMenu();
        trayIcon = new TrayIcon(blue, "");
        trayIcon.setImageAutoSize(true);
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a pop-up menu components
        JMenuItem refreshItem = new JMenuItem("Arbeitsbeginn neu laden");
        refreshItem.addActionListener(e -> refreshStartTime());
        JMenuItem newTaskItem = new JMenuItem("Neuer Task");
        newTaskItem.addActionListener(e -> whatAreYouDoing.askForTask());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(-1));


        serverChecker = new ServerChecker();

        popup.add(serverChecker.getMenuItem());
        popup.add(refreshItem);
        popup.add(newTaskItem);
        popup.add(exitItem);

        //trayIcon.setPopupMenu(popup);
        trayIcon.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popup.setLocation(e.getX(), e.getY());
                    popup.setInvoker(popup);
                    popup.setVisible(true);
                }
            }
        });
        tray.add(trayIcon);
        new Thread(this).start();
        whatAreYouDoing = new WhatAreYouDoing();


    }


    public static void main(String[] args) throws Exception {
        green = ImageIO.read(ClassLoader.getSystemResource("green.png"));
        blue = ImageIO.read(ClassLoader.getSystemResource("blue.png"));
        red = ImageIO.read(ClassLoader.getSystemResource("red.png"));
        yellow = ImageIO.read(ClassLoader.getSystemResource("yellow.png"));
        upIcon = new ImageIcon(green);
        downIcon = new ImageIcon(red);
        new GoHome();


    }

    private void updateTrayIcon() {

        checkForUpdate();
        Duration workDuration = Duration.between(startWorkDay, LocalTime.now());
        int minutesLeft = (int) (worDayDurationMinutes - (workDuration.getSeconds() / 60));

        if (minutesLeft < warningBoundary) {
            if (!redWarning) {
                trayIcon.displayMessage("Zeitüberschreitung", "Noch " + warningBoundary + " Minuten!", TrayIcon.MessageType.ERROR);
                redWarning = true;
            }
            trayIcon.setImage(red);
        } else if (minutesLeft <= infoBoundary) {
            if (!yellowWarning) {
                trayIcon.displayMessage("Zeitüberschreitung", "Noch " + infoBoundary + " Minuten!", TrayIcon.MessageType.WARNING);
                yellowWarning = true;
            }
            trayIcon.setImage(yellow);
        } else {
            trayIcon.setImage(green);
        }
        int hoursLeft = minutesLeft / 60;
        minutesLeft = minutesLeft % 60;

        trayIcon.setToolTip("Noch " + hoursLeft + ":" + ((minutesLeft < 10) ? "0" : "") + minutesLeft + " bis " + endWorkDay);

    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000 * 30);
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
        endWorkDay = startWorkDay.plusMinutes((long) (worDayDurationMinutes));
        System.out.println("Started work day on: " + startWorkDay);
        System.out.println("End work day on: " + endWorkDay);
        yellowWarning = false;
        redWarning = false;
    }
}

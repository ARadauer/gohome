package com.radauer.gohome;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.radauer.gohome.GoHome.green;
import static com.radauer.gohome.Settings.serverCheckIntervallSecounds;

/**
 * Server Checker Tool
 */
public class ServerChecker implements Runnable {

    private boolean overallUp = false;

    private List<ServerToCheck> servers = new ArrayList<>();

    private JMenu menuItem;

    public ServerChecker() {
        menuItem = new JMenu("Server status");
        menuItem.setIcon(new ImageIcon(green));

        for (String[] app : Settings.apps) {
            JMenuItem serverItem = new JMenuItem(app[1]);

            menuItem.add(serverItem);
            ServerToCheck server = new ServerToCheck(app[0], app[1], serverItem);
            servers.add(server);

            serverItem.addActionListener(e -> showServerInfo(server));
        }


        new Thread(this).start();
    }

    private void showServerInfo(ServerToCheck server) {
        JFrame frame = new JFrame(server.getName());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.add(new JTable(server));

        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Check Servers");
            checkServers();
            try {
                Thread.sleep(serverCheckIntervallSecounds * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkServers() {
        boolean newUp = true;
        for (ServerToCheck server : servers) {

            try {

                long t = System.currentTimeMillis();
                String result = Util.readUrl(server.getUrl());
                int duration = (int) (System.currentTimeMillis() - t);
                boolean up = result.contains("{\"status\":\"UP\"}");


                JSch jsch = new JSch();
                Session session = jsch.getSession(user, host);
                session.setPassword(password);
                session.connect(timeout);
                session.setPortForwardingL(listenPort, destHost, destPort);




                System.out.println("Result from " + server.getName());
                System.out.println(result);
                server.addMessurePoint(up, duration);
                if (!up) {
                    newUp = false;
                }

            } catch (IOException e) {
                server.addMessurePoint(false, 0);
                newUp = false;
                e.printStackTrace();
            }

        }
        this.overallUp = newUp;
    }

    public boolean isOverallUp() {
        return overallUp;
    }

    public void setOverallUp(boolean overallUp) {
        this.overallUp = overallUp;
    }

    public List<ServerToCheck> getServers() {
        return servers;
    }

    public void setServers(List<ServerToCheck> servers) {
        this.servers = servers;
    }

    public JMenuItem getMenuItem() {
        return menuItem;
    }
}

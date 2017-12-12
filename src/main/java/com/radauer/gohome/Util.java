package com.radauer.gohome;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Andreas on 12.12.2017.
 */
public class Util {

    public static String readUrl(String urlString) throws IOException {
        URL url = new URL(urlString);

        StringBuilder result = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }

        }
        return result.toString();
    }
}

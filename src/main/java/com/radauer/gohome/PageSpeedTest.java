package com.radauer.gohome;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PageSpeedTest
{
    private static ObjectMapper mapper = new ObjectMapper();

    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    private static File fileToWrite = new File("d:/pagespeed.txt");
    private static String apiUrl = "https://www.googleapis.com/pagespeedonline/v4/runPagespeed";
    private static String baseUrl = "https://cc.porscheinformatik.com/";

    public static String[] pages = {
        "cc-at/de_AT_VW/V/models", "cc-at/de_AT_AUDI/A/models", "cc-at/de_AT_SKODA/C/models",
        "cc-at/de_AT_SEAT/S/models", "cc-at/de_AT_LNF/L/models", "cc-at/de_AT_PORSCHE/P/models"};
    public static boolean[] withoutTemplates = {false, true};
    public static String[] strategies = {"desktop", "mobile"};

    public static void main(String[] args) throws IOException
    {
        LocalDateTime today = LocalDateTime.now();
        if (!fileToWrite.exists())
        {
            fileToWrite.createNewFile();
        }
        for (String page : pages)
        {
            for (boolean withTemplate : withoutTemplates)
            {
                for (String strategy : strategies)
                {
                    String urlToTest = apiUrl + "?strategy=" + strategy +
                        "&url=" + baseUrl + page
                        + (withTemplate ? "" : "=VUID=pagespeed");

                    System.out.println("Test: " + urlToTest);
                    String score = getScore(urlToTest);
                    System.out.println("Score: " + score);
                    String result =
                        dateFormat.format(today) + ";" + page + ";" + strategy + ";" + (withTemplate ? "mitBranding" :
                            "ohneBranding") + ";" + score + "\r\n";
                    writeResult(result);
                }
            }
        }

    }

    private static String getScore(String url) throws IOException
    {
        JsonNode node = mapper.readTree(new URL(url));
        return node.get("ruleGroups").get("SPEED").get("score").asText();

    }

    private static void writeResult(String text)
    {
        try
        {
            Files.write(fileToWrite.toPath(), text.getBytes(), StandardOpenOption.APPEND);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

package org.micds.util;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@UtilityClass
public class HttpUtil {
    private static final String TITLE_START = "<title>", TITLE_END = "</title>";

    public static String getTitle(final URL url) {
        BufferedReader br;
        String line;
        boolean foundStartTag = false;
        boolean foundEndTag = false;
        int startIndex, endIndex;
        String title = "";

        try {
            br = new BufferedReader(new InputStreamReader(url.openStream()));

            while ((line = br.readLine()) != null && !foundEndTag) {
                if (!foundStartTag && (startIndex = line.toLowerCase().indexOf(TITLE_START)) != -1) {
                    foundStartTag = true;
                } else {
                    startIndex = -TITLE_START.length();
                }

                if (foundStartTag && (endIndex = line.toLowerCase().indexOf(TITLE_END)) != -1) {
                    foundEndTag = true;
                } else {
                    endIndex = line.length();
                }

                if (foundStartTag || foundEndTag) {
                    title += line.substring(startIndex + TITLE_START.length(), endIndex);
                }
            }

            br.close();
        } catch (IOException e) {
            System.out.println(e);
        }

        return title;
    }

}

package org.micds.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringEscapeUtils;
import org.micds.TastyTunes;
import org.micds.req.SongRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;

@UtilityClass
public class HttpUtil {
    public static final String TITLE_START = "<title>", TITLE_END = "</title>", YT_SUFFIX = " - YouTube",
            YT_SHORT = "youtu.be/", YT_WATCH = "watch?v=", SND_SUFFIX = " | Free Listening on SoundCloud",
            SC_LINK = "soundcloud.com", YT_LINK = "youtube.com";

    /**
     * Reads a web page and finds the title
     *
     * @param url the website url
     * @return the page title
     */
    public static String getTitle(final URL url) {
        BufferedReader br;
        String line;
        boolean foundStartTag = false;
        boolean foundEndTag = false;
        int startIndex, endIndex;
        StringBuilder title = new StringBuilder();

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
                    title.append(line.substring(startIndex + TITLE_START.length(), endIndex));
                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // remove website suffixes at the end
        int ytEnd = title.length() - YT_SUFFIX.length();
        if (ytEnd >= 0 && title.substring(ytEnd).equals(YT_SUFFIX)) {
            title.setLength(ytEnd);
        } else {
            int sndEnd = title.length() - SND_SUFFIX.length();
            if (sndEnd >= 0 && title.substring(sndEnd).equals(SND_SUFFIX)) {
                title.setLength(sndEnd);
            }
        }

        return StringEscapeUtils.unescapeHtml4(title.toString());
    }

    /**
     * Uses youtube-dl to download the mp3 file of the passed url to the passed directory
     *
     * @param directory the location to save the mp3
     * @param url       the url to the media to be downloaded
     */
    public static void downloadMP3(final String directory, final String url) {
        final String[] envp = {"PATH=" + TastyTunes.getFFMPEG()};
        StringBuilder cmd = new StringBuilder("cmd /c ")
                .append(TastyTunes.getYouTubeDL())
                .append(" --extract-audio --audio-format mp3 --audio-quality=320k")
                .append(" --output ").append(directory).append(directory.endsWith("\\") ? "" : "\\")
                .append("%(id)s.%(ext)s ").append(url);

        try {
            Process p = Runtime.getRuntime().exec(cmd.toString(), envp);

            /*
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            System.out.println("output:\n");
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            System.out.println("errors:\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            */

            int exitVal = p.waitFor();
            if (exitVal != 0) {
                throw new RuntimeException("Failed to download");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Optional<String> getID(SongRequest req) {
        final String link = req.getLink(), linkLower = req.getLink().toLowerCase();

        int timeIndex = linkLower.indexOf("?t=");
        int end = (timeIndex != -1) ? timeIndex : link.length();

        int ytShort = linkLower.indexOf(YT_SHORT);
        if (ytShort != -1) {
            return Optional.of(link.substring(ytShort + YT_SHORT.length(), end));
        }

        int ytFull = linkLower.indexOf(YT_WATCH);
        if (ytFull != -1) {
            return Optional.of(link.substring(ytFull + YT_WATCH.length(), end));
        }

        if (linkLower.contains("soundcloud")) {
            String resolveLink = "http://api.soundcloud.com/resolve.json?url=" + link
                    + "&client_id=02gUJC0hH2ct1EGOcYXQIzRFU91c72Ea";

            try {
                URL resolve = new URL(resolveLink);

                BufferedReader br = new BufferedReader(new InputStreamReader(resolve.openStream()));

                JsonParser jp = new JsonParser();
                JsonObject obj = jp.parse(br).getAsJsonObject();

                if (obj.has("status") && obj.get("status").getAsString().equals("302 - Found")) {
                    resolveLink = obj.get("location").getAsString();
                    resolve = new URL(resolveLink);
                    br = new BufferedReader(new InputStreamReader(resolve.openStream()));
                    obj = jp.parse(br).getAsJsonObject();
                }

                br.close();

                if (obj.has("id")) {
                    return Optional.of(obj.get("id").getAsString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

}

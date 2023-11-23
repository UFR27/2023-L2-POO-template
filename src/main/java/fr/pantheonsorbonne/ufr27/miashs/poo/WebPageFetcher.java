package fr.pantheonsorbonne.ufr27.miashs.poo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebPageFetcher {

    public static void main(String... args) {
        System.out.println(ContentProxy.getFresh());
    }

    private static final String PROXY = "https://scrapper.miage.dev/url";
    //private static final String PROXY = "http://localhost:8080/url";

    public String getFreshContent(String urlString) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(PROXY);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(true); // Enable automatic redirect handling

            // Sending urlString as plain text
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = urlString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            // Handling manual redirects (3xx codes)
            while (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                    responseCode == HttpURLConnection.HTTP_SEE_OTHER) {

                // Get redirect URL from "location" header field
                String newUrl = connection.getHeaderField("Location");
                url = new URL(newUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = urlString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                responseCode = connection.getResponseCode();
            }

            // Process the final response
            if (responseCode >= 200 && responseCode <= 299) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                return content.toString();
            } else {
                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching content", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

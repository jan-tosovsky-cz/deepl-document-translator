package tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class App {

    private static final String TRANSLATE_URL = "https://api-free.deepl.com/v2/document";
    private static final String DEEPL_AUTH_KEY = System.getenv("DEEPL_AUTH_KEY");
    private static final String LINE_BREAK = "\r\n";

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void main(String[] args) throws Exception {
        Path path = Paths.get(args[0]);
        String targetLanguage = args[1];

        translateDocument(path, "EN", targetLanguage);
    }

    private static void translateDocument(
            Path path, String sourceLanguage, String targetLanguage)
            throws Exception {

        String boundary = UUID.randomUUID().toString();

        String[] headers = new String[]{
                "Content-Type", "multipart/form-data;boundary=" + boundary,
                "Authorization", "DeepL-Auth-Key " + DEEPL_AUTH_KEY
        };

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("source_lang", sourceLanguage);
        dataMap.put("target_lang", targetLanguage);
        dataMap.put("file", path);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TRANSLATE_URL))
                .headers(headers)
                .POST(ofMimeMultipartData(dataMap, boundary))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }

    private static HttpRequest.BodyPublisher ofMimeMultipartData(
            Map<String, Object> data, String boundary)
            throws IOException {

        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
                PrintWriter writer = new PrintWriter(outputStreamWriter)) {

            for (Map.Entry<String, Object> entry : data.entrySet()) {

                if (entry.getValue() instanceof String) {

                    writer.append("--").append(boundary).append(LINE_BREAK);
                    writer
                            .append("Content-Disposition: form-data; name=\"")
                            .append(entry.getKey())
                            .append("\"")
                            .append(LINE_BREAK);
                    writer.append(LINE_BREAK);
                    writer.append((String) entry.getValue()).append(LINE_BREAK);
                    writer.flush();

                } else if (entry.getValue() instanceof Path) {

                    Path path = (Path) entry.getValue();

                    writer.append("--").append(boundary).append(LINE_BREAK);
                    writer
                            .append("Content-Disposition: form-data; name=\"")
                            .append(entry.getKey())
                            .append("\"; filename=\"")
                            .append(path.getFileName().toString())
                            .append("\"")
                            .append(LINE_BREAK);
                    writer.append("Content-Type: application/octet-stream").append(LINE_BREAK);
                    //writer.append("Content-Type: text/plain").append(LINE_BREAK);
                    //writer.append("Content-Transfer-Encoding: binary").append(LINE_BREAK);
                    writer.append(LINE_BREAK);
                    writer.flush();

                    try (InputStream inputStream = Files.newInputStream(path)) {
                        inputStream.transferTo(outputStream);
                    }

                    writer.append(LINE_BREAK);
                    writer.flush();
                }
            }

            writer.append("--").append(boundary).append("--").append(LINE_BREAK);
            writer.flush();
            writer.close();

            return HttpRequest.BodyPublishers.ofByteArray(outputStream.toByteArray());
        }
    }

}

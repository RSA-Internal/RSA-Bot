package org.rsa.wandbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rsa.wandbox.entities.CompileParameter;
import org.rsa.wandbox.entities.CompileResult;
import org.rsa.wandbox.entities.CompilerInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class WandboxAPI {

    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeReference<List<CompilerInfo>> COMPILER_INFO_LIST_TYPE = new TypeReference<>() {};

    private String httpRequest(String endpoint, String method, String body) {
        String BASE_URL = "https://wandbox.org";
        String targetURL = BASE_URL + "/" + endpoint;

        try {
            URL url = new URL(targetURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);

            if ("POST".equals(method)) {
                con.setDoOutput(true);
                if (null == body) {
                    System.out.println("Method was POST but no body was provided.");
                } else {
                    try(OutputStream os = con.getOutputStream()) {
                        byte[] input = body.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                }
            }

            StringBuilder response = new StringBuilder();

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            con.disconnect();

            return response.toString();
        } catch (IOException e) {
            System.err.println("Failed to process httpRequest: {URL: " + targetURL + ", Method: " + method + "}.\n" + e.getMessage());
            return "";
        }
    }

    public List<CompilerInfo> getList() {
        try {
            String jsonResult = httpRequest("/api/list.json", "GET", null);
            return mapper.readValue(jsonResult, COMPILER_INFO_LIST_TYPE);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to get compiler list. " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public CompileResult compileJson(CompileParameter compileParameter) {
        try {
            String jsonResult = httpRequest("/api/compile.json", "POST", compileParameter.toJson());
            return mapper.readValue(jsonResult, CompileResult.class);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to compile code. " + e.getMessage());
            CompileResult result = new CompileResult();
            result.setStatus("0");
            result.setCompiler_error(e.getMessage());
            return result;
        }
    }
}

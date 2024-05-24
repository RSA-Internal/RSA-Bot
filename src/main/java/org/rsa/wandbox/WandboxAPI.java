package org.rsa.wandbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class WandboxAPI {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeReference<List<CompilerInfo>> COMPILER_INFO_LIST_TYPE = new TypeReference<>() {};

    private static String httpRequest(String endpoint, String method, String body) {
        String BASE_URL = "https://wandbox.org";
        String targetURL = BASE_URL + "/" + endpoint;

        try {
            URL url = new URL(targetURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);

            if ("POST".equals(method)) {
                con.setDoOutput(true);
                if (null == body) {
                    log.warn("Method was POST but no body was provided.");
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
            log.error("Failed to process httpRequest: {URL: " + targetURL + ", Method: " + method + "}.\n" + e.getMessage());
            return "";
        }
    }

    public static List<CompilerInfo> getList() {
        try {
            String jsonResult = httpRequest("/api/list.json", "GET", null);
            return mapper.readValue(jsonResult, COMPILER_INFO_LIST_TYPE);
        } catch (JsonProcessingException e) {
            log.error("Failed to get compiler list. " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public static CompileResult compileJson(CompileParameter compileParameter) {
        try {
            String jsonResult = httpRequest("/api/compile.json", "POST", compileParameter.toJson());
            return mapper.readValue(jsonResult, CompileResult.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to compile code. " + e.getMessage());
            CompileResult result = new CompileResult();
            result.setStatus("0");
            result.setCompiler_error(e.getMessage());
            return result;
        }
    }
}

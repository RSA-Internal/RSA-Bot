package org.rsa.aws;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
public class SecretsManager {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<HashMap<String, String>> SECRET_TYPE_REFERENCE = new TypeReference<>() {};

    public static final String BOT_TOKEN_KEY = "prod/RSABot/token";

    private static SecretsManagerClient getSecretManager() {
        return SecretsManagerClient.builder()
            .region(Region.US_WEST_2)
            .build();
    }

    public static String getValue(String secretName) {
        try {
            SecretsManagerClient secretsClient = getSecretManager();
            GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            GetSecretValueResponse valueResponse = secretsClient.getSecretValue(valueRequest);
            String secretResponse = valueResponse.secretString();
            HashMap<String, String> secret = objectMapper.readValue(secretResponse, SECRET_TYPE_REFERENCE);
            String secretValue = secret.values().stream().findFirst().orElse(null);

            secretsClient.close();
            return secretValue;
        } catch (SecretsManagerException e) {
            log.error(e.awsErrorDetails().errorMessage());
            return null;
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}

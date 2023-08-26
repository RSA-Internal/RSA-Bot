package org.rsa.aws;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

import java.io.IOException;
import java.util.HashMap;

public class SecretsManager {

    private static SecretsManagerClient instance;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<HashMap<String, String>> SECRET_TYPE_REFERENCE = new TypeReference<>() {};

    public static final String BOT_TOKEN = "prod/RSABot/token";

    public static SecretsManagerClient getSecretManager() {
        if (null == instance) {
            instance = SecretsManagerClient.builder()
                    .region(Region.US_WEST_2)
                    .build();
        }
        return instance;
    }

    public static String getValue(SecretsManagerClient secretsClient, String secretName) {
        try {
            GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();

            GetSecretValueResponse valueResponse = secretsClient.getSecretValue(valueRequest);
            String secretResponse = valueResponse.secretString();
            System.out.println(secretResponse);
            HashMap<String, String> secret = objectMapper.readValue(secretResponse, SECRET_TYPE_REFERENCE);
            System.out.println(secret);
            String secretValue = secret.values().stream().findFirst().orElse(null);

            System.out.println("Secret: " + secretValue);
            return secretValue;
        } catch (SecretsManagerException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            return null;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}

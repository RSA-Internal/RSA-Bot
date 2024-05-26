package org.rsa.net.apis.wandbox.models;

public record CompileResultModel(
        String status,
        String compiler_output,
        String compiler_error,
        String compiler_message,
        String program_output,
        String program_error,
        String program_message
) {
    public String getStatus() {
        return switch (status) {
            case "0" -> "Success";
            case "1" -> "Failed";
            default -> "Unknown";
        };
    }
}

package org.rsa.net.apis.wandbox.models;

import com.google.gson.annotations.SerializedName;
import org.rsa.net.apis.wandbox.models.compiler.SwitchModel;

import java.util.List;

public record CompilerInfoModel(
        @SerializedName("compiler-option-raw") boolean compilerOptionRaw,
        @SerializedName("runtime-option-raw") boolean runtimeOptionRaw,
        @SerializedName("display-compile-command") String displayCompileCommand,
        List<SwitchModel> switchModels,
        String name,
        String version,
        String language,
        @SerializedName("display-name") String displayName,
        List<String> templates
) {}

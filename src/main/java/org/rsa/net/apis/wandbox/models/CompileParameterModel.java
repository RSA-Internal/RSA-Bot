package org.rsa.net.apis.wandbox.models;

import com.google.gson.annotations.SerializedName;

public record CompileParameterModel(
        String code,
        String options,
        String compiler,
        @SerializedName("compiler-option-raw") String compilerOptionRaw
) {}

package org.rsa.net.apis.wandbox.models.compiler;

import com.google.gson.annotations.SerializedName;

public record OptionModel(
        String name,
        @SerializedName("display-flags") String displayFlags,
        @SerializedName("display-name") String displayName
) {}

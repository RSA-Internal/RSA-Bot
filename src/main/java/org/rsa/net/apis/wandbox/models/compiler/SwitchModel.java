package org.rsa.net.apis.wandbox.models.compiler;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public record SwitchModel(
        @SerializedName("default") Object myDefault,
        String name,
        @SerializedName("display-flags") String displayFlags,
        @SerializedName("display-name") String displayName,
        List<OptionModel> optionModels,
        String type
) {}

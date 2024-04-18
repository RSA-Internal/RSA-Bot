package org.rsa.net.apis.wandbox;

import com.google.common.reflect.TypeToken;
import org.rsa.net.HttpClient;
import org.rsa.net.apis.wandbox.models.CompileParameterModel;
import org.rsa.net.apis.wandbox.models.CompileResultModel;
import org.rsa.net.apis.wandbox.models.CompilerInfoModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class WandboxAPI {
    private static final Type COMPILER_INFO_LIST_TYPE = new TypeToken<List<CompilerInfoModel>>() {}.getType();

    private static final String BASE_URL = "https://wandbox.org";

    public static List<CompilerInfoModel> getList() {
        try {
            return HttpClient.get(BASE_URL + "/api/list.json", COMPILER_INFO_LIST_TYPE);
        } catch (IOException e) {
            System.err.println("Failed to get compiler list. " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public static CompileResultModel compileJson(CompileParameterModel compileParameterModel) {
        try {
            return HttpClient.post(BASE_URL + "/api/compile.json", compileParameterModel, CompileResultModel.class);
        } catch (IOException e) {
            System.err.println("Failed to compile code. " + e.getMessage());
            return new CompileResultModel("1", "", e.getMessage(), "", "", "", "");
        }
    }
}

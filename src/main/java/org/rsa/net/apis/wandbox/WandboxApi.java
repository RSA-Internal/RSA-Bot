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

public class WandboxApi {
    private static final Type COMPILER_INFO_LIST_TYPE = new TypeToken<List<CompilerInfoModel>>() {}.getType();
    private final String BASE_URL;
    private final HttpClient httpClient;

    public WandboxApi(HttpClient httpClient, String baseUrl) {
        this.httpClient = httpClient;
        this.BASE_URL = baseUrl;
    }

    public List<CompilerInfoModel> getList() {
        try {
            return httpClient.get(BASE_URL + "/api/list.json", COMPILER_INFO_LIST_TYPE);
        } catch (IOException e) {
            System.err.println("Failed to get compiler list. " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public CompileResultModel compileJson(CompileParameterModel compileParameterModel) {
        try {
            return httpClient.post(BASE_URL + "/api/compile.json", compileParameterModel, CompileResultModel.class);
        } catch (IOException e) {
            System.err.println("Failed to compile code. " + e.getMessage());
            return new CompileResultModel("1", "", e.getMessage(), "", "", "", "");
        }
    }
}

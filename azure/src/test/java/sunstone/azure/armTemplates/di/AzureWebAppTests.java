package sunstone.azure.armTemplates.di;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import sunstone.annotation.Parameter;
import sunstone.annotation.inject.Hostname;
import sunstone.azure.annotation.AzureWebApplication;
import sunstone.azure.annotation.WithAzureArmTemplate;
import sunstone.azure.armTemplates.AzureTestConstants;

import java.io.IOException;

@WithAzureArmTemplate(template = "sunstone/azure/armTemplates/eapWebApp.json",
        parameters = {@Parameter(k = "appName", v = AzureTestConstants.instanceName)}, group = AzureWebAppTests.group)
public class AzureWebAppTests {
    public static final String group = "sunstone-web-app";
    @AzureWebApplication(name = AzureTestConstants.instanceName, group = AzureWebAppTests.group)
    Hostname hostname;

    @Test
    public void test() throws IOException, InterruptedException {
        // todo we need waiters!
        waitForHttpOK(hostname, 1000 * 60 * 3);
        OkHttpClient client = new OkHttpClient();

        // todo we need preconfigured rest assured injected!
        Request request = new Request.Builder()
                .url("http://" + hostname.get())
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        Assertions.assertThat(response.code()).isEqualTo(200);
    }

    private static void waitForHttpOK(Hostname url, int timeoutMilis) throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMilis) {
            try {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://" + url.get())
                        .method("GET", null)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.code() == 200) {
                    return;
                }
            } catch (Exception e) {
                Thread.sleep(200);
            }
        }
        Assertions.fail("timeout");
    }
}

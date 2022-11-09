package sunstone.core;


import com.azure.core.credential.TokenCredential;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.appservice.models.WebApp;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.wildfly.extras.sunstone.api.impl.ObjectProperties;
import org.wildfly.extras.sunstone.api.impl.ObjectType;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Set;

public class AzureUtils {
    private static ObjectProperties objectProperties = new ObjectProperties(ObjectType.JUNIT5, null);

    static AzureResourceManager getResourceManager() {
        return AzureResourceManager
                .authenticate(getCredentials(), new AzureProfile(AzureEnvironment.AZURE))
                .withSubscription(objectProperties.getProperty(JUnit5Config.Azure.SUBSCRIPTION_ID));
    }

    private static TokenCredential getCredentials() {
        return new ClientSecretCredentialBuilder()
                .tenantId(objectProperties.getProperty(JUnit5Config.Azure.TENANT_ID))
                .clientId(objectProperties.getProperty(JUnit5Config.Azure.APPLICATION_ID))
                .clientSecret(objectProperties.getProperty(JUnit5Config.Azure.PASSWORD))
                .build();
    }

    static boolean propertiesForArmClientArePresent() {
        return objectProperties.getProperty(JUnit5Config.Azure.SUBSCRIPTION_ID) != null
                && objectProperties.getProperty(JUnit5Config.Azure.TENANT_ID) != null
                && objectProperties.getProperty(JUnit5Config.Azure.APPLICATION_ID) != null
                && objectProperties.getProperty(JUnit5Config.Azure.PASSWORD) != null;
    }

    static VirtualMachine findAzureVM(AzureResourceManager arm, String name, Set<String> resourceGroups) {
        for (String rg : resourceGroups) {
            VirtualMachine vm = arm.virtualMachines().getByResourceGroup(rg, name);
            if (vm != null) {
                return vm;
            }
        }
        return null;
    }

    static WebApp findAzureWebApp(AzureResourceManager arm, String name, Set<String> resourceGroups) {
        for (String rg : resourceGroups) {
            WebApp app = arm.webApps().getByResourceGroup(rg, name);
            if (app != null) {
                return app;
            }
        }
        return null;
    }

    static void waitForWebApp(WebApp app) throws InterruptedException, IOException {
        OkHttpClient client = new OkHttpClient();
        // 20 minutes in millis
        long timeout = TimeoutUtil.adjust(1200000);
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeout) {
            try {
                Response response = client.newCall(new Request.Builder()
                                .url("http://" + app.defaultHostname())
                                .method("GET", null)
                                .build())
                        .execute();
                int code = response.code();
                /*
                Skip 404 with very specific error.
                The error comes from Azure portal when web app is already deployed but DNS has some troubles with redirecting
                the connection
                 */
                if (code == 404 && response.body() != null
                        && response.body().string().contains("404 Web Site not found.")
                        && response.body().string().contains("Custom domain has not been configured inside Azure. See <a href=\"https://go.microsoft.com/fwlink/?linkid=2194614\">how to map an existing domain</a> to resolve this.")
                        && response.body().string().contains("Client cache is still pointing the domain to old IP address. Clear the cache by running the command <i>ipconfig/flushdns.</i>")
                ) {
                    continue;
                }
                if (code < 500) {
                    return;
                }
            } catch (SocketTimeoutException e) {
                // skipping timeout
                Thread.sleep(TimeoutUtil.adjust(500));
            }
        }
        throw new RuntimeException("Timeout!");
    }
}

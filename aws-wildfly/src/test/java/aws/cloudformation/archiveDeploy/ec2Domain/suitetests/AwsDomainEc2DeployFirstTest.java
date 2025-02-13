package aws.cloudformation.archiveDeploy.ec2Domain.suitetests;


import aws.cloudformation.AwsTestConstants;
import sunstone.annotation.OperatingMode;
import sunstone.annotation.WildFly;
import sunstone.aws.annotation.AwsEc2Instance;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.assertj.core.api.Assertions;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import sunstone.annotation.Deployment;
import sunstone.annotation.Parameter;
import sunstone.aws.annotation.WithAwsCfTemplate;
import sunstone.inject.Hostname;

import java.io.IOException;

import static aws.cloudformation.AwsTestConstants.region;

@WithAwsCfTemplate(parameters = {
        @Parameter(k = "instanceName", v = AwsTestConstants.instanceName)
},
        template = "sunstone/aws/cloudformation/eapDomain.yaml", region = region, perSuite = true)
public class AwsDomainEc2DeployFirstTest {
    @AwsEc2Instance(nameTag = AwsTestConstants.instanceName, region = region)
    @WildFly(mode = OperatingMode.DOMAIN)
    Hostname hostname;

    @Deployment(name = "testapp.war")
    @AwsEc2Instance(nameTag = AwsTestConstants.instanceName, region = region)
    @WildFly(mode = OperatingMode.DOMAIN)
    static WebArchive deploy() {
        return ShrinkWrap.create(WebArchive.class)
                .addAsWebResource(new StringAsset("Hello World"), "index.jsp");
    }

    @Test
    public void test() throws IOException {
        OkHttpClient client = new OkHttpClient();

        //check all servers in group
        int[] ports = {8080,8230};
        for (int port : ports) {
            Request request = new Request.Builder()
                    .url("http://" + hostname.get() + ":" + port + "/testapp")
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            Assertions.assertThat(response.body().string()).isEqualTo("Hello World");
        }
    }
}

package sunstone.core;


public class JUnit5Config {

    public static final String TIMEOUT_FACTOR = "timeout.factor";

    /**
     * Azure SDK related keys used in JUnit5 module
     */
    public static final class Azure {
        public static final String SUBSCRIPTION_ID = "az.subscriptionId";
        public static final String TENANT_ID = "az.tenantId";
        public static final String APPLICATION_ID = "az.applicationId";
        public static final String PASSWORD = "az.password";
        public static final String REGION = "az.region";
        public static final String GROUP = "az.group";
    }

    /**
     * AWS SDK related keys used in JUnit5 module
     */
    public static final class Aws {
        public static final String ACCESS_KEY_ID = "aws.accessKeyID";
        public static final String SECRET_ACCESS_KEY = "aws.secretAccessKey";
        public static final String REGION = "aws.region";
    }
}

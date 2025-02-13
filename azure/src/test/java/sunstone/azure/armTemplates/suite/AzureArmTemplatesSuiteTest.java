package sunstone.azure.armTemplates.suite;

import sunstone.azure.armTemplates.suite.tests.PerSuiteAzArmTemplate;
import sunstone.azure.armTemplates.suite.tests.PerSuitePerClassAzArmTemplates;
import sunstone.azure.armTemplates.suite.tests.TwoSamePerSuiteAzArmTemplates;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({PerSuiteAzArmTemplate.class, PerSuitePerClassAzArmTemplates.class, TwoSamePerSuiteAzArmTemplates.class})
public class AzureArmTemplatesSuiteTest {
    public static final String GROUP = "AzArmSuiteTest";
}

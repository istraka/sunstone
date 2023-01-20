package sunstone.core;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import sunstone.api.WithAzureArmTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@WithAzureArmTemplate(template = "")
public class WitAzureArmTemplateTest {

    @AfterAll
    public static void reset() {
        TestSunstoneDeployer.reset();
    }

    @Test
    public void called() {
        assertThat(TestSunstoneDeployer.called).isTrue();
        assertThat(TestSunstoneDeployer.counter).isEqualTo(1);
    }
}
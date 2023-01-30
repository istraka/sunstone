package sunstone.core.api;


import org.junit.jupiter.api.extension.ExtensionContext;
import sunstone.core.exceptions.SunstoneException;

import java.lang.annotation.Annotation;

public interface SunstoneCloudDeployer {
    void deploy(Annotation clazz, ExtensionContext ctx) throws SunstoneException;
}

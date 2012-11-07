package pl.kjaklik.maven.jsprecompile;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

/**
 * @author Krzysiek Jaklik
 *         created: 07.11.12 22:01
 */
public class PrecompileMojoTest extends AbstractMojoTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testPrecompileHandlebars() throws Exception {
        File testPom = new File(getBasedir(), "target/test-classes/unit/handlebars/config.xml");

        PrecompileMojo mojo  = (PrecompileMojo)lookupMojo("precompile", testPom);

        assertNotNull(mojo);

        mojo.execute();
    }
}

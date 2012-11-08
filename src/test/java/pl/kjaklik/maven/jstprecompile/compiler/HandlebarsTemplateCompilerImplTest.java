package pl.kjaklik.maven.jstprecompile.compiler;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.StringWriter;

import static org.junit.Assert.assertNotNull;

/**
 * @author Krzysiek Jaklik
 *         created: 08.11.12 17:05
 */
public class HandlebarsTemplateCompilerImplTest {

    private HandlebarsTemplateCompilerImpl compiler;

    @Before
    public void setup() {
        compiler = new HandlebarsTemplateCompilerImpl();
        compiler.prepare();
    }

    @Test
    public void testCompilation() throws Exception {

        File template = new File(getClass().getClassLoader().getResource("pl/kjaklik/maven/jstprecompile/compiler/handlebars/tmpl1.handlebars").toURI());

        compiler.setClassName("template");
        compiler.setNamespace("Handlebars.templates");
        compiler.setOptions("{}");

        StringWriter writer = new StringWriter();

        writer.write(compiler.generateNamespaceInitialisation());
        compiler.precompileFile(template, writer);

        String result = writer.toString();

        assertNotNull(result);

        String value = testTemplate(result, "hbT1", "{title: 'test', body: 'lalala'}");

        assertNotNull(value);
        System.out.println(value);
    }

    private String testTemplate(String script, String template, String data) {
        compiler.evaluate(script);
        return compiler.evaluate(String.format("%s.%s(%s)", compiler.getNamespace(), template, data)).toString();
    }
}

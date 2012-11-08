package pl.kjaklik.maven.jstprecompile.compiler;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Krzysiek Jaklik
 *         created: 07.11.12 21:39
 */
public class HandlebarsTemplateCompilerImpl extends AbstractTemplateCompiler implements TemplateCompiler {

    @Override
    public void prepare() {
        super.prepare();

        loadClasspathScript("pl/kjaklik/maven/jstprecompile/scripts/engine/handlebars.js");
        loadClasspathScript("pl/kjaklik/maven/jstprecompile/scripts/toolkit/handlebars-toolkit.js");
    }

    @Override
    protected void precompileFile(File file, Writer writer) throws IOException {
        loadTemplateFile(file);

        Object result = evaluate(String.format("jstprecompileHandlebarsToolkit.generate('%s', '%s', %s)",
                                               getNamespace(), getClassName(), getOptions()));

        IOUtils.write(result.toString(), writer);
    }

}

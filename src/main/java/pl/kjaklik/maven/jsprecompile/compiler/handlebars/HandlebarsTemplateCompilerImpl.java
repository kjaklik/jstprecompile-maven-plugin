package pl.kjaklik.maven.jsprecompile.compiler.handlebars;

import org.apache.commons.io.IOUtils;
import pl.kjaklik.maven.jsprecompile.compiler.AbstractTemplateCompiler;
import pl.kjaklik.maven.jsprecompile.compiler.TemplateCompiler;

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

        loadClasspathScript("pl/kjaklik/maven/jsprecompile/scripts/engine/handlebars.js");
        loadClasspathScript("pl/kjaklik/maven/jsprecompile/scripts/toolkit/handlebars-toolkit.js");
    }

    @Override
    public void compileGroup(Iterable<File> templates, File output) {
        Writer writer = null;

        try {
            writer = prepareOutputWriter(output);
            IOUtils.write(generateNamespaceInitialisation(), writer);

            compileGroup(templates, writer);
        } catch(IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(writer != null) {
                IOUtils.closeQuietly(writer);
            }
        }
    }

    @Override
    public void compileGroup(Iterable<File> templates, Writer writer) throws IOException {
        for(File templateFile : templates) {
            precompileFile(templateFile, writer);
            IOUtils.write("\n", writer);
        }
    }



    private void precompileFile(File file, Writer writer) throws IOException {
        loadTemplateFile(file);

        Object result = evaluate(String.format("jsprecompileHandlebarsToolkit.generate('%s', '%s')", getNamespace(), getClassName()));

        IOUtils.write(result.toString(), writer);
    }

}

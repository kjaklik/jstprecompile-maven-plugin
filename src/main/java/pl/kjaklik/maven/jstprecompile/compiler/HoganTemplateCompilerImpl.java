package pl.kjaklik.maven.jstprecompile.compiler;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Krzysiek Jaklik
 *         created: 18.10.12 10:31
 */
public class HoganTemplateCompilerImpl extends AbstractTemplateCompiler implements TemplateCompiler {

    @Override
    public boolean isSupportDevMode() {
        return true;
    }

    @Override
    public void prepare() {
        super.prepare();

        loadClasspathScript("pl/kjaklik/maven/jstprecompile/scripts/engine/hogan.js");
        loadClasspathScript("pl/kjaklik/maven/jstprecompile/scripts/toolkit/hogan-toolkit.js");
    }

    @Override
    public void devCompileGroup(Iterable<File> templates, File output) {
        Writer writer = null;

        try {
            writer = prepareOutputWriter(output);
            IOUtils.write(generateNamespaceInitialisation(), writer);

            devCompileGroup(templates, writer);
        } catch(IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(writer != null) {
                IOUtils.closeQuietly(writer);
            }
        }
    }

    @Override
    public void devCompileGroup(Iterable<File> templates, Writer writer) throws IOException {
        for(File templateFile : templates) {
            devPrecompileFile(templateFile, writer);
            IOUtils.write("\n", writer);
        }
    }

    @Override
    protected void precompileFile(File file, Writer writer) throws IOException {
        loadTemplateFile(file);

        Object result = evaluate(String.format("jstprecompileHoganToolkit.generate('%s', '%s', %s)",
                                               getNamespace(), getClassName(), getOptions()));

        IOUtils.write(result.toString(), writer);
    }


    private void devPrecompileFile(File templateFile, Writer writer) throws IOException {
        String filepath = FilenameUtils.separatorsToUnix(StringUtils.removeStart(templateFile.getAbsolutePath(), getDevModeBaseDir().getAbsolutePath()));

        String script = readClasspathFile("pl/kjaklik/maven/jstprecompile/scripts/template/hogan-dev-template.js");
        script = StringUtils.replace(script, "{{namespace}}", getNamespace());
        script = StringUtils.replace(script, "{{classname}}", getClassName());
        script = StringUtils.replace(script, "{{filepath}}", filepath);

        IOUtils.write(script, writer);
    }
}

package pl.kjaklik.maven.jsprecompile.compiler;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;

import java.io.*;

/**
 * @author Krzysiek Jaklik
 *         created: 18.10.12 10:32
 */
public abstract class AbstractTemplateCompiler implements TemplateCompiler {
    protected Context context;
    protected Scriptable scope;

    private String namespace;
    private String className;

    private File devModeBaseDir;

    @Override
    public void prepare() {
        assertNotPrepared();

        context = ContextFactory.getGlobal().enterContext();
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_1_8);
        scope = new Global(context);

        loadClasspathScript("pl/kjaklik/maven/jsprecompile/scripts/env.js");
        loadClasspathScript("pl/kjaklik/maven/jsprecompile/scripts/jquery.js");
    }

    @Override
    public boolean isPrepared() {
        return context != null;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public boolean isSupportDevMode() {
        return false;
    }

    @Override
    public void devCompileGroup(Iterable<File> templates, File output) {
        throw new IllegalStateException("DevMode not supported!");
    }

    @Override
    public void devCompileGroup(Iterable<File> templates, Writer output) throws IOException {
        throw new IllegalStateException("DevMode not supported!");
    }

    @Override
    public File getDevModeBaseDir() {
        return devModeBaseDir;
    }

    @Override
    public void setDevModeBaseDir(File devModeBaseDir) {
        this.devModeBaseDir = devModeBaseDir;
    }

    protected void assertPrepared() {
        if(!isPrepared()) {
            throw new IllegalStateException("Compiler is not prepared!");
        }
    }

    protected void assertNotPrepared() {
        if(isPrepared()) {
            throw new IllegalStateException("Compiler is already prepared!");
        }
    }

    protected String generateNamespaceInitialisation() {
        StringBuilder builder = new StringBuilder();
        StringBuilder fullname = new StringBuilder();
        String[] parts = StringUtils.split(getNamespace(), '.');
        for(int i = 0; i < parts.length; i++) {
            if(i > 0) {
                fullname.append('.');
            }
            fullname.append(parts[i]);
            builder.append(String.format("if(typeof(%s)=='undefined'){%s%s={};}", fullname.toString(), i > 0 ? "" : "var ", fullname.toString()));
        }
        builder.append('\n');
        return builder.toString();
    }

    protected void loadTemplateFile(File file) {
        evaluate(String.format("window.location = '%s';", StringUtils.replaceOnce(file.toURI().toString(), "file:/", "file:///")));
    }

    protected Object evaluate(String command) {
        assertPrepared();

        return context.evaluateString(scope, command, "evaluate", 1, null);
    }

    protected void loadClasspathScript(String location) {
        assertPrepared();

        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(location));
            context.evaluateReader(scope, reader, FilenameUtils.getName(location), 1, null);

        } catch(IOException e) {
            throw new RuntimeException("Unable to load script from location " + location, e);
        } finally {
            if(reader != null) {
                IOUtils.closeQuietly(reader);
            }
        }
    }

    protected String readClasspathFile(String location) {
        InputStream stream = null;
        try {
            stream = this.getClass().getClassLoader().getResourceAsStream(location);
            return IOUtils.toString(stream);
        } catch(IOException e) {
            throw new RuntimeException("Unable to read file from location " + location, e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    protected Writer prepareOutputWriter(File file) throws IOException {
        return new BufferedWriter(new FileWriter(file, false));
    }
}

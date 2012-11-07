package pl.kjaklik.maven.jsprecompile.compiler;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Krzysiek Jaklik
 *         created: 18.10.12 10:29
 */
public interface TemplateCompiler {

    void prepare();
    boolean isPrepared();
    String getNamespace();
    void setNamespace(String namespace);

    String getClassName();
    void setClassName(String className);

    boolean isSupportDevMode();
    void setDevModeBaseDir(File baseDir);
    File getDevModeBaseDir();

    void compileGroup(Iterable<File> templates, File output);
    void compileGroup(Iterable<File> templates, Writer output) throws IOException;

    void devCompileGroup(Iterable<File>  templates, File output);
    void devCompileGroup(Iterable<File>  templates, Writer output) throws IOException;
}

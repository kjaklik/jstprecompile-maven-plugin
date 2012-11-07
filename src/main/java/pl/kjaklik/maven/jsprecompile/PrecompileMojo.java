package pl.kjaklik.maven.jsprecompile;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import pl.kjaklik.maven.jsprecompile.compiler.TemplateCompiler;
import pl.kjaklik.maven.jsprecompile.compiler.hogan.HoganTemplateCompilerImpl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Krzysiek Jaklik
 *         created: 19.10.12 07:42
 *
 * @goal precompile
 * @phase package
 */
public class PrecompileMojo extends AbstractMojo implements Mojo {
    private static Map<TemplateType, TemplateCompiler> compilers;
    static {
        compilers = new HashMap<TemplateType, TemplateCompiler>();
        compilers.put(TemplateType.HoganJS, new HoganTemplateCompilerImpl());
    }

    /**
     * Base directory from which relative paths are resolved while looking for templates.
     * @parameter default-value="${project.build.directory}/${project.build.finalName}/WEB-INF"
     */
    private File inputBaseDir;

    /**
     * Base directory from which relative paths are resolved while creating precompiled javascript files.
     * @parameter default-value="${project.build.directory}/${project.build.finalName}/WEB-INF"
     */
    private File outputBaseDir;

    /**
     * List of template groups.
     * @parameter
     */
    private List<Group> groups;

    /**
     * Whether execution should be skipped.
     * @parameter default-value="false"
     */
    private boolean skip;

    /**
     * Whether templates should be recompiled even if they are up to date.
     * @parameter default-value="false"
     */
    private boolean force;
    /**
     * Should templates be generated in development mode.
     * @parameter default-value="false"
     */
    private boolean devMode;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if(skip) {
            getLog().info("jsprecompile:precompile skipped!");
        }
        if(force) {
            getLog().info("Compilation will be forces even if groups are up to date");
        }
        if(devMode) {
            getLog().info("Compilation in devMode enabled");
        }
        if(groups == null || groups.isEmpty()) {
            throw new MojoFailureException("No template groups specified for compilation!");
        }
        long startTime = System.currentTimeMillis();
        for(int i = 0; i < groups.size(); i++) {
            getLog().info(String.format("Processing group %d - %s", i + 1, groups.get(i).getName()));
            processGroup(groups.get(i));
            getLog().info("+-- Done");
        }
        getLog().info(String.format("Total time: %.3f s", (System.currentTimeMillis() - startTime) / 1000d));
    }

    private void processGroup(Group group) throws MojoExecutionException {
        List<File> templates = group.getSources(inputBaseDir);
        File outputFile = group.getOutputFile(outputBaseDir);
        if(isUpToDate(templates, outputFile)) {
            getLog().info("+-- Up to date - skipping compilation");
            return;
        }
        TemplateCompiler compiler = compilers.get(group.getTemplateType());
        if(compiler == null) {
            throw new MojoExecutionException("Unsupported template type: " + group.getTemplateType().name());
        }
        try {
            compiler.setNamespace(group.getNamespace());
            compiler.setClassName(group.getClassName());

            if(!compiler.isPrepared()) {
                getLog().debug("+-- Preparing compiler for " + group.getTemplateType() + "...");
                compiler.prepare();
            }
            getLog().debug(String.format("+-- Compiling %d source file%s...", templates.size(), templates.size() != 1 ? "s" : ""));
            if(devMode && compiler.isSupportDevMode()) {
                compiler.setDevModeBaseDir(outputBaseDir);
                compiler.devCompileGroup(templates, outputFile);
            } else {
                if(devMode) {
                    getLog().warn("!-- " + group.getTemplateType().name() + " does not support devMode - performing normal compilation");
                }
                compiler.compileGroup(templates, outputFile);
            }
        } catch(Exception e) {
            throw new MojoExecutionException(e.getMessage());
        }
        if(group.isRemoveSources()) {
            if(devMode && compiler.isSupportDevMode()) {
                getLog().info("+-- Source files will not be removed because devMode is enabled");
            } else {
                getLog().info("+-- Removing source files...");
                for(File file : templates) {
                    FileUtils.deleteQuietly(file);
                }
            }
        }
    }

    private boolean isUpToDate(List<File> files, File outputFile) {
        if(force || !outputFile.exists()) {
            return false;
        }

        for(File file : files) {
            if(FileUtils.isFileNewer(file, outputFile)) {
                return false;
            }
        }
        return true;
    }
}

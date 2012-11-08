package pl.kjaklik.maven.jstprecompile;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Krzysiek Jaklik
 *         created: 19.10.12 07:49
 */
public class Group {

    /**
     * Group name.
     */
    private String name;

    /**
     * Template type.
     */
    private TemplateType _templateType;

    /**
     * JavaScript literal object containing configuration options for compiler.
     */
    private String options;

    /**
     * Javascript namespace for generated templates.
     */
    private String namespace;

    /**
     * HTML class name identifying template elements.
     */
    private String className;

    /**
     * Name for generated javascript file containing precompiled templates.
     */
    private String outputFile;

    /**
     * List of files to include in templates precompilation.
     */
    private String[] includes;

    /**
     * List of files to exclude from templates precompilation.
     */
    private String[] excludes;

    /**
     * If source files used in precompilation should be removed from final war.
     */
    private boolean removeSources = false;

    public String getName() {
        if(StringUtils.isBlank(name)) {
            name = String.format("%s[%s - %s]", getTemplateType().name(), getNamespace(), getClassName());
        }
        return name;
    }

    public TemplateType getTemplateType() {
        return _templateType == null ? TemplateType.DEFAULT_TYPE : _templateType;
    }

    public String getNamespace() {
        if(StringUtils.isBlank(namespace)) {
            namespace = getTemplateType().getDefaultNamespace();
        }
        return namespace;
    }

    public String getClassName() {
        if(StringUtils.isBlank(className)) {
            className = getTemplateType().getDefaultClassName();
        }
        return className;
    }

    public boolean isRemoveSources() {
        return removeSources;
    }

    public String getOptions() {
        if(StringUtils.isBlank(options)) {
            return "{}";
        }
        return options;
    }

    public List<File> getSources(File basedir) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setCaseSensitive(true);
        scanner.setBasedir(basedir);
        if(includes != null) {
            scanner.setIncludes(includes);
        }
        if(excludes != null) {
            scanner.setExcludes(excludes);
        }
        scanner.addDefaultExcludes();
        scanner.scan();

        List<File> result = new ArrayList<File>();
        for(String filename : scanner.getIncludedFiles()) {
            File file = new File(basedir, filename);
            if(!result.contains(file)) {
                result.add(file);
            }
        }
        return result;
    }

    public File getOutputFile(File basedir) {
        File tmpFile = new File(outputFile);
        if(tmpFile.isAbsolute()) {
            return tmpFile;
        } else {
            return new File(basedir, outputFile);
        }
    }

    public void setTemplateType(String templateTypeString) {
        try {
            this._templateType = TemplateType.valueOf(templateTypeString);
        } catch(IllegalArgumentException e) {
            this._templateType = TemplateType.DEFAULT_TYPE;
        }
    }
}

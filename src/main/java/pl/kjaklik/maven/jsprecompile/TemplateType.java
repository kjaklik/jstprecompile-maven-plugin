package pl.kjaklik.maven.jsprecompile;

/**
 * @author Krzysiek Jaklik
 *         created: 19.10.12 10:40
 */
public enum TemplateType {
    HoganJS("HoganTemplates", "template"),
    HandlebarsJS("Handlebars.templates", "template");

    public static final TemplateType DEFAULT_TYPE = HoganJS;

    private String defaultNamespace;
    private String defaultClassName;

    private TemplateType(String defaultNamespace, String defaultClassName) {
        this.defaultNamespace = defaultNamespace;
        this.defaultClassName = defaultClassName;
    }

    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    public String getDefaultClassName() {
        return defaultClassName;
    }
}

/*
 * JDD API Doc.
 * 意见、建议、技术支持，请联系：xujiuxing@126.com
 */
package com.jdd.plugin.apidoc;

/**
 * 文档类型.
 * @author xujiuxing
 *
 */
public enum DocType {

    markdown(new MarkdownDocOutput(), ".md"), asciidoc(new AsciiDocOutput(), ".adoc");

    private ApiDocOutput apiDocOutput;

    private String extension;

    public ApiDocOutput getApiDocOutput() {
        return apiDocOutput;
    }

    public void setApiDocOutput(final ApiDocOutput apiDocOutput) {
        this.apiDocOutput = apiDocOutput;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(final String extension) {
        this.extension = extension;
    }

    DocType(final ApiDocOutput apiDocOutput, final String extension) {
        this.apiDocOutput = apiDocOutput;
        this.extension = extension;
    }

}

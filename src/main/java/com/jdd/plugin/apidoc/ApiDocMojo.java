package com.jdd.plugin.apidoc;

/*
 * JDD API Doc.
 * 意见、建议、技术支持，请联系：xujiuxing@126.com
 */

import java.io.File;
import java.util.Arrays;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

/**
 * ApiDoc plugin.
 * @author xujiuxing
 */
@Mojo(name = "apidoc", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true)
public class ApiDocMojo extends AbstractMojo {

    @Parameter(property = "skip")
    private boolean skip;

    @Parameter(property = "doctypes")
    private String[] doctypes;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
    private File outputDirectory;

    /**
     * 插件类处理入口.
     * @throws MojoExecutionException 插件异常
     */
    public void execute() throws MojoExecutionException {

        if (skip || !outputDirectory.exists()) {
            return;
        }

//        printBanner();

        try {
            final ApiDocBuilder builder = new ApiDocBuilder(project, toDocType(doctypes));
            builder.build();
        } catch (Exception e) {
            throw new MojoExecutionException("Error creating API Doc file ", e);
        }
    }

    /**
     * 类型转换.
     * @param docTypes 插件配置属性doctypes
     * @return DocType枚举
     */
    private DocType[] toDocType(final String[] docTypes) {
        final DocType[] values = DocType.values();
        DocType[] src = new DocType[0];
        for (int i = 0; i < values.length; i++) {
            final DocType docType = values[i];
            if (Arrays.asList(docTypes).contains(docType.name())) {
                DocType[] dest = new DocType[src.length + 1];
                System.arraycopy(src, 0, dest, 0, src.length);
                dest[src.length] = docType;
                src = dest;
            }
        }
        return src;
    }

    /**
     * 打印banner.
     */
    private void printBanner() {
        System.out.println(StringUtils.rightPad(" ", 70, "_"));
        System.out.print(StringUtils.rightPad("|       _  ____        _     ____   ___    ____", 70, " "));
        System.out.println("|");
        System.out.print(StringUtils.rightPad("|      | ||  _ \\      / \\   |  _ \\ |_ _|  |  _ \\   ___    ___", 70, " "));
        System.out.println("|");
        System.out.print(StringUtils.rightPad("|   _  | || | | |    / _ \\  | |_) | | |   | | | | / _ \\  / __|", 70, " "));
        System.out.println("|");
        System.out.print(StringUtils.rightPad("|  | |_| || |_| |   / ___ \\ |  __/  | |   | |_| || (_) || (__", 70, " "));
        System.out.println("|");
        System.out.print(StringUtils.rightPad("|   \\___/ |____/   /_/   \\_\\|_|    |___|  |____/  \\___/  \\___|", 70, " "));
        System.out.println("|");
        System.out.print(StringUtils.rightPad("| ", 70, " "));
        System.out.println("|");
        System.out.print(StringUtils.rightPad("| 意见、建议、技术支持，请联系：xujiuxing@126.com", 55, " "));
        System.out.println("|");
        System.out.print(StringUtils.rightPad("|", 70, "_"));
        System.out.println("|");
    }

}

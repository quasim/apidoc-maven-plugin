/*
 * JDD API Doc.
 * 意见、建议、技术支持，请联系：xujiuxing@126.com
 */
package com.jdd.plugin.apidoc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

/**
 * 解析项目源代码并生成API文档.
 *
 * @author xujiuxing
 */
public class ApiDocBuilder {

    private final MavenProject project;

    private final ApiDocOutput[] apiDocOutputs;

    private final Writer[] writers;

    /**
     * 构造器，传入MavenProject和DocType.
     * @param project 当前maven工程
     * @param docTypes 输出的文档类型
     * @throws Exception 异常
     */
    public ApiDocBuilder(final MavenProject project, final DocType[] docTypes) throws Exception {
        this.project = project;
        this.apiDocOutputs = new ApiDocOutput[docTypes.length];
        this.writers = new Writer[docTypes.length];
        for (int i = 0; i < docTypes.length; i++) {
            final DocType docType = docTypes[i];
            try {
                apiDocOutputs[i] = docType.getApiDocOutput();
                final List<String> dirs = Arrays.asList("src", "docs", docType.name(), project.getBuild().getFinalName() + docType.getExtension());
                final String path = StringUtils.join(dirs.iterator(), File.separator);
                final File apidoc = new File(project.getBasedir(), path);
                if (!apidoc.exists()) {
                    apidoc.getParentFile().mkdirs();
                    apidoc.createNewFile();
                }
                writers[i] = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(apidoc), "UTF-8"));
            } catch (Exception e) {
                throw e;
            }
        }
    }

    /**
     * 开始处理项目源代码，生成API文档.
     * @throws Exception 异常
     */
    public void build() throws Exception {

        for (int i = 0; i < apiDocOutputs.length; i++) {
            apiDocOutputs[i].writeApiArtifact(project, writers[i]);
        }

        final JavaDocBuilder builder = new JavaDocBuilder();
        builder.setEncoding("UTF-8");
        collectProjectCompileSourceRoots(getRootProject(project), builder);

        try {
            int classIndex = 1;
            final JavaSource[] javaSources = builder.getSources();
            for (final JavaSource javaSource : javaSources) {
                final JavaClass[] javaClasses = javaSource.getClasses();
                for (final JavaClass javaClass : javaClasses) {
                    if (javaClass.isInterface()) {
                        final DocletTag apidocTag = javaClass.getTagByName(Tag.api.name());
                        if (apidocTag != null) {
                            for (int i = 0; i < apiDocOutputs.length; i++) {
                                apiDocOutputs[i].writeClass(project, writers[i], javaClass, classIndex);
                            }
                            classIndex++;
                        }
                    }
                }
            }
            flushAll(writers);
        } catch (Exception e) {
            throw e;
        } finally {
            closeAll(writers);
        }
    }

    /**
     * 获取根应用.
     *
     * @param project 当前maven工程
     * @return maven根目录
     */
    private MavenProject getRootProject(final MavenProject project) {
        if (project.hasParent()) {
            return getRootProject(project.getParent());
        }
        return project;
    }

    /**
     * 由Parent_MavenProject递归收集各Modules的CompileSourceRoots，将其添加到JavaDocBuilder目录树里.
     *
     * @param project 当前maven工程
     * @param builder qdox JavaDocBuilder
     */
    private void collectProjectCompileSourceRoots(final MavenProject project, final JavaDocBuilder builder) {
        final List<MavenProject> collectedProjects = project.getCollectedProjects();
        if (collectedProjects != null) {
            for (final MavenProject mp : collectedProjects) {
                final List<String> compileSourceRoots = mp.getCompileSourceRoots();
                for (final String sourcepath : compileSourceRoots) {
                    final File file = new File(sourcepath);
                    if (file.exists()) {
                        builder.addSourceTree(file);
                    }
                }
                collectProjectCompileSourceRoots(mp, builder);
            }
        }
    }

    private void flushAll(final Writer[] writers) {
        for (final Writer writer : writers) {
            try {
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeAll(final Writer[] writers) {
        for (final Writer writer : writers) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

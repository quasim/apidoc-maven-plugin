/*
 * JDD API Doc.
 * 意见、建议、技术支持，请联系：xujiuxing@126.com
 */
package com.jdd.plugin.apidoc;

import java.io.IOException;
import java.io.Writer;

import com.thoughtworks.qdox.model.JavaClass;

import org.apache.maven.project.MavenProject;

/**
 * 输出API接口文档.
 *
 * @author xujiuxing
 */
public interface ApiDocOutput {

    void writeApiArtifact(final MavenProject project, final Writer writer) throws IOException;

    void writeClass(final MavenProject project, final Writer writer, final JavaClass javaClass, final int classIndex) throws IOException;

}

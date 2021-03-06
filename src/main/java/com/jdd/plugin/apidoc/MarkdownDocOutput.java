/*
 * JDD API Doc.
 * 意见、建议、技术支持，请联系：xujiuxing@126.com
 */
package com.jdd.plugin.apidoc;

import com.thoughtworks.qdox.model.*;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static com.jdd.plugin.apidoc.Constants.LINE_SEPARATOR;

/**
 * 解析源文件，调用MarkdownDocTemplate输出文档.
 *
 * @author xujiuxing
 */
public class MarkdownDocOutput extends AbstractApiDocOutput {

    @Override
    public void writeApiArtifact(final MavenProject project, final Writer writer) throws IOException {
        final Artifact artifact = project.getArtifact();
        MarkdownDocTemplate.MAVEN_POM.write(writer, artifact.toString());
    }

    @Override
    public void writeClass(final MavenProject project, final Writer writer, final JavaClass javaClass, final int classIndex) throws IOException {
        MarkdownDocTemplate.CLASS_INDEX.write(writer, classIndex);
        final DocletTag classTag = javaClass.getTagByName(Tag.title.name());
        if (classTag == null) {
            MarkdownDocTemplate.API_CLASS.write(writer, StringUtils.deleteWhitespace(javaClass.getComment()));
        } else {
            MarkdownDocTemplate.API_CLASS.write(writer, classTag.getValue());
        }
        final JavaMethod[] javaMethods = javaClass.getMethods();
        int titleIndex = 0;
        for (final JavaMethod javaMethod : javaMethods) {
            final DocletTag titleTag = javaMethod.getTagByName(Tag.title.name());
            final DocletTag descTag = javaMethod.getTagByName(Tag.desc.name());
            if (titleTag != null && StringUtils.isNotBlank(titleTag.getValue())) {
                titleIndex++;
                MarkdownDocTemplate.TITLE_INDEX.write(writer, classIndex, titleIndex);
                MarkdownDocTemplate.API_TITLE.write(writer, titleTag.getValue());
                MarkdownDocTemplate.API_REFERENCE.write(writer, getReference(javaMethod));
                if (descTag == null || StringUtils.isBlank(descTag.getValue())) {
                    MarkdownDocTemplate.API_DESC.write(writer, javaMethod.getComment());
                } else {
                    MarkdownDocTemplate.API_DESC.write(writer, descTag.getValue());
                }
                writeParameter(project, writer, javaMethod.getParameters());
                writeReturnType(project, writer, javaMethod.getGenericReturnType());
            }
        }
    }

    private void writeParameter(final MavenProject project, final Writer writer, final JavaParameter[] javaParameters) throws IOException {
        if (javaParameters != null && javaParameters.length != 0) {
            writeParameterTHead(writer);
            writeParameterTBody(project, writer, javaParameters);
            writer.append(LINE_SEPARATOR);
        }
    }

    private void writeReturnType(final MavenProject project, final Writer writer, final Type type) throws IOException {
        if (type.getFullyQualifiedName().startsWith(project.getGroupId())) {
            writeReturnTHead(writer, type);
            writeReturnTBody(project, writer, type);
        }
        final Type[] actualTypeArguments = type.getActualTypeArguments();
        if (actualTypeArguments != null && actualTypeArguments.length != 0) {
            for (final Type actualType : actualTypeArguments) {
                writeReturnType(project, writer, actualType);
            }
        }
    }

    private void writeGenericType(final MavenProject project, final Writer writer, final String parameterName, final Type[] types) throws IOException {
        if (types == null || types.length == 0) {
            return;
        }
        for (final Type type : types) {
            if (type.getFullyQualifiedName().startsWith(project.getGroupId())) {
                writeGenericTypeTHead(writer, parameterName, type);
                writeGenericTypeTBody(project, writer, type.getJavaClass().getFields());
            }
        }
    }

    private void writeParameterTHead(final Writer writer) throws IOException {
        MarkdownDocTemplate.PARAMETER_THEAD.write(writer, "");
    }

    private void writeFieldTHead(final Writer writer, final JavaParameter javaParameter) throws IOException {
        MarkdownDocTemplate.FIELD_THEAD.write(writer, new Object[]{javaParameter.getName(), javaParameter.getType().getFullyQualifiedName()});
    }

    private void writeFieldTHead(final Writer writer, final JavaField javaField) throws IOException {
        MarkdownDocTemplate.FIELD_THEAD.write(writer, new Object[]{javaField.getName(), javaField.getType().getFullyQualifiedName()});
    }

    private void writeReturnTHead(final Writer writer, final Type type) throws IOException {
        MarkdownDocTemplate.RETURN_THEAD.write(writer, type.getFullyQualifiedName());
    }

    private void writeGenericTypeTHead(final Writer writer, final String parameterName, final Type type) throws IOException {
        MarkdownDocTemplate.GENERIC_TYPE_THEAD.write(writer, new Object[]{parameterName, type.getFullyQualifiedName()});
    }

    private void writeParameterTBody(final MavenProject project, final Writer writer, final JavaParameter[] javaParameters) throws IOException {
        if (javaParameters != null && javaParameters.length != 0) {
            final Set<JavaParameter> nonPrimitiveJavaParameters = new LinkedHashSet<JavaParameter>();
            for (final JavaParameter javaParameter : javaParameters) {
                final String parameterName = javaParameter.getName();
                final String parameterType = javaParameter.getType().getFullyQualifiedName();
                final String parameterComment = getComment(javaParameter);
                MarkdownDocTemplate.PARAMETER_TBODY.write(writer, new Object[]{parameterName, parameterType, "是", parameterComment});
                if (javaParameter.getType().getJavaClass().isEnum()) {
                    continue;
                }
                if (javaParameter.getType().getFullyQualifiedName().startsWith(project.getGroupId())) {
                    nonPrimitiveJavaParameters.add(javaParameter);
                }
                final Type[] actualTypeArguments = javaParameter.getType().getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length != 0) {
                    nonPrimitiveJavaParameters.add(javaParameter);
                }
            }
            for (final JavaParameter javaParameter : nonPrimitiveJavaParameters) {
                if (javaParameter.getType().getFullyQualifiedName().startsWith(project.getGroupId())) {
                    writeFieldTHead(writer, javaParameter);
                    writeFieldTBody(project, writer, javaParameter.getType().getJavaClass().getFields());
                }
                final Type[] actualTypeArguments = javaParameter.getType().getActualTypeArguments();
                writeGenericType(project, writer, javaParameter.getName(), actualTypeArguments);
            }
        }
    }

    private void writeFieldTBody(final MavenProject project, final Writer writer, final JavaField[] javaFields) throws IOException {
        final Set<JavaField> nonPrimitiveJavaFields = new HashSet<JavaField>();
        for (final JavaField javaField : javaFields) {
            final String fieldName = javaField.getName();
            final String fieldType = javaField.getType().getFullyQualifiedName();
            final String nullable = javaField.getType().isPrimitive() ? "是" : getNullableAnnotation(javaField.getAnnotations());
            MarkdownDocTemplate.FIELD_TBODY.write(writer, new Object[]{fieldName, fieldType, nullable, Utils.toHtmlWhitespace(javaField.getComment())});
            if (javaField.getType().getJavaClass().isEnum() || equalsTypeWithParent(javaField)) {
                continue;
            }
            if (javaField.getType().getFullyQualifiedName().startsWith(project.getGroupId())) {
                nonPrimitiveJavaFields.add(javaField);
            }
        }
        writer.append(LINE_SEPARATOR);
        for (final JavaField javaField : nonPrimitiveJavaFields) {
            writeFieldTHead(writer, javaField);
            writeFieldTBody(project, writer, javaField.getType().getJavaClass().getFields());
        }
    }

    private void writeReturnTBody(final MavenProject project, final Writer writer, final Type type) throws IOException {
        final List<Type> nonPrimitiveTypes = new ArrayList<Type>();
        final Type[] actualTypeArguments = type.getActualTypeArguments();
        final JavaField[] fields = type.getJavaClass().getFields();
        for (final JavaField field : fields) {
            if (field.isStatic() || field.isFinal()) {
                continue;
            }
            final String typeName = field.getType().isResolved() ? field.getType().getFullyQualifiedName() : StringUtils.join(actualTypeArguments, ",");
            MarkdownDocTemplate.RETURN_TBODY.write(writer, new Object[]{field.getName(), typeName, Utils.toHtmlWhitespace(field.getComment())});
            if (field.getType().getJavaClass().isEnum() || equalsTypeWithParent(field)) {
                continue;
            }
            final Type[] types = field.getType().getActualTypeArguments();
            if (field.getType().getFullyQualifiedName().startsWith(project.getGroupId()) || (types != null && types.length != 0)) {
                nonPrimitiveTypes.add(field.getType());
            }
        }
        for (final Type nonPrimitiveType : nonPrimitiveTypes) {
            if (nonPrimitiveType.getFullyQualifiedName().startsWith(project.getGroupId())) {
                writeReturnType(project, writer, nonPrimitiveType);
            }
            final Type[] actualTypes = nonPrimitiveType.getActualTypeArguments();
            if (actualTypes != null && actualTypes.length != 0) {
                for (final Type actualType : actualTypes) {
                    writeReturnType(project, writer, actualType);
                }
            }
        }
        writer.append(LINE_SEPARATOR);
    }

    private void writeGenericTypeTBody(final MavenProject project, final Writer writer, final JavaField[] javaFields) throws IOException {
        this.writeFieldTBody(project, writer, javaFields);
    }

}

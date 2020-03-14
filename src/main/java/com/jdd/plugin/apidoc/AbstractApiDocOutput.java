/*
 * JDD API Doc.
 * 意见、建议、技术支持，请联系：xujiuxing@126.com
 */
package com.jdd.plugin.apidoc;

import com.thoughtworks.qdox.model.*;

import static com.jdd.plugin.apidoc.Constants.OUTPUT_FIELD_ANNOTATION_NAME;


/**
 * ApiDocOutput抽象类.
 *
 * @author xujiuxing
 */
public abstract class AbstractApiDocOutput implements ApiDocOutput {

    /**
     * 获取方法引用路径，例如此方法的引用路径是：com.jd.plugin.apidoc.ApiDocMojo#getReference.
     *
     * @param javaMethod javaMethod
     * @return method reference
     */
    protected String getReference(final JavaMethod javaMethod) {
        return javaMethod.getParentClass().getFullyQualifiedName() + "#" + javaMethod.getName();
    }

    protected String getComment(final JavaParameter javaParameter) {
        final JavaMethod method = javaParameter.getParentMethod();
        final DocletTag[] docletTags = method.getTagsByName(Tag.param.name());
        for (final DocletTag docletTag : docletTags) {
            if (docletTag.getValue().trim().startsWith(javaParameter.getName() + " ")) {
                return docletTag.getValue().trim().substring(javaParameter.getName().length());
            }
        }
        return "";
    }

    /**
     * 属性字段的注解类名是否Required、NotNull或NotEmpty（不区分大小写），如果"是"为必填字段，否则该字段可为空.
     *
     * @param annotations 对象属性字段的注解类
     * @return 是否必填
     */
    public String getNullableAnnotation(final Annotation[] annotations) {
        for (final Annotation annotation : annotations) {
            final String name = getClassShortName(annotation.getType().getJavaClass().getName());
            for (final String item : OUTPUT_FIELD_ANNOTATION_NAME) {
                if (item.equalsIgnoreCase(name)) {
                    return "是";
                }
            }
        }
        return "否";
    }

    /**
     * 类名去掉package.
     *
     * @param className 类名
     * @return class simple name
     */
    public String getClassShortName(final String className) {
        final int lastDotIndex = className.lastIndexOf(46);
        int nameEndIndex = className.indexOf("$$");
        if (nameEndIndex == -1) {
            nameEndIndex = className.length();
        }

        String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
        shortName = shortName.replace('$', '.');
        return shortName;
    }

    /**
     * field是否与class为同一类型.
     *
     * @param javaField JavaField对象
     * @return boolean
     */
    public boolean equalsTypeWithParent(final JavaField javaField) {
        final Type[] actualTypeArguments = javaField.getType().getActualTypeArguments();
        if (actualTypeArguments == null) {
            return javaField.getParentClass().asType().equals(javaField.getType());
        } else {
            for (final Type type : actualTypeArguments) {
                if (type.equals(javaField.getParentClass().asType())) {
                    return true;
                }
            }
        }
        return false;
    }

}

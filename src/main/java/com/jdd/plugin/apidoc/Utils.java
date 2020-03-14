/*
 * JDD API Doc.
 * 意见、建议、技术支持，请联系：xujiuxing@126.com
 */
package com.jdd.plugin.apidoc;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * 工具类.
 *
 * @author xujiuxing
 */
public final class Utils {

    private Utils() {
    }

    /**
     * 换行符转换为<br>.
     *
     * @param str 被替换字符串
     * @return 字符串
     */
    public static String toHtmlWhitespace(final String str) {
        if (str == null) {
            return "";
        }
        return str.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
    }

    /**
     * 递归遍历目录以及子目录中的所有文件.
     *
     * @param file 当前遍历文件或目录
     * @param fileFilter 文件过滤规则对象，选择要保留的文件，只对文件有效，不过滤目录
     * @return 文件列表
     */
    public static List<File> loopFiles(final File file, final FileFilter fileFilter) {
        final List<File> fileList = new ArrayList<>();
        if (null == file || !file.exists()) {
            return fileList;
        }

        if (file.isDirectory()) {
            final File[] subFiles = file.listFiles();
            if (subFiles != null) {
                for (final File tmp : subFiles) {
                    fileList.addAll(loopFiles(tmp, fileFilter));
                }
            }
        } else {
            if (null == fileFilter || fileFilter.accept(file)) {
                fileList.add(file);
            }
        }

        return fileList;
    }
}

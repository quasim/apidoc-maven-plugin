/*
 * JDD API Doc.
 * 意见、建议、技术支持，请联系：xujiuxing@126.com
 */
package com.jdd.plugin.apidoc;

import java.io.IOException;
import java.io.Writer;

import org.codehaus.plexus.util.StringUtils;

import static com.jdd.plugin.apidoc.Constants.ASCIIDOC_TABLE_START_LINE;
import static com.jdd.plugin.apidoc.Constants.LINE_SEPARATOR;

/**
 * asciidoc文档模板.
 * @author xujiuxing
 *
 */
public enum AsciiDocTemplate {
    MAVEN_POM {
        @Override
        public void write(final Writer writer, final Object... content) throws IOException {
            writer.append("== 接口依赖包").append(LINE_SEPARATOR)
                    .append("....").append(LINE_SEPARATOR)
                    .append((String) content[0]).append(LINE_SEPARATOR)
                    .append("....").append(LINE_SEPARATOR);
            writer.append(LINE_SEPARATOR);
        }
    },
    CLASS_INDEX {
        @Override
        public void write(final Writer writer, final Object... content) throws IOException {
            writer.append(String.format("== %d. ", content));
        }
    },
    API_CLASS {
        @Override
        public void write(final Writer writer, final Object... content) throws IOException {
            writer.append((String) content[0]).append(LINE_SEPARATOR);
        }
    },
    TITLE_INDEX {
        @Override
        public void write(final Writer writer, final Object... content) throws IOException {
            writer.append(String.format("=== %d.%d ", content));
        }
    },
    API_TITLE {
        @Override
        public void write(final Writer writer, final Object... content) throws IOException {
            writer.append((String) content[0]).append(LINE_SEPARATOR);
        }
    },
    API_REFERENCE {
        @Override
        public void write(final Writer writer, final Object... content) throws IOException {
            writer.append("- 接口名：" + content[0]).append(LINE_SEPARATOR).append(LINE_SEPARATOR);
        }
    },
    API_DESC {
        @Override
        public void write(final Writer writer, final Object... content) throws IOException {
            if (content == null || content.length == 0 || StringUtils.isBlank((String) content[0])) {
                return;
            }
            writer.append("____").append(LINE_SEPARATOR)
                    .append((String) content[0]).append(LINE_SEPARATOR)
                    .append("____").append(LINE_SEPARATOR);
            writer.append(LINE_SEPARATOR);
        }
    }, PARAMETER_THEAD {
        @Override
        public void write(final Writer writer, final Object... content) throws IOException {
            writer.append(".请求参数：").append(LINE_SEPARATOR)
                    .append(ASCIIDOC_TABLE_START_LINE).append(LINE_SEPARATOR)
                    .append("| 参数名 | 参数类型 | 是否必填 | 参数说明 ").append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR);
        }
    }, PARAMETER_TBODY_ROW {
        @Override
        public void write(final Writer writer, final Object... content) throws IOException {
            writer.append("| ").append((String) content[0]).append(LINE_SEPARATOR)
                    .append("| ").append((String) content[1]).append(LINE_SEPARATOR)
                    .append("| ").append((String) content[2]).append(LINE_SEPARATOR)
                    .append("| ").append((String) content[3]).append(LINE_SEPARATOR);
        }
    }, TBODY_END {
        @Override
        public void write(final Writer writer, final Object... content) throws IOException {
            writer.append(ASCIIDOC_TABLE_START_LINE).append(LINE_SEPARATOR);
            writer.append(LINE_SEPARATOR);
        }
    }, RETURN_THEAD {
        @Override
        public void write(final Writer writer, final Object... content) throws IOException {
            final String line = String.format(".返回结果：%s", content);
            writer.append(line).append(LINE_SEPARATOR)
                    .append(ASCIIDOC_TABLE_START_LINE).append(LINE_SEPARATOR)
                    .append("| 字段名 | 字段类型 | 字段说明 ").append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR);
        }
    }, RETURN_TBODY_ROW {
        @Override
        public void write(final Writer writer, final Object... content) throws IOException {
            writer.append("| ").append((String) content[0]).append(LINE_SEPARATOR)
                    .append("| ").append((String) content[1]).append(LINE_SEPARATOR)
                    .append("| ").append((String) content[2]).append(LINE_SEPARATOR);
        }
    }, FIELD_THEAD {
        @Override
        public void write(final Writer writer, final Object... content) throws IOException {
            final String line = String.format(".请求参数：%s(%s)", content);
            writer.append(line).append(LINE_SEPARATOR)
                    .append(ASCIIDOC_TABLE_START_LINE).append(LINE_SEPARATOR)
                    .append("| 参数名 | 参数类型 | 是否必填 | 参数说明 ").append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR);
        }
    }, FIELD_TBODY_ROW {
        @Override
        public void write(final Writer writer, final Object... content) throws IOException {
            writer.append("| ").append((String) content[0]).append(LINE_SEPARATOR)
                    .append("| ").append((String) content[1]).append(LINE_SEPARATOR)
                    .append("| ").append((String) content[2]).append(LINE_SEPARATOR)
                    .append("| ").append((String) content[3]).append(LINE_SEPARATOR);
        }
    }, GENERIC_TYPE_THEAD {
        @Override
        public void write(final Writer writer, final Object... content) throws IOException {
            final String line = String.format(".请求参数泛型类：%s(%s)", content);
            writer.append(line).append(LINE_SEPARATOR)
                    .append(ASCIIDOC_TABLE_START_LINE).append(LINE_SEPARATOR)
                    .append("| 参数名 | 参数类型 | 是否必填 | 参数说明 ").append(LINE_SEPARATOR)
                    .append(LINE_SEPARATOR);
        }
    };

    public abstract void write(final Writer writer, final Object... content) throws IOException;
}

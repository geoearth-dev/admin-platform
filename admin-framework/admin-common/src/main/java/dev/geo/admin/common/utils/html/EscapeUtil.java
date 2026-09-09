package dev.geo.admin.common.utils.html;

import cn.hutool.http.HtmlUtil;

/**
 * HTML 安全处理工具。
 */
public class EscapeUtil {
    /**
     * 将文本中的 HTML 特殊字符转义为实体。
     *
     * <p>适用于普通文本作为 HTML 内容输出的场景。</p>
     */
    public static String escape(String text) {
        return text == null ? null : HtmlUtil.escape(text);
    }

    /**
     * 还原被转义的HTML特殊字符
     *
     * @param content 包含转义符的HTML内容
     * @return 转换后的字符串
     */
    public static String unescape(String content) {
        return content == null ? null : HtmlUtil.unescape(content);
    }

    /**
     * 删除全部 HTML 标签，保留标签中的文字。
     *
     * <p>只用于提取纯文本，不等同于富文本 XSS 清洗。</p>
     */
    public static String clean(String html) {
        return html == null ? null : HtmlUtil.cleanHtmlTag(html);
    }

}

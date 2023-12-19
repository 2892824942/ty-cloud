package com.ty.mid.framework.core.util;


import com.ty.mid.framework.common.lang.ThreadSafe;

import java.util.regex.Pattern;

/**
 * 字符串工具类，继承自 Spring
 */
@ThreadSafe
public class StringUtils extends org.springframework.util.StringUtils {

    private static Pattern HTML_PATTERN = Pattern.compile("<(\"[^\"]*\"|'[^']*'|[^'\">])*>");

    public static String coverMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return mobile;
        }

        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    public static String coverIdNo(String idNo) {
        if (StringUtils.isEmpty(idNo)) {
            return idNo;
        }

        return idNo.replaceAll("(\\d{4})\\d+(\\d{2})", "$1**********$2");
    }

    public static String coverBankCardNo(String bankCardNo) {
        if (StringUtils.isEmpty(bankCardNo) || bankCardNo.length() < 10) {
            return bankCardNo;
        }

        String prefix = bankCardNo.substring(0, 4);
        String suffix = bankCardNo.substring(bankCardNo.length() - 4);

        return prefix.concat(" **** **** ").concat(suffix);
    }

    public static boolean isHtml(String str) {
        if (isEmpty(str)) {
            return false;
        }

        return HTML_PATTERN.matcher(str).find();
    }
}
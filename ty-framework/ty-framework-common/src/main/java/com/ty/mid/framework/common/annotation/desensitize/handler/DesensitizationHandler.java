package com.ty.mid.framework.common.annotation.desensitize.handler;

import com.ty.mid.framework.common.annotation.desensitize.*;
import com.ty.mid.framework.common.exception.FrameworkException;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;

/**
 * 脱敏处理器接口
 *
 * @author suyouliang
 */
public interface DesensitizationHandler<S extends Annotation> {

    /**
     * 脱敏
     *
     * @param origin     原始字符串
     * @param annotation 注解信息
     * @return 脱敏后的字符串
     */
    String desensitize(String origin, S annotation);



    @Getter
    enum DesensitizeEnum {
        BANK_CARD(BankCardDesensitize.class) {
            @Override
            DesensitizeContext getDesensitize(Annotation annotation) {
                if (annotation instanceof BankCardDesensitize) {
                    BankCardDesensitize desensitize = (BankCardDesensitize) annotation;
                    return new DesensitizeContext(desensitize.prefixKeep(), desensitize.suffixKeep(), desensitize.replacer());
                }
                return null;
            }
        },
        CAR_LICENSE(CarLicenseDesensitize.class) {
            @Override
            DesensitizeContext getDesensitize(Annotation annotation) {
                if (annotation instanceof CarLicenseDesensitize) {
                    CarLicenseDesensitize desensitize = (CarLicenseDesensitize) annotation;
                    return new DesensitizeContext(desensitize.prefixKeep(), desensitize.suffixKeep(), desensitize.replacer());
                }
                return null;
            }
        },
        CHINESE_NAME(ChineseNameDesensitize.class) {
            @Override
            DesensitizeContext getDesensitize(Annotation annotation) {
                if (annotation instanceof ChineseNameDesensitize) {
                    ChineseNameDesensitize desensitize = (ChineseNameDesensitize) annotation;
                    return new DesensitizeContext(desensitize.prefixKeep(), desensitize.suffixKeep(), desensitize.replacer());
                }
                return null;
            }
        },
        EMAIL_DESENSITIZE(EmailDesensitize.class) {
            @Override
            DesensitizeContext getDesensitize(Annotation annotation) {
                if (annotation instanceof EmailDesensitize) {
                    EmailDesensitize desensitize = (EmailDesensitize) annotation;
                    return new DesensitizeContext(desensitize.regex(), desensitize.replacer());
                }
                return null;
            }
        },
        FIXED_PHONE(FixedPhoneDesensitize.class) {
            @Override
            DesensitizeContext getDesensitize(Annotation annotation) {
                if (annotation instanceof FixedPhoneDesensitize) {
                    FixedPhoneDesensitize desensitize = (FixedPhoneDesensitize) annotation;
                    return new DesensitizeContext(desensitize.prefixKeep(), desensitize.suffixKeep(), desensitize.replacer());
                }
                return null;
            }
        },
        ID_CARD(IdCardDesensitize.class) {
            @Override
            DesensitizeContext getDesensitize(Annotation annotation) {
                if (annotation instanceof IdCardDesensitize) {
                    IdCardDesensitize desensitize = (IdCardDesensitize) annotation;
                    return new DesensitizeContext(desensitize.prefixKeep(), desensitize.suffixKeep(), desensitize.replacer());
                }
                return null;
            }
        },
        MOBILE(MobileDesensitize.class) {
            @Override
            DesensitizeContext getDesensitize(Annotation annotation) {
                if (annotation instanceof MobileDesensitize) {
                    MobileDesensitize desensitize = (MobileDesensitize) annotation;
                    return new DesensitizeContext(desensitize.prefixKeep(), desensitize.suffixKeep(), desensitize.replacer());
                }
                return null;
            }
        },
        PASSWORD(PasswordDesensitize.class) {
            @Override
            DesensitizeContext getDesensitize(Annotation annotation) {
                if (annotation instanceof PasswordDesensitize) {
                    PasswordDesensitize desensitize = (PasswordDesensitize) annotation;
                    return new DesensitizeContext(desensitize.prefixKeep(), desensitize.suffixKeep(), desensitize.replacer());
                }
                return null;
            }
        },
        ;
        private Class<?> Clazz;

        DesensitizeEnum(Class<?> clazz) {
            Clazz = clazz;
        }


        abstract DesensitizeContext getDesensitize(Annotation annotation);


        public static DesensitizeContext toDesensitizeInfo(Annotation annotation) {
            for (DesensitizeEnum enumObj : values()) {
                if (annotation.annotationType().isAssignableFrom(enumObj.getClazz())) {
                    return enumObj.getDesensitize(annotation);
                }
            }
            throw new FrameworkException("未找到匹配脱敏注解");
        }

    }

    @Data
    @NoArgsConstructor
    class DesensitizeContext {
        private Integer prefixKeep;
        private Integer suffixKeep;
        private String replacer;
        private String regex;

        public DesensitizeContext(int prefixKeep, int suffixKeep, String replacer) {
            this.prefixKeep = prefixKeep;
            this.suffixKeep = suffixKeep;
            this.replacer = replacer;

        }

        public DesensitizeContext(String regex, String replacer) {
            this.regex = regex;
            this.replacer = replacer;

        }
    }
}

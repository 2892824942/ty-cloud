package com.ty.mid.framework.encrypt.core.encryptor.desensitize.handler;

import cn.hutool.core.lang.Assert;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.encrypt.annotation.*;
import com.ty.mid.framework.encrypt.core.context.DesensitizeEncryptContext;
import lombok.Getter;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationType;

import java.lang.annotation.Annotation;

/**
 * 脱敏处理器接口 <p>
 *
 * @author suyouliang
 */
public interface DesensitizationHandler<S extends Annotation> {

    /**
     * 脱敏
     *
     * @param origin 原始字符串
     * @return 脱敏后的字符串
     */
    String desensitize(String origin);


    @Getter
    enum DesensitizeEnum {
        BANK_CARD(BankCardDesensitize.class) {
            @Override
            DesensitizeEncryptContext getDesensitize(Annotation annotation) {
                if (annotation instanceof BankCardDesensitize) {
                    BankCardDesensitize desensitize = (BankCardDesensitize) annotation;
                    return new DesensitizeEncryptContext(desensitize.prefixKeep(), desensitize.suffixKeep(), desensitize.replacer(), desensitize);
                }
                return null;
            }
        },
        CAR_LICENSE(CarLicenseDesensitize.class) {
            @Override
            DesensitizeEncryptContext getDesensitize(Annotation annotation) {
                if (annotation instanceof CarLicenseDesensitize) {
                    CarLicenseDesensitize desensitize = (CarLicenseDesensitize) annotation;
                    return new DesensitizeEncryptContext(desensitize.prefixKeep(), desensitize.suffixKeep(), desensitize.replacer(), desensitize);
                }
                return null;
            }
        },
        CHINESE_NAME(ChineseNameDesensitize.class) {
            @Override
            DesensitizeEncryptContext getDesensitize(Annotation annotation) {
                if (annotation instanceof ChineseNameDesensitize) {
                    ChineseNameDesensitize desensitize = (ChineseNameDesensitize) annotation;
                    return new DesensitizeEncryptContext(desensitize.prefixKeep(), desensitize.suffixKeep(), desensitize.replacer(), desensitize);
                }
                return null;
            }
        },
        EMAIL_DESENSITIZE(EmailDesensitize.class) {
            @Override
            DesensitizeEncryptContext getDesensitize(Annotation annotation) {
                if (annotation instanceof EmailDesensitize) {
                    EmailDesensitize desensitize = (EmailDesensitize) annotation;
                    return new DesensitizeEncryptContext(desensitize.regex(), desensitize.replacer(), desensitize);
                }
                return null;
            }
        },
        FIXED_PHONE(FixedPhoneDesensitize.class) {
            @Override
            DesensitizeEncryptContext getDesensitize(Annotation annotation) {
                if (annotation instanceof FixedPhoneDesensitize) {
                    FixedPhoneDesensitize desensitize = (FixedPhoneDesensitize) annotation;
                    return new DesensitizeEncryptContext(desensitize.prefixKeep(), desensitize.suffixKeep(), desensitize.replacer(), desensitize);
                }
                return null;
            }
        },
        ID_CARD(IdCardDesensitize.class) {
            @Override
            DesensitizeEncryptContext getDesensitize(Annotation annotation) {
                if (annotation instanceof IdCardDesensitize) {
                    IdCardDesensitize desensitize = (IdCardDesensitize) annotation;
                    return new DesensitizeEncryptContext(desensitize.prefixKeep(), desensitize.suffixKeep(), desensitize.replacer(), desensitize);
                }
                return null;
            }
        },
        MOBILE(MobileDesensitize.class) {
            @Override
            DesensitizeEncryptContext getDesensitize(Annotation annotation) {
                if (annotation instanceof MobileDesensitize) {
                    MobileDesensitize desensitize = (MobileDesensitize) annotation;
                    return new DesensitizeEncryptContext(desensitize.prefixKeep(), desensitize.suffixKeep(), desensitize.replacer(), desensitize);
                }
                return null;
            }
        },
        PASSWORD(PasswordDesensitize.class) {
            @Override
            DesensitizeEncryptContext getDesensitize(Annotation annotation) {
                if (annotation instanceof PasswordDesensitize) {
                    PasswordDesensitize desensitize = (PasswordDesensitize) annotation;
                    return new DesensitizeEncryptContext(desensitize.prefixKeep(), desensitize.suffixKeep(), desensitize.replacer(), desensitize);
                }
                return null;
            }
        },
        /**
         * 这种方式是保留最上层EncryptField直接指定脱敏的方式,此方式需要指定对应的加密方式,还要指定desensitizeType,不推荐使用这种方式.但考虑有人喜欢使用同一的的注解头,方便查询保留了
         * 推荐直接使用脱敏注解,如@PasswordDesensitize,@MobileDesensitize等
         */
        COMMON(EncryptField.class) {
            @Override
            DesensitizeEncryptContext getDesensitize(Annotation annotation) {
                EncryptField desensitize = (EncryptField) annotation;
                DesensitizeEnum desensitizeEnum = desensitize.desensitizeType();
                Assert.notNull(desensitizeEnum, "使用通用注解EncryptField实现脱密时,需指定desensitizeType字段");
                Assert.notEquals(desensitizeEnum, DEFAULT, "使用通用注解EncryptField实现脱密时,需指定desensitizeType字段");

                Assert.notEquals(desensitizeEnum, COMMON, "使用通用注解EncryptField实现脱密时,desensitizeType不可指定COMMON方式");
                Class<? extends Annotation> clazz = (Class<? extends Annotation>) desensitizeEnum.getClazz();
                AnnotationType instance = AnnotationType.getInstance(clazz);
                Annotation newDefaultAnnotation = AnnotationParser.annotationForMap(clazz, instance.memberDefaults());
                //这里作为上层父类注解,再次分发
                return desensitizeEnum.getDesensitize(newDefaultAnnotation);
            }
        },
        /**
         * 仅用来标识默认值
         */
        DEFAULT(Void.class) {
            @Override
            DesensitizeEncryptContext getDesensitize(Annotation annotation) {
                return null;
            }
        },
        ;;
        private Class<?> Clazz;

        DesensitizeEnum(Class<?> clazz) {
            Clazz = clazz;
        }

        public static DesensitizeEncryptContext toDesensitizeInfo(Annotation annotation) {
            for (DesensitizeEnum enumObj : values()) {
                if (annotation.annotationType().isAssignableFrom(enumObj.getClazz())) {
                    return enumObj.getDesensitize(annotation);
                }
            }
            throw new FrameworkException("未找到匹配脱敏注解");
        }

        abstract DesensitizeEncryptContext getDesensitize(Annotation annotation);

    }


}

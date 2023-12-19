package com.ty.mid.framework.common.constant;

/**
 * description: EncryptConstant
 * date: 2021/7/14 10:13
 * author: wuqiuhang
 */
public interface EncryptConstant {
    interface EncryptType {
        String RC4 = "1";

        String AES = "2";

        String RSA = "3";
    }

    interface EncryptOperate {
        String ENCRYPT = "encrypt";

        String DECRYPT = "decrypt";

    }
}

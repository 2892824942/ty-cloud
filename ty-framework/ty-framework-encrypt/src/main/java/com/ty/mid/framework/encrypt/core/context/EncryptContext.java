package com.ty.mid.framework.encrypt.core.context;

import com.ty.mid.framework.encrypt.enumd.AlgorithmType;
import com.ty.mid.framework.encrypt.enumd.EncodeType;
import lombok.Data;

/**
 * 加密上下文 用于encryptor传递必要的参数。
 *
 * @author suyouliang
 * @version 4.6.0
 */
@Data
public class EncryptContext {

    /**
     * 默认算法
     */
    private AlgorithmType algorithm;

    /**
     * 编码方式，base64/hex
     */
    private EncodeType encode;
}

package com.ty.mid.framework.security.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ty.mid.framework.common.exception.FrameworkException;
import com.ty.mid.framework.common.util.Validator;
import com.ty.mid.framework.core.config.SecurityConfiguration;
import com.ty.mid.framework.core.util.AesUtils;
import com.ty.mid.framework.security.AuthToken;
import com.ty.mid.framework.security.TokenGenerator;

import java.io.IOException;
import java.util.Map;

public class AesTokenGenerator implements TokenGenerator {

    private SecurityConfiguration configuration;
    private ObjectMapper objectMapper;

    public AesTokenGenerator(SecurityConfiguration configuration, ObjectMapper mapper) {
        this.configuration = configuration;
        this.objectMapper = mapper;

        Validator.requireNonEmpty(this.configuration.getAes().getAesKey(), "[AesTokenGenerator] AesKey 必须配置！");
        if (this.configuration.getAes().getAesKey().length() % 16 != 0) {
            throw new FrameworkException("[AesTokenGenerator]  AesKey 长度必须为 [16] 的整数倍");
        }
    }

    @Override
    public String generateToken(Map<String, ?> tokenData) {
        try {
            String json = this.objectMapper.writeValueAsString(tokenData);
            return AesUtils.encrypt(json, this.configuration.getAes().getAesKey());
        } catch (JsonProcessingException e) {
            throw new FrameworkException(e.getMessage());
        }
    }

    @Override
    public <A extends AuthToken> String generateSureToken(A tokenData) {
        try {
            String json = this.objectMapper.writeValueAsString(tokenData);
            return AesUtils.encrypt(json, this.configuration.getAes().getAesKey());
        } catch (JsonProcessingException e) {
            throw new FrameworkException(e.getMessage());
        }
    }

    @Override
    public <A extends AuthToken> A decodeSureToken(String token) {
        try {
            String json = AesUtils.decrypt(token, this.configuration.getAes().getAesKey());
            return objectMapper.readValue(json, new TypeReference<A>() {
            });
        } catch (IOException e) {
            throw new FrameworkException(e.getMessage());
        }
    }


    @Override
    public Map<String, Object> decodeToken(String token) {
        try {
            String json = AesUtils.decrypt(token, this.configuration.getAes().getAesKey());
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new FrameworkException(e.getMessage());
        }
    }
}

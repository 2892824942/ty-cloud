package com.ty.mid.framework.common.util;

import com.ty.mid.framework.common.pojo.KVPair;
import com.ty.mid.framework.common.lang.NonNull;
import com.ty.mid.framework.common.util.collection.MiscUtils;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class DomainUtil {

    /**
     * 重命名键值对
     *
     * @param dto
     * @param keyName
     * @param valueName
     * @return
     */
    public static Map<String, Object> renameKeyValue(@NonNull KVPair dto, @NonNull String keyName, @NonNull String valueName) {
        if (dto == null) {
            return null;
        }

        return MiscUtils.toMap(keyName, dto.getKey(), valueName, dto.getValue());
    }

    /**
     * 重命名键值对
     *
     * @param dtos
     * @param keyName
     * @param valueName
     * @return
     */
    public static Collection<Map<String, Object>> renameKeyValue(@NonNull Collection<KVPair> dtos, @NonNull String keyName, @NonNull String valueName) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream().map(d -> renameKeyValue(d, keyName, valueName)).collect(Collectors.toList());
    }

}

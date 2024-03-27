package com.ty.mid.framework.service.wrapper;

import java.util.Collection;
import java.util.Map;

public interface UserNameTranslation {
    Map<Long, String> getUserNameMap(Collection<Long> userIdList);
}

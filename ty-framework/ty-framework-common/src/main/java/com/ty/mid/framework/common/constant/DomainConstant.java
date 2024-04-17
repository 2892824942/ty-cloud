package com.ty.mid.framework.common.constant;

public interface DomainConstant {

    interface System {
        /**
         * RPC API 的前缀
         */
        String RPC_API_PREFIX = "/rpc-api";

        String[] DEFAULT_EXCLUDE_URI = new String[]{"/**/webjars/**", "/**/swagger-resources/**", "/**/v3/api-docs/**"
                , "/**/v3/api-docs/**", "/**/doc.html", "/**/*.js", "/**/*.css","/favicon.ico"};
    }

    interface Fields {
        String ID = "id";

        String ADD_BY = "addBy";

        String UPDATE_BY = "updateBy";

        String ADD_TIME = "addTime";

        String UPDATE_TIME = "updateTime";

        String ADD_BY_PARTY = "addByParty";

        String UPDATE_BY_PARTY = "updateByParty";

        String IS_DELETED = "isDeleted";

        String IS_DISABLED = "isDisabled";

        String IS_ENABLED = "isEnabled";

        String SEQUENCE = "sequence";

        String REVISION = "revision";

        String FROM_DATE = "fromDate";

        String THRU_DATE = "thruDate";
    }

    interface Columns {

        String ID = "id";

        String CREATOR = "creator";

        String UPDATER = "updater";

        String CREATE_TIME = "add_time";

        String UPDATE_TIME = "update_time";

        String DELETED = "deleted";

        String DISABLED = "disabled";

        String ENABLED = "enabled";

        String SEQUENCE = "sequence";

        String VERSION = "version";

        String FROM_DATE = "from_date";

        String TO_DATE = "to_date";

        String PARTY_ID = "party_id";

        String USER_LOGIN_ID = "user_login_id";
    }

}

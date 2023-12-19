package com.ty.mid.framework.starter.configuration;

import com.ty.mid.framework.security.shiro.subject.NoSessionSubjectFactory;
import org.apache.shiro.authc.AbstractAuthenticator;
import org.apache.shiro.authc.AuthenticationListener;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.spring.web.config.ShiroWebConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class AbstractShiroWebAutoConfiguration extends ShiroWebConfiguration {

    @Autowired(required = false)
    private List<AuthenticationListener> listeners = new ArrayList<>(0);

    protected LinkedHashMap<String, String> staticResources() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("/**/*.css", "anon");
        map.put("/**/*.js", "anon");
        map.put("/**/*.png", "anon");
        return map;
    }

    protected LinkedHashMap<String, String> swaggerUiResources() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("/swagger-ui.html", "anon");
        map.put("/swagger-ui.html/**", "anon");
        map.put("/swagger-resources/**", "anon");
        map.put("/v2/api-docs/**", "anon");
        map.put("/webjars/**", "anon");
        return map;
    }

    protected LinkedHashMap<String, String> h2ConsoleResources() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("/h2-console/**", "cors, noSessionCreation, anon");
        map.put("/authc/login", "cors, noSessionCreation, anon");
        return map;
    }

    @Override
    protected SubjectFactory subjectFactory() {
        return new NoSessionSubjectFactory();
    }

    @Override
    protected SessionStorageEvaluator sessionStorageEvaluator() {
        DefaultSessionStorageEvaluator evaluator = new DefaultSessionStorageEvaluator();
        evaluator.setSessionStorageEnabled(false);
        return evaluator;
    }

    protected LinkedHashMap<String, String> otherResources() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("/**", "cors, noSessionCreation, rest, authc");
        return map;
    }

    @Override
    protected Authenticator authenticator() {
        Authenticator authenticator = super.authenticator();
        if (authenticator instanceof AbstractAuthenticator) {
            AbstractAuthenticator abstractAuthenticator = (AbstractAuthenticator) authenticator;
            abstractAuthenticator.setAuthenticationListeners(listeners == null ? Collections.emptyList() : listeners);
        }

        return authenticator;
    }
}

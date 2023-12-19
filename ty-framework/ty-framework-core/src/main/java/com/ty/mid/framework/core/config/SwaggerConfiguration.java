package com.ty.mid.framework.core.config;

import com.ty.mid.framework.common.constant.FrameworkConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "framework.swagger")
public class SwaggerConfiguration {

    private String title = FrameworkConstant.Swagger.title;

    private String description = FrameworkConstant.Swagger.description;

    private String version = FrameworkConstant.Swagger.version;

    private String termsOfServiceUrl = FrameworkConstant.Swagger.termsOfServiceUrl;

    private String contactName = FrameworkConstant.Swagger.contactName;

    private String contactUrl = FrameworkConstant.Swagger.contactUrl;

    private String contactEmail = FrameworkConstant.Swagger.contactEmail;

    private String license = FrameworkConstant.Swagger.license;

    private String licenseUrl = FrameworkConstant.Swagger.licenseUrl;

    private String defaultIncludePattern = FrameworkConstant.Swagger.defaultIncludePattern;

    private String host = FrameworkConstant.Swagger.host;

    private String[] protocols = FrameworkConstant.Swagger.protocols;

    private boolean useDefaultResponseMessages = FrameworkConstant.Swagger.useDefaultResponseMessages;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    public void setTermsOfServiceUrl(String termsOfServiceUrl) {
        this.termsOfServiceUrl = termsOfServiceUrl;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactUrl() {
        return contactUrl;
    }

    public void setContactUrl(String contactUrl) {
        this.contactUrl = contactUrl;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public String getDefaultIncludePattern() {
        return defaultIncludePattern;
    }

    public void setDefaultIncludePattern(String defaultIncludePattern) {
        this.defaultIncludePattern = defaultIncludePattern;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String[] getProtocols() {
        return protocols;
    }

    public void setProtocols(final String[] protocols) {
        this.protocols = protocols;
    }

    public boolean isUseDefaultResponseMessages() {
        return useDefaultResponseMessages;
    }

    public void setUseDefaultResponseMessages(final boolean useDefaultResponseMessages) {
        this.useDefaultResponseMessages = useDefaultResponseMessages;
    }
}
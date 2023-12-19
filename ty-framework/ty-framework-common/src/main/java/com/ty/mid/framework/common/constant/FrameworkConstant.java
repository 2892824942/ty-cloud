package com.ty.mid.framework.common.constant;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuchenglong
 * @createTime 2019-08-15 10:07
 */
public interface FrameworkConstant {

    interface Async {

        int corePoolSize = 2;
        int maxPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        int queueCapacity = 10000;
    }

    interface Http {

        interface Cache {

            int timeToLiveInDays = 1461; // 4 years (including leap day)
        }
    }

    interface Cache {

        interface Hazelcast {

            int timeToLiveSeconds = 3600; // 1 hour
            int backupCount = 1;

            interface ManagementCenter {

                boolean enabled = false;
                int updateInterval = 3;
                String url = "";
            }
        }

        interface Ehcache {

            int timeToLiveSeconds = 3600; // 1 hour
            long maxEntries = 100;
        }

        interface Infinispan {

            String configFile = "default-configs/default-jgroups-tcp.xml";
            boolean statsEnabled = false;

            interface Local {

                long timeToLiveSeconds = 60; // 1 minute
                long maxEntries = 100;
            }

            interface Distributed {

                long timeToLiveSeconds = 60; // 1 minute
                long maxEntries = 100;
                int instanceCount = 1;
            }

            interface Replicated {

                long timeToLiveSeconds = 60; // 1 minute
                long maxEntries = 100;
            }
        }

        interface Memcached {

            boolean enabled = false;
            String servers = "localhost:11211";
            int expiration = 300; // 5 minutes
            boolean useBinaryProtocol = true;
        }
    }

    interface Mail {
        boolean enabled = false;
        String from = "";
        String baseUrl = "";
    }

    interface Security {

        interface ClientAuthorization {

            String accessTokenUri = null;
            String tokenServiceId = null;
            String clientId = null;
            String clientSecret = null;
        }

        interface Authentication {

            interface Jwt {

                String secret = null;
                String base64Secret = null;
                long tokenValidityInSeconds = 1800; // 30 minutes
                long tokenValidityInSecondsForRememberMe = 2592000; // 30 days
            }
        }

        interface RememberMe {

            String key = null;
        }
    }

    interface Swagger {

        String title = "Application API";
        String description = "API documentation";
        String version = "0.0.1";
        String termsOfServiceUrl = null;
        String contactName = null;
        String contactUrl = null;
        String contactEmail = null;
        String license = null;
        String licenseUrl = null;
        String defaultIncludePattern = "/api/.*";
        String host = null;
        String[] protocols = {};
        boolean useDefaultResponseMessages = true;
    }

    interface Metrics {

        interface Jmx {

            boolean enabled = false;
        }

        interface Logs {

            boolean enabled = false;
            long reportFrequency = 60;

        }

        interface Prometheus {

            boolean enabled = false;
            String endpoint = "/prometheusMetrics";
        }
    }

    interface Logging {

        boolean useJsonFormat = false;

        interface Logstash {

            boolean enabled = false;
            String host = "localhost";
            int port = 5000;
            int queueSize = 512;
        }
    }

    interface Gateway {

        Map<String, List<String>> authorizedMicroservicesEndpoints = new LinkedHashMap<>();

        interface RateLimiting {

            boolean enabled = false;
            long limit = 100_000L;
            int durationInSeconds = 3_600;

        }
    }

    interface Ribbon {

        String[] displayOnActiveProfiles = null;
    }

    interface Registry {

        String password = null;
    }

    interface HttpMethod {
        String GET = "GET";
        String HEAD = "HEAD";
        String POST = "POST";
        String PUT = "PUT";
        String PATCH = "PATCH";
        String DELETE = "DELETE";
        String OPTIONS = "OPTIONS";
        String TRACE = "TRACE";
    }

    interface Encode {

        String UTF_8 = "UTF-8";

        Charset CHARSET_UTF_8 = Charset.forName(UTF_8);

    }

    interface AES {
        String DEFAULT_MODE_NAME = "ECB";
        int DEFAULT_BLOCK_SIZE = 128;
        int DEFAULT_KEY_SIZE = 128;
        String DEFAULT_PADDING_NAME = "PKCS5Padding";
    }

}
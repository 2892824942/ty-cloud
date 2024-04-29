package com.ty.mid.framework.api.switcher.deserializer.support;

import com.ty.mid.framework.api.switcher.constant.ApiSwitcherConstant;
import com.ty.mid.framework.api.switcher.exception.ApiSwitcherConfigDeserializeException;
import com.ty.mid.framework.api.switcher.model.ApiSwitcherConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于键值对配置的反序列化器 <p>
 * @author suyouliang <p>
 * @createTime 2023-08-14 18:25 
 */
public class AbstractKeyValueApiSwitcherConfigDeserializer extends AbstractApiSwitcherConfigDeserializer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // 开始时间后缀
    private String fromDateSuffix = ApiSwitcherConstant.DEFAULT_FROM_DATE_SUFFIX;
    // 结束时间后缀
    private String thruDateSuffix = ApiSwitcherConstant.DEFAULT_THRU_DATE_SUFFIX;
    // 提示消息后缀
    private String tipSuffix = ApiSwitcherConstant.DEFAULT_TIP_SUFFIX;
    // 日期格式
    private String datePattern = ApiSwitcherConstant.DEFAULT_DATE_PATTERN;
    // 行分隔符
    private String lineSeparator = ApiSwitcherConstant.DEFAULT_LINE_SEPARATOR;

    public AbstractKeyValueApiSwitcherConfigDeserializer() {
    }

    public AbstractKeyValueApiSwitcherConfigDeserializer(String fromDateSuffix, String thruDateSuffix, String tipSuffix) {
        this.fromDateSuffix = fromDateSuffix;
        this.thruDateSuffix = thruDateSuffix;
        this.tipSuffix = tipSuffix;
    }

    public AbstractKeyValueApiSwitcherConfigDeserializer(String fromDateSuffix, String thruDateSuffix, String tipSuffix, String datePattern) {
        this.fromDateSuffix = fromDateSuffix;
        this.thruDateSuffix = thruDateSuffix;
        this.tipSuffix = tipSuffix;
        this.datePattern = datePattern;
    }

    public AbstractKeyValueApiSwitcherConfigDeserializer(String fromDateSuffix, String thruDateSuffix, String tipSuffix, String datePattern, String lineSeparator) {
        this.fromDateSuffix = fromDateSuffix;
        this.thruDateSuffix = thruDateSuffix;
        this.tipSuffix = tipSuffix;
        this.datePattern = datePattern;
        this.lineSeparator = lineSeparator;
    }

    protected Map<String, ApiSwitcherConfig> doDeserialize(String configString) {
        String[] lineArray = configString.split(lineSeparator);

        List<String> lines = new ArrayList<>(Arrays.asList(lineArray));

        // remove error lines
        lines.removeAll(this.resolveIgnoreLines(lines));

        if (CollectionUtils.isEmpty(lines)) {
            throw new ApiSwitcherConfigDeserializeException("after empty/comment/error lines remove ,there is no line to read!");
        }

        log.info("read {} config lines", lines.size());

        Map<String, ApiSwitcherConfig> configMap = this.readConfig(lines);
        if (CollectionUtils.isEmpty(configMap)) {
            log.info("after read config, there is no config left!");
        }

        return configMap;
    }

    /**
     * 将行数据转换为 ApiSwitcherConfig
     *
     * @param lines
     * @return
     */
    private Map<String, ApiSwitcherConfig> readConfig(List<String> lines) {
        Map<String, ApiSwitcherConfig> configMap = new HashMap<>();

        // read config
        for (String line : lines) {
            String[] kv = line.split("=");
            String key = kv[0];
            String value = kv[1];

            if (StringUtils.isEmpty(value)) {
                log.info("skip {} config because config value is empty", key);
                continue;
            }

            String api = key.split("\\.")[0];
            ApiSwitcherConfig config = this.getOrInitConfig(api, configMap);

            // read tip
            if (key.endsWith(tipSuffix)) {
                config.setTipMessage(value);
                continue;
            }

            // read fromDate
            if (key.endsWith(fromDateSuffix)) {
                config.setFromDate(this.formatDate(value));
                continue;
            }

            // read thruDate
            if (key.endsWith(thruDateSuffix)) {
                config.setThruDate(this.formatDate(value));
                continue;
            }
        }

        // check config valid
        Set<String> invalidConfig = configMap.keySet().stream().filter(k -> configMap.get(k).isConfigInValid()).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(invalidConfig)) {
            log.warn("there is {} config invalid, will remove it", invalidConfig);
            invalidConfig.stream().forEach(a -> log.warn("invalid key: {}", a));
        }

        return configMap.entrySet().stream().filter(e -> !invalidConfig.contains(e.getKey())).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    private ApiSwitcherConfig getOrInitConfig(String api, Map<String, ApiSwitcherConfig> map) {
        if (null == map.get(api)) {
            ApiSwitcherConfig config = new ApiSwitcherConfig(api);
            config.setApiName(api);

            map.put(api, config);
        }

        return map.get(api);
    }

    /**
     * 从所有配置行中读取 错误行、空行、注释行
     *
     * @param lines
     * @return
     */
    private List<String> resolveIgnoreLines(List<String> lines) {
        if (CollectionUtils.isEmpty(lines)) {
            return Collections.emptyList();
        }

        // empty lines
        List<String> emptyLines = lines.stream().filter(a -> StringUtils.isEmpty(a)).collect(Collectors.toList());

        // comment lines
        List<String> commentLines = lines.stream().filter(a -> StringUtils.trimLeadingWhitespace(a).startsWith("#")).collect(Collectors.toList());

        // error lines
        List<String> errorLines = lines.stream()
                .filter(a -> {
                    // = 分隔
                    String[] split = a.split("=");
                    if (split == null || split.length != 2 || StringUtils.isEmpty(split[0])) {
                        return true;
                    }

                    // . 分隔
                    String[] key = split[0].split("\\.");
                    if (split.length != 2) {
                        return true;
                    }

                    return StringUtils.isEmpty(key[0]) || StringUtils.isEmpty(key[1]);
                }).collect(Collectors.toList());

        log.info("found {} empty lines, {} comment lines, {} error lines", emptyLines.size(), commentLines.size(), errorLines.size());

        if (!CollectionUtils.isEmpty(errorLines)) {
            log.warn("there have {} error format lines, correct line format is: {}", errorLines.size(), "api.suffix=xxxx");
            errorLines.stream().forEach(a -> log.warn("error line: {}", a));
        }

        List<String> merged = new ArrayList<>();
        merged.addAll(emptyLines);
        merged.addAll(commentLines);
        merged.addAll(errorLines);

        return merged;
    }

    private Date formatDate(String dateString) {
        if (StringUtils.isEmpty(dateString)) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(this.datePattern);

        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            log.warn("parse date string error", e);
        }
        return date;
    }
}

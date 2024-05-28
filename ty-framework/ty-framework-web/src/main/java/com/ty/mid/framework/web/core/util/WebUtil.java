package com.ty.mid.framework.web.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ty.mid.framework.common.util.GenericsUtil;
import com.ty.mid.framework.common.util.JsonUtils;
import com.ty.mid.framework.core.util.servlet.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationPart;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class WebUtil {
    public static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, Integer order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(order);
        return bean;
    }

    public static Map<String, Object> getBody(HttpServletRequest request) {
        //解析出的requestBody中有很多\r\n以及空字符,这里处理
        Map<String, Object> bodyMap = new HashMap<>();
        //1.Json格式解析
        if (ServletUtils.isJsonRequest(request)) {
            String body = ServletUtils.getBody(request);
            bodyMap = StrUtil.isNotBlank(body)
                    ? JsonUtils.parseObject(body, new TypeReference<Map<String, Object>>() {
            })
                    : Collections.emptyMap();
            return bodyMap;
        }
        //2.Form表单解析(仅只获取文件)
        if (ServletUtils.isFormRequest(request)) {
            try {
                Collection<Part> parts = request.getParts();
                // 获取表单中的数据,这里只获取文件的,非文件参数可以在paramMap中获取
                for (Part part : parts) {
                    ApplicationPart applicationPart = (ApplicationPart) part;
                    Field fileItemField = ReflectUtil.getField(ApplicationPart.class, "fileItem");
                    fileItemField.setAccessible(true);
                    FileItem fileItem = (FileItem) fileItemField.get(applicationPart);
                    String name = applicationPart.getName();
                    if (fileItem.isFormField()) {
                        String filedValue = new String(fileItem.get());
                        putOrMergeIfPresent(bodyMap, name, filedValue);
                        continue;
                    }
                    //文件类型数据,只取文件名,文件内容太大
                    String submittedFileName = applicationPart.getSubmittedFileName();
                    bodyMap.put(name, submittedFileName);
                }
            } catch (IOException | ServletException | IllegalAccessException e) {
                log.error("form表单数据解析失败", e);
            }
            return bodyMap;
        }
        //2.Form-urlencoded表单解析,暂时没有找到很好的方式,既可以重新读取,由不至于body写入args时丢失
//         if (ServletUtils.isFormUrlRequest(request)) {
//            String body = ServletUtils.getBody(request);
//            if (StrUtil.isEmpty(body)) {
//                return bodyMap;
//            }
//            String[] split = body.split("&");
//            if (ArrayUtil.isNotEmpty(split)) {
//                //同一组数据
//                for (String s : split) {
//                    String[] innerSplit = s.split("=");
//                    if (innerSplit.length < 1) {
//                        continue;
//                    }
//                    String fieldName = innerSplit[0];
//                    String fieldValue = null;
//                    if (innerSplit.length == 2) {
//                        fieldValue = innerSplit[1];
//                    }
//                    putOrMergeIfPresent(bodyMap, fieldName, fieldValue);
//                }
//            }
//            return bodyMap;

//    }
        return bodyMap;
}


    private static void putOrMergeIfPresent(Map<String, Object> bodyMap, String fieldName, String fieldValue) {
        Object existValue = bodyMap.get(fieldName);
        if (Objects.isNull(existValue)) {
            bodyMap.put(fieldName, fieldValue);
            return;
        }
        //合并数组形式
        if (Collection.class.isAssignableFrom(existValue.getClass())) {
            Collection<Object> existValueCollection = GenericsUtil.cast(existValue);
            existValueCollection.add(fieldValue);
            return;
        }
        //存在还未合并
        List<Object> mergeValue = CollUtil.newArrayList(existValue, fieldValue);
        bodyMap.put(fieldName, mergeValue);

    }

}

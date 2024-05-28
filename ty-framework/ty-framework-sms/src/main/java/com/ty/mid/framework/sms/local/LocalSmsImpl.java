package com.ty.mid.framework.sms.local;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.callback.CallBack;
import org.dromara.sms4j.api.entity.SmsResponse;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 新增厂商的具体短信逻辑实现{@link SmsBlend}
 * 本类的逻辑：总是在日志中输出参数信息，并成功返回调用结果。
 *
 * @author huangchengxing
 */


@Slf4j
public class LocalSmsImpl implements SmsBlend {

    @Override
    public String getConfigId() {
        return LocalConfig.CONFIG_ID;
    }

    @Override
    public String getSupplier() {
        return LocalConfig.SUPPLIER;
    }

    /**
     * <p>说明：发送固定消息模板短信
     * <p>此方法将使用配置文件中预设的短信模板进行短信发送
     * <p>该方法指定的模板变量只能存在一个（配置文件中）
     * <p>如使用的是腾讯的短信，参数字符串中可以同时存在多个参数，使用 & 分隔例如：您的验证码为{1}在{2}分钟内有效，可以传为  message="xxxx"+"&"+"5"
     * sendMessage
     *
     * @param phone 接收短信的手机号
     *              message 消息内容
     * @author :Wind
     */
    @Override
    public SmsResponse sendMessage(String phone, String message) {
        log.info("send message: phone={}, message={}", phone, message);
        return getResponse(new JSONObject()
                .set("phone", phone)
                .set("message", message));
    }

    /**
     * sendMessage
     * <p>说明：发送固定消息模板多模板参数短信
     *
     * @param phone    接收短信的手机号
     * @param messages 模板内容
     * @author :Wind
     */
    @Override
    public SmsResponse sendMessage(String phone, LinkedHashMap<String, String> messages) {
        log.info("send message: phone={}, messages={}", phone, messages);
        return getResponse(new JSONObject()
                .set("phone", phone)
                .set("messages", new JSONObject(messages)));
    }

    /**
     * <p>说明：使用自定义模板发送短信
     * sendMessage
     *
     * @param templateId 模板id
     * @param messages   key为模板变量名称 value为模板变量值
     * @author :Wind
     */
    @Override
    public SmsResponse sendMessage(String phone, String templateId, LinkedHashMap<String, String> messages) {
        log.info("send message: phone={}, templateId={}, messages={}", phone, templateId, messages);
        return getResponse(new JSONObject()
                .set("phone", phone)
                .set("templateId", templateId)
                .set("messages", new JSONObject(messages)));
    }

    /**
     * <p>说明：群发固定模板短信
     * massTexting
     *
     * @author :Wind
     */
    @Override
    public SmsResponse massTexting(List<String> phones, String message) {
        log.info("mass texting: phones={}, message={}", phones, message);
        return getResponse(new JSONObject()
                .set("phones", phones)
                .set("message", message));
    }

    /**
     * <p>说明：使用自定义模板群发短信
     * massTexting
     *
     * @author :Wind
     */
    @Override
    public SmsResponse massTexting(List<String> phones, String templateId, LinkedHashMap<String, String> messages) {
        log.info("mass texting: phones={}, templateId={}, messages={}", phones, templateId, messages);
        return getResponse(new JSONObject()
                .set("phones", phones)
                .set("templateId", templateId)
                .set("messages", new JSONObject(messages)));
    }

    /**
     * <p>说明：异步短信发送，固定消息模板短信
     * sendMessageAsync
     *
     * @param phone    要发送的号码
     * @param message  发送内容
     * @param callBack 回调
     * @author :Wind
     */
    @Override
    public void sendMessageAsync(String phone, String message, CallBack callBack) {
        log.info("send message asynchronously: phone={}, message={}", phone, message);
        // do nothing
        callBack.callBack(getResponse(new JSONObject()
                .set("phone", phone)
                .set("message", message)));
    }

    /**
     * <p>说明：异步发送短信，不关注发送结果
     * sendMessageAsync
     *
     * @param phone   要发送的号码
     * @param message 发送内容
     * @author :Wind
     */
    @Override
    public void sendMessageAsync(String phone, String message) {
        log.info("send message asynchronously: phone={}, message={}", phone, message);
        // do nothing
    }

    /**
     * <p>说明：异步短信发送，使用自定义模板发送短信
     * sendMessage
     *
     * @param templateId 模板id
     * @param messages   key为模板变量名称 value为模板变量值
     * @param callBack   回调
     * @author :Wind
     */
    @Override
    public void sendMessageAsync(String phone, String templateId, LinkedHashMap<String, String> messages, CallBack callBack) {
        log.info("send message asynchronously: phone={}, templateId={}, messages={}", phone, templateId, messages);
        // do nothing
        callBack.callBack(getResponse(new JSONObject()
                .set("phone", phone)
                .set("templateId", templateId)
                .set("messages", new JSONObject(messages))));
    }

    /**
     * <p>说明：异步短信发送，使用自定义模板发送短信，不关注发送结果
     * sendMessageAsync
     *
     * @param templateId 模板id
     * @param messages   key为模板变量名称 value为模板变量值
     * @author :Wind
     */
    @Override
    public void sendMessageAsync(String phone, String templateId, LinkedHashMap<String, String> messages) {
        log.info("send message asynchronously: phone={}, templateId={}, messages={}", phone, templateId, messages);
        // do nothing
    }

    /**
     * <p>说明：使用固定模板发送延时短信
     * delayedMessage
     *
     * @param phone       接收短信的手机号
     * @param message     要发送的短信
     * @param delayedTime 延迟时间
     * @author :Wind
     */
    @Override
    public void delayedMessage(String phone, String message, Long delayedTime) {
        log.info("delayed message: phone={}, message={}, delayedTime={}", phone, message, delayedTime);
    }

    /**
     * <p>说明：使用自定义模板发送定时短信 sendMessage
     * delayedMessage
     *
     * @param templateId  模板id
     * @param messages    key为模板变量名称 value为模板变量值
     * @param phone       要发送的手机号
     * @param delayedTime 延迟的时间
     * @author :Wind
     */
    @Override
    public void delayedMessage(String phone, String templateId, LinkedHashMap<String, String> messages, Long delayedTime) {
        log.info("delayed message: phone={}, templateId={}, messages={}, delayedTime={}", phone, templateId, messages, delayedTime);
    }

    /**
     * <p>说明：群发延迟短信
     * delayMassTexting
     *
     * @param phones 要群体发送的手机号码
     * @author :Wind
     */
    @Override
    public void delayMassTexting(List<String> phones, String message, Long delayedTime) {
        log.info("delayed mass texting: phones={}, message={}, delayedTime={}", phones, message, delayedTime);
    }

    /**
     * <p>说明：使用自定义模板发送群体延迟短信
     * delayMassTexting
     *
     * @param phones      要群体发送的手机号码
     * @param templateId  模板id
     * @param messages    key为模板变量名称 value为模板变量值
     * @param delayedTime 延迟的时间
     * @author :Wind
     */
    @Override
    public void delayMassTexting(List<String> phones, String templateId, LinkedHashMap<String, String> messages, Long delayedTime) {
        log.info("delayed mass texting: phones={}, templateId={}, messages={}, delayedTime={}", phones, templateId, messages, delayedTime);
    }

    /*
     * <p>说明：封装结果的方法（可以没有）
     * getResponse
     *
     * @param resJson      厂商的返回信息
     * @author :Wind
     * */
    private SmsResponse getResponse(JSONObject resJson) {
        SmsResponse smsResponse = new SmsResponse();
        smsResponse.setSuccess(true);
        smsResponse.setData(resJson);
        smsResponse.setConfigId(getConfigId());
        return smsResponse;
    }
}

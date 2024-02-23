package com.ty.mid.framework.web.jackson.core.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.DateTimeSerializerBase;

import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * LocalDateTime序列化规则
 * <p>
 * 会将LocalDateTime序列化为毫秒级时间戳
 */
public class LocalDateTimeSerializer extends DateTimeSerializerBase<LocalDateTime> {

    public static final LocalDateTimeSerializer INSTANCE = new LocalDateTimeSerializer();

    protected LocalDateTimeSerializer() {
        super(LocalDateTime.class, null, null);
    }

    @Override
    public DateTimeSerializerBase<LocalDateTime> withFormat(Boolean aBoolean, DateFormat dateFormat) {
        return null;
    }

    @Override
    protected long _timestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (this._asTimestamp(provider)) {
            gen.writeNumber(this._timestamp(value));
        } else {
            this._serializeAsString(new Date(_timestamp(value)), gen, provider);
        }
        gen.writeNumber(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }
}

package ty.framework.sms.test;

import com.ty.mid.framework.core.spring.SpringContextHelper;
import com.ty.mid.framework.sms.local.LocalConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Map;

@SpringBootTest(classes = SmsTestApplication.class)
@Slf4j
@RequiredArgsConstructor
public class SmsTests {
    private final ApplicationContext applicationContext;

    @Test
    public void testApi() throws Exception {
        Map<String, SmsBlend> beansOfType = applicationContext.getBeansOfType(SmsBlend.class);
        SmsBlend smsBlend = SmsFactory.getSmsBlend(LocalConfig.CONFIG_ID);
        smsBlend.sendMessage("13288889999", "helloWorld!");
    }

}

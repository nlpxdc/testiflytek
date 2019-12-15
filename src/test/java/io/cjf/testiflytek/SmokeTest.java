package io.cjf.testiflytek;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

public class SmokeTest {

    @Test
    void aaa(){
        String aaa = "{\"data\":null,\"err_no\":0,\"failed\":null,\"ok\":-1}";
        final JSONObject jsonObject = JSON.parseObject(aaa);
        final Boolean ok = jsonObject.getBoolean("ok");
    }
}

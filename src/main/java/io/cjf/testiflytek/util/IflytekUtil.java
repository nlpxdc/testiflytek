package io.cjf.testiflytek.util;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;

public class IflytekUtil {
    public static String sign(String appId, String appSecret, Long ts){
        String baseString = appId+ts;
        final String md5Str = DigestUtils.md5DigestAsHex(baseString.getBytes());
        final byte[] bytes = HmacUtils.hmacSha1(appSecret, md5Str);
        final String signStr = Base64Utils.encodeToString(bytes);
        return signStr;
    }
}

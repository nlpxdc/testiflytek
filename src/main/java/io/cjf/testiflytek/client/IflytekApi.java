package io.cjf.testiflytek.client;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "iflytekapi", url = "http://raasr.xfyun.cn", configuration = IflytekApi.Configuration.class)
public interface IflytekApi {

    @PostMapping(value = "/api/prepare", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String prepare(@RequestBody Map<String, ?> form);

    @PostMapping(value = "/api/getProgress", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String getProgress(@RequestBody Map<String, ?> form);

    @PostMapping(value = "/api/merge", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String merge(@RequestBody Map<String, ?> form);

    @PostMapping(value = "/api/getResult", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String getResult(@RequestBody Map<String, ?> form);

    class Configuration {
        @Bean
        Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> converters) {
            return new SpringFormEncoder(new SpringEncoder(converters));
        }
    }

}

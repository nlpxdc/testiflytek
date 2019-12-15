package io.cjf.testiflytek.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.cjf.testiflytek.util.IflytekUtil;
import io.cjf.testiflytek.util.SliceIdGenerator;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

@Service
public class IflytekServiceImpl implements IflytekService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IflytekApi iflytekApi;

    @Value("${ifly.appId}")
    private String appId;

    @Value("${ifly.appSecret}")
    private String appSecret;

    private CloseableHttpClient httpClient = HttpClients.createDefault();

    public static final int SLICE_SIZE = 1024 * 1024 * 10;

    @Override
    public String prepare(String file_name, Long file_len, Long slice_num, boolean seperate, byte speakersNum, String language) {

        final Date now = new Date();
        final long nowTimestamp = now.getTime();
        long ts = nowTimestamp / 1000;

        final String signa = IflytekUtil.sign(appId, appSecret, ts);

        final HashMap<String, Object> form = new HashMap<>();
        form.put("app_id", appId);
        form.put("signa", signa);
        form.put("ts", ts);
        form.put("file_len", file_len);
        form.put("file_name", file_name);
        form.put("slice_num", slice_num);
        form.put("has_seperate", seperate);
        form.put("speaker_number", speakersNum);
        form.put("lfasr_type", 0);
        form.put("language", language);

        final String jsonStr = iflytekApi.prepare(form);
        final JSONObject jsonObject = JSONObject.parseObject(jsonStr);

        final String taskId = jsonObject.getString("data");
        logger.info("taskId: {}", taskId);
        return taskId;
    }

    @Override
    public Byte getProgress(String taskId) {
        final Date now = new Date();
        final long nowTimestamp = now.getTime();
        long ts = nowTimestamp / 1000;

        final String signa = IflytekUtil.sign(appId, appSecret, ts);

        final HashMap<String, Object> form = new HashMap<>();
        form.put("app_id", appId);
        form.put("signa", signa);
        form.put("ts", ts);
        form.put("task_id", taskId);

        final String jsonStr = iflytekApi.getProgress(form);
        final JSONObject jsonObject = JSON.parseObject(jsonStr);
        final String dataStr = jsonObject.getString("data");
        final JSONObject statusObj = JSON.parseObject(dataStr);
        final Byte status = statusObj.getByte("status");

        return status;
    }

    @Override
    public Boolean uploadSlice2(String taskId, String sliceId, byte[] content) throws IOException {
        final Date now = new Date();
        final long nowTimestamp = now.getTime();
        long ts = nowTimestamp / 1000;

        final String signa = IflytekUtil.sign(appId, appSecret, ts);

        HttpPost httpPost = new HttpPost("http://raasr.xfyun.cn/api/upload");
        MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();

        StringBody app_idvalue = new StringBody(appId, ContentType.create(MediaType.TEXT_PLAIN_VALUE, Consts.UTF_8));
        reqEntity.addPart("app_id", app_idvalue);
        StringBody signavalue = new StringBody(signa, ContentType.create(MediaType.TEXT_PLAIN_VALUE, Consts.UTF_8));
        reqEntity.addPart("signa", signavalue);
        StringBody tsvalue = new StringBody(Long.toString(ts), ContentType.create(MediaType.TEXT_PLAIN_VALUE, Consts.UTF_8));
        reqEntity.addPart("ts", tsvalue);
        StringBody task_idvalue = new StringBody(taskId, ContentType.create(MediaType.TEXT_PLAIN_VALUE, Consts.UTF_8));
        reqEntity.addPart("task_id", task_idvalue);
        StringBody slice_idvalue = new StringBody(sliceId, ContentType.create(MediaType.TEXT_PLAIN_VALUE, Consts.UTF_8));
        reqEntity.addPart("slice_id", slice_idvalue);

        reqEntity.addPart("content", new ByteArrayBody(content, ContentType.DEFAULT_BINARY, sliceId));

        HttpEntity httpEntiy = reqEntity.build();

        httpPost.setEntity(httpEntiy);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        String jsonStr = EntityUtils.toString(httpResponse.getEntity());

        final JSONObject jsonObject = JSON.parseObject(jsonStr);
        final Byte ok = jsonObject.getByte("ok");

        return ok == 0;
    }

    @Override
    public void uploadFile(String taskId, File file) throws IOException {

        final SliceIdGenerator sliceIdGenerator = new SliceIdGenerator();
        byte[] slice = new byte[SLICE_SIZE];
        int len = 0;

        try (FileInputStream fis = new FileInputStream(file)) {
            while ((len = fis.read(slice)) > 0) {
                if (fis.available() == 0) {
                    slice = Arrays.copyOfRange(slice, 0, len);
                }
                String sliceId = sliceIdGenerator.getNextSliceId();
                final Boolean ok = uploadSlice2(taskId, sliceId, slice);
                if (ok == null || !ok) {
                    throw new IOException("upload file failed");
                }
            }
        }

    }

    @Override
    public Boolean merge(String taskId) {
        final Date now = new Date();
        final long nowTimestamp = now.getTime();
        long ts = nowTimestamp / 1000;

        final String signa = IflytekUtil.sign(appId, appSecret, ts);

        final HashMap<String, Object> form = new HashMap<>();
        form.put("app_id", appId);
        form.put("signa", signa);
        form.put("ts", ts);
        form.put("task_id", taskId);

        final String jsonStr = iflytekApi.merge(form);
        final JSONObject jsonObject = JSON.parseObject(jsonStr);
        final Byte ok = jsonObject.getByte("ok");

        return ok == 0;
    }

    @Override
    public JSONArray getResult(String taskId) {
        final Date now = new Date();
        final long nowTimestamp = now.getTime();
        long ts = nowTimestamp / 1000;

        final String signa = IflytekUtil.sign(appId, appSecret, ts);

        final HashMap<String, Object> form = new HashMap<>();
        form.put("app_id", appId);
        form.put("signa", signa);
        form.put("ts", ts);
        form.put("task_id", taskId);

        final String jsonStr = iflytekApi.getResult(form);
        final JSONObject jsonObject = JSON.parseObject(jsonStr);
        final String dataStr = jsonObject.getString("data");
        final JSONArray results = JSON.parseArray(dataStr);

        return results;
    }

}

package io.cjf.testiflytek.client;

import com.alibaba.fastjson.JSONArray;

import java.io.File;
import java.io.IOException;

public interface IflytekService {
    String prepare(String file_name, Long file_len, Long slice_num, boolean seperate, byte speakersNum, String language);

    Byte getProgress(String taskId);

    Boolean uploadSlice2(String taskId, String sliceId, byte[] content) throws IOException;

    void uploadFile(String taskId, File file) throws IOException;

    Boolean merge(String taskId);

    JSONArray getResult(String taskId);
}

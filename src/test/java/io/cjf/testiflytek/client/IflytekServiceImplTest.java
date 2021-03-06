package io.cjf.testiflytek.client;

import com.alibaba.fastjson.JSONArray;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IflytekServiceImplTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IflytekServiceImpl iflytekService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void prepare() {
        String pathname = "speech/myheartwillgoon.m4a";
        final File file = new File(pathname);
        final String filename = file.getName();
        final long filelength = file.length();
        long sliceNum = filelength/1024/1024/10 + 1;
        boolean seperate = false;
        byte speakersNum = 1;
        String language = "en";
        final String taskId = iflytekService.prepare(filename, filelength, sliceNum, seperate, speakersNum, language);
        assertTrue(taskId != null);
        assertTrue(!taskId.isEmpty());
    }

    @Test
    void getProgress(){
        String taskId = "61b91fc8332f47f0b5500769207b89d9";
        final Byte progress = iflytekService.getProgress(taskId);
        assertTrue(progress == 0);
    }

    @Test
    void uploadSlice2() throws IOException {
        String taskId = "61b91fc8332f47f0b5500769207b89d9";
        String sliceId = "aaaaaaaaaa";
        String pathname = "speech/eleme.m4a";
        final File file = new File(pathname);
        byte[] fileContent = Files.readAllBytes(file.toPath());
        final Boolean ok = iflytekService.uploadSlice2(taskId, sliceId, fileContent);
        assertTrue(ok);
    }

    @Test
    void uploadFile() throws IOException {
        String taskId = "61b91fc8332f47f0b5500769207b89d9";
        String pathname = "speech/myheartwillgoon.m4a";
        final File file = new File(pathname);
        iflytekService.uploadFile(taskId, file);
    }

    @Test
    void merge(){
        String taskId = "61b91fc8332f47f0b5500769207b89d9";
        final Boolean merge = iflytekService.merge(taskId);
        assertTrue(merge);
    }

    @Test
    void getResult(){
        String taskId = "61b91fc8332f47f0b5500769207b89d9";
        final JSONArray results = iflytekService.getResult(taskId);
        assertNotNull(results);
        assertTrue(results.size() != 0);
    }

}
package io.cjf.testiflytek.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/test")
public class TestController {
    @Value("${my.strs}")
    private List<String> strings;
    @Value("${my.strs}")
    private Set<String> stringSet;
    @Value("#{${my.map}}")
    private Map map;

    @GetMapping("/hello")
    public void hello() {
    }

    @PostMapping("/hello2")
    public void hello2(@RequestParam String pp) {

    }

    @PostMapping("/upload/{aa}")
    public void upload(@RequestPart List<MultipartFile> files,
                       @RequestParam List<String> pp,
                       @PathVariable String[] aa) {

    }

}

package com.project.growfit;

import com.project.growfit.global.response.ResultCode;
import com.project.growfit.global.response.ResultResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public ResultResponse<?> home() {
        List<Map<Integer, String>> dataList = Arrays.asList(
                createMap(1, "First"),
                createMap(2, "Second"),
                createMap(3, "Third")
        );
        System.out.println("test");
        return new ResultResponse<>(ResultCode.RESPONSE_TEST, dataList);
    }

    private Map<Integer, String> createMap(int key, String value) {
        Map<Integer, String> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}

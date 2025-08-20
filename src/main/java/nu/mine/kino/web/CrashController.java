package nu.mine.kino.web;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/crash")
public class CrashController {

    @GetMapping("/oom")
    public String oom() {
        List<byte[]> list = new ArrayList<>();
        while (true) {
            list.add(new byte[1024 * 1024]); // 1MBずつ確保
        }
    }

    @GetMapping("/exit")
    public void exit() {
        System.exit(1);
    }
}

package nu.mine.kino.web;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/load")
public class LoadController {

    private final List<byte[]> allocations = new ArrayList<>();

    // CPU負荷
    @GetMapping("/cpu")
    public String cpuLoad(@RequestParam(defaultValue = "10") int seconds) {
        long end = System.currentTimeMillis() + (seconds * 1000L);
        while (System.currentTimeMillis() < end) {
            Math.sqrt(Math.random()); // 無駄な計算
        }
        return seconds + "秒間CPU負荷をかけました";
    }

    // メモリ確保（保持）
    @GetMapping("/memory")
    public String memoryLoad(@RequestParam(defaultValue = "50") int mb) {
        allocations.add(new byte[mb * 1024 * 1024]);
        return mb + "MBのメモリを確保しました (累計=" + allocations.size() + "回)";
    }

    // スレッド大量生成
    @GetMapping("/threads")
    public String threadLoad(@RequestParam(defaultValue = "100") int count) {
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(60_000); // 1分間スリープ
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
        return count + "個のスレッドを作成しました";

    }
}

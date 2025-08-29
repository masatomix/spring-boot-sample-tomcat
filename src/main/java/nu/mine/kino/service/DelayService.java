package nu.mine.kino.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class DelayService {

    @Async
    public CompletableFuture<String> delaySeconds(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000L);
        return CompletableFuture.completedFuture(seconds + "秒待ちました");
    }
}

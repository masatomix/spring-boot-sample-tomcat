package nu.mine.kino.service;

import org.springframework.stereotype.Service;


@Service
public class DelayService {

    // @Async
    public String delaySeconds(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000L);
        return seconds + "秒待ちました";
    }
}

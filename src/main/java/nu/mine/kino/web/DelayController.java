package nu.mine.kino.web;

import nu.mine.kino.service.DelayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class DelayController {

    private final DelayService delayService;

    public DelayController(DelayService delayService) {
        this.delayService = delayService;
    }

    @GetMapping("/delay")
    public String delay(@RequestParam(defaultValue = "30") int seconds) throws InterruptedException {
        return delayService.delaySeconds(seconds);
    }
}

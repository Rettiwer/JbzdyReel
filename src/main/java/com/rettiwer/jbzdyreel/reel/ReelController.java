package com.rettiwer.jbzdyreel.reel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReelController {
    @Autowired
    private ReelService reelService;

    @GetMapping("/reels")
    public List<Reel> getReels(@RequestParam Integer pageNo) {
        return reelService.getReelsPage(pageNo);
    }
}

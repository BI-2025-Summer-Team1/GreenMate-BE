package kr.bi.greenmate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
public class TempController {
    @Autowired
    private TempService tempService;

    @GetMapping("/temp")
    public String temp() {
        return tempService.temp();
    }
}

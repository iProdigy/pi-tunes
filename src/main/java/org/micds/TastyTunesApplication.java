package org.micds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@SpringBootApplication
public class TastyTunesApplication {

    @RequestMapping(value = "/request", method = RequestMethod.GET)
    public String requestForm(Model model) {
        model.addAttribute("req", new SongRequest());
        return "request";
    }

    @RequestMapping(value = "/request", method = RequestMethod.POST)
    public String requestSubmit(@ModelAttribute SongRequest req, Model model) {
        model.addAttribute("req", req);
        return "result";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "index";
    }

    @ModelAttribute("songRequest")
    public SongRequest createModel() {
        // Expose SongRequest as a model attribute so it can be bound to a bean
        return new SongRequest();
    }

    public static void main(String[] args) {
        SpringApplication.run(TastyTunesApplication.class, args);
    }

}

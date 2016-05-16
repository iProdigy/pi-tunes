package org.micds.web;

import org.micds.player.SongClient;
import org.micds.req.RequestQueue;
import org.micds.req.SongRequest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@SpringBootApplication
public class WebController {

    @RequestMapping(value = "/queue", method = RequestMethod.GET)
    public String queue() {
        return "queue";
    }

    @RequestMapping(value = "/request", method = RequestMethod.GET)
    public String requestForm(Model model) {
        model.addAttribute("req", new SongRequest());
        return "request";
    }

    @RequestMapping(value = "/request", method = RequestMethod.POST)
    public String requestSubmit(@ModelAttribute @Valid SongRequest req, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "request";
        }

        model.addAttribute("req", req);
        RequestQueue.getQueue().add(req);
        new Thread(() -> SongClient.getInstance().update()).start(); // avoid blocking this thread
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

}

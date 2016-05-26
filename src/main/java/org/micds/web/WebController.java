package org.micds.web;

import org.micds.PiTunes;
import org.micds.req.MediaValidator;
import org.micds.req.SongRequest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
    public String requestSubmit(@ModelAttribute(value = "songRequest") @Valid SongRequest req, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "request";
        }

        model.addAttribute("req", req);
        PiTunes.getRequestQueue().add(req);
        PiTunes.getSongClient().updateFromNewThread();
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

    @Bean
    public Validator localValidatorFactoryBean() {
        return new MediaValidator();
    }

    @InitBinder("songRequest")
    public void initSongReqBinder(WebDataBinder binder) {
        binder.addValidators(new MediaValidator());
    }

}

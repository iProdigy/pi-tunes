package org.micds;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.micds.web.WebController;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebController.class)
public class PiTunesAppTester {

    @Test
    public void contextLoads() {
        // TODO: More comprehensive tests
    }

}

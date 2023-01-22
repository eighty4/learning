package eighty4.learning.spring.native_app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringNativeController {

    public record DataModel(String title, int count) {}

    @GetMapping("/data")
    public DataModel data() {
        return new DataModel("Precipitation Levels", 88);
    }
}

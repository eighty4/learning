package eighty4.akimbo.tests.injection;

import eighty4.akimbo.annotation.http.HttpService;
import eighty4.akimbo.annotation.http.RequestMapping;

import javax.inject.Inject;

@HttpService
public class InjectScenarios {

    private final DataApi dataApi;

    @Inject
    public InjectScenarios(DataApi dataApi) {
        this.dataApi = dataApi;
    }

    @RequestMapping("/inject/test")
    public void asdf() {

    }
}

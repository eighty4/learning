package eighty4.akimbo.samples.http;

import eighty4.akimbo.annotation.http.HttpService;
import eighty4.akimbo.annotation.http.RequestMapping;

import javax.inject.Inject;

@HttpService
public class SampleHttpService {

    @Inject
    public SampleHttpService() {
    }

    @RequestMapping
    public void doNothing() {
    }

}

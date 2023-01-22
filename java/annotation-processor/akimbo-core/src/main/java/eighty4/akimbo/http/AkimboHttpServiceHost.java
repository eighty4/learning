package eighty4.akimbo.http;

import eighty4.akimbo.AkimboServiceHost;

public class AkimboHttpServiceHost implements AkimboServiceHost {

    private final HttpServiceProvider httpServiceProvider;

    public AkimboHttpServiceHost(HttpServiceProvider httpServiceProvider) {
        this.httpServiceProvider = httpServiceProvider;
    }

    @Override
    public void startServices() {
        httpServiceProvider.get().ifPresent(HttpServiceWrapper::start);
    }
}

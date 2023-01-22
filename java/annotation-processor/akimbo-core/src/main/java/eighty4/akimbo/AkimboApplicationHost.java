package eighty4.akimbo;

import java.util.List;

public class AkimboApplicationHost {
    private final List<AkimboServiceHost> akimboServiceHosts;

    public AkimboApplicationHost(List<AkimboServiceHost> akimboServiceHosts) {
        this.akimboServiceHosts = akimboServiceHosts;
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void start() {
        akimboServiceHosts.stream()
                .map(serviceHost -> new Thread(serviceHost::startServices))
                .forEach(Thread::start);
    }

    private void stop() {
        akimboServiceHosts.forEach(AkimboServiceHost::stopServices);
    }
}

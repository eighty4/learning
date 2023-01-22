package eighty4.akimbo.tests;

import com.google.common.flogger.FluentLogger;
import eighty4.akimbo.annotation.AkimboApp;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@AkimboApp
public class TestApplication {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    @PostConstruct
    public void onStartup() {
        logger.atInfo().log("onStartup");
    }

    @PreDestroy
    public void onShutdown() {
        logger.atInfo().log("onShutdown");
    }

}

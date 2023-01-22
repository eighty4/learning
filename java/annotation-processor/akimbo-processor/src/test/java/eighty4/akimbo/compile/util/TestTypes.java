package eighty4.akimbo.compile.util;

import dagger.Module;
import dagger.Provides;
import eighty4.akimbo.annotation.AkimboApp;
import eighty4.akimbo.annotation.http.HttpService;

import javax.inject.Inject;

public abstract class TestTypes implements TestModels {

    @AkimboApp
    public static class TestApp {

    }

    @AkimboApp(includes = ModuleWithHttpServiceWithoutDependencies.class)
    public static class TestAppIncludingModule {

    }

    @Module
    public interface ModuleWithHttpServiceWithoutDependencies {

        @Provides
        static HttpServiceWithoutDependencies providesHttpServiceWithoutDependencies() {
            return new HttpServiceWithoutDependencies();
        }
    }

    @HttpService
    public static class HttpServiceWithoutDependencies {

        @Inject // todo handle no-arg constructor
        public HttpServiceWithoutDependencies() {
        }
    }

    @HttpService
    public static class HttpServiceWithDependency {

        private final DataApi dataApi;

        public final DataApi publicProperty;

        private final DataApi privateProperty;

        @Inject
        public HttpServiceWithDependency(DataApi dataApi) {
            this.dataApi = dataApi;
            publicProperty = dataApi;
            privateProperty = dataApi;
        }

        public DataApi getDataApi() {
            return dataApi;
        }

    }

    @HttpService
    public static class DataApi {

    }

    public static class NonServiceTypeWithDataApiField {

        private final DataApi dataApi = new DataApi();

        public DataApi getDataApi() {
            return dataApi;
        }

    }

}

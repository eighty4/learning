package eighty4.akimbo.compile.http;

import com.squareup.javapoet.ClassName;
import eighty4.akimbo.annotation.http.HttpService;
import eighty4.akimbo.compile.component.AkimboComponentDefinition;
import eighty4.akimbo.compile.component.ServiceAkimboComponent;
import eighty4.akimbo.compile.service.ServiceRegistrar;
import eighty4.akimbo.compile.source.SourceFile;
import eighty4.akimbo.compile.source.provides.ProvidesMethod;
import eighty4.akimbo.http.AkimboHttpServiceHost;

import java.util.Collections;
import java.util.List;

public class HttpServiceRegistrar extends ServiceRegistrar<HttpServiceRegistration> {

    public HttpServiceRegistrar(String akimboSourcesPackageName) {
        super(akimboSourcesPackageName);
    }

    @Override
    public ClassName getServiceHostType() {
        return ClassName.get(AkimboHttpServiceHost.class);
    }

    @Override
    public String getServiceHostSubPackage() {
        return "http";
    }

    @Override
    protected boolean isComponentToRegister(ServiceAkimboComponent component) {
        return component.getTypeElement().getAnnotation(HttpService.class) != null;
    }

    @Override
    protected HttpServiceRegistration createServiceRegistration(ServiceAkimboComponent component) {
        return new HttpServiceRegistration(component);
    }

    @Override
    public List<ProvidesMethod> getComponentModuleProvidesMethods(AkimboComponentDefinition component) {
        return getServiceRegistration(component)
                .map(HttpServiceProvidesMethod::new)
                .<List<ProvidesMethod>>map(List::of)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<ProvidesMethod> getHostModuleProvidesMethods() {
        return List.of(
                new ProvidesHttpHandlersMethod(getServiceRegistrations()),
                new ProvidesHttpServerMethod(),
                new ProvidesHttpHostMethod()
        );
    }

    @Override
    public List<SourceFile> buildComponentSourceFiles(AkimboComponentDefinition component) {
        return getServiceRegistration(component)
                .map(HttpServiceFile::new)
                .<List<SourceFile>>map(List::of)
                .orElse(Collections.emptyList());
    }
}

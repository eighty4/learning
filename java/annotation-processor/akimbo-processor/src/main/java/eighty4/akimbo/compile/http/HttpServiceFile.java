package eighty4.akimbo.compile.http;

import com.squareup.javapoet.TypeSpec;
import eighty4.akimbo.compile.source.SourceFile;

import javax.lang.model.element.Modifier;

public class HttpServiceFile extends SourceFile {

    private final HttpServiceRegistration httpServiceRegistration;

    public HttpServiceFile(HttpServiceRegistration httpServiceRegistration) {
        super(httpServiceRegistration.getComponent().getPackageName(), httpServiceRegistration.getName());
        this.httpServiceRegistration = httpServiceRegistration;
    }

    @Override
    public TypeSpec toSpec() {
        TypeSpec.Builder httpHandlers = TypeSpec.classBuilder(getType())
                .addModifiers(Modifier.PUBLIC);

        httpServiceRegistration.getServiceMethodRegistrations().stream()
                .map(HttpHandlerForm::new)
                .map(HttpHandlerForm::toSpec)
                .forEach(httpHandlers::addType);
        return httpHandlers.build();
    }
}

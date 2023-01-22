package eighty4.akimbo.compile.component;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import eighty4.akimbo.compile.service.Parameter;
import eighty4.akimbo.compile.service.PropertyValueParameter;

import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.List;

public class ProvidedAkimboComponent extends ComponentReference implements AkimboComponentDefinition {

    private final ClassName moduleType;

    public ProvidedAkimboComponent(String name, TypeName type, String qualifiedName, ClassName moduleType) {
        super(name, type, qualifiedName);
        this.moduleType = moduleType;
    }

    @Override
    public String getPackageName() {
        return moduleType.packageName();
    }

    @Override
    public List<Parameter> getInjectionParameters() {
        return Collections.emptyList();
    }

    @Override
    public List<PropertyValueParameter> getPropertyParameters() {
        return Collections.emptyList();
    }

    @Override
    public TypeElement getTypeElement() {
        return null;
    }

    @Override
    public ClassName getModuleType() {
        return moduleType;
    }

}

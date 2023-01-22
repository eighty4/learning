package eighty4.akimbo.compile.resolution.expression;

import com.squareup.javapoet.ClassName;
import eighty4.akimbo.compile.component.AkimboComponentReference;
import eighty4.akimbo.compile.component.ProvidedAkimboComponent;
import eighty4.akimbo.compile.resolution.expression.visitor.provides.ComponentProvisionResolution;
import eighty4.akimbo.compile.service.PropertyValueParameter;

import java.util.List;
import java.util.Set;

public class ExpressionAkimboComponent extends ProvidedAkimboComponent {

    private final Set<AkimboComponentReference> resolvedComponentDependencies;

    public ExpressionAkimboComponent(ComponentProvisionResolution componentProvisionResolution,
                                     Set<AkimboComponentReference> resolvedComponentDependencies,
                                     ClassName moduleType) {
        super(componentProvisionResolution.getResolvedName(), componentProvisionResolution.getResolvedType(), null, moduleType);
        this.resolvedComponentDependencies = resolvedComponentDependencies;
    }

    @Override
    public Set<AkimboComponentReference> getComponentDependencies() {
        return resolvedComponentDependencies;
    }

    @Override
    public List<PropertyValueParameter> getPropertyParameters() {
        return super.getPropertyParameters();
    }

}

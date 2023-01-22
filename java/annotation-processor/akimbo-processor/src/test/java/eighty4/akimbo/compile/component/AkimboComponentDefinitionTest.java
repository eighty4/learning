package eighty4.akimbo.compile.component;

import com.squareup.javapoet.TypeName;
import eighty4.akimbo.compile.util.CompilationTest;
import eighty4.akimbo.compile.util.TestTypes.TestApp;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import javax.lang.model.element.TypeElement;

import static org.assertj.core.api.Assertions.assertThat;

public class AkimboComponentDefinitionTest extends CompilationTest {

    @Test
    public void equalsWithReference() {
        TypeElement typeElement = getTypeElement(TestApp.class);
        ComponentReference reference = new ComponentReference("arbitrary", TypeName.get(typeElement.asType()));
        AkimboComponentDefinition registration = new ServiceAkimboComponent(typeElement);
        Assertions.assertThat(reference.equals(registration)).isTrue();
        assertThat(registration.equals(reference)).isTrue();
    }
}

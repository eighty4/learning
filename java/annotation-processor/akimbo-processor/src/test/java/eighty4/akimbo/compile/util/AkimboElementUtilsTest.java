package eighty4.akimbo.compile.util;

import eighty4.akimbo.annotation.http.RequestMapping;
import org.junit.Test;

import javax.lang.model.element.TypeElement;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class AkimboElementUtilsTest extends CompilationTest {

    @RequestMapping(value = "foobar")
    static class TestClass {

    }

    @Test
    public void getAnnotationValue() {
        TypeElement typeElement = getTypeElement(TestClass.class);
        Optional<String> result = AkimboElementUtils.getAnnotationValue(typeElement, RequestMapping.class, String.class, "value");
        assertThat(result).isPresent().hasValue("foobar");
    }
}
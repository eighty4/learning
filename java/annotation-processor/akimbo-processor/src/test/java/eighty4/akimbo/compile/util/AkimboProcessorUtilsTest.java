package eighty4.akimbo.compile.util;

import com.squareup.javapoet.TypeName;
import org.junit.Test;

import static eighty4.akimbo.compile.util.AkimboProcessorUtils.getComponentNameFromProvidesMethodName;
import static eighty4.akimbo.compile.util.AkimboProcessorUtils.getNameFromTypeName;
import static eighty4.akimbo.compile.util.AkimboProcessorUtils.lowerCaseFirstChar;
import static eighty4.akimbo.compile.util.AkimboProcessorUtils.upperCaseFirstChar;
import static org.assertj.core.api.Assertions.assertThat;

public class AkimboProcessorUtilsTest {

    @Test
    public void lowerCaseFirstChar_doesItsJob() {
        assertThat(lowerCaseFirstChar("Foo")).isEqualTo("foo");
    }

    @Test
    public void upperCaseFirstChar_doesItsJob() {
        assertThat(upperCaseFirstChar("foo")).isEqualTo("Foo");
    }

    @Test
    public void getNameFromTypeName_returnsNameFromType() {
        String name = getNameFromTypeName(TypeName.get(String.class));
        assertThat(name).isEqualTo("String");
    }

    @Test
    public void getComponentNameFromProvidesMethodName_trimsProvides() {
        String result = getComponentNameFromProvidesMethodName("providesSomeComponent");
        assertThat(result).isEqualTo("someComponent");
    }

    @Test
    public void getComponentNameFromProvidesMethodName_trimsProvide() {
        String result = getComponentNameFromProvidesMethodName("provideSomeComponent");
        assertThat(result).isEqualTo("someComponent");
    }

    @Test
    public void getComponentNameFromProvidesMethodName_usesStringWithoutProvidePrefix() {
        String result = getComponentNameFromProvidesMethodName("someComponent");
        assertThat(result).isEqualTo("someComponent");
    }
}

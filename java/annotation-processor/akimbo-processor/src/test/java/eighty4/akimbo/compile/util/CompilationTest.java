package eighty4.akimbo.compile.util;

import com.google.testing.compile.CompilationRule;
import org.junit.Rule;

import javax.lang.model.element.TypeElement;

public abstract class CompilationTest {

    @Rule
    public CompilationRule rule = new CompilationRule();

    protected TypeElement getTypeElement(Class<?> type) {
        return rule.getElements().getTypeElement(type.getCanonicalName());
    }

}

package eighty4.akimbo.compile;

import com.google.common.truth.StringSubject;
import com.google.testing.compile.Compilation;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import eighty4.akimbo.annotation.http.HttpService;
import eighty4.akimbo.compile.util.ProcessorTest;
import org.junit.Test;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.nio.charset.Charset;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static eighty4.akimbo.compile.util.TestSpecs.APP;

public class AkimboProcessorTest extends ProcessorTest {

    @Test
    public void createsAppHostComponentAndMainFiles() {
        Compilation compilation = compileFiles("test.code", APP);

        assertThat(compilation).succeeded();

        StringSubject applicationHostComponent = assertThat(compilation)
                .generatedSourceFile("test.code.a_k_i_m_b_o.AkimboApplicationHost_Component")
                .contentsAsString(Charset.defaultCharset());
        applicationHostComponent.contains("@Component(\n    modules = AkimboApplicationHost_Module.class\n)");
        applicationHostComponent.contains("public interface AkimboApplicationHost_Component {");
        applicationHostComponent.contains("AkimboApplicationHost akimboApplicationHost();");

        assertThat(compilation).generatedSourceFile("test.code.a_k_i_m_b_o.AkimboApplicationMain")
                .contentsAsString(Charset.defaultCharset())
                .contains("public class AkimboApplicationMain {\n  public static void main(String[] args) {\n" +
                        "    DaggerAkimboApplicationHost_Component.create().akimboApplicationHost().start();\n  }\n}");
    }

    @Test
    public void createsAppHostWithPostConstruct() {
        Compilation compilation = compileFiles("test.code", APP, TypeSpec.classBuilder("PostConstructTest")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(MethodSpec.methodBuilder("onStartupBehavior")
                        .addAnnotation(PostConstruct.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(void.class)
                        .build())
                .build());

        assertThat(compilation).succeeded();

        StringSubject postConstructTestModule = assertThat(compilation)
                .generatedSourceFile("test.code.PostConstructTest_Module")
                .contentsAsString(Charset.defaultCharset());
        postConstructTestModule.contains("@Module");
        postConstructTestModule.contains("public interface PostConstructTest_Module {");
        postConstructTestModule.contains("@Singleton\n  @Provides\n  static PostConstructTest providePostConstructTest() {\n    return new PostConstructTest();\n  }");

        StringSubject lifecycleHookModule = assertThat(compilation)
                .generatedSourceFile("test.code.a_k_i_m_b_o.lifecycle.AkimboLifecycleServiceHost_Module")
                .contentsAsString(Charset.defaultCharset());

        lifecycleHookModule.contains("@Module(\n    includes = {PostConstructTest_Module.class}\n)");
        lifecycleHookModule.contains("public interface AkimboLifecycleServiceHost_Module {");
        lifecycleHookModule.contains("@Singleton\n  @Provides\n  static Map<LifecyclePhase, List<LifecycleHook>> providesLifecycleHooks(\n      "
                + "PostConstructTest postConstructTest) {\n    List<LifecycleHook> startupHooks = new ArrayList<>();\n    List<LifecycleHook> shutdownHooks = new ArrayList<>();");
        lifecycleHookModule.contains("startupHooks.add(new LifecycleHook(\"postConstructTest\", \"onStartupBehavior\", postConstructTest::onStartupBehavior));");
    }

    @Test
    public void unresolvedComponentError() {
        TypeSpec dependency = TypeSpec.interfaceBuilder("DataApi").addModifiers(Modifier.PUBLIC).build();
        TypeSpec service = TypeSpec.classBuilder("TestHttpService")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(HttpService.class)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Inject.class)
                        .addParameter(ClassName.bestGuess("test.code.DataApi"), "dataApi")
                        .build())
                .build();

        Compilation compilation = compileFiles("test.code", APP, dependency, service);

        assertThat(compilation).failed();
    }

    @Test
    public void resolveNonServiceDependency_whenDependencyAddedFirst() {
        TypeSpec dependency = TypeSpec.classBuilder("DataApi").addModifiers(Modifier.PUBLIC).build();
        TypeSpec service = TypeSpec.classBuilder("TestHttpService")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(HttpService.class)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Inject.class)
                        .addParameter(ClassName.bestGuess("test.code.DataApi"), "dataApi")
                        .build())
                .build();

        assertThat(compileFiles("test.code", APP, dependency, service)).succeeded();
    }

    @Test
    public void resolveNonServiceDependency_whenServiceAddedFirst() {
        TypeSpec dependency = TypeSpec.classBuilder("DataApi").addModifiers(Modifier.PUBLIC).build();
        TypeSpec service = TypeSpec.classBuilder("TestHttpService")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(HttpService.class)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Inject.class)
                        .addParameter(ClassName.bestGuess("test.code.DataApi"), "dataApi")
                        .build())
                .build();

        assertThat(compileFiles("test.code", APP, service, dependency)).succeeded();
    }

    @Test
    public void resolveNonServiceDependencyWithDependenciesOnServiceComponent() {
        TypeSpec serviceOne = TypeSpec.classBuilder("TestHttpServiceOne").addModifiers(Modifier.PUBLIC).build();
        TypeSpec dependency = TypeSpec.classBuilder("DataApi")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Inject.class)
                        .addParameter(ClassName.bestGuess("test.code.TestHttpServiceOne"), "service")
                        .build())
                .build();
        TypeSpec serviceTwo = TypeSpec.classBuilder("TestHttpServiceTwo")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(HttpService.class)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Inject.class)
                        .addParameter(ClassName.bestGuess("test.code.DataApi"), "dataApi")
                        .build())
                .build();

        assertThat(compileFiles("test.code", APP, serviceOne, serviceTwo, dependency)).succeeded();
    }

}

package eighty4.akimbo.gradle;

import org.gradle.api.GradleException;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AkimboRunTask extends JavaExec {

    private final JavaPluginConvention java;

    public AkimboRunTask() {
        setGroup(ApplicationPlugin.APPLICATION_GROUP);
        setDescription("Run Akimbo application");
        java = getProject().getConvention().getPlugin(JavaPluginConvention.class);
        classpath(java.getSourceSets().findByName(SourceSet.MAIN_SOURCE_SET_NAME).getRuntimeClasspath());
    }

    @Override
    public String getMain() {
        ClassLoader classLoader = getClasspathClassLoader();
        Set<String> mainCandidates = getClasspath().getFiles().stream()
                .map(File::toPath)
                .flatMap(compileClasspathPath -> {
                    try {
                        if (!Files.exists(compileClasspathPath) || !Files.isDirectory(compileClasspathPath)) {
                            return Stream.empty();
                        } else {
                            return Files.walk(compileClasspathPath)
                                    .map(compileClasspath -> {
                                        if (!Files.isDirectory(compileClasspath) && compileClasspath.toString().endsWith("/a_k_i_m_b_o/AkimboApplicationMain.class")) {
                                            String classFqn = getFqn(compileClasspathPath, compileClasspath);
                                            Class<?> clazz = getClass(classLoader, classFqn);
                                            if (hasMainMethod(clazz)) {
                                                return classFqn;
                                            }
                                        }
                                        return null;
                                    })
                                    .filter(Objects::nonNull);
                        }
                    } catch (IOException e) {
                        throw new GradleException("Failed walking compile classpath dir " + compileClasspathPath, e);
                    }
                })
                .collect(Collectors.toSet());

        if (mainCandidates.size() > 1) {
            throw new GradleException("There's more than one main method to run: " + String.join(", ", mainCandidates));
        }
        return mainCandidates.stream().findFirst().orElseThrow(() -> new GradleException("There is no main method to run"));
    }

    private boolean hasMainMethod(Class<?> clazz) {
        return Stream.of(clazz.getMethods())
                .filter(method -> method.getName().equals("main"))
                .filter(method -> Modifier.isStatic(method.getModifiers()))
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .anyMatch(method -> {
                    Parameter[] parameters = method.getParameters();
                    return parameters.length == 1 && parameters[0].getType().isArray()
                            && parameters[0].getType().getComponentType() == String.class;
                });
    }

    private String getFqn(Path compileClasspathPath, Path compileClasspath) {
        String classFqn = compileClasspath.toString().substring(compileClasspathPath.toString().length() + 1).replaceAll("/", ".");
        classFqn = classFqn.substring(0, classFqn.length() - 6);
        return classFqn;
    }

    private Class<?> getClass(ClassLoader classLoader, String className) {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new GradleException("", e);
        }
    }

    private ClassLoader getClasspathClassLoader() {
        URL[] urls = getClasspath().getFiles().stream()
                .map(file -> {
                    try {
                        return file.toURI().toURL();
                    } catch (MalformedURLException e) {
                        throw new GradleException("Failed getting URL for classpath file " + file, e);
                    }
                })
                .toArray(URL[]::new);
        return new URLClassLoader(urls, null);
    }
}

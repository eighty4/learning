package eighty4.akimbo.compile.util;

import dagger.Module;
import dagger.Provides;
import eighty4.akimbo.annotation.AkimboApp;

import javax.annotation.processing.Generated;
import javax.inject.Inject;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AkimboElementUtils {

    public static boolean isDaggerModule(TypeElement e) {
        return getAnnotation(e, Module.class).isPresent();
    }

    public static boolean isGeneratedDaggerSource(TypeElement e) {
        return getAnnotationValue(e, Generated.class, String.class, "value")
                .map(v -> v.equals("dagger.internal.codegen.ComponentProcessor"))
                .orElse(false);
    }

    public static TypeElement getAkimboAppElement(Set<? extends Element> elements) {
        return elements.stream()
                .filter(e -> hasAnnotation(e, AkimboApp.class))
                .findFirst()
                .map(e -> (TypeElement) e)
                .orElseThrow();
    }

    public static boolean isConstructableComponent(TypeElement e) {
        // todo validate has one @Inject constructor
        return !e.getModifiers().contains(Modifier.ABSTRACT) && !e.getKind().isInterface();
    }

    public static Optional<ExecutableElement> findInjectConstructor(Element element) {
        return new AkimboElementScanner(e -> e.getKind() == ElementKind.CONSTRUCTOR && hasAnnotation(e, Inject.class))
                .startScan(element)
                .stream()
                .map(e -> (ExecutableElement) e)
                .findFirst();
    }

    public static Optional<ExecutableElement> findMethodByName(Element element, String methodName) {
        return new AkimboElementScanner(e -> e.getKind() == ElementKind.METHOD && e.getSimpleName().toString().equals(methodName))
                .startScan(element)
                .stream()
                .map(e -> (ExecutableElement) e)
                .findFirst();
    }

    public static List<ExecutableElement> findMethods(Element element) {
        return new AkimboElementScanner(e -> e.getKind() == ElementKind.METHOD)
                .startScan(element)
                .stream()
                .map(e -> (ExecutableElement) e)
                .collect(Collectors.toList());
    }

    public static List<ExecutableElement> findProvidesMethods(TypeElement typeElement) {
        return new AkimboElementScanner(e -> e.getKind() == ElementKind.METHOD && hasAnnotation(e, Provides.class))
                .startScan(typeElement)
                .stream()
                .map(e -> (ExecutableElement) e)
                .collect(Collectors.toList());
    }

    public static boolean hasMethodsWithAnnotations(Element element, Set<Class<? extends Annotation>> annotationClasses) {
        return findMethods(element).stream().anyMatch(ee -> annotationClasses.stream().anyMatch(ac -> hasAnnotation(ee, ac)));
    }

    private static Optional<AnnotationMirror> getAnnotation(Element element, Class<? extends Annotation> annotationType) {
        String annotationName = annotationType.getCanonicalName();
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            if (isSameAnnotation(annotation, annotationName)) {
                return Optional.of(annotation);
            }
        }
        return Optional.empty();
    }

    public static boolean hasAnnotation(Element element, Class<? extends Annotation> annotationType) {
        return getAnnotation(element, annotationType).isPresent();
    }

    private static boolean isSameAnnotation(AnnotationMirror annotationMirror, String annotationName) {
        return Objects.equals(annotationName, annotationMirror.getAnnotationType().toString());
    }

    public static <T> Optional<T> getAnnotationValue(Element element, Class<? extends Annotation> annotationClass, Class<T> valueType, String... annotationFields) {
        return getAnnotation(element, annotationClass).flatMap(annotation -> Stream.of(annotationFields)
                .map(annotationField -> getAnnotationValue(annotation, valueType, annotationField))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst());
    }

    @SuppressWarnings({"unchecked"})
    private static <T> Optional<T> getAnnotationValue(AnnotationMirror annotation, Class<T> valueType, String annotationField) {
        return annotation.getElementValues().entrySet().stream()
                .filter(e -> annotationField.equals(e.getKey().getSimpleName().toString()))
                .map(Map.Entry::getValue)
                .map(AnnotationValue::getValue)
                .map(v -> valueType == String.class ? (T) v.toString() : (T) v)
                .findFirst();
    }

    public static AnnotationValue getAnnotationValue(TypeElement typeElement, Class<?> annotationClass, String annotationField) {
        String clazzName = annotationClass.getName();
        for (AnnotationMirror annotationMirror : typeElement.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().toString().equals(clazzName)) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
                    if (entry.getKey().getSimpleName().toString().equals(annotationField)) {
                        return entry.getValue();
                    }
                }
            }
        }
        return null;
    }
}

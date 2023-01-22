package eighty4.akimbo.compile.util;

import javax.lang.model.element.Element;
import javax.lang.model.util.ElementScanner9;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AkimboElementScanner extends ElementScanner9<List<Element>, List<Element>> {

    private Predicate<Element> isMatchingElement;

    AkimboElementScanner(Predicate<Element> isMatchingElement) {
        super(new ArrayList<>());
        this.isMatchingElement = isMatchingElement;
    }

    List<Element> startScan(Element e) {
        return scan(e, DEFAULT_VALUE);
    }

    @Override
    public List<Element> scan(Element e, List<Element> matchingElements) {
        if (isMatchingElement.test(e)) {
            matchingElements.add(e);
        }
        return super.scan(e, matchingElements);
    }
}

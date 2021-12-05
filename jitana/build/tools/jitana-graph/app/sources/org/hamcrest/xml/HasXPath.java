package org.hamcrest.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Condition;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.core.IsAnything;
import org.w3c.dom.Node;

public class HasXPath extends TypeSafeDiagnosingMatcher<Node> {
    private static final Condition.Step<Object, String> NODE_EXISTS = nodeExists();
    public static final NamespaceContext NO_NAMESPACE_CONTEXT = null;
    private static final IsAnything<String> WITH_ANY_CONTENT = new IsAnything<>("");
    private final XPathExpression compiledXPath;
    private final QName evaluationMode;
    private final Matcher<String> valueMatcher;
    private final String xpathString;

    public HasXPath(String xPathExpression, Matcher<String> valueMatcher2) {
        this(xPathExpression, NO_NAMESPACE_CONTEXT, valueMatcher2);
    }

    public HasXPath(String xPathExpression, NamespaceContext namespaceContext, Matcher<String> valueMatcher2) {
        this(xPathExpression, namespaceContext, valueMatcher2, XPathConstants.STRING);
    }

    private HasXPath(String xPathExpression, NamespaceContext namespaceContext, Matcher<String> valueMatcher2, QName mode) {
        this.compiledXPath = compiledXPath(xPathExpression, namespaceContext);
        this.xpathString = xPathExpression;
        this.valueMatcher = valueMatcher2;
        this.evaluationMode = mode;
    }

    public boolean matchesSafely(Node item, Description mismatch) {
        return evaluated(item, mismatch).and(NODE_EXISTS).matching(this.valueMatcher);
    }

    public void describeTo(Description description) {
        description.appendText("an XML document with XPath ").appendText(this.xpathString);
        if (this.valueMatcher != null) {
            description.appendText(StringUtils.SPACE).appendDescriptionOf(this.valueMatcher);
        }
    }

    private Condition<Object> evaluated(Node item, Description mismatch) {
        try {
            return Condition.matched(this.compiledXPath.evaluate(item, this.evaluationMode), mismatch);
        } catch (XPathExpressionException e) {
            mismatch.appendText(e.getMessage());
            return Condition.notMatched();
        }
    }

    private static Condition.Step<Object, String> nodeExists() {
        return new Condition.Step<Object, String>() {
            public Condition<String> apply(Object value, Description mismatch) {
                if (value != null) {
                    return Condition.matched(String.valueOf(value), mismatch);
                }
                mismatch.appendText("xpath returned no results.");
                return Condition.notMatched();
            }
        };
    }

    private static XPathExpression compiledXPath(String xPathExpression, NamespaceContext namespaceContext) {
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            if (namespaceContext != null) {
                xPath.setNamespaceContext(namespaceContext);
            }
            return xPath.compile(xPathExpression);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException("Invalid XPath : " + xPathExpression, e);
        }
    }

    @Factory
    public static Matcher<Node> hasXPath(String xPath, Matcher<String> valueMatcher2) {
        return hasXPath(xPath, NO_NAMESPACE_CONTEXT, valueMatcher2);
    }

    @Factory
    public static Matcher<Node> hasXPath(String xPath, NamespaceContext namespaceContext, Matcher<String> valueMatcher2) {
        return new HasXPath(xPath, namespaceContext, valueMatcher2, XPathConstants.STRING);
    }

    @Factory
    public static Matcher<Node> hasXPath(String xPath) {
        return hasXPath(xPath, NO_NAMESPACE_CONTEXT);
    }

    @Factory
    public static Matcher<Node> hasXPath(String xPath, NamespaceContext namespaceContext) {
        return new HasXPath(xPath, namespaceContext, WITH_ANY_CONTENT, XPathConstants.NODE);
    }
}

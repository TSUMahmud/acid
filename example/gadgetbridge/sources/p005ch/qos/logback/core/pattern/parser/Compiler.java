package p005ch.qos.logback.core.pattern.parser;

import java.util.Map;
import p005ch.qos.logback.core.pattern.CompositeConverter;
import p005ch.qos.logback.core.pattern.Converter;
import p005ch.qos.logback.core.pattern.DynamicConverter;
import p005ch.qos.logback.core.pattern.LiteralConverter;
import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.status.ErrorStatus;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.core.pattern.parser.Compiler */
class Compiler<E> extends ContextAwareBase {
    final Map converterMap;
    Converter<E> head;
    Converter<E> tail;
    final Node top;

    Compiler(Node node, Map map) {
        this.top = node;
        this.converterMap = map;
    }

    private void addToList(Converter<E> converter) {
        if (this.head == null) {
            this.tail = converter;
            this.head = converter;
            return;
        }
        this.tail.setNext(converter);
        this.tail = converter;
    }

    /* access modifiers changed from: package-private */
    public Converter<E> compile() {
        LiteralConverter literalConverter;
        this.tail = null;
        this.head = null;
        for (Node node = this.top; node != null; node = node.next) {
            int i = node.type;
            if (i != 0) {
                if (i == 1) {
                    SimpleKeywordNode simpleKeywordNode = (SimpleKeywordNode) node;
                    DynamicConverter createConverter = createConverter(simpleKeywordNode);
                    if (createConverter != null) {
                        createConverter.setFormattingInfo(simpleKeywordNode.getFormatInfo());
                        createConverter.setOptionList(simpleKeywordNode.getOptions());
                        literalConverter = createConverter;
                    } else {
                        LiteralConverter literalConverter2 = new LiteralConverter("%PARSER_ERROR[" + simpleKeywordNode.getValue() + "]");
                        addStatus(new ErrorStatus("[" + simpleKeywordNode.getValue() + "] is not a valid conversion word", this));
                        literalConverter = literalConverter2;
                    }
                } else if (i == 2) {
                    CompositeNode compositeNode = (CompositeNode) node;
                    CompositeConverter createCompositeConverter = createCompositeConverter(compositeNode);
                    if (createCompositeConverter == null) {
                        addError("Failed to create converter for [%" + compositeNode.getValue() + "] keyword");
                        literalConverter = new LiteralConverter("%PARSER_ERROR[" + compositeNode.getValue() + "]");
                    } else {
                        createCompositeConverter.setFormattingInfo(compositeNode.getFormatInfo());
                        createCompositeConverter.setOptionList(compositeNode.getOptions());
                        Compiler compiler = new Compiler(compositeNode.getChildNode(), this.converterMap);
                        compiler.setContext(this.context);
                        createCompositeConverter.setChildConverter(compiler.compile());
                        literalConverter = createCompositeConverter;
                    }
                }
                addToList(literalConverter);
            } else {
                addToList(new LiteralConverter((String) node.getValue()));
            }
        }
        return this.head;
    }

    /* access modifiers changed from: package-private */
    public CompositeConverter<E> createCompositeConverter(CompositeNode compositeNode) {
        String str = (String) compositeNode.getValue();
        String str2 = (String) this.converterMap.get(str);
        if (str2 != null) {
            try {
                return (CompositeConverter) OptionHelper.instantiateByClassName(str2, (Class<?>) CompositeConverter.class, this.context);
            } catch (Exception e) {
                addError("Failed to instantiate converter class [" + str2 + "] as a composite converter for keyword [" + str + "]", e);
                return null;
            }
        } else {
            addError("There is no conversion class registered for composite conversion word [" + str + "]");
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public DynamicConverter<E> createConverter(SimpleKeywordNode simpleKeywordNode) {
        String str = (String) simpleKeywordNode.getValue();
        String str2 = (String) this.converterMap.get(str);
        if (str2 != null) {
            try {
                return (DynamicConverter) OptionHelper.instantiateByClassName(str2, (Class<?>) DynamicConverter.class, this.context);
            } catch (Exception e) {
                addError("Failed to instantiate converter class [" + str2 + "] for keyword [" + str + "]", e);
                return null;
            }
        } else {
            addError("There is no conversion class registered for conversion word [" + str + "]");
            return null;
        }
    }
}

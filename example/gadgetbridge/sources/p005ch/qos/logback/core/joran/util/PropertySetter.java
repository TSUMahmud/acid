package p005ch.qos.logback.core.joran.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import p005ch.qos.logback.core.joran.spi.DefaultClass;
import p005ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import p005ch.qos.logback.core.spi.ContextAwareBase;
import p005ch.qos.logback.core.util.AggregationType;
import p005ch.qos.logback.core.util.PropertySetterException;

/* renamed from: ch.qos.logback.core.joran.util.PropertySetter */
public class PropertySetter extends ContextAwareBase {
    protected MethodDescriptor[] methodDescriptors;
    protected Object obj;
    protected Class<?> objClass;
    protected PropertyDescriptor[] propertyDescriptors;

    /* renamed from: ch.qos.logback.core.joran.util.PropertySetter$1 */
    static /* synthetic */ class C05161 {
        static final /* synthetic */ int[] $SwitchMap$ch$qos$logback$core$util$AggregationType = new int[AggregationType.values().length];

        static {
            try {
                $SwitchMap$ch$qos$logback$core$util$AggregationType[AggregationType.NOT_FOUND.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$util$AggregationType[AggregationType.AS_BASIC_PROPERTY.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$util$AggregationType[AggregationType.AS_COMPLEX_PROPERTY.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public PropertySetter(Object obj2) {
        this.obj = obj2;
        this.objClass = obj2.getClass();
    }

    private String capitalizeFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private AggregationType computeRawAggregationType(Method method) {
        Class<?> parameterClassForMethod = getParameterClassForMethod(method);
        return parameterClassForMethod == null ? AggregationType.NOT_FOUND : StringToObjectConverter.canBeBuiltFromSimpleString(parameterClassForMethod) ? AggregationType.AS_BASIC_PROPERTY : AggregationType.AS_COMPLEX_PROPERTY;
    }

    private Method findAdderMethod(String str) {
        String capitalizeFirstLetter = capitalizeFirstLetter(str);
        return getMethod("add" + capitalizeFirstLetter);
    }

    private Method findSetterMethod(String str) {
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(Introspector.decapitalize(str));
        if (propertyDescriptor != null) {
            return propertyDescriptor.getWriteMethod();
        }
        return null;
    }

    private Class<?> getParameterClassForMethod(Method method) {
        if (method == null) {
            return null;
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            return null;
        }
        return parameterTypes[0];
    }

    private boolean isSanityCheckSuccessful(String str, Method method, Class<?>[] clsArr, Object obj2) {
        String str2;
        Class<?> cls = obj2.getClass();
        if (clsArr.length != 1) {
            str2 = "Wrong number of parameters in setter method for property [" + str + "] in " + this.obj.getClass().getName();
        } else if (clsArr[0].isAssignableFrom(obj2.getClass())) {
            return true;
        } else {
            addError("A \"" + cls.getName() + "\" object is not assignable to a \"" + clsArr[0].getName() + "\" variable.");
            addError("The class \"" + clsArr[0].getName() + "\" was loaded by ");
            addError("[" + clsArr[0].getClassLoader() + "] whereas object of type ");
            str2 = "\"" + cls.getName() + "\" was loaded by [" + cls.getClassLoader() + "].";
        }
        addError(str2);
        return false;
    }

    private boolean isUnequivocallyInstantiable(Class<?> cls) {
        if (cls.isInterface()) {
            return false;
        }
        try {
            return cls.newInstance() != null;
        } catch (InstantiationException e) {
            return false;
        } catch (IllegalAccessException e2) {
            return false;
        }
    }

    public void addBasicProperty(String str, String str2) {
        if (str2 != null) {
            String capitalizeFirstLetter = capitalizeFirstLetter(str);
            Method findAdderMethod = findAdderMethod(capitalizeFirstLetter);
            if (findAdderMethod == null) {
                addError("No adder for property [" + capitalizeFirstLetter + "].");
                return;
            }
            Class[] parameterTypes = findAdderMethod.getParameterTypes();
            isSanityCheckSuccessful(capitalizeFirstLetter, findAdderMethod, parameterTypes, str2);
            try {
                if (StringToObjectConverter.convertArg(this, str2, parameterTypes[0]) != null) {
                    invokeMethodWithSingleParameterOnThisObject(findAdderMethod, str2);
                }
            } catch (Throwable th) {
                addError("Conversion to type [" + parameterTypes[0] + "] failed. ", th);
            }
        }
    }

    public void addComplexProperty(String str, Object obj2) {
        Method findAdderMethod = findAdderMethod(str);
        if (findAdderMethod == null) {
            addError("Could not find method [add" + str + "] in class [" + this.objClass.getName() + "].");
        } else if (isSanityCheckSuccessful(str, findAdderMethod, findAdderMethod.getParameterTypes(), obj2)) {
            invokeMethodWithSingleParameterOnThisObject(findAdderMethod, obj2);
        }
    }

    public AggregationType computeAggregationType(String str) {
        Method findAdderMethod = findAdderMethod(str);
        if (findAdderMethod != null) {
            int i = C05161.$SwitchMap$ch$qos$logback$core$util$AggregationType[computeRawAggregationType(findAdderMethod).ordinal()];
            if (i == 1) {
                return AggregationType.NOT_FOUND;
            }
            if (i == 2) {
                return AggregationType.AS_BASIC_PROPERTY_COLLECTION;
            }
            if (i == 3) {
                return AggregationType.AS_COMPLEX_PROPERTY_COLLECTION;
            }
        }
        Method findSetterMethod = findSetterMethod(str);
        return findSetterMethod != null ? computeRawAggregationType(findSetterMethod) : AggregationType.NOT_FOUND;
    }

    /* access modifiers changed from: package-private */
    public <T extends Annotation> T getAnnotation(String str, Class<T> cls, Method method) {
        if (method != null) {
            return method.getAnnotation(cls);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public Class<?> getByConcreteType(String str, Method method) {
        Class<?> parameterClassForMethod = getParameterClassForMethod(method);
        if (parameterClassForMethod != null && isUnequivocallyInstantiable(parameterClassForMethod)) {
            return parameterClassForMethod;
        }
        return null;
    }

    public Class<?> getClassNameViaImplicitRules(String str, AggregationType aggregationType, DefaultNestedComponentRegistry defaultNestedComponentRegistry) {
        Class<?> findDefaultComponentType = defaultNestedComponentRegistry.findDefaultComponentType(this.obj.getClass(), str);
        if (findDefaultComponentType != null) {
            return findDefaultComponentType;
        }
        Method relevantMethod = getRelevantMethod(str, aggregationType);
        if (relevantMethod == null) {
            return null;
        }
        Class<?> defaultClassNameByAnnonation = getDefaultClassNameByAnnonation(str, relevantMethod);
        return defaultClassNameByAnnonation != null ? defaultClassNameByAnnonation : getByConcreteType(str, relevantMethod);
    }

    /* access modifiers changed from: package-private */
    public Class<?> getDefaultClassNameByAnnonation(String str, Method method) {
        DefaultClass defaultClass = (DefaultClass) getAnnotation(str, DefaultClass.class, method);
        if (defaultClass != null) {
            return defaultClass.value();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public Method getMethod(String str) {
        if (this.methodDescriptors == null) {
            introspect();
        }
        int i = 0;
        while (true) {
            MethodDescriptor[] methodDescriptorArr = this.methodDescriptors;
            if (i >= methodDescriptorArr.length) {
                return null;
            }
            if (str.equals(methodDescriptorArr[i].getName())) {
                return this.methodDescriptors[i].getMethod();
            }
            i++;
        }
    }

    public Object getObj() {
        return this.obj;
    }

    public Class<?> getObjClass() {
        return this.objClass;
    }

    /* access modifiers changed from: protected */
    public PropertyDescriptor getPropertyDescriptor(String str) {
        if (this.propertyDescriptors == null) {
            introspect();
        }
        int i = 0;
        while (true) {
            PropertyDescriptor[] propertyDescriptorArr = this.propertyDescriptors;
            if (i >= propertyDescriptorArr.length) {
                return null;
            }
            if (str.equals(propertyDescriptorArr[i].getName())) {
                return this.propertyDescriptors[i];
            }
            i++;
        }
    }

    /* access modifiers changed from: package-private */
    public Method getRelevantMethod(String str, AggregationType aggregationType) {
        String capitalizeFirstLetter = capitalizeFirstLetter(str);
        if (aggregationType == AggregationType.AS_COMPLEX_PROPERTY_COLLECTION) {
            return findAdderMethod(capitalizeFirstLetter);
        }
        if (aggregationType == AggregationType.AS_COMPLEX_PROPERTY) {
            return findSetterMethod(capitalizeFirstLetter);
        }
        throw new IllegalStateException(aggregationType + " not allowed here");
    }

    /* access modifiers changed from: protected */
    public void introspect() {
        try {
            this.propertyDescriptors = Introspector.getPropertyDescriptors(this.objClass);
            this.methodDescriptors = Introspector.getMethodDescriptors(this.objClass);
        } catch (IntrospectionException e) {
            addError("Failed to introspect " + this.obj + ": " + e.getMessage());
            this.propertyDescriptors = new PropertyDescriptor[0];
            this.methodDescriptors = new MethodDescriptor[0];
        }
    }

    /* access modifiers changed from: package-private */
    public void invokeMethodWithSingleParameterOnThisObject(Method method, Object obj2) {
        Class<?> cls = obj2.getClass();
        try {
            method.invoke(this.obj, new Object[]{obj2});
        } catch (Exception e) {
            addError("Could not invoke method " + method.getName() + " in class " + this.obj.getClass().getName() + " with parameter of type " + cls.getName(), e);
        }
    }

    public void setComplexProperty(String str, Object obj2) {
        StringBuilder sb;
        Class<?> cls;
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(Introspector.decapitalize(str));
        if (propertyDescriptor == null) {
            sb = new StringBuilder();
            sb.append("Could not find PropertyDescriptor for [");
            sb.append(str);
            sb.append("] in ");
            cls = this.objClass;
        } else {
            Method writeMethod = propertyDescriptor.getWriteMethod();
            if (writeMethod == null) {
                sb = new StringBuilder();
                sb.append("Not setter method for property [");
                sb.append(str);
                sb.append("] in ");
                cls = this.obj.getClass();
            } else if (isSanityCheckSuccessful(str, writeMethod, writeMethod.getParameterTypes(), obj2)) {
                try {
                    invokeMethodWithSingleParameterOnThisObject(writeMethod, obj2);
                    return;
                } catch (Exception e) {
                    addError("Could not set component " + this.obj + " for parent component " + this.obj, e);
                    return;
                }
            } else {
                return;
            }
        }
        sb.append(cls.getName());
        addWarn(sb.toString());
    }

    public void setProperty(PropertyDescriptor propertyDescriptor, String str, String str2) throws PropertySetterException {
        Method writeMethod = propertyDescriptor.getWriteMethod();
        if (writeMethod != null) {
            Class[] parameterTypes = writeMethod.getParameterTypes();
            if (parameterTypes.length == 1) {
                try {
                    Object convertArg = StringToObjectConverter.convertArg(this, str2, parameterTypes[0]);
                    if (convertArg != null) {
                        try {
                            writeMethod.invoke(this.obj, new Object[]{convertArg});
                        } catch (Exception e) {
                            throw new PropertySetterException((Throwable) e);
                        }
                    } else {
                        throw new PropertySetterException("Conversion to type [" + parameterTypes[0] + "] failed.");
                    }
                } catch (Throwable th) {
                    throw new PropertySetterException("Conversion to type [" + parameterTypes[0] + "] failed. ", th);
                }
            } else {
                throw new PropertySetterException("#params for setter != 1");
            }
        } else {
            throw new PropertySetterException("No setter for property [" + str + "].");
        }
    }

    public void setProperty(String str, String str2) {
        if (str2 != null) {
            String decapitalize = Introspector.decapitalize(str);
            PropertyDescriptor propertyDescriptor = getPropertyDescriptor(decapitalize);
            if (propertyDescriptor == null) {
                addWarn("No such property [" + decapitalize + "] in " + this.objClass.getName() + ".");
                return;
            }
            try {
                setProperty(propertyDescriptor, decapitalize, str2);
            } catch (PropertySetterException e) {
                addWarn("Failed to set property [" + decapitalize + "] to value \"" + str2 + "\". ", e);
            }
        }
    }
}

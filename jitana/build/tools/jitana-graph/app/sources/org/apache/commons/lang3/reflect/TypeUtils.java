package org.apache.commons.lang3.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.Builder;
import p005ch.qos.logback.core.CoreConstants;

public class TypeUtils {
    public static final WildcardType WILDCARD_ALL = wildcardType().withUpperBounds(Object.class).build();

    public static class WildcardTypeBuilder implements Builder<WildcardType> {
        private Type[] lowerBounds;
        private Type[] upperBounds;

        private WildcardTypeBuilder() {
        }

        public WildcardTypeBuilder withUpperBounds(Type... bounds) {
            this.upperBounds = bounds;
            return this;
        }

        public WildcardTypeBuilder withLowerBounds(Type... bounds) {
            this.lowerBounds = bounds;
            return this;
        }

        public WildcardType build() {
            return new WildcardTypeImpl(this.upperBounds, this.lowerBounds);
        }
    }

    private static final class GenericArrayTypeImpl implements GenericArrayType {
        private final Type componentType;

        private GenericArrayTypeImpl(Type componentType2) {
            this.componentType = componentType2;
        }

        public Type getGenericComponentType() {
            return this.componentType;
        }

        public String toString() {
            return TypeUtils.toString((Type) this);
        }

        public boolean equals(Object obj) {
            return obj == this || ((obj instanceof GenericArrayType) && TypeUtils.equals((GenericArrayType) this, (Type) (GenericArrayType) obj));
        }

        public int hashCode() {
            return 1072 | this.componentType.hashCode();
        }
    }

    private static final class ParameterizedTypeImpl implements ParameterizedType {
        private final Class<?> raw;
        private final Type[] typeArguments;
        private final Type useOwner;

        private ParameterizedTypeImpl(Class<?> raw2, Type useOwner2, Type[] typeArguments2) {
            this.raw = raw2;
            this.useOwner = useOwner2;
            this.typeArguments = (Type[]) typeArguments2.clone();
        }

        public Type getRawType() {
            return this.raw;
        }

        public Type getOwnerType() {
            return this.useOwner;
        }

        public Type[] getActualTypeArguments() {
            return (Type[]) this.typeArguments.clone();
        }

        public String toString() {
            return TypeUtils.toString((Type) this);
        }

        public boolean equals(Object obj) {
            return obj == this || ((obj instanceof ParameterizedType) && TypeUtils.equals((ParameterizedType) this, (Type) (ParameterizedType) obj));
        }

        public int hashCode() {
            return ((((1136 | this.raw.hashCode()) << 4) | Objects.hashCode(this.useOwner)) << 8) | Arrays.hashCode(this.typeArguments);
        }
    }

    private static final class WildcardTypeImpl implements WildcardType {
        private static final Type[] EMPTY_BOUNDS = new Type[0];
        private final Type[] lowerBounds;
        private final Type[] upperBounds;

        private WildcardTypeImpl(Type[] upperBounds2, Type[] lowerBounds2) {
            this.upperBounds = (Type[]) ObjectUtils.defaultIfNull(upperBounds2, EMPTY_BOUNDS);
            this.lowerBounds = (Type[]) ObjectUtils.defaultIfNull(lowerBounds2, EMPTY_BOUNDS);
        }

        public Type[] getUpperBounds() {
            return (Type[]) this.upperBounds.clone();
        }

        public Type[] getLowerBounds() {
            return (Type[]) this.lowerBounds.clone();
        }

        public String toString() {
            return TypeUtils.toString((Type) this);
        }

        public boolean equals(Object obj) {
            return obj == this || ((obj instanceof WildcardType) && TypeUtils.equals((WildcardType) this, (Type) (WildcardType) obj));
        }

        public int hashCode() {
            return ((18688 | Arrays.hashCode(this.upperBounds)) << 8) | Arrays.hashCode(this.lowerBounds);
        }
    }

    public static boolean isAssignable(Type type, Type toType) {
        return isAssignable(type, toType, (Map<TypeVariable<?>, Type>) null);
    }

    private static boolean isAssignable(Type type, Type toType, Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (toType == null || (toType instanceof Class)) {
            return isAssignable(type, (Class<?>) (Class) toType);
        }
        if (toType instanceof ParameterizedType) {
            return isAssignable(type, (ParameterizedType) toType, typeVarAssigns);
        }
        if (toType instanceof GenericArrayType) {
            return isAssignable(type, (GenericArrayType) toType, typeVarAssigns);
        }
        if (toType instanceof WildcardType) {
            return isAssignable(type, (WildcardType) toType, typeVarAssigns);
        }
        if (toType instanceof TypeVariable) {
            return isAssignable(type, (TypeVariable<?>) (TypeVariable) toType, typeVarAssigns);
        }
        throw new IllegalStateException("found an unhandled type: " + toType);
    }

    private static boolean isAssignable(Type type, Class<?> toClass) {
        if (type == null) {
            if (toClass == null || !toClass.isPrimitive()) {
                return true;
            }
            return false;
        } else if (toClass == null) {
            return false;
        } else {
            if (toClass.equals(type)) {
                return true;
            }
            if (type instanceof Class) {
                return ClassUtils.isAssignable((Class<?>) (Class) type, toClass);
            }
            if (type instanceof ParameterizedType) {
                return isAssignable((Type) getRawType((ParameterizedType) type), toClass);
            }
            if (type instanceof TypeVariable) {
                for (Type bound : ((TypeVariable) type).getBounds()) {
                    if (isAssignable(bound, toClass)) {
                        return true;
                    }
                }
                return false;
            } else if (type instanceof GenericArrayType) {
                if (toClass.equals(Object.class)) {
                    return true;
                }
                if (!toClass.isArray() || !isAssignable(((GenericArrayType) type).getGenericComponentType(), toClass.getComponentType())) {
                    return false;
                }
                return true;
            } else if (type instanceof WildcardType) {
                return false;
            } else {
                throw new IllegalStateException("found an unhandled type: " + type);
            }
        }
    }

    private static boolean isAssignable(Type type, ParameterizedType toParameterizedType, Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }
        if (toParameterizedType == null) {
            return false;
        }
        if (toParameterizedType.equals(type)) {
            return true;
        }
        Class<?> toClass = getRawType(toParameterizedType);
        Map<TypeVariable<?>, Type> fromTypeVarAssigns = getTypeArguments(type, toClass, (Map<TypeVariable<?>, Type>) null);
        if (fromTypeVarAssigns == null) {
            return false;
        }
        if (fromTypeVarAssigns.isEmpty()) {
            return true;
        }
        Map<TypeVariable<?>, Type> toTypeVarAssigns = getTypeArguments(toParameterizedType, toClass, typeVarAssigns);
        for (TypeVariable<?> var : toTypeVarAssigns.keySet()) {
            Type toTypeArg = unrollVariableAssignments(var, toTypeVarAssigns);
            Type fromTypeArg = unrollVariableAssignments(var, fromTypeVarAssigns);
            if ((toTypeArg != null || !(fromTypeArg instanceof Class)) && fromTypeArg != null && !toTypeArg.equals(fromTypeArg)) {
                if (!(toTypeArg instanceof WildcardType) || !isAssignable(fromTypeArg, toTypeArg, typeVarAssigns)) {
                    return false;
                }
            }
        }
        return true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: java.lang.reflect.Type} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: java.lang.reflect.TypeVariable<?>} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.reflect.Type unrollVariableAssignments(java.lang.reflect.TypeVariable<?> r2, java.util.Map<java.lang.reflect.TypeVariable<?>, java.lang.reflect.Type> r3) {
        /*
        L_0x0000:
            java.lang.Object r0 = r3.get(r2)
            java.lang.reflect.Type r0 = (java.lang.reflect.Type) r0
            boolean r1 = r0 instanceof java.lang.reflect.TypeVariable
            if (r1 == 0) goto L_0x0014
            boolean r1 = r0.equals(r2)
            if (r1 != 0) goto L_0x0014
            r2 = r0
            java.lang.reflect.TypeVariable r2 = (java.lang.reflect.TypeVariable) r2
            goto L_0x0000
        L_0x0014:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.reflect.TypeUtils.unrollVariableAssignments(java.lang.reflect.TypeVariable, java.util.Map):java.lang.reflect.Type");
    }

    private static boolean isAssignable(Type type, GenericArrayType toGenericArrayType, Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }
        if (toGenericArrayType == null) {
            return false;
        }
        if (toGenericArrayType.equals(type)) {
            return true;
        }
        Type toComponentType = toGenericArrayType.getGenericComponentType();
        if (type instanceof Class) {
            Class<?> cls = (Class) type;
            if (!cls.isArray() || !isAssignable((Type) cls.getComponentType(), toComponentType, typeVarAssigns)) {
                return false;
            }
            return true;
        } else if (type instanceof GenericArrayType) {
            return isAssignable(((GenericArrayType) type).getGenericComponentType(), toComponentType, typeVarAssigns);
        } else {
            if (type instanceof WildcardType) {
                for (Type bound : getImplicitUpperBounds((WildcardType) type)) {
                    if (isAssignable(bound, (Type) toGenericArrayType)) {
                        return true;
                    }
                }
                return false;
            } else if (type instanceof TypeVariable) {
                for (Type bound2 : getImplicitBounds((TypeVariable) type)) {
                    if (isAssignable(bound2, (Type) toGenericArrayType)) {
                        return true;
                    }
                }
                return false;
            } else if (type instanceof ParameterizedType) {
                return false;
            } else {
                throw new IllegalStateException("found an unhandled type: " + type);
            }
        }
    }

    private static boolean isAssignable(Type type, WildcardType toWildcardType, Map<TypeVariable<?>, Type> typeVarAssigns) {
        Type type2 = type;
        WildcardType wildcardType = toWildcardType;
        Map<TypeVariable<?>, Type> map = typeVarAssigns;
        if (type2 == null) {
            return true;
        }
        if (wildcardType == null) {
            return false;
        }
        if (wildcardType.equals(type2)) {
            return true;
        }
        Type[] toUpperBounds = getImplicitUpperBounds(toWildcardType);
        Type[] toLowerBounds = getImplicitLowerBounds(toWildcardType);
        if (type2 instanceof WildcardType) {
            WildcardType wildcardType2 = (WildcardType) type2;
            Type[] upperBounds = getImplicitUpperBounds(wildcardType2);
            Type[] lowerBounds = getImplicitLowerBounds(wildcardType2);
            for (Type toBound : toUpperBounds) {
                Type toBound2 = substituteTypeVariables(toBound, map);
                for (Type bound : upperBounds) {
                    if (!isAssignable(bound, toBound2, map)) {
                        return false;
                    }
                }
            }
            for (Type toBound3 : toLowerBounds) {
                Type toBound4 = substituteTypeVariables(toBound3, map);
                for (Type bound2 : lowerBounds) {
                    if (!isAssignable(toBound4, bound2, map)) {
                        return false;
                    }
                }
            }
            return true;
        }
        for (Type toBound5 : toUpperBounds) {
            if (!isAssignable(type2, substituteTypeVariables(toBound5, map), map)) {
                return false;
            }
        }
        for (Type toBound6 : toLowerBounds) {
            if (!isAssignable(substituteTypeVariables(toBound6, map), type2, map)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAssignable(Type type, TypeVariable<?> toTypeVariable, Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }
        if (toTypeVariable == null) {
            return false;
        }
        if (toTypeVariable.equals(type)) {
            return true;
        }
        if (type instanceof TypeVariable) {
            for (Type bound : getImplicitBounds((TypeVariable) type)) {
                if (isAssignable(bound, toTypeVariable, typeVarAssigns)) {
                    return true;
                }
            }
        }
        if ((type instanceof Class) || (type instanceof ParameterizedType) || (type instanceof GenericArrayType) || (type instanceof WildcardType)) {
            return false;
        }
        throw new IllegalStateException("found an unhandled type: " + type);
    }

    private static Type substituteTypeVariables(Type type, Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (!(type instanceof TypeVariable) || typeVarAssigns == null) {
            return type;
        }
        Type replacementType = typeVarAssigns.get(type);
        if (replacementType != null) {
            return replacementType;
        }
        throw new IllegalArgumentException("missing assignment type for type variable " + type);
    }

    public static Map<TypeVariable<?>, Type> getTypeArguments(ParameterizedType type) {
        return getTypeArguments(type, getRawType(type), (Map<TypeVariable<?>, Type>) null);
    }

    public static Map<TypeVariable<?>, Type> getTypeArguments(Type type, Class<?> toClass) {
        return getTypeArguments(type, toClass, (Map<TypeVariable<?>, Type>) null);
    }

    private static Map<TypeVariable<?>, Type> getTypeArguments(Type type, Class<?> toClass, Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        if (type instanceof Class) {
            return getTypeArguments((Class<?>) (Class) type, toClass, subtypeVarAssigns);
        }
        if (type instanceof ParameterizedType) {
            return getTypeArguments((ParameterizedType) type, toClass, subtypeVarAssigns);
        }
        if (type instanceof GenericArrayType) {
            return getTypeArguments(((GenericArrayType) type).getGenericComponentType(), toClass.isArray() ? toClass.getComponentType() : toClass, subtypeVarAssigns);
        } else if (type instanceof WildcardType) {
            for (Type bound : getImplicitUpperBounds((WildcardType) type)) {
                if (isAssignable(bound, toClass)) {
                    return getTypeArguments(bound, toClass, subtypeVarAssigns);
                }
            }
            return null;
        } else if (type instanceof TypeVariable) {
            for (Type bound2 : getImplicitBounds((TypeVariable) type)) {
                if (isAssignable(bound2, toClass)) {
                    return getTypeArguments(bound2, toClass, subtypeVarAssigns);
                }
            }
            return null;
        } else {
            throw new IllegalStateException("found an unhandled type: " + type);
        }
    }

    private static Map<TypeVariable<?>, Type> getTypeArguments(ParameterizedType parameterizedType, Class<?> toClass, Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        Map<TypeVariable<?>, Type> typeVarAssigns;
        Class<?> cls = getRawType(parameterizedType);
        if (!isAssignable((Type) cls, toClass)) {
            return null;
        }
        Type ownerType = parameterizedType.getOwnerType();
        if (ownerType instanceof ParameterizedType) {
            ParameterizedType parameterizedOwnerType = (ParameterizedType) ownerType;
            typeVarAssigns = getTypeArguments(parameterizedOwnerType, getRawType(parameterizedOwnerType), subtypeVarAssigns);
        } else if (subtypeVarAssigns != null) {
            typeVarAssigns = new HashMap<>(subtypeVarAssigns);
        }
        Type[] typeArgs = parameterizedType.getActualTypeArguments();
        TypeVariable<?>[] typeParams = cls.getTypeParameters();
        for (int i = 0; i < typeParams.length; i++) {
            Type typeArg = typeArgs[i];
            typeVarAssigns.put(typeParams[i], typeVarAssigns.containsKey(typeArg) ? typeVarAssigns.get(typeArg) : typeArg);
        }
        if (toClass.equals(cls) != 0) {
            return typeVarAssigns;
        }
        return getTypeArguments(getClosestParentType(cls, toClass), toClass, typeVarAssigns);
    }

    private static Map<TypeVariable<?>, Type> getTypeArguments(Class<?> cls, Class<?> toClass, Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        HashMap<TypeVariable<?>, Type> typeVarAssigns;
        if (!isAssignable((Type) cls, toClass)) {
            return null;
        }
        if (cls.isPrimitive()) {
            if (toClass.isPrimitive()) {
                return new HashMap();
            }
            cls = ClassUtils.primitiveToWrapper(cls);
        }
        if (subtypeVarAssigns != null) {
            typeVarAssigns = new HashMap<>(subtypeVarAssigns);
        }
        if (toClass.equals(cls)) {
            return typeVarAssigns;
        }
        return getTypeArguments(getClosestParentType(cls, toClass), toClass, (Map<TypeVariable<?>, Type>) typeVarAssigns);
    }

    public static Map<TypeVariable<?>, Type> determineTypeArguments(Class<?> cls, ParameterizedType superType) {
        Validate.notNull(cls, "cls is null", new Object[0]);
        Validate.notNull(superType, "superType is null", new Object[0]);
        Class<?> superClass = getRawType(superType);
        if (!isAssignable((Type) cls, superClass)) {
            return null;
        }
        if (cls.equals(superClass)) {
            return getTypeArguments(superType, superClass, (Map<TypeVariable<?>, Type>) null);
        }
        Type midType = getClosestParentType(cls, superClass);
        if (midType instanceof Class) {
            return determineTypeArguments((Class) midType, superType);
        }
        ParameterizedType midParameterizedType = (ParameterizedType) midType;
        Map<TypeVariable<?>, Type> typeVarAssigns = determineTypeArguments(getRawType(midParameterizedType), superType);
        mapTypeVariablesToArguments(cls, midParameterizedType, typeVarAssigns);
        return typeVarAssigns;
    }

    private static <T> void mapTypeVariablesToArguments(Class<T> cls, ParameterizedType parameterizedType, Map<TypeVariable<?>, Type> typeVarAssigns) {
        Type ownerType = parameterizedType.getOwnerType();
        if (ownerType instanceof ParameterizedType) {
            mapTypeVariablesToArguments(cls, (ParameterizedType) ownerType, typeVarAssigns);
        }
        Type[] typeArgs = parameterizedType.getActualTypeArguments();
        TypeVariable<?>[] typeVars = getRawType(parameterizedType).getTypeParameters();
        List<TypeVariable<Class<T>>> typeVarList = Arrays.asList(cls.getTypeParameters());
        for (int i = 0; i < typeArgs.length; i++) {
            TypeVariable<?> typeVar = typeVars[i];
            Type typeArg = typeArgs[i];
            if (typeVarList.contains(typeArg) && typeVarAssigns.containsKey(typeVar)) {
                typeVarAssigns.put((TypeVariable) typeArg, typeVarAssigns.get(typeVar));
            }
        }
    }

    private static Type getClosestParentType(Class<?> cls, Class<?> superClass) {
        Class<?> midClass;
        if (superClass.isInterface()) {
            Type genericInterface = null;
            for (Type midType : cls.getGenericInterfaces()) {
                if (midType instanceof ParameterizedType) {
                    midClass = getRawType((ParameterizedType) midType);
                } else if (midType instanceof Class) {
                    midClass = (Class) midType;
                } else {
                    throw new IllegalStateException("Unexpected generic interface type found: " + midType);
                }
                if (isAssignable((Type) midClass, superClass) && isAssignable(genericInterface, (Type) midClass)) {
                    genericInterface = midType;
                }
            }
            if (genericInterface != null) {
                return genericInterface;
            }
        }
        return cls.getGenericSuperclass();
    }

    public static boolean isInstance(Object value, Type type) {
        if (type == null) {
            return false;
        }
        if (value != null) {
            return isAssignable((Type) value.getClass(), type, (Map<TypeVariable<?>, Type>) null);
        }
        if (!(type instanceof Class) || !((Class) type).isPrimitive()) {
            return true;
        }
        return false;
    }

    public static Type[] normalizeUpperBounds(Type[] bounds) {
        Validate.notNull(bounds, "null value specified for bounds array", new Object[0]);
        if (bounds.length < 2) {
            return bounds;
        }
        Set<Type> types = new HashSet<>(bounds.length);
        for (Type type1 : bounds) {
            boolean subtypeFound = false;
            Type[] arr$ = bounds;
            int len$ = arr$.length;
            int i$ = 0;
            while (true) {
                if (i$ < len$) {
                    Type type2 = arr$[i$];
                    if (type1 != type2 && isAssignable(type2, type1, (Map<TypeVariable<?>, Type>) null)) {
                        subtypeFound = true;
                        break;
                    }
                    i$++;
                } else {
                    break;
                }
            }
            if (!subtypeFound) {
                types.add(type1);
            }
        }
        return (Type[]) types.toArray(new Type[types.size()]);
    }

    public static Type[] getImplicitBounds(TypeVariable<?> typeVariable) {
        Validate.notNull(typeVariable, "typeVariable is null", new Object[0]);
        Type[] bounds = typeVariable.getBounds();
        if (bounds.length != 0) {
            return normalizeUpperBounds(bounds);
        }
        return new Type[]{Object.class};
    }

    public static Type[] getImplicitUpperBounds(WildcardType wildcardType) {
        Validate.notNull(wildcardType, "wildcardType is null", new Object[0]);
        Type[] bounds = wildcardType.getUpperBounds();
        if (bounds.length != 0) {
            return normalizeUpperBounds(bounds);
        }
        return new Type[]{Object.class};
    }

    public static Type[] getImplicitLowerBounds(WildcardType wildcardType) {
        Validate.notNull(wildcardType, "wildcardType is null", new Object[0]);
        Type[] bounds = wildcardType.getLowerBounds();
        if (bounds.length != 0) {
            return bounds;
        }
        return new Type[]{null};
    }

    public static boolean typesSatisfyVariables(Map<TypeVariable<?>, Type> typeVarAssigns) {
        Validate.notNull(typeVarAssigns, "typeVarAssigns is null", new Object[0]);
        for (Map.Entry<TypeVariable<?>, Type> entry : typeVarAssigns.entrySet()) {
            Type type = entry.getValue();
            Type[] arr$ = getImplicitBounds(entry.getKey());
            int len$ = arr$.length;
            int i$ = 0;
            while (true) {
                if (i$ < len$) {
                    if (!isAssignable(type, substituteTypeVariables(arr$[i$], typeVarAssigns), typeVarAssigns)) {
                        return false;
                    }
                    i$++;
                }
            }
        }
        return true;
    }

    private static Class<?> getRawType(ParameterizedType parameterizedType) {
        Type rawType = parameterizedType.getRawType();
        if (rawType instanceof Class) {
            return (Class) rawType;
        }
        throw new IllegalStateException("Wait... What!? Type of rawType: " + rawType);
    }

    public static Class<?> getRawType(Type type, Type assigningType) {
        Map<TypeVariable<?>, Type> typeVarAssigns;
        Type typeArgument;
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            return getRawType((ParameterizedType) type);
        }
        if (type instanceof TypeVariable) {
            if (assigningType == null) {
                return null;
            }
            GenericDeclaration genericDeclaration = ((TypeVariable) type).getGenericDeclaration();
            if (!(genericDeclaration instanceof Class) || (typeVarAssigns = getTypeArguments(assigningType, (Class) genericDeclaration)) == null || (typeArgument = typeVarAssigns.get(type)) == null) {
                return null;
            }
            return getRawType(typeArgument, assigningType);
        } else if (type instanceof GenericArrayType) {
            return Array.newInstance(getRawType(((GenericArrayType) type).getGenericComponentType(), assigningType), 0).getClass();
        } else {
            if (type instanceof WildcardType) {
                return null;
            }
            throw new IllegalArgumentException("unknown type: " + type);
        }
    }

    public static boolean isArrayType(Type type) {
        return (type instanceof GenericArrayType) || ((type instanceof Class) && ((Class) type).isArray());
    }

    public static Type getArrayComponentType(Type type) {
        if (type instanceof Class) {
            Class<?> clazz = (Class) type;
            if (clazz.isArray()) {
                return clazz.getComponentType();
            }
            return null;
        } else if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        } else {
            return null;
        }
    }

    public static Type unrollVariables(Map<TypeVariable<?>, Type> typeArguments, Type type) {
        Map<TypeVariable<?>, Type> parameterizedTypeArguments;
        if (typeArguments == null) {
            typeArguments = Collections.emptyMap();
        }
        if (containsTypeVariables(type)) {
            if (type instanceof TypeVariable) {
                return unrollVariables(typeArguments, typeArguments.get(type));
            }
            if (type instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) type;
                if (p.getOwnerType() == null) {
                    parameterizedTypeArguments = typeArguments;
                } else {
                    parameterizedTypeArguments = new HashMap<>(typeArguments);
                    parameterizedTypeArguments.putAll(getTypeArguments(p));
                }
                Type[] args = p.getActualTypeArguments();
                for (int i = 0; i < args.length; i++) {
                    Type unrolled = unrollVariables(parameterizedTypeArguments, args[i]);
                    if (unrolled != null) {
                        args[i] = unrolled;
                    }
                }
                return parameterizeWithOwner(p.getOwnerType(), (Class<?>) (Class) p.getRawType(), args);
            } else if (type instanceof WildcardType) {
                WildcardType wild = (WildcardType) type;
                return wildcardType().withUpperBounds(unrollBounds(typeArguments, wild.getUpperBounds())).withLowerBounds(unrollBounds(typeArguments, wild.getLowerBounds())).build();
            }
        }
        return type;
    }

    /* JADX WARNING: type inference failed for: r1v4, types: [java.lang.Object[]] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.reflect.Type[] unrollBounds(java.util.Map<java.lang.reflect.TypeVariable<?>, java.lang.reflect.Type> r4, java.lang.reflect.Type[] r5) {
        /*
            r0 = r5
            r1 = 0
        L_0x0002:
            int r2 = r0.length
            if (r1 >= r2) goto L_0x001d
            r2 = r0[r1]
            java.lang.reflect.Type r2 = unrollVariables(r4, r2)
            if (r2 != 0) goto L_0x0018
            int r3 = r1 + -1
            java.lang.Object[] r1 = org.apache.commons.lang3.ArrayUtils.remove((T[]) r0, (int) r1)
            r0 = r1
            java.lang.reflect.Type[] r0 = (java.lang.reflect.Type[]) r0
            r1 = r3
            goto L_0x001a
        L_0x0018:
            r0[r1] = r2
        L_0x001a:
            int r1 = r1 + 1
            goto L_0x0002
        L_0x001d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.reflect.TypeUtils.unrollBounds(java.util.Map, java.lang.reflect.Type[]):java.lang.reflect.Type[]");
    }

    public static boolean containsTypeVariables(Type type) {
        if (type instanceof TypeVariable) {
            return true;
        }
        if (type instanceof Class) {
            if (((Class) type).getTypeParameters().length > 0) {
                return true;
            }
            return false;
        } else if (type instanceof ParameterizedType) {
            for (Type arg : ((ParameterizedType) type).getActualTypeArguments()) {
                if (containsTypeVariables(arg)) {
                    return true;
                }
            }
            return false;
        } else if (!(type instanceof WildcardType)) {
            return false;
        } else {
            WildcardType wild = (WildcardType) type;
            if (containsTypeVariables(getImplicitLowerBounds(wild)[0]) || containsTypeVariables(getImplicitUpperBounds(wild)[0])) {
                return true;
            }
            return false;
        }
    }

    public static final ParameterizedType parameterize(Class<?> raw, Type... typeArguments) {
        return parameterizeWithOwner((Type) null, raw, typeArguments);
    }

    public static final ParameterizedType parameterize(Class<?> raw, Map<TypeVariable<?>, Type> typeArgMappings) {
        Validate.notNull(raw, "raw class is null", new Object[0]);
        Validate.notNull(typeArgMappings, "typeArgMappings is null", new Object[0]);
        return parameterizeWithOwner((Type) null, raw, extractTypeArgumentsFrom(typeArgMappings, raw.getTypeParameters()));
    }

    public static final ParameterizedType parameterizeWithOwner(Type owner, Class<?> raw, Type... typeArguments) {
        Type useOwner;
        Validate.notNull(raw, "raw class is null", new Object[0]);
        if (raw.getEnclosingClass() == null) {
            Validate.isTrue(owner == null, "no owner allowed for top-level %s", raw);
            useOwner = null;
        } else if (owner == null) {
            useOwner = raw.getEnclosingClass();
        } else {
            Validate.isTrue(isAssignable(owner, raw.getEnclosingClass()), "%s is invalid owner type for parameterized %s", owner, raw);
            useOwner = owner;
        }
        Validate.noNullElements((T[]) typeArguments, "null type argument at index %s", new Object[0]);
        Validate.isTrue(raw.getTypeParameters().length == typeArguments.length, "invalid number of type parameters specified: expected %d, got %d", Integer.valueOf(raw.getTypeParameters().length), Integer.valueOf(typeArguments.length));
        return new ParameterizedTypeImpl(raw, useOwner, typeArguments);
    }

    public static final ParameterizedType parameterizeWithOwner(Type owner, Class<?> raw, Map<TypeVariable<?>, Type> typeArgMappings) {
        Validate.notNull(raw, "raw class is null", new Object[0]);
        Validate.notNull(typeArgMappings, "typeArgMappings is null", new Object[0]);
        return parameterizeWithOwner(owner, raw, extractTypeArgumentsFrom(typeArgMappings, raw.getTypeParameters()));
    }

    private static Type[] extractTypeArgumentsFrom(Map<TypeVariable<?>, Type> mappings, TypeVariable<?>[] variables) {
        Type[] result = new Type[variables.length];
        int index = 0;
        TypeVariable<?>[] arr$ = variables;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            TypeVariable<?> var = arr$[i$];
            Validate.isTrue(mappings.containsKey(var), "missing argument mapping for %s", toString((Type) var));
            result[index] = mappings.get(var);
            i$++;
            index++;
        }
        return result;
    }

    public static WildcardTypeBuilder wildcardType() {
        return new WildcardTypeBuilder();
    }

    public static GenericArrayType genericArrayType(Type componentType) {
        return new GenericArrayTypeImpl((Type) Validate.notNull(componentType, "componentType is null", new Object[0]));
    }

    public static boolean equals(Type t1, Type t2) {
        if (Objects.equals(t1, t2)) {
            return true;
        }
        if (t1 instanceof ParameterizedType) {
            return equals((ParameterizedType) t1, t2);
        }
        if (t1 instanceof GenericArrayType) {
            return equals((GenericArrayType) t1, t2);
        }
        if (t1 instanceof WildcardType) {
            return equals((WildcardType) t1, t2);
        }
        return false;
    }

    /* access modifiers changed from: private */
    public static boolean equals(ParameterizedType p, Type t) {
        if (!(t instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType other = (ParameterizedType) t;
        if (!equals(p.getRawType(), other.getRawType()) || !equals(p.getOwnerType(), other.getOwnerType())) {
            return false;
        }
        return equals(p.getActualTypeArguments(), other.getActualTypeArguments());
    }

    /* access modifiers changed from: private */
    public static boolean equals(GenericArrayType a, Type t) {
        return (t instanceof GenericArrayType) && equals(a.getGenericComponentType(), ((GenericArrayType) t).getGenericComponentType());
    }

    /* access modifiers changed from: private */
    public static boolean equals(WildcardType w, Type t) {
        if (!(t instanceof WildcardType)) {
            return false;
        }
        WildcardType other = (WildcardType) t;
        if (!equals(getImplicitLowerBounds(w), getImplicitLowerBounds(other)) || !equals(getImplicitUpperBounds(w), getImplicitUpperBounds(other))) {
            return false;
        }
        return true;
    }

    private static boolean equals(Type[] t1, Type[] t2) {
        if (t1.length != t2.length) {
            return false;
        }
        for (int i = 0; i < t1.length; i++) {
            if (!equals(t1[i], t2[i])) {
                return false;
            }
        }
        return true;
    }

    public static String toString(Type type) {
        Validate.notNull(type);
        if (type instanceof Class) {
            return classToString((Class) type);
        }
        if (type instanceof ParameterizedType) {
            return parameterizedTypeToString((ParameterizedType) type);
        }
        if (type instanceof WildcardType) {
            return wildcardTypeToString((WildcardType) type);
        }
        if (type instanceof TypeVariable) {
            return typeVariableToString((TypeVariable) type);
        }
        if (type instanceof GenericArrayType) {
            return genericArrayTypeToString((GenericArrayType) type);
        }
        throw new IllegalArgumentException(ObjectUtils.identityToString(type));
    }

    public static String toLongString(TypeVariable<?> var) {
        Validate.notNull(var, "var is null", new Object[0]);
        StringBuilder buf = new StringBuilder();
        Object genericDeclaration = var.getGenericDeclaration();
        if (genericDeclaration instanceof Class) {
            Class<?> c = (Class) genericDeclaration;
            while (c.getEnclosingClass() != null) {
                buf.insert(0, c.getSimpleName()).insert(0, '.');
                c = c.getEnclosingClass();
            }
            buf.insert(0, c.getName());
        } else if (genericDeclaration instanceof Type) {
            buf.append(toString((Type) genericDeclaration));
        } else {
            buf.append(genericDeclaration);
        }
        buf.append(CoreConstants.COLON_CHAR);
        buf.append(typeVariableToString(var));
        return buf.toString();
    }

    public static <T> Typed<T> wrap(final Type type) {
        return new Typed<T>() {
            public Type getType() {
                return type;
            }
        };
    }

    public static <T> Typed<T> wrap(Class<T> type) {
        return wrap((Type) type);
    }

    private static String classToString(Class<?> c) {
        if (c.isArray()) {
            return toString((Type) c.getComponentType()) + "[]";
        }
        StringBuilder buf = new StringBuilder();
        if (c.getEnclosingClass() != null) {
            buf.append(classToString(c.getEnclosingClass()));
            buf.append('.');
            buf.append(c.getSimpleName());
        } else {
            buf.append(c.getName());
        }
        if (c.getTypeParameters().length > 0) {
            buf.append('<');
            appendAllTo(buf, ", ", c.getTypeParameters());
            buf.append('>');
        }
        return buf.toString();
    }

    private static String typeVariableToString(TypeVariable<?> v) {
        StringBuilder buf = new StringBuilder(v.getName());
        Type[] bounds = v.getBounds();
        if (bounds.length > 0 && (bounds.length != 1 || !Object.class.equals(bounds[0]))) {
            buf.append(" extends ");
            appendAllTo(buf, " & ", v.getBounds());
        }
        return buf.toString();
    }

    private static String parameterizedTypeToString(ParameterizedType p) {
        StringBuilder buf = new StringBuilder();
        Type useOwner = p.getOwnerType();
        Class<?> raw = (Class) p.getRawType();
        if (useOwner == null) {
            buf.append(raw.getName());
        } else {
            if (useOwner instanceof Class) {
                buf.append(((Class) useOwner).getName());
            } else {
                buf.append(useOwner.toString());
            }
            buf.append('.');
            buf.append(raw.getSimpleName());
        }
        int[] recursiveTypeIndexes = findRecursiveTypes(p);
        if (recursiveTypeIndexes.length > 0) {
            appendRecursiveTypes(buf, recursiveTypeIndexes, p.getActualTypeArguments());
        } else {
            buf.append('<');
            appendAllTo(buf, ", ", p.getActualTypeArguments()).append('>');
        }
        return buf.toString();
    }

    private static void appendRecursiveTypes(StringBuilder buf, int[] recursiveTypeIndexes, Type[] argumentTypes) {
        for (int i = 0; i < recursiveTypeIndexes.length; i++) {
            buf.append('<');
            appendAllTo(buf, ", ", argumentTypes[i].toString()).append('>');
        }
        Type[] argumentsFiltered = (Type[]) ArrayUtils.removeAll((T[]) argumentTypes, recursiveTypeIndexes);
        if (argumentsFiltered.length > 0) {
            buf.append('<');
            appendAllTo(buf, ", ", argumentsFiltered).append('>');
        }
    }

    private static int[] findRecursiveTypes(ParameterizedType p) {
        Type[] filteredArgumentTypes = (Type[]) Arrays.copyOf(p.getActualTypeArguments(), p.getActualTypeArguments().length);
        int[] indexesToRemove = new int[0];
        for (int i = 0; i < filteredArgumentTypes.length; i++) {
            if ((filteredArgumentTypes[i] instanceof TypeVariable) && containsVariableTypeSameParametrizedTypeBound((TypeVariable) filteredArgumentTypes[i], p)) {
                indexesToRemove = ArrayUtils.add(indexesToRemove, i);
            }
        }
        return indexesToRemove;
    }

    private static boolean containsVariableTypeSameParametrizedTypeBound(TypeVariable<?> typeVariable, ParameterizedType p) {
        return ArrayUtils.contains((Object[]) typeVariable.getBounds(), (Object) p);
    }

    private static String wildcardTypeToString(WildcardType w) {
        StringBuilder buf = new StringBuilder().append('?');
        Type[] lowerBounds = w.getLowerBounds();
        Type[] upperBounds = w.getUpperBounds();
        if (lowerBounds.length > 1 || (lowerBounds.length == 1 && lowerBounds[0] != null)) {
            buf.append(" super ");
            appendAllTo(buf, " & ", lowerBounds);
        } else if (upperBounds.length > 1 || (upperBounds.length == 1 && !Object.class.equals(upperBounds[0]))) {
            buf.append(" extends ");
            appendAllTo(buf, " & ", upperBounds);
        }
        return buf.toString();
    }

    private static String genericArrayTypeToString(GenericArrayType g) {
        return String.format("%s[]", new Object[]{toString(g.getGenericComponentType())});
    }

    private static <T> StringBuilder appendAllTo(StringBuilder buf, String sep, T... types) {
        Validate.notEmpty((T[]) Validate.noNullElements(types));
        if (types.length > 0) {
            buf.append(toString(types[0]));
            for (int i = 1; i < types.length; i++) {
                buf.append(sep);
                buf.append(toString(types[i]));
            }
        }
        return buf;
    }

    private static <T> String toString(T object) {
        return object instanceof Type ? toString((Type) object) : object.toString();
    }
}

package ffmTests;

import java.lang.foreign.Arena;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public final class NativeObject {
    private static final List<String> pointerTypes = List.of(
        "java.lang.foreign.MemorySegment",
        "ffmTests.NativePointer",
        "ffmTests.NativePointer$OfBoolean",
        "ffmTests.NativePointer$OfByte",
        "ffmTests.NativePointer$OfShort",
        "ffmTests.NativePointer$OfInt",
        "ffmTests.NativePointer$OfLong",
        "ffmTests.NativePointer$OfFloat",
        "ffmTests.NativePointer$OfDouble",
        "ffmTests.NativePointer$OfAddress"
    );
    private static final Type methodHandleType = Type.getType("Ljava/lang/invoke/MethodHandle;");

    public static <T> T get(final Class<T> clazz, final SymbolLookup lib) {
        final String className = clazz.getName().replace('.', '/');
        final var implType = Type.getObjectType(className + "$Impl");

        final var cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(
            Opcodes.V25,
            Opcodes.ACC_PUBLIC,
            implType.getInternalName(),
            null,
            "java/lang/Object",
            new String[] { className }
        );

        final var constructor = new GeneratorAdapter(
            Opcodes.ACC_PUBLIC,
            new Method("<init>", "([Ljava/lang/invoke/MethodHandle;)V"),
            null,
            null,
            cw
        );
        constructor.visitCode();
        constructor.loadThis();
        constructor.invokeConstructor(
            Type.getType(Object.class),
            Method.getMethod("void <init>()")
        );

        final var handles = generateMembers(clazz, lib, implType, cw, constructor);
        constructor.visitInsn(Opcodes.RETURN);
        constructor.endMethod();
        cw.visitEnd();

        final Class<T> implClass = defineClass(clazz, cw.toByteArray());
        try {
            @SuppressWarnings("unchecked")
            T retValue = (T)implClass.getDeclaredConstructors()[0].newInstance((Object) handles);
            return retValue;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> MethodHandle[] generateMembers(final Class<T> clazz, final SymbolLookup lib, final Type implType,
            final ClassWriter cw, final GeneratorAdapter constructor) {
        var handles = new MethodHandle[clazz.getDeclaredMethods().length]; 
        var index = 0;
        var linker = Linker.nativeLinker();
        for (var methodReflect : clazz.getDeclaredMethods()) {
            var methodDesc = Method.getMethod(methodReflect);

            // all modifiers that are relevant to us
            final var modifiers  = Opcodes.ACC_PUBLIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED
                | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL | Opcodes.ACC_BRIDGE
                | Opcodes.ACC_NATIVE | Opcodes.ACC_STRICT | Opcodes.ACC_SYNTHETIC;
            if ((methodReflect.getModifiers() & modifiers) != Opcodes.ACC_PUBLIC)
                continue; // only public instance methods are assumed to be native methods
            for (Field field : clazz.getFields())
                if (field.getName().equals(methodReflect.getName()))
                    throw new IllegalStateException("Method name collides with field name: " + field.getName()
                        + ". it is not supported by the generator.");

            var cFuncDesc = getInternalDescriptor(methodDesc);
            
            var functionRef = linker.downcallHandle(
                lib.find(cFuncDesc.getName()).get(),
                Utils.MakeCFunctionDescriptor(methodReflect) //FIXME: dont use reflection
            );

            handles[index] = functionRef;
            initCFuncField(cw, implType, constructor, index, methodDesc);
            index++;

            final var method = new GeneratorAdapter(
                Opcodes.ACC_PUBLIC,
                methodDesc,
                null,
                null,
                cw
            );
            method.visitCode();
            generateCfuncWrapper(implType, methodDesc, cFuncDesc, method);
            method.endMethod();
        }
        return handles;
    }

    private static Method getInternalDescriptor(Method methodDesc) {
        int argIndex = 0;
        Type[] argTypes = null;
        Type returnType = null;

        for (var paramType : methodDesc.getArgumentTypes()) {
            if (paramType.getSort() == Type.ARRAY)
                throw new IllegalStateException("Array types are not supported at this time: " + paramType); //TODO: support arrays 
            if (paramType.getSort() == Type.OBJECT && !pointerTypes.contains(paramType.getClassName()))
                throw new IllegalStateException("Only primitive types are supported at this time: " + paramType); //TODO: support strings and class types

            switch (paramType.getSort()) {
                case Type.OBJECT:
                    if (argTypes == null) {
                        argTypes = methodDesc.getArgumentTypes();
                        returnType = methodDesc.getReturnType();
                    }
                    argTypes[argIndex] = Type.getType(MemorySegment.class);
                    break;
                default:
                    break;
            }
            argIndex++;
        }

        if (methodDesc.getReturnType().getSort() == Type.ARRAY)
            throw new IllegalStateException("Array types are not supported at this time: " + methodDesc.getReturnType());
        if (methodDesc.getReturnType().getSort() == Type.OBJECT && !pointerTypes.contains(methodDesc.getReturnType().getClassName()))
            throw new IllegalStateException("Only primitive types are supported at this time: " + methodDesc.getReturnType());
        switch (methodDesc.getReturnType().getSort()) {
            case Type.OBJECT:
                if (argTypes == null)
                    argTypes = methodDesc.getArgumentTypes();
                returnType = Type.getType(MemorySegment.class);
                break;
            default:
                break;
        }

        return argTypes != null || returnType != null ?
                    new Method(methodDesc.getName(), returnType, argTypes)
                    : methodDesc;
    }
    
    private static void generateCfuncWrapper(final Type implType, final Method methodDesc,
        final Method cFuncDesc, final GeneratorAdapter method) {
            final var tryStart = new Label();
            final var tryEnd = new Label();
            
            method.loadThis();
            method.getField(implType, methodDesc.getName(), methodHandleType);
            loadCFuncArgs(method, methodDesc, cFuncDesc);
            method.mark(tryStart);
            method.invokeVirtual(
                methodHandleType,
                new Method(
                    "invokeExact",
                    cFuncDesc.getReturnType(),
                    cFuncDesc.getArgumentTypes()
                )
            );
            method.mark(tryEnd);
            transformReturnValue(method, methodDesc, cFuncDesc);
            method.returnValue();
            
            method.catchException(tryStart, tryEnd, Type.getType(Throwable.class));
            final var exLocal = method.newLocal(Type.getType(Throwable.class));
            method.storeLocal(exLocal);
            method.newInstance(Type.getType(AssertionError.class));
            method.dup();
            method.loadLocal(exLocal);
            method.invokeConstructor(
                Type.getType(AssertionError.class),
                new Method("<init>", "(Ljava/lang/Object;)V")
            );
            method.throwException();
    }

    private static void loadCFuncArgs(final GeneratorAdapter method, final Method methodDesc, final Method cFuncDesc) {
        if (methodDesc.equals(cFuncDesc)) {
            method.loadArgs();
            return;
        }

        final var argumentTypes = methodDesc.getArgumentTypes();
        if (cFuncDesc.getArgumentTypes().length > 0 && cFuncDesc.getArgumentTypes()[0].equals(Type.getType(Arena.class))) {
            method.invokeStatic(Type.getType(Arena.class),
                                new Method(
                                    "ofAuto",
                                    Type.getType(Arena.class),
                                    new Type[] {}
                                ));
            method.dup();
            @SuppressWarnings("unused") //TODO: check when an arena is needed
            var arena = method.newLocal(Type.getType(Arena.class));
        }
        for (int i = 0; i < argumentTypes.length; i++) {
            final var arg = argumentTypes[i];
            method.loadArg(i);
            
            if (arg.getSort() == Type.OBJECT && pointerTypes.indexOf(arg.getClassName()) > 0) {
                Method getSegment = new Method(
                    "getSegment",
                    Type.getType(MemorySegment.class),
                    new Type[] {}
                );
                if (arg.equals(Type.getType(NativePointer.class)))
                    method.invokeInterface(arg, getSegment);
                else
                    method.invokeVirtual(arg, getSegment);
            }
        }
    }

    private static void transformReturnValue(final GeneratorAdapter method, final Method methodDesc, final Method cFuncDesc) {
        if (methodDesc.getReturnType().equals(cFuncDesc.getReturnType()))
            return;

        var retType = methodDesc.getReturnType();
        if (retType.getSort() == Type.OBJECT && pointerTypes.indexOf(retType.getClassName()) > 0) {
            if (retType.equals(Type.getType(NativePointer.class)))
                retType = Type.getType(NativePointer.OfVoid.class);

            var retLocal = method.newLocal(retType);
            method.newInstance(retType);
            method.dup();
            method.storeLocal(retLocal);
            method.swap();
            method.invokeConstructor(retType,
                                     new Method(
                                         "<init>",
                                         Type.VOID_TYPE,
                                         new Type[] { Type.getType(MemorySegment.class) }
                                     ));
            method.loadLocal(retLocal);
        }
    }

    private static void initCFuncField(final ClassWriter cw, final Type implType, 
        final GeneratorAdapter constructor, final int index, final Method method) {
        cw.visitField(
            Opcodes.ACC_PRIVATE,
            method.getName(),
            methodHandleType.getDescriptor(),
            null,
            null
        ).visitEnd();
        
        constructor.loadArg(0);
        constructor.push(index);
        constructor.arrayLoad(methodHandleType);
        constructor.loadThis();
        constructor.swap();
        constructor.putField(
            implType,
            method.getName(),
            methodHandleType
        );
    }

    private static <T> Class<T> defineClass(final Class<T> clazz, final byte[] bytecode) {
        //Utils.DebugPrintClassBytes(bytecode);
        Utils.DebugDumpclassFile(bytecode, clazz.getName()+"$Impl"); //TODO: remove dump once the generator is done
        var defClassLoader = clazz.getClassLoader();
        final var currentClassLoader = Thread.currentThread().getContextClassLoader();
        for (var clLoad = currentClassLoader; clLoad != null; clLoad = clLoad.getParent()) {
            if (clLoad == defClassLoader) {
                defClassLoader = currentClassLoader;
                break;
            }
        }
        final var classLoader = new ClassLoader(defClassLoader) {
            public Class<?> defineClass(byte[] classData) {
                return super.defineClass(null, classData, 0, classData.length);
            }
        };
        @SuppressWarnings("unchecked")
        final Class<T> implClass = (Class<T>) classLoader.defineClass(bytecode);
        return implClass;
    }
}

package ffmTests;

import java.io.File;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Utils {
    //private static final MemoryLayout nativeCharLayout = ValueLayout.JAVA_CHAR;//todo: find how a char is represented in native code

    private Utils() {
        assert false : "Cannot instantiate utility class";
    }

    public static StructLayout MakeCStruct(final MemoryLayout... members) {
        final List<MemoryLayout> memberList = Arrays.asList(members);
        
        int structSize = 0;
        for (int i = 0; i < memberList.size(); i++) {
            final MemoryLayout member = memberList.get(i);
            if (structSize % member.byteAlignment() != 0) {
                memberList.add(i, MemoryLayout.paddingLayout(
                        member.byteAlignment() - (structSize % member.byteAlignment())));
                structSize += memberList.get(i).byteSize();
                i++;
            }
            structSize += member.byteSize();
        }
        
        return MemoryLayout.structLayout(memberList.toArray(new MemoryLayout[0]));
    }
    public static <T> StructLayout MakeCStruct(final Class<T> clazz){
        final List<MemoryLayout> memberList = new ArrayList<>();
        for (final var field : clazz.getDeclaredFields())
            memberList.add(getValueLayout(field.getType(), false, false).withName(field.getName()));
        return MakeCStruct(memberList.toArray(new MemoryLayout[0]));
    }

    public static FunctionDescriptor MakeCFunctionDescriptor(final Method method) {
        final MemoryLayout returnLayout = getValueLayout(method.getReturnType(), true, false);
        final MemoryLayout[] paramLayouts = Arrays.stream(method.getParameterTypes())
            .map(paramType -> getValueLayout(paramType, false, true))
            .filter(t -> t != null)
            .toArray(MemoryLayout[]::new);

        return returnLayout == null
         ? FunctionDescriptor.ofVoid(paramLayouts)
         : FunctionDescriptor.of(
            returnLayout,
            paramLayouts
        );
    }

    private static MemoryLayout getValueLayout(final Class<?> type, final boolean allowVoid, boolean allowArena) {
        return switch (type) {
            case Class<?> c when c == void.class  -> { if (allowVoid) yield null;
                                                       else throw new IllegalArgumentException("type 'void' is not supported in this context."); }
            case Class<?> c when c == Arena.class -> { if (allowArena) yield null;
                                                       else throw new IllegalArgumentException("type 'Arena' is not supported in this context."); }
            case Class<?> c when c == byte.class     || c == Byte.class      -> ValueLayout.JAVA_BYTE;
            case Class<?> c when c == boolean.class  || c == Boolean.class   -> ValueLayout.JAVA_BOOLEAN;
            case Class<?> c when c == short.class    || c == Short.class     -> ValueLayout.JAVA_SHORT;
            case Class<?> c when c == char.class     || c == Character.class -> ValueLayout.JAVA_CHAR;
            case Class<?> c when c == int.class      || c == Integer.class   -> ValueLayout.JAVA_INT;
            case Class<?> c when c == long.class     || c == Long.class      -> ValueLayout.JAVA_LONG;
            case Class<?> c when c == float.class    || c == Float.class     -> ValueLayout.JAVA_FLOAT;
            case Class<?> c when c == double.class   || c == Double.class    -> ValueLayout.JAVA_DOUBLE;
            case Class<?> c when c == MemorySegment.class
                                 || NativePointer.class.isAssignableFrom(c)
                                 || c == String.class                        -> ValueLayout.ADDRESS;
            case Class<?> c when c.isArray()       -> throw new IllegalArgumentException("Arrays are not corrently supported");
            case Class<?> c -> MakeCStruct(c);
        };
    }

    // public static @Nullable UnionLayout MakeCUnion(@NonNull MemoryLayout @NonNull... members) {
    //     return MemoryLayout.unionLayout(members);
    // }

    static void DebugPrintClassBytes(final byte[] classBytes) {
        for (int i = 0; i < classBytes.length; i++) {
            if (i % 16 == 0)
                System.out.printf("0000%02X0: ", i / 16);
            System.out.printf(i % 2 == 0 ? "%02X" : "%02X ", classBytes[i]);
            if ((i + 1) % 16 == 0)
                System.out.println();
        }
        System.out.println();
        for (int i = 0; i < classBytes.length; i++)
            System.out.printf("%02X", classBytes[i]);
        System.out.println();
        System.out.println();
    }
    static void DebugDumpclassFile(final byte[] classBytes, final String className) {
        final var filePath = "debug_dump" + File.separator + System.currentTimeMillis() + "_" + className.replace('.', '_') + ".class";
        new File(filePath).delete();
        try (var fos = new java.io.FileOutputStream(filePath)) {
            fos.write(classBytes);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}

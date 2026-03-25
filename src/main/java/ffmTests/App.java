package ffmTests;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.MemoryLayout.PathElement;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jspecify.annotations.Nullable;

import ffmTests.NativePointer.OfLong;

public class App {
    public static void main(String[] args) {
        Path libPath = Path.of("libnative.so");

        try (InputStream link = App.class.getResourceAsStream("/libnative.so")) {
            Files.copy(link, libPath.toFile().getAbsoluteFile().toPath());
        } catch (Exception ignored) {}

        long setupTime = System.currentTimeMillis();
        Arena arena = Arena.ofAuto();
        var lib = SymbolLookup.libraryLookup(libPath, arena);
        ExternalLib externalLib;
        externalLib = NativeObject.get(ExternalLib.class, lib);
        try {
        } catch (Throwable e) {
            var sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            System.err.println(sw.toString().substring(0, Math.min(sw.getBuffer().length(), 1024)));
            System.err.println("Failed to load native library or find symbols.");
            return;
        }//ExternalLib.getInstance(libPath, arena);
        //externalLib = new App$ExternalLib$Impl(externalLib);

        long startTime = System.currentTimeMillis();
        var ptr = new OfLong(arena.allocateFrom(ValueLayout.JAVA_LONG, 42L));
        //boolean initSuccess = externalLib.initNative(1, "TestName", 3.14, 2.71, true, ptr);
        externalLib.setPtr(ptr);
        if (true){//initSuccess) {

            System.out.println("Native object initialized successfully.");
            System.out.println("ID: " + externalLib.getID());
            // System.out.println("Name: " + externalLib.getName());
            System.out.println("X: " + externalLib.getX());
            System.out.println("Y: " + externalLib.getY());
            System.out.println("Is Active: " + externalLib.getIsActive());
            System.out.println("Ptr Value: " + externalLib.getPtrValue());

            // Modify values
            externalLib.setID(2);
            // externalLib.setName("NewName");
            externalLib.setX(1.61);
            externalLib.setY(0.57);
            externalLib.setIsActive(false);
            externalLib.setPtrValue(84L);

            System.out.println("After modifications:");
            System.out.println("ID: " + externalLib.getID());
            // System.out.println("Name: " + externalLib.getName());
            System.out.println("X: " + externalLib.getX());
            System.out.println("Y: " + externalLib.getY());
            System.out.println("Is Active: " + externalLib.getIsActive());
            System.out.println("Ptr Value: " + externalLib.getPtrValue());

            System.out.println("Testing pointer:");
            System.out.println("modifing ptr externally...");
            externalLib.setPtrValue(128L);
            System.out.println("current local Ptr Value: " + ptr.get());
            System.out.println("modifing ptr locally...");
            ptr.set(256L);
            System.out.println("current external Ptr Value: " + externalLib.getPtrValue());

            System.out.println("Retrieving native data struct:");
            //ExternalLib.nativeData data = externalLib.getNativeData();
            // System.out.println("ID: " + data.id);
            // System.out.println("Name: " + data.name);
            // System.out.println("X: " + data.x);
            // System.out.println("Y: " + data.y);
            // System.out.println("Is Active: " + data.isActive);
            // System.out.println("Ptr Value: " + data.ptr.GetLong());

            System.out.println("Cleaning up native object.");
            externalLib.freeNative();

            long endTime = System.currentTimeMillis();
            System.out.println("Setup Time: " + (startTime - setupTime) + " ms");
            System.out.println("Execution Time: " + (endTime - startTime) + " ms");
        } else {
            System.err.println("Failed to initialize native object.");
        }
    }

    public static interface ExternalLib {
        static ExternalLib getInstance(Path libName, @Nullable Arena arena) {
            final Arena instanceArena = arena != null ? arena : Arena.ofAuto();
            
            SymbolLookup lib = SymbolLookup.libraryLookup(libName, instanceArena);
            Linker linker = Linker.nativeLinker();

            var initNativeMH = linker.downcallHandle(
                    lib.find("initNative").get(),
                    FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN,
                            ValueLayout.JAVA_INT,
                            ValueLayout.ADDRESS,
                            ValueLayout.JAVA_DOUBLE,
                            ValueLayout.JAVA_DOUBLE,
                            ValueLayout.JAVA_BOOLEAN,
                            ValueLayout.ADDRESS)
            );
            var freeNativeMH = linker.downcallHandle(
                lib.find("freeNative").get(),
                FunctionDescriptor.ofVoid()
            );
            var getNativeDataMH = linker.downcallHandle(
                lib.find("getNativeData").get(),
                FunctionDescriptor.of(nativeData.STRUCT_LAYOUT)
            );

            var getIDMH = linker.downcallHandle(
                lib.find("getID").get(),
                FunctionDescriptor.of(ValueLayout.JAVA_INT)
            );
            var getNameMH = linker.downcallHandle(
                    lib.find("getName").get(),
                    FunctionDescriptor.of(ValueLayout.ADDRESS)
            );
            var getXMH = linker.downcallHandle(
                    lib.find("getX").get(),
                    FunctionDescriptor.of(ValueLayout.JAVA_DOUBLE)
            );
            var getYMH = linker.downcallHandle(
                    lib.find("getY").get(),
                    FunctionDescriptor.of(ValueLayout.JAVA_DOUBLE)
            );
            var getIsActiveMH = linker.downcallHandle(
                    lib.find("getIsActive").get(),
                    FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN)
            );
            var getPtrMH = linker.downcallHandle(
                    lib.find("getPtr").get(),
                    FunctionDescriptor.of(ValueLayout.ADDRESS)
            );
            var getPtrValueMH = linker.downcallHandle(
                    lib.find("getPtrValue").get(),
                    FunctionDescriptor.of(ValueLayout.JAVA_LONG)
            );

            var setIDMH = linker.downcallHandle(
                    lib.find("setID").get(),
                    FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT)
            );
            var setNameMH = linker.downcallHandle(
                    lib.find("setName").get(),
                    FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
            );
            var setXMH = linker.downcallHandle(
                    lib.find("setX").get(),
                    FunctionDescriptor.ofVoid(ValueLayout.JAVA_DOUBLE)
            );
            var setYMH = linker.downcallHandle( 
                    lib.find("setY").get(),
                    FunctionDescriptor.ofVoid(ValueLayout.JAVA_DOUBLE)
            );
            var setIsActiveMH = linker.downcallHandle(
                    lib.find("setIsActive").get(),
                    FunctionDescriptor.ofVoid(ValueLayout.JAVA_BOOLEAN)
            );
            var setPtrMH = linker.downcallHandle(
                    lib.find("setPtr").get(),
                    FunctionDescriptor.ofVoid(ValueLayout.ADDRESS)
            );
            var setPtrValueMH = linker.downcallHandle(
                    lib.find("setPtrValue").get(),
                    FunctionDescriptor.ofVoid(ValueLayout.JAVA_LONG)
            );
            

            return new ExternalLib() {
                private final Arena arena = instanceArena;

                //@Override
                public boolean initNative(int id, String name, double x, double y, boolean isActive, LongRef ptr) {
                    try {
                        return (boolean) initNativeMH.invoke(
                                id,
                                arena.allocateFrom(name),
                                x,
                                y,
                                isActive,
                                ptr.getPointer()
                        );
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public void freeNative() {
                    try {
                        freeNativeMH.invoke();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                //@Override
                public nativeData getNativeData() {
                    MemorySegment rawData;
                    try {
                        rawData = (MemorySegment) getNativeDataMH.invoke((SegmentAllocator)arena);    
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }

                    nativeData data = new nativeData();
                    data.id                   = rawData.get(ValueLayout.JAVA_INT,     nativeData.STRUCT_LAYOUT__ID_OFFSET);
                    MemorySegment nameSegment = rawData.get(ValueLayout.ADDRESS,      nativeData.STRUCT_LAYOUT__NAME_OFFSET);
                    data.x                    = rawData.get(ValueLayout.JAVA_DOUBLE,  nativeData.STRUCT_LAYOUT__X_OFFSET);
                    data.y                    = rawData.get(ValueLayout.JAVA_DOUBLE,  nativeData.STRUCT_LAYOUT__Y_OFFSET);
                    data.isActive             = rawData.get(ValueLayout.JAVA_BOOLEAN, nativeData.STRUCT_LAYOUT__IS_ACTIVE_OFFSET);
                    MemorySegment ptrSegment  = rawData.get(ValueLayout.ADDRESS,      nativeData.STRUCT_LAYOUT__PTR_OFFSET);

                    data.name = nameSegment.reinterpret(Long.MAX_VALUE).getString(0);
                    data.ptr = new LongRef(ptrSegment);

                    return data;
                }

                @Override
                public int getID() {
                    try {
                        return (int) getIDMH.invoke();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                //@Override
                public String getName() {
                    MemorySegment namePtr;
                    try {
                        namePtr = (MemorySegment) getNameMH.invoke();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }

                    return namePtr.reinterpret(Long.MAX_VALUE).getString(0);
                }
                @Override
                public double getX() {
                    try {
                        return (double) getXMH.invoke();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public double getY() {
                    try {
                        return (double) getYMH.invoke();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public boolean getIsActive() {
                    try {
                        return (boolean) getIsActiveMH.invoke();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                //@Override
                public OfLong getPtr() {
                    MemorySegment ptr;
                    try {
                        ptr = (MemorySegment) getPtrMH.invoke();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }

                    return new OfLong(ptr);
                }
                @Override
                public long getPtrValue() {
                    try {
                        return (long) getPtrValueMH.invoke();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                
                @Override
                public void setID(int id) {
                    try {
                        setIDMH.invoke(id);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                //@Override
                public void setName(String name) {
                    MemorySegment nameptr = arena.allocateFrom(name);
                    try {
                        setNameMH.invoke(nameptr);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public void setX(double x) {
                    try {
                        setXMH.invoke(x);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public void setY(double y) {
                    try {
                        setYMH.invoke(y);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public void setIsActive(boolean isActive) {
                    try {
                        setIsActiveMH.invoke(isActive);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                //@Override
                public void setPtr(OfLong ptr) {
                    try {
                        setPtrMH.invoke(ptr.getSegment());
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public void setPtrValue(long value) {
                    try {
                        setPtrValueMH.invoke(value);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }

        //boolean initNative(int id, String name, double x, double y, boolean isActive, OfLong ptr);
        void freeNative();
        //nativeData getNativeData();
        
        int getID();
        //String getName();
        double getX();
        double getY();
        boolean getIsActive();
        OfLong getPtr();
        long getPtrValue();

        void setID(int id);
        //void setName(String name);
        void setX(double x);
        void setY(double y);
        void setIsActive(boolean isActive);
        void setPtr(OfLong ptr);
        void setPtrValue(long value);

        public static class nativeData {
            private static final boolean IS_64_BIT = true;//System.getProperty("sun.arch.data.model") == "64";

            public static final MemoryLayout STRUCT_LAYOUT =  MemoryLayout.structLayout(
                        ValueLayout.JAVA_INT.withName("id"),
                        MemoryLayout.paddingLayout(IS_64_BIT ? 4 : 0),
                        ValueLayout.ADDRESS.withName("name"),
                        ValueLayout.JAVA_DOUBLE.withName("x"),
                        ValueLayout.JAVA_DOUBLE.withName("y"),
                        ValueLayout.JAVA_BOOLEAN.withName("isActive"),
                        MemoryLayout.paddingLayout(IS_64_BIT ? 7 : 3),
                        ValueLayout.ADDRESS.withName("ptr")
                );
            public static final long STRUCT_LAYOUT__ID_OFFSET        = STRUCT_LAYOUT.byteOffset(PathElement.groupElement("id"));
            public static final long STRUCT_LAYOUT__NAME_OFFSET      = STRUCT_LAYOUT.byteOffset(PathElement.groupElement("name"));
            public static final long STRUCT_LAYOUT__X_OFFSET         = STRUCT_LAYOUT.byteOffset(PathElement.groupElement("x"));
            public static final long STRUCT_LAYOUT__Y_OFFSET         = STRUCT_LAYOUT.byteOffset(PathElement.groupElement("y"));
            public static final long STRUCT_LAYOUT__IS_ACTIVE_OFFSET = STRUCT_LAYOUT.byteOffset(PathElement.groupElement("isActive"));
            public static final long STRUCT_LAYOUT__PTR_OFFSET       = STRUCT_LAYOUT.byteOffset(PathElement.groupElement("ptr"));

            public int id;
            public String name;
            public double x;
            public double y;
            public boolean isActive;
            public LongRef ptr;
        }
    }

    public static class LongRef {
        private MemorySegment pointer;
        public LongRef(Arena arena, long value) {
            this(arena);
            pointer.set(ValueLayout.JAVA_LONG, 0, value);
        }
        public LongRef(Arena arena) {
            this.pointer = arena.allocate(ValueLayout.JAVA_LONG);
        }
        public LongRef(MemorySegment pointer) {
            this.pointer = pointer.reinterpret(ValueLayout.JAVA_LONG.byteSize());
        }
        public long GetLong() {
            return pointer.get(ValueLayout.JAVA_LONG, 0);
        }
        public void SetLong(long value) {
            pointer.set(ValueLayout.JAVA_LONG, 0, value);
        }
        public MemorySegment getPointer() {
            return pointer;
        }
    }
}

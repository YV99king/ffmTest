package ffmTests;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;


public sealed interface NativePointer<T> permits //TODO: change size of pointer buffer
    NativePointer.OfBoolean,
    NativePointer.OfByte,
    NativePointer.OfShort,
    NativePointer.OfInt,
    NativePointer.OfLong,
    NativePointer.OfFloat,
    NativePointer.OfDouble,
    NativePointer.OfAddress,
    NativePointer.OfVoid {

    long address();
    MemorySegment getSegment();
    T get();
    void set(T value);

    default void setType(Class<?> type) {
        throw new UnsupportedOperationException("This operation is not supported by this pointer type.");
    }
    
    public final class OfBoolean implements NativePointer<Boolean> {
        private final MemorySegment address;
    
        public OfBoolean(final long address) {
            this.address = MemorySegment.ofAddress(address);
        }
        public OfBoolean(MemorySegment address) {
            if (address.byteSize() == 0)
                address = address.reinterpret(1);
            this.address = address;
        }
    
        @Override
        public long address() {
            return address.address();
        }
        @Override
        public MemorySegment getSegment() {
            return address;
        }
        @Override
        public Boolean get() {
            return address.get(ValueLayout.JAVA_BYTE, 0) != 0;
        }
        @Override
        public void set(final Boolean value) {
            address.set(ValueLayout.JAVA_BYTE, 0, (byte)(value ? 1 : 0));
        }
    }
    
    public final class OfByte implements NativePointer<Byte> {
        private final MemorySegment address;
    
        public OfByte(final long address) {
            this.address = MemorySegment.ofAddress(address);
        }
        public OfByte(MemorySegment address) {
            if (address.byteSize() == 0)
                address = address.reinterpret(1);
            this.address = address;
        }
    
        @Override
        public long address() {
            return address.address();
        }
        @Override
        public MemorySegment getSegment() {
            return address;
        }
        @Override
        public Byte get() {
            return address.get(ValueLayout.JAVA_BYTE, 0);
        }
        @Override
        public void set(final Byte value) {
            address.set(ValueLayout.JAVA_BYTE, 0, value);
        }
    }
    
    public final class OfShort implements NativePointer<Short> {
        private final MemorySegment address;
    
        public OfShort(final long address) {
            this.address = MemorySegment.ofAddress(address);
        }
        public OfShort(MemorySegment address) {
            if (address.byteSize() == 0)
                address = address.reinterpret(1);
            this.address = address;
        }
    
        @Override
        public long address() {
            return address.address();
        }
        @Override
        public MemorySegment getSegment() {
            return address;
        }
        @Override
        public Short get() {
            return address.get(ValueLayout.JAVA_SHORT, 0);
        }
        @Override
        public void set(final Short value) {
            address.set(ValueLayout.JAVA_SHORT, 0, value);
        }
    }
    
    public final class OfInt implements NativePointer<Integer> {
        private final MemorySegment address;
        
        public OfInt(final long address) {
            this.address = MemorySegment.ofAddress(address);
        }
        public OfInt(MemorySegment address) {
            if (address.byteSize() == 0)
                address = address.reinterpret(1);
            this.address = address;
        }

        @Override
        public long address() {
            return address.address();
        }
        @Override
        public MemorySegment getSegment() {
            return address;
        }
        @Override
        public Integer get() {
            return address.get(ValueLayout.JAVA_INT, 0);
        }
        @Override
        public void set(final Integer value) {
            address.set(ValueLayout.JAVA_INT, 0, value);
        }
    }
    
    public final class OfLong implements NativePointer<Long> {
        private final MemorySegment address;

        public OfLong(final long address) {
            this.address = MemorySegment.ofAddress(address);
        }
        public OfLong(MemorySegment address) {
            if (address.byteSize() == 0)
                address = address.reinterpret(1);
            this.address = address;
        }

        @Override
        public long address() {
            return address.address();
        }
        @Override
        public MemorySegment getSegment() {
            return address;
        }
        @Override
        public Long get() {
            return address.get(ValueLayout.JAVA_LONG, 0);
        }
        @Override
        public void set(final Long value) {
            address.set(ValueLayout.JAVA_LONG, 0, value);
        }
    }
    
    public final class OfFloat implements NativePointer<Float> {
        private final MemorySegment address;

        public OfFloat(final long address) {
            this.address = MemorySegment.ofAddress(address);
        }
        public OfFloat(MemorySegment address) {
            if (address.byteSize() == 0)
                address = address.reinterpret(1);
            this.address = address;
        }

        @Override
        public long address() {
            return address.address();
        }
        @Override
        public MemorySegment getSegment() {
            return address;
        }
        @Override
        public Float get() {
            return address.get(ValueLayout.JAVA_FLOAT, 0);
        }
        @Override
        public void set(final Float value) {
            address.set(ValueLayout.JAVA_FLOAT, 0, value);
        }
    }
    
    public final class OfDouble implements NativePointer<Double> {
        private final MemorySegment address;

        public OfDouble(final long address) {
            this.address = MemorySegment.ofAddress(address);
        }
        public OfDouble(MemorySegment address) {
            if (address.byteSize() == 0)
                address = address.reinterpret(1);
            this.address = address;
        }

        @Override
        public long address() {
            return address.address();
        }
        @Override
        public MemorySegment getSegment() {
            return address;
        }
        @Override
        public Double get() {
            return address.get(ValueLayout.JAVA_DOUBLE, 0);
        }
        @Override
        public void set(final Double value) {
            address.set(ValueLayout.JAVA_DOUBLE, 0, value);
        }
    }
    
    public final class OfAddress implements NativePointer<MemorySegment> {
        private final MemorySegment address;

        public OfAddress(final long address) {
            this.address = MemorySegment.ofAddress(address);
        }
        public OfAddress(MemorySegment address) {
            if (address.byteSize() == 0)
                address = address.reinterpret(1);
            this.address = address;
        }

        @Override
        public long address() {
            return address.address();
        }
        @Override
        public MemorySegment getSegment() {
            return address;
        }
        @Override
        public MemorySegment get() {
            return address.get(ValueLayout.ADDRESS, 0);
        }
        @Override
        public void set(final MemorySegment value) {
            address.set(ValueLayout.ADDRESS, 0, value);
        }
    }

    final class OfVoid implements NativePointer<Object> {
        private NativePointer<?> inner;

        public OfVoid(final long address) {
            this.inner = new OfByte(address);
        }
        public OfVoid(final MemorySegment address) {
            this.inner = new OfByte(address);
        }
        @Override
        public long address() {
            return inner.address();
        }
        @Override
        public MemorySegment getSegment() {
            return inner.getSegment();
        }
        @Override
        public Object get() {
            return inner.get();
        }
        @Override
        public void set(Object value) {
            switch (value) {
                case Boolean b ->  ((OfBoolean)inner).set(b);
                case Byte b ->  ((OfByte)inner).set(b);
                case Short s ->  ((OfShort)inner).set(s);
                case Integer i ->  ((OfInt)inner).set(i);
                case Long l ->  ((OfLong)inner).set(l);
                case Float f ->  ((OfFloat)inner).set(f);
                case Double d -> ((OfDouble)inner).set(d);
                case MemorySegment m -> ((OfAddress)inner).set(m);
                default -> throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
            }
        }

        @Override
public void setType(Class<?> type) {
    if (type == boolean.class || type == Boolean.class) {
        inner = new OfBoolean(inner.address());
    } else if (type == byte.class || type == Byte.class) {
        inner = new OfByte(inner.address());
    } else if (type == short.class || type == Short.class) {
        inner = new OfShort(inner.address());
    } else if (type == int.class || type == Integer.class) {
        inner = new OfInt(inner.address());
    } else if (type == long.class || type == Long.class) {
        inner = new OfLong(inner.address());
    } else if (type == float.class || type == Float.class) {
        inner = new OfFloat(inner.address());
    } else if (type == double.class || type == Double.class) {
        inner = new OfDouble(inner.address());
    } else if (type == MemorySegment.class) {
        inner = new OfAddress(inner.address());
    } else {
        throw new IllegalArgumentException("Unsupported type: " + type.getName());
    }
}
    }
}

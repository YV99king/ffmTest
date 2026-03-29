package ffmTests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Annotation to specify the encoding of a string parameter when passing it to native code. This is used to determine how to convert the string to a byte array when passing it to native code.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.METHOD})
public @interface StringEncoding { //TODO: allow specifying string life time
    /**
     * The encoding of the string parameter. This is used to determine how to convert the string to a byte array when passing it to native code.
     * @return The encoding of the string parameter.
     */
    Encoding value() default Encoding.UTF8;

    /**
     * The endianess of the string parameter. This is used to determine how to convert the string to a byte array when passing it to native code. This is only relevant for UTF-16 and UTF-32 encodings, as they can have different byte orders.
     * @return The endianess of the string parameter.
     */
    Endianess endianess() default Endianess.PLATFORM_DEFAULT;

    /**
     * Enum to specify the encoding of a string parameter when passing it to native code. This is used to determine how to convert the string to a byte array when passing it to native code.
     */
    public static enum Encoding {
        /**
         * The ASCII encoding. This encoding can only represent characters in the ASCII character set (0-127). It is not recommended to use this encoding, as it cannot represent all characters and will throw an exception if a non-ASCII character is encountered.
         */
        ASCII("ASCII", StandardCharsets.US_ASCII),
        /**
         * The UTF-8 encoding. This encoding can represent all characters in the Unicode standard and is the most widely supported encoding. this is the default encoding for native calls ganarated using this library.
         */
        UTF8("UTF-8", StandardCharsets.UTF_8),
        /**
         * The UTF-16 encoding. This encoding can represent all characters in the Unicode standard. It is more recommended to use the UTF-8 encoding, as it is more memory efficient.
         */
        UTF16("UTF-16", StandardCharsets.UTF_16BE, StandardCharsets.UTF_16LE),
        /**
         * The UTF-32 encoding. This encoding can represent all characters in the Unicode standard. It is not recommended to use this encoding, as it is very memory inefficient.
         */
        UTF32("UTF-32", StandardCharsets.UTF_32BE, StandardCharsets.UTF_32LE);
        
        private final String encodingName;
        public final Charset encodingBig;
        private final Charset encodingLittle;

        private Encoding(String encodingName, Charset encoding) {
            this.encodingName = encodingName;
            this.encodingBig = encoding;
            this.encodingLittle = encoding;
        }
        private Encoding(String encodingName, Charset encodingBig, Charset encodingLittle) {
            this.encodingName = encodingName;
            this.encodingBig = encodingBig;
            this.encodingLittle = encodingLittle;
        }

        public Charset getEncoding(Endianess endianess) {
            return endianess.isBigEndian ? encodingBig : encodingLittle;
        }

        /**
         * Get the string representation of this encoding.
         * @return The string representation of this encoding.
         */
        public String getEncodingName() {
            return encodingName;
        }

        @Override
        public String toString() {
            return encodingName;
        }
    }

    /**
     * Enum to specify the endianess of a string parameter when passing it to native code. This is used to determine how to convert the string to a byte array when passing it to native code. This is only relevant for UTF-16 and UTF-32 encodings, as they can have different byte orders.
     */
    public enum Endianess {
        /**
         * Use the platform's default endianess. This is determined at runtime and will be either big-endian or little-endian depending on the platform (usually little-endian on modern systems). This is the default endianess for native calls generated using this library.
         */
        PLATFORM_DEFAULT(isPlatformBigEndian()),
        /**
         * Use little-endian byte order. In this byte order, the least significant byte is stored first. This is the most common byte order on modern systems.
         */
        LITTLE_ENDIAN(false),
        /**
         * Use big-endian byte order. In this byte order, the most significant byte is stored first. This byte order is less common on modern systems but is still used in some contexts (e.g., network protocols).
         */
        BIG_ENDIAN(true);

        final boolean isBigEndian;

        private static boolean isPlatformBigEndian() {
            return java.nio.ByteOrder.nativeOrder() == java.nio.ByteOrder.BIG_ENDIAN;
        }

        private Endianess(boolean isBigEndian) {
            this.isBigEndian = isBigEndian;
        }
    }
}

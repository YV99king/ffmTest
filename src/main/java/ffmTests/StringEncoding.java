package ffmTests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the encoding of a string parameter when passing it to native code. This is used to determine how to convert the string to a byte array when passing it to native code.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringEncoding {
    /**
     * The encoding of the string parameter. This is used to determine how to convert the string to a byte array when passing it to native code.
     * @return The encoding of the string parameter.
     */
    Encoding value() default Encoding.UTF8;

    /**
     * Enum to specify the encoding of a string parameter when passing it to native code. This is used to determine how to convert the string to a byte array when passing it to native code.
     */
    public static enum Encoding {
        /**
         * The ASCII encoding. This encoding can only represent characters in the ASCII character set (0-127). It is not recommended to use this encoding, as it cannot represent all characters and will throw an exception if a non-ASCII character is encountered.
         */
        ASCII("ASCII"),
        /**
         * The UTF-8 encoding. This encoding can represent all characters in the Unicode standard and is the most widely supported encoding. this is the default encoding for native calls ganarated using this library.
         */
        UTF8("UTF-8"),
        /**
         * The UTF-16 encoding. This encoding can represent all characters in the Unicode standard. It is more recommended to use the UTF-8 encoding, as it is more memory efficient.
         */
        UTF16("UTF-16"),
        /**
         * The UTF-32 encoding. This encoding can represent all characters in the Unicode standard. It is not recommended to use this encoding, as it is very memory inefficient.
         */
        UTF32("UTF-32");
        
        private final String encoding;

        Encoding(String encoding) {
            this.encoding = encoding;
        }

        /**
         * Get the string representation of this encoding.
         * @return The string representation of this encoding.
         */
        public String getEncoding() {
            return encoding;
        }

        @Override
        public String toString() {
            return encoding;
        }
    }
}

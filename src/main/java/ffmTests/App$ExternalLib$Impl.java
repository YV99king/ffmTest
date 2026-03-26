package ffmTests;

import ffmTests.App.ExternalLib;
import ffmTests.NativePointer.OfLong;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;

public class App$ExternalLib$Impl implements ExternalLib {
   private MethodHandle setName;
   private MethodHandle initNative;
   private MethodHandle getID;
   private MethodHandle setID;
   private MethodHandle getX;
   private MethodHandle getY;
   private MethodHandle getIsActive;
   private MethodHandle getPtrValue;
   private MethodHandle setX;
   private MethodHandle setY;
   private MethodHandle setIsActive;
   private MethodHandle setPtrValue;
   private MethodHandle freeNative;
   private MethodHandle getPtr;
   private MethodHandle setPtr;

   public App$ExternalLib$Impl(MethodHandle[] var1) {
      this.setName = var1[0];
      this.initNative = var1[1];
      this.getID = var1[2];
      this.setID = var1[3];
      this.getX = var1[4];
      this.getY = var1[5];
      this.getIsActive = var1[6];
      this.getPtrValue = var1[7];
      this.setX = var1[8];
      this.setY = var1[9];
      this.setIsActive = var1[10];
      this.setPtrValue = var1[11];
      this.freeNative = var1[12];
      this.getPtr = var1[13];
      this.setPtr = var1[14];
   }

   public void setName(String var1) {
      MethodHandle var10000 = this.setName;
      Arena var2 = Arena.ofAuto();
      MemorySegment var10001 = var2.allocateFrom(var1, StandardCharsets.UTF_8);

      try {
         var10000.invokeExact(var10001);
      } catch (Throwable var4) {
         throw new AssertionError(var4);
      }
   }

   public boolean initNative(int var1, String var2, double var3, double var5, boolean var7, OfLong var8) {
      MethodHandle var10000 = this.initNative;
      int var10001 = var1;
      Arena var9 = Arena.ofAuto();
      MemorySegment var10002 = var9.allocateFrom(var2, StandardCharsets.UTF_8);
      double var10003 = var3;
      double var10004 = var5;
      boolean var10005 = var7;
      MemorySegment var10006 = var8.getSegment();

      try {
         return (boolean) var10000.invokeExact(var10001, var10002, var10003, var10004, var10005, var10006);
      } catch (Throwable var11) {
         throw new AssertionError(var11);
      }
   }

   public int getID() {
      MethodHandle var10000 = this.getID;

      try {
         return (int) var10000.invokeExact();
      } catch (Throwable var2) {
         throw new AssertionError(var2);
      }
   }

   public void setID(int var1) {
      MethodHandle var10000 = this.setID;
      int var10001 = var1;

      try {
         var10000.invokeExact(var10001);
      } catch (Throwable var3) {
         throw new AssertionError(var3);
      }
   }

   public double getX() {
      MethodHandle var10000 = this.getX;

      try {
         return (double) var10000.invokeExact();
      } catch (Throwable var2) {
         throw new AssertionError(var2);
      }
   }

   public double getY() {
      MethodHandle var10000 = this.getY;

      try {
         return (double) var10000.invokeExact();
      } catch (Throwable var2) {
         throw new AssertionError(var2);
      }
   }

   public boolean getIsActive() {
      MethodHandle var10000 = this.getIsActive;

      try {
         return (boolean) var10000.invokeExact();
      } catch (Throwable var2) {
         throw new AssertionError(var2);
      }
   }

   public long getPtrValue() {
      MethodHandle var10000 = this.getPtrValue;

      try {
         return (long) var10000.invokeExact();
      } catch (Throwable var2) {
         throw new AssertionError(var2);
      }
   }

   public void setX(double var1) {
      MethodHandle var10000 = this.setX;
      double var10001 = var1;

      try {
         var10000.invokeExact(var10001);
      } catch (Throwable var4) {
         throw new AssertionError(var4);
      }
   }

   public void setY(double var1) {
      MethodHandle var10000 = this.setY;
      double var10001 = var1;

      try {
         var10000.invokeExact(var10001);
      } catch (Throwable var4) {
         throw new AssertionError(var4);
      }
   }

   public void setIsActive(boolean var1) {
      MethodHandle var10000 = this.setIsActive;
      boolean var10001 = var1;

      try {
         var10000.invokeExact(var10001);
      } catch (Throwable var3) {
         throw new AssertionError(var3);
      }
   }

   public void setPtrValue(long var1) {
      MethodHandle var10000 = this.setPtrValue;
      long var10001 = var1;

      try {
         var10000.invokeExact(var10001);
      } catch (Throwable var4) {
         throw new AssertionError(var4);
      }
   }

   public void freeNative() {
      MethodHandle var10000 = this.freeNative;

      try {
         var10000.invokeExact();
      } catch (Throwable var2) {
         throw new AssertionError(var2);
      }
   }

   public OfLong getPtr() {
      MethodHandle var10000 = this.getPtr;

      MemorySegment var4;
      try {
         var4 = (MemorySegment) var10000.invokeExact();
      } catch (Throwable var3) {
         throw new AssertionError(var3);
      }

      OfLong var1;
      var1 = new OfLong(var4);
      return var1;
   }

   public void setPtr(OfLong var1) {
      MethodHandle var10000 = this.setPtr;
      MemorySegment var10001 = var1.getSegment();

      try {
         var10000.invokeExact(var10001);
      } catch (Throwable var3) {
         throw new AssertionError(var3);
      }
   }
}
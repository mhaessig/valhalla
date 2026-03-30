/*
 * Copyright (c) 2025, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package compiler.valhalla.inlinetypes;

import jdk.internal.value.ValueClass;
import jdk.internal.vm.annotation.LooselyConsistentValue;
import jdk.internal.vm.annotation.NullRestricted;

import jdk.test.lib.Asserts;
import jdk.test.whitebox.WhiteBox;

/*
 * @test
 * @summary Test support for null markers in (flat) arrays.
 * @library /test/lib /
 * @requires (os.simpleArch == "x64" | os.simpleArch == "aarch64")
 * @enablePreview
 * @modules java.base/jdk.internal.value
 *          java.base/jdk.internal.vm.annotation
 * @build jdk.test.whitebox.WhiteBox
 * @run driver jdk.test.lib.helpers.ClassFileInstaller jdk.test.whitebox.WhiteBox
 * @run main/othervm -Xbootclasspath/a:. -XX:+UnlockDiagnosticVMOptions -XX:+WhiteBoxAPI
 *                   -Xbatch -XX:-UseNullableValueFlattening -XX:-UseAtomicValueFlattening -XX:+UseNonAtomicValueFlattening
 *                   -XX:TieredStopAtLevel=2
 *                   ${test.main.class}
 */

public class TestC1CheckFlatArray {

    private static final WhiteBox WHITEBOX = WhiteBox.getWhiteBox();
    private static final boolean UseArrayFlattening = WHITEBOX.getBooleanVMFlag("UseArrayFlattening");
    private static final boolean UseNullableValueFlattening = WHITEBOX.getBooleanVMFlag("UseNullableValueFlattening");
    private static final boolean UseNonAtomicValueFlattening = WHITEBOX.getBooleanVMFlag("UseNonAtomicValueFlattening");
    private static final boolean UseAtomicValueFlattening = WHITEBOX.getBooleanVMFlag("UseAtomicValueFlattening");

    // Is naturally atomic and has null-free, non-atomic, flat (1 bytes), null-free, atomic, flat (1 bytes) and nullable, atomic, flat (4 bytes) layouts
    @LooselyConsistentValue
    static value class OneByte {
        byte b;

        public OneByte(byte b) {
            this.b = b;
        }

        static final OneByte DEFAULT = new OneByte((byte)0);
    }

    // Has null-free, non-atomic, flat (2 bytes), null-free, atomic, flat (2 bytes) and nullable, atomic, flat (4 bytes) layouts
    @LooselyConsistentValue
    static value class TwoBytes {
        byte b1;
        byte b2;

        public TwoBytes(byte b1, byte b2) {
            this.b1 = b1;
            this.b2 = b2;
        }

        static final TwoBytes DEFAULT = new TwoBytes((byte)0, (byte)0);
    }

    // Has null-free, non-atomic, flat (4 bytes), null-free, atomic, flat (4 bytes) and nullable, atomic, flat (8 bytes) layouts
    @LooselyConsistentValue
    static value class TwoShorts {
        short s1;
        short s2;

        public TwoShorts(short s1, short s2) {
            this.s1 = s1;
            this.s2 = s2;
        }

        static final TwoShorts DEFAULT = new TwoShorts((short)0, (short)0);
    }

    // Has null-free, non-atomic flat (8 bytes) and null-free, atomic, flat (8 bytes) layouts
    @LooselyConsistentValue
    static value class TwoInts {
        int i1;
        int i2;

        public TwoInts(int i1, int i2) {
            this.i1 = i1;
            this.i2 = i2;
        }

        static final TwoInts DEFAULT = new TwoInts(0, 0);
    }

    // Has null-free, non-atomic flat (16 bytes) layout
    @LooselyConsistentValue
    static value class TwoLongs {
        long l1;
        long l2;

        public TwoLongs(int l1, int l2) {
            this.l1 = l1;
            this.l2 = l2;
        }

        static final TwoLongs DEFAULT = new TwoLongs(0, 0);
    }

    // Has null-free, non-atomic, flat (5 bytes), null-free, atomic, flat (8 bytes) and nullable, atomic, flat (8 bytes) layouts
    @LooselyConsistentValue
    static value class ByteAndOop {
        byte b;
        MyClass obj;

        public ByteAndOop(byte b, MyClass obj) {
            this.b = b;
            this.obj = obj;
        }

        static final ByteAndOop DEFAULT = new ByteAndOop((byte)0, null);
    }

    static class MyClass {
        int x;

        public MyClass(int x) {
            this.x = x;
        }
    }

    @LooselyConsistentValue
    static value class IntAndArrayOop {
        int i;
        MyClass[] array;

        public IntAndArrayOop(int i, MyClass[] array) {
            this.i = i;
            this.array = array;
        }

        static final IntAndArrayOop DEFAULT = new IntAndArrayOop(0, null);
    }

    public static void testWrite0(OneByte[] array, int i, OneByte val) {
        array[i] = val;
    }

    public static void testWrite1(TwoBytes[] array, int i, TwoBytes val) {
        array[i] = val;
    }

    public static void testWrite2(TwoShorts[] array, int i, TwoShorts val) {
        array[i] = val;
    }

    public static void testWrite3(TwoInts[] array, int i, TwoInts val) {
        array[i] = val;
    }

    public static void testWrite4(TwoLongs[] array, int i, TwoLongs val) {
        array[i] = val;
    }

    public static void testWrite5(ByteAndOop[] array, int i, ByteAndOop val) {
        array[i] = val;
    }

    public static void testWrite6(Object[] array, int i, Object val) {
        array[i] = val;
    }

    public static OneByte testRead0(OneByte[] array, int i) {
        return array[i];
    }

    public static TwoBytes testRead1(TwoBytes[] array, int i) {
        return array[i];
    }

    public static TwoShorts testRead2(TwoShorts[] array, int i) {
        return array[i];
    }

    public static TwoInts testRead3(TwoInts[] array, int i) {
        return array[i];
    }

    public static TwoLongs testRead4(TwoLongs[] array, int i) {
        return array[i];
    }

    public static ByteAndOop testRead5(ByteAndOop[] array, int i) {
        return array[i];
    }

    public static Object testRead6(Object[] array, int i) {
        return array[i];
    }

    static final OneByte CANARY0 = new OneByte((byte)42);

    public static void checkCanary0(OneByte[] array) {
        Asserts.assertEQ(array[0], CANARY0);
        Asserts.assertEQ(array[2], CANARY0);
    }

    static final TwoBytes CANARY1 = new TwoBytes((byte)42, (byte)42);

    public static void checkCanary1(TwoBytes[] array) {
        Asserts.assertEQ(array[0], CANARY1);
        Asserts.assertEQ(array[2], CANARY1);
    }

    static final TwoShorts CANARY2 = new TwoShorts((short)42, (short)42);

    public static void checkCanary2(TwoShorts[] array) {
        Asserts.assertEQ(array[0], CANARY2);
        Asserts.assertEQ(array[2], CANARY2);
    }

    static final TwoInts CANARY3 = new TwoInts(42, 42);

    public static void checkCanary3(TwoInts[] array) {
        Asserts.assertEQ(array[0], CANARY3);
        Asserts.assertEQ(array[2], CANARY3);
    }

    static final TwoLongs CANARY4 = new TwoLongs(42, 42);

    public static void checkCanary4(TwoLongs[] array) {
        Asserts.assertEQ(array[0], CANARY4);
        Asserts.assertEQ(array[2], CANARY4);
    }

    static final ByteAndOop CANARY5 = new ByteAndOop((byte)42, new MyClass(42));

    public static void checkCanary5(ByteAndOop[] array) {
        Asserts.assertEQ(array[0], CANARY5);
        Asserts.assertEQ(array[2], CANARY5);
    }

    public static TwoBytes[] testNullRestrictedArrayIntrinsic(int size, int idx, TwoBytes val) {
        TwoBytes[] nullFreeArray = (TwoBytes[])ValueClass.newNullRestrictedNonAtomicArray(TwoBytes.class, size, TwoBytes.DEFAULT);
        Asserts.assertEquals(ValueClass.isFlatArray(nullFreeArray), UseArrayFlattening && UseNonAtomicValueFlattening);
        Asserts.assertTrue(ValueClass.isNullRestrictedArray(nullFreeArray));
        Asserts.assertEquals(nullFreeArray[idx], TwoBytes.DEFAULT);
        testWrite1(nullFreeArray, idx, val);
        Asserts.assertEQ(testRead1(nullFreeArray, idx), val);
        return nullFreeArray;
    }

    // Non-final value to initialize null-restricted arrays
    static Object initVal1 = CANARY1;
    static TwoBytes initVal2 = CANARY1;

    public static TwoBytes[] testNullRestrictedArrayIntrinsicDynamic1(int size, int idx, TwoBytes val) {
        TwoBytes[] nullFreeArray = (TwoBytes[])ValueClass.newNullRestrictedNonAtomicArray(TwoBytes.class, size, initVal1);
        Asserts.assertEquals(ValueClass.isFlatArray(nullFreeArray), UseArrayFlattening && UseNonAtomicValueFlattening);
        Asserts.assertTrue(ValueClass.isNullRestrictedArray(nullFreeArray));
        Asserts.assertEquals(nullFreeArray[idx], CANARY1);
        testWrite1(nullFreeArray, idx, val);
        Asserts.assertEQ(testRead1(nullFreeArray, idx), val);
        return nullFreeArray;
    }

    public static TwoBytes[] testNullRestrictedArrayIntrinsicDynamic2(int size, int idx, TwoBytes val) {
        TwoBytes[] nullFreeArray = (TwoBytes[])ValueClass.newNullRestrictedNonAtomicArray(TwoBytes.class, size, initVal2);
        Asserts.assertEquals(ValueClass.isFlatArray(nullFreeArray), UseArrayFlattening && UseNonAtomicValueFlattening);
        Asserts.assertTrue(ValueClass.isNullRestrictedArray(nullFreeArray));
        Asserts.assertEquals(nullFreeArray[idx], CANARY1);
        testWrite1(nullFreeArray, idx, val);
        Asserts.assertEQ(testRead1(nullFreeArray, idx), val);
        return nullFreeArray;
    }

    static byte myByte = 0;

    public static TwoBytes[] testNullRestrictedArrayIntrinsicDynamic3(int size, int idx, TwoBytes val) {
        TwoBytes[] nullFreeArray = (TwoBytes[])ValueClass.newNullRestrictedNonAtomicArray(TwoBytes.class, size, new TwoBytes(++myByte, myByte));
        Asserts.assertEquals(ValueClass.isFlatArray(nullFreeArray), UseArrayFlattening && UseNonAtomicValueFlattening);
        Asserts.assertTrue(ValueClass.isNullRestrictedArray(nullFreeArray));
        Asserts.assertEquals(nullFreeArray[idx], new TwoBytes(myByte, myByte));
        testWrite1(nullFreeArray, idx, val);
        Asserts.assertEQ(testRead1(nullFreeArray, idx), val);
        return nullFreeArray;
    }

    public static TwoBytes[] testNullRestrictedAtomicArrayIntrinsic(int size, int idx, TwoBytes val) {
        TwoBytes[] nullFreeAtomicArray = (TwoBytes[])ValueClass.newNullRestrictedAtomicArray(TwoBytes.class, size, TwoBytes.DEFAULT);
        Asserts.assertEquals(ValueClass.isFlatArray(nullFreeAtomicArray), UseArrayFlattening && UseAtomicValueFlattening);
        Asserts.assertTrue(ValueClass.isNullRestrictedArray(nullFreeAtomicArray));
        Asserts.assertEquals(nullFreeAtomicArray[idx], TwoBytes.DEFAULT);
        testWrite1(nullFreeAtomicArray, idx, val);
        Asserts.assertEQ(testRead1(nullFreeAtomicArray, idx), val);
        return nullFreeAtomicArray;
    }

    public static TwoBytes[] testNullRestrictedAtomicArrayIntrinsicDynamic1(int size, int idx, TwoBytes val) {
        TwoBytes[] nullFreeAtomicArray = (TwoBytes[])ValueClass.newNullRestrictedAtomicArray(TwoBytes.class, size, initVal1);
        Asserts.assertEquals(ValueClass.isFlatArray(nullFreeAtomicArray), UseArrayFlattening && UseAtomicValueFlattening);
        Asserts.assertTrue(ValueClass.isNullRestrictedArray(nullFreeAtomicArray));
        Asserts.assertEquals(nullFreeAtomicArray[idx], CANARY1);
        testWrite1(nullFreeAtomicArray, idx, val);
        Asserts.assertEQ(testRead1(nullFreeAtomicArray, idx), val);
        return nullFreeAtomicArray;
    }

    public static TwoBytes[] testNullRestrictedAtomicArrayIntrinsicDynamic2(int size, int idx, TwoBytes val) {
        TwoBytes[] nullFreeAtomicArray = (TwoBytes[])ValueClass.newNullRestrictedAtomicArray(TwoBytes.class, size, initVal2);
        Asserts.assertEquals(ValueClass.isFlatArray(nullFreeAtomicArray), UseArrayFlattening && UseAtomicValueFlattening);
        Asserts.assertTrue(ValueClass.isNullRestrictedArray(nullFreeAtomicArray));
        Asserts.assertEquals(nullFreeAtomicArray[idx], CANARY1);
        testWrite1(nullFreeAtomicArray, idx, val);
        Asserts.assertEQ(testRead1(nullFreeAtomicArray, idx), val);
        return nullFreeAtomicArray;
    }

    public static TwoBytes[] testNullRestrictedAtomicArrayIntrinsicDynamic3(int size, int idx, TwoBytes val) {
        TwoBytes[] nullFreeAtomicArray = (TwoBytes[])ValueClass.newNullRestrictedAtomicArray(TwoBytes.class, size, new TwoBytes(++myByte, myByte));
        Asserts.assertEquals(ValueClass.isFlatArray(nullFreeAtomicArray), UseArrayFlattening && UseAtomicValueFlattening);
        Asserts.assertTrue(ValueClass.isNullRestrictedArray(nullFreeAtomicArray));
        Asserts.assertEquals(nullFreeAtomicArray[idx], new TwoBytes(myByte, myByte));
        testWrite1(nullFreeAtomicArray, idx, val);
        Asserts.assertEQ(testRead1(nullFreeAtomicArray, idx), val);
        return nullFreeAtomicArray;
    }

    public static TwoBytes[] testNullableAtomicArrayIntrinsic(int size, int idx, TwoBytes val) {
        TwoBytes[] nullableAtomicArray = (TwoBytes[])ValueClass.newNullableAtomicArray(TwoBytes.class, size);
        Asserts.assertEquals(ValueClass.isFlatArray(nullableAtomicArray), UseArrayFlattening && UseNullableValueFlattening);
        Asserts.assertFalse(ValueClass.isNullRestrictedArray(nullableAtomicArray));
        Asserts.assertEquals(nullableAtomicArray[idx], null);
        testWrite1(nullableAtomicArray, idx, val);
        Asserts.assertEQ(testRead1(nullableAtomicArray, idx), val);
        return nullableAtomicArray;
    }

    @LooselyConsistentValue
    static value class MyValueEmpty {
        static final MyValueEmpty DEFAULT = new MyValueEmpty();
    }

    @LooselyConsistentValue
    static value class ValueHolder1 {
        TwoBytes val;

        public ValueHolder1(TwoBytes val) {
            this.val = val;
        }

        static final ValueHolder1 DEFAULT = new ValueHolder1(null);
    }

    // Test support for replaced arrays
    public static void testScalarReplacement1(OneByte valNullFree, OneByte val, boolean trap) {
        OneByte[] nullFreeArray = (OneByte[])ValueClass.newNullRestrictedNonAtomicArray(OneByte.class, 2, OneByte.DEFAULT);
        nullFreeArray[0] = valNullFree;
        nullFreeArray[1] = new OneByte((byte)42);
        if (trap) {
            Asserts.assertEQ(nullFreeArray[0], valNullFree);
            Asserts.assertEQ(nullFreeArray[1], new OneByte((byte)42));
        }

        OneByte[] nullFreeAtomicArray = (OneByte[])ValueClass.newNullRestrictedAtomicArray(OneByte.class, 2, OneByte.DEFAULT);
        nullFreeAtomicArray[0] = valNullFree;
        nullFreeAtomicArray[1] = new OneByte((byte)42);
        if (trap) {
            Asserts.assertEQ(nullFreeAtomicArray[0], valNullFree);
            Asserts.assertEQ(nullFreeAtomicArray[1], new OneByte((byte)42));
        }

        OneByte[] nullableAtomicArray = (OneByte[])ValueClass.newNullableAtomicArray(OneByte.class, 4);
        nullableAtomicArray[0] = valNullFree;
        nullableAtomicArray[1] = val;
        nullableAtomicArray[2] = new OneByte((byte)42);
        nullableAtomicArray[3] = null;
        if (trap) {
            Asserts.assertEQ(nullableAtomicArray[0], valNullFree);
            Asserts.assertEQ(nullableAtomicArray[1], val);
            Asserts.assertEQ(nullableAtomicArray[2], new OneByte((byte)42));
            Asserts.assertEQ(nullableAtomicArray[3], null);
        }

        OneByte[] nullableArray = new OneByte[4];
        nullableArray[0] = valNullFree;
        nullableArray[1] = val;
        nullableArray[2] = new OneByte((byte)42);
        nullableArray[3] = null;
        if (trap) {
            Asserts.assertEQ(nullableArray[0], valNullFree);
            Asserts.assertEQ(nullableArray[1], val);
            Asserts.assertEQ(nullableArray[2], new OneByte((byte)42));
            Asserts.assertEQ(nullableArray[3], null);
        }
    }

    // Test support for scalar replaced arrays
    public static void testScalarReplacement2(TwoBytes val, boolean trap) {
        ValueHolder1[] nullFreeArray = (ValueHolder1[])ValueClass.newNullRestrictedNonAtomicArray(ValueHolder1.class, 1, ValueHolder1.DEFAULT);
        nullFreeArray[0] = new ValueHolder1(val);
        if (trap) {
            Asserts.assertEQ(nullFreeArray[0].val, val);
        }

        ValueHolder1[] nullFreeAtomicArray = (ValueHolder1[])ValueClass.newNullRestrictedAtomicArray(ValueHolder1.class, 1, ValueHolder1.DEFAULT);
        nullFreeAtomicArray[0] = new ValueHolder1(val);
        if (trap) {
            Asserts.assertEQ(nullFreeAtomicArray[0].val, val);
        }

        ValueHolder1[] nullableAtomicArray = (ValueHolder1[])ValueClass.newNullableAtomicArray(ValueHolder1.class, 2);
        nullableAtomicArray[0] = new ValueHolder1(val);
        nullableAtomicArray[1] = ValueHolder1.DEFAULT;
        if (trap) {
            Asserts.assertEQ(nullableAtomicArray[0].val, val);
            Asserts.assertEQ(nullableAtomicArray[1].val, null);
        }

        ValueHolder1[] nullableArray = new ValueHolder1[2];
        nullableArray[0] = new ValueHolder1(val);
        nullableArray[1] = ValueHolder1.DEFAULT;
        if (trap) {
            Asserts.assertEQ(nullableArray[0].val, val);
            Asserts.assertEQ(nullableArray[1].val, null);
        }
    }

    static void produceGarbage() {
        for (int i = 0; i < 100; ++i) {
            Object[] arrays = new Object[1024];
            for (int j = 0; j < arrays.length; j++) {
                arrays[j] = new int[1024];
            }
        }
        System.gc();
    }

    static TwoShorts[] array1 = (TwoShorts[])ValueClass.newNullRestrictedAtomicArray(TwoShorts.class, 1, TwoShorts.DEFAULT);
    static TwoShorts[] array2 = (TwoShorts[])ValueClass.newNullableAtomicArray(TwoShorts.class, 1);
    static {
        array2[0] = TwoShorts.DEFAULT;
    }
    static TwoShorts[] array3 = new TwoShorts[] { TwoShorts.DEFAULT };

    // Catches an issue with type speculation based on profiling
    public static void testProfiling() {
        array1[0] = new TwoShorts(array1[0].s1, (short)0);
        array2[0] = new TwoShorts(array2[0].s1, (short)0);
        array3[0] = new TwoShorts(array3[0].s1, (short)0);
    }

    static final OneByte[] NULL_FREE_ARRAY_0 = (OneByte[])ValueClass.newNullRestrictedNonAtomicArray(OneByte.class, 2, OneByte.DEFAULT);
    static final OneByte[] NULL_FREE_ATOMIC_ARRAY_0 = (OneByte[])ValueClass.newNullRestrictedAtomicArray(OneByte.class, 2, OneByte.DEFAULT);
    static final OneByte[] NULLABLE_ARRAY_0 = new OneByte[2];
    static final OneByte[] NULLABLE_ATOMIC_ARRAY_0 = (OneByte[])ValueClass.newNullableAtomicArray(OneByte.class, 2);

    static final TwoBytes[] NULL_FREE_ARRAY_1 = (TwoBytes[])ValueClass.newNullRestrictedNonAtomicArray(TwoBytes.class, 2, TwoBytes.DEFAULT);
    static final TwoBytes[] NULL_FREE_ATOMIC_ARRAY_1 = (TwoBytes[])ValueClass.newNullRestrictedAtomicArray(TwoBytes.class, 2, TwoBytes.DEFAULT);
    static final TwoBytes[] NULLABLE_ARRAY_1 = new TwoBytes[2];
    static final TwoBytes[] NULLABLE_ATOMIC_ARRAY_1 = (TwoBytes[])ValueClass.newNullableAtomicArray(TwoBytes.class, 2);

    static final TwoShorts[] NULL_FREE_ARRAY_2 = (TwoShorts[])ValueClass.newNullRestrictedNonAtomicArray(TwoShorts.class, 2, TwoShorts.DEFAULT);
    static final TwoShorts[] NULL_FREE_ATOMIC_ARRAY_2 = (TwoShorts[])ValueClass.newNullRestrictedAtomicArray(TwoShorts.class, 2, TwoShorts.DEFAULT);
    static final TwoShorts[] NULLABLE_ARRAY_2 = new TwoShorts[2];
    static final TwoShorts[] NULLABLE_ATOMIC_ARRAY_2 = (TwoShorts[])ValueClass.newNullableAtomicArray(TwoShorts.class, 2);

    static final TwoInts[] NULL_FREE_ARRAY_3 = (TwoInts[])ValueClass.newNullRestrictedNonAtomicArray(TwoInts.class, 1, TwoInts.DEFAULT);
    static final TwoInts[] NULL_FREE_ATOMIC_ARRAY_3 = (TwoInts[])ValueClass.newNullRestrictedAtomicArray(TwoInts.class, 1, TwoInts.DEFAULT);
    static final TwoInts[] NULLABLE_ARRAY_3 = new TwoInts[1];
    static final TwoInts[] NULLABLE_ATOMIC_ARRAY_3 = (TwoInts[])ValueClass.newNullableAtomicArray(TwoInts.class, 1);

    static final TwoLongs[] NULL_FREE_ARRAY_4 = (TwoLongs[])ValueClass.newNullRestrictedNonAtomicArray(TwoLongs.class, 1, TwoLongs.DEFAULT);
    static final TwoLongs[] NULL_FREE_ATOMIC_ARRAY_4 = (TwoLongs[])ValueClass.newNullRestrictedAtomicArray(TwoLongs.class, 1, TwoLongs.DEFAULT);
    static final TwoLongs[] NULLABLE_ARRAY_4 = new TwoLongs[1];
    static final TwoLongs[] NULLABLE_ATOMIC_ARRAY_4 = (TwoLongs[])ValueClass.newNullableAtomicArray(TwoLongs.class, 1);

    static final ByteAndOop[] NULL_FREE_ARRAY_5 = (ByteAndOop[])ValueClass.newNullRestrictedNonAtomicArray(ByteAndOop.class, 1, ByteAndOop.DEFAULT);
    static final ByteAndOop[] NULL_FREE_ATOMIC_ARRAY_5 = (ByteAndOop[])ValueClass.newNullRestrictedAtomicArray(ByteAndOop.class, 1, ByteAndOop.DEFAULT);
    static final ByteAndOop[] NULLABLE_ARRAY_5 = new ByteAndOop[1];
    static final ByteAndOop[] NULLABLE_ATOMIC_ARRAY_5 = (ByteAndOop[])ValueClass.newNullableAtomicArray(ByteAndOop.class, 1);

    // Test access to constant arrays
    public static void testConstantArrays(int i) {
        OneByte val0 = new OneByte((byte)i);
        Asserts.assertEQ(NULL_FREE_ARRAY_0[0], OneByte.DEFAULT);
        Asserts.assertEQ(NULL_FREE_ATOMIC_ARRAY_0[0], OneByte.DEFAULT);
        Asserts.assertEQ(NULLABLE_ARRAY_0[0], null);
        Asserts.assertEQ(NULLABLE_ATOMIC_ARRAY_0[0], null);

        try {
            NULL_FREE_ARRAY_0[0] = null;
            throw new RuntimeException("No NPE thrown");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            NULL_FREE_ATOMIC_ARRAY_0[0] = null;
            throw new RuntimeException("No NPE thrown");
        } catch (NullPointerException e) {
            // Expected
        }

        NULL_FREE_ARRAY_0[0] = val0;
        NULL_FREE_ATOMIC_ARRAY_0[0] = val0;
        NULLABLE_ARRAY_0[0] = val0;
        NULLABLE_ATOMIC_ARRAY_0[0] = val0;

        Asserts.assertEQ(NULL_FREE_ARRAY_0[0], val0);
        Asserts.assertEQ(NULL_FREE_ATOMIC_ARRAY_0[0], val0);
        Asserts.assertEQ(NULLABLE_ARRAY_0[0], val0);
        Asserts.assertEQ(NULLABLE_ATOMIC_ARRAY_0[0], val0);

        NULL_FREE_ARRAY_0[0] = OneByte.DEFAULT;
        NULL_FREE_ATOMIC_ARRAY_0[0] = OneByte.DEFAULT;
        NULLABLE_ARRAY_0[0] = null;
        NULLABLE_ATOMIC_ARRAY_0[0] = null;

        TwoBytes val1 = new TwoBytes((byte)i, (byte)i);
        Asserts.assertEQ(NULL_FREE_ARRAY_1[0], TwoBytes.DEFAULT);
        Asserts.assertEQ(NULL_FREE_ATOMIC_ARRAY_1[0], TwoBytes.DEFAULT);
        Asserts.assertEQ(NULLABLE_ARRAY_1[0], null);
        Asserts.assertEQ(NULLABLE_ATOMIC_ARRAY_1[0], null);

        try {
            NULL_FREE_ARRAY_1[0] = null;
            throw new RuntimeException("No NPE thrown");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            NULL_FREE_ATOMIC_ARRAY_1[0] = null;
            throw new RuntimeException("No NPE thrown");
        } catch (NullPointerException e) {
            // Expected
        }

        NULL_FREE_ARRAY_1[0] = val1;
        NULL_FREE_ATOMIC_ARRAY_1[0] = val1;
        NULLABLE_ARRAY_1[0] = val1;
        NULLABLE_ATOMIC_ARRAY_1[0] = val1;

        Asserts.assertEQ(NULL_FREE_ARRAY_1[0], val1);
        Asserts.assertEQ(NULL_FREE_ATOMIC_ARRAY_1[0], val1);
        Asserts.assertEQ(NULLABLE_ARRAY_1[0], val1);
        Asserts.assertEQ(NULLABLE_ATOMIC_ARRAY_1[0], val1);

        NULL_FREE_ARRAY_1[0] = TwoBytes.DEFAULT;
        NULL_FREE_ATOMIC_ARRAY_1[0] = TwoBytes.DEFAULT;
        NULLABLE_ARRAY_1[0] = null;
        NULLABLE_ATOMIC_ARRAY_1[0] = null;

        TwoShorts val2 = new TwoShorts((short)i, (short)i);
        Asserts.assertEQ(NULL_FREE_ARRAY_2[0], TwoShorts.DEFAULT);
        Asserts.assertEQ(NULL_FREE_ATOMIC_ARRAY_2[0], TwoShorts.DEFAULT);
        Asserts.assertEQ(NULLABLE_ARRAY_2[0], null);
        Asserts.assertEQ(NULLABLE_ATOMIC_ARRAY_2[0], null);

        try {
            NULL_FREE_ARRAY_2[0] = null;
            throw new RuntimeException("No NPE thrown");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            NULL_FREE_ATOMIC_ARRAY_2[0] = null;
            throw new RuntimeException("No NPE thrown");
        } catch (NullPointerException e) {
            // Expected
        }

        NULL_FREE_ARRAY_2[0] = val2;
        NULL_FREE_ATOMIC_ARRAY_2[0] = val2;
        NULLABLE_ARRAY_2[0] = val2;
        NULLABLE_ATOMIC_ARRAY_2[0] = val2;

        Asserts.assertEQ(NULL_FREE_ARRAY_2[0], val2);
        Asserts.assertEQ(NULL_FREE_ATOMIC_ARRAY_2[0], val2);
        Asserts.assertEQ(NULLABLE_ARRAY_2[0], val2);
        Asserts.assertEQ(NULLABLE_ATOMIC_ARRAY_2[0], val2);

        NULL_FREE_ARRAY_2[0] = TwoShorts.DEFAULT;
        NULL_FREE_ATOMIC_ARRAY_2[0] = TwoShorts.DEFAULT;
        NULLABLE_ARRAY_2[0] = null;
        NULLABLE_ATOMIC_ARRAY_2[0] = null;

        TwoInts val3 = new TwoInts(i, i);
        Asserts.assertEQ(NULL_FREE_ARRAY_3[0], TwoInts.DEFAULT);
        Asserts.assertEQ(NULL_FREE_ATOMIC_ARRAY_3[0], TwoInts.DEFAULT);
        Asserts.assertEQ(NULLABLE_ARRAY_3[0], null);
        Asserts.assertEQ(NULLABLE_ATOMIC_ARRAY_3[0], null);

        try {
            NULL_FREE_ARRAY_3[0] = null;
            throw new RuntimeException("No NPE thrown");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            NULL_FREE_ATOMIC_ARRAY_3[0] = null;
            throw new RuntimeException("No NPE thrown");
        } catch (NullPointerException e) {
            // Expected
        }

        NULL_FREE_ARRAY_3[0] = val3;
        NULL_FREE_ATOMIC_ARRAY_3[0] = val3;
        NULLABLE_ARRAY_3[0] = val3;
        NULLABLE_ATOMIC_ARRAY_3[0] = val3;

        Asserts.assertEQ(NULL_FREE_ARRAY_3[0], val3);
        Asserts.assertEQ(NULL_FREE_ATOMIC_ARRAY_3[0], val3);
        Asserts.assertEQ(NULLABLE_ARRAY_3[0], val3);
        Asserts.assertEQ(NULLABLE_ATOMIC_ARRAY_3[0], val3);

        NULL_FREE_ARRAY_3[0] = TwoInts.DEFAULT;
        NULL_FREE_ATOMIC_ARRAY_3[0] = TwoInts.DEFAULT;
        NULLABLE_ARRAY_3[0] = null;
        NULLABLE_ATOMIC_ARRAY_3[0] = null;

        TwoLongs val4 = new TwoLongs(i, i);
        Asserts.assertEQ(NULL_FREE_ARRAY_4[0], TwoLongs.DEFAULT);
        Asserts.assertEQ(NULL_FREE_ATOMIC_ARRAY_4[0], TwoLongs.DEFAULT);
        Asserts.assertEQ(NULLABLE_ARRAY_4[0], null);
        Asserts.assertEQ(NULLABLE_ATOMIC_ARRAY_4[0], null);

        try {
            NULL_FREE_ARRAY_4[0] = null;
            throw new RuntimeException("No NPE thrown");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            NULL_FREE_ATOMIC_ARRAY_4[0] = null;
            throw new RuntimeException("No NPE thrown");
        } catch (NullPointerException e) {
            // Expected
        }

        NULL_FREE_ARRAY_4[0] = val4;
        NULL_FREE_ATOMIC_ARRAY_4[0] = val4;
        NULLABLE_ARRAY_4[0] = val4;
        NULLABLE_ATOMIC_ARRAY_4[0] = val4;

        Asserts.assertEQ(NULL_FREE_ARRAY_4[0], val4);
        Asserts.assertEQ(NULL_FREE_ATOMIC_ARRAY_4[0], val4);
        Asserts.assertEQ(NULLABLE_ARRAY_4[0], val4);
        Asserts.assertEQ(NULLABLE_ATOMIC_ARRAY_4[0], val4);

        NULL_FREE_ARRAY_4[0] = TwoLongs.DEFAULT;
        NULL_FREE_ATOMIC_ARRAY_4[0] = TwoLongs.DEFAULT;
        NULLABLE_ARRAY_4[0] = null;
        NULLABLE_ATOMIC_ARRAY_4[0] = null;

        ByteAndOop val5 = new ByteAndOop((byte)i, new MyClass(i));
        Asserts.assertEQ(NULL_FREE_ARRAY_5[0], ByteAndOop.DEFAULT);
        Asserts.assertEQ(NULL_FREE_ATOMIC_ARRAY_5[0], ByteAndOop.DEFAULT);
        Asserts.assertEQ(NULLABLE_ARRAY_5[0], null);
        Asserts.assertEQ(NULLABLE_ATOMIC_ARRAY_5[0], null);

        try {
            NULL_FREE_ARRAY_5[0] = null;
            throw new RuntimeException("No NPE thrown");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            NULL_FREE_ATOMIC_ARRAY_5[0] = null;
            throw new RuntimeException("No NPE thrown");
        } catch (NullPointerException e) {
            // Expected
        }

        NULL_FREE_ARRAY_5[0] = val5;
        NULL_FREE_ATOMIC_ARRAY_5[0] = val5;
        NULLABLE_ARRAY_5[0] = val5;
        NULLABLE_ATOMIC_ARRAY_5[0] = val5;

        Asserts.assertEQ(NULL_FREE_ARRAY_5[0], val5);
        Asserts.assertEQ(NULL_FREE_ATOMIC_ARRAY_5[0], val5);
        Asserts.assertEQ(NULLABLE_ARRAY_5[0], val5);
        Asserts.assertEQ(NULLABLE_ATOMIC_ARRAY_5[0], val5);

        NULL_FREE_ARRAY_5[0] = ByteAndOop.DEFAULT;
        NULL_FREE_ATOMIC_ARRAY_5[0] = ByteAndOop.DEFAULT;
        NULLABLE_ARRAY_5[0] = null;
        NULLABLE_ATOMIC_ARRAY_5[0] = null;
    }

    // Test correct wiring of memory for flat accesses
    public static OneByte testMemoryEffects0() {
        NULLABLE_ARRAY_0[1] = CANARY0;
        NULLABLE_ATOMIC_ARRAY_0[1] = CANARY0;
        return NULLABLE_ARRAY_0[1];
    }

    public static TwoBytes testMemoryEffects1() {
        NULLABLE_ARRAY_1[1] = CANARY1;
        NULLABLE_ATOMIC_ARRAY_1[1] = CANARY1;
        return NULLABLE_ARRAY_1[1];
    }

    public static TwoShorts testMemoryEffects2() {
        NULLABLE_ARRAY_2[1] = CANARY2;
        NULLABLE_ATOMIC_ARRAY_2[1] = CANARY2;
        return NULLABLE_ARRAY_2[1];
    }

    static IntAndArrayOop[] nullableAtomicArray6 = (IntAndArrayOop[])ValueClass.newNullableAtomicArray(IntAndArrayOop.class, 3);
    static MyValueEmpty valEmpty = new MyValueEmpty();

    public static void test() {
        MyValueEmpty[] nullFreeAtomicArrayEmpty = (MyValueEmpty[])ValueClass.newNullRestrictedAtomicArray(MyValueEmpty.class, 3, MyValueEmpty.DEFAULT);
        MyValueEmpty[] nullableArrayEmpty = new MyValueEmpty[3];
        IntAndArrayOop val6 = new IntAndArrayOop(42, new MyClass[1]);
        nullableAtomicArray6[2] = val6;
        Asserts.assertEQ(nullableAtomicArray6[2], val6);

        nullFreeAtomicArrayEmpty[1] = valEmpty;
        nullableArrayEmpty[2] = valEmpty;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100_000; ++i) {
            test();
        }
    }
}


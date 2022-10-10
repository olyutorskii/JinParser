/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import java.io.UnsupportedEncodingException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 */
public class ShiftJisTest {

    public ShiftJisTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception{
    }

    @AfterClass
    public static void tearDownClass() throws Exception{
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of isShiftJIS1stByte method, of class ShiftJis.
     */
    @Test
    public void testIsShiftJIS1stByte(){
        System.out.println("isShiftJIS1stByte");

        for(int ival=0x00; ival <= 0x80; ival++){
            byte bval = (byte)ival;
            assertFalse(ShiftJis.isShiftJIS1stByte(bval));
        }

        for(int ival=0x81; ival <= 0x9f; ival++){
            byte bval = (byte)ival;
            assertTrue(ShiftJis.isShiftJIS1stByte(bval));
        }

        for(int ival=0xa0; ival <= 0xdf; ival++){
            byte bval = (byte)ival;
            assertFalse(ShiftJis.isShiftJIS1stByte(bval));
        }

        for(int ival=0xe0; ival <= 0xfc; ival++){
            byte bval = (byte)ival;
            assertTrue(ShiftJis.isShiftJIS1stByte(bval));
        }

        for(int ival=0xfd; ival <= 0xff; ival++){
            byte bval = (byte)ival;
            assertFalse(ShiftJis.isShiftJIS1stByte(bval));
        }

        byte[] array;
        try{
            // 全角スペース
            array = "\u3000".getBytes("Shift_JIS");
            assertTrue(ShiftJis.isShiftJIS1stByte(array[0]));
            // 「熙」
            array = "\u7199".getBytes("Shift_JIS");
            assertTrue(ShiftJis.isShiftJIS1stByte(array[0]));
            // 「＂」
            array = "\uff02".getBytes("Windows-31J");
            assertTrue(ShiftJis.isShiftJIS1stByte(array[0]));
        }catch(UnsupportedEncodingException e){
            fail();
        }

        return;
    }

    /**
     * Test of isShiftJIS2ndByte method, of class ShiftJis.
     */
    @Test
    public void testIsShiftJIS2ndByte(){
        System.out.println("isShiftJIS2ndByte");

        for(int ival=0x00; ival <= 0x3f; ival++){
            byte bval = (byte)ival;
            assertFalse(ShiftJis.isShiftJIS2ndByte(bval));
        }

        for(int ival=0x40; ival <= 0x7e; ival++){
            byte bval = (byte)ival;
            assertTrue(ShiftJis.isShiftJIS2ndByte(bval));
        }

        for(int ival=0x7f; ival <= 0x7f; ival++){
            byte bval = (byte)ival;
            assertFalse(ShiftJis.isShiftJIS2ndByte(bval));
        }

        for(int ival=0x80; ival <= 0xfc; ival++){
            byte bval = (byte)ival;
            assertTrue(ShiftJis.isShiftJIS2ndByte(bval));
        }

        for(int ival=0xfd; ival <= 0xff; ival++){
            byte bval = (byte)ival;
            assertFalse(ShiftJis.isShiftJIS2ndByte(bval));
        }

        byte[] array;
        try{
            // 全角スペース
            array = "\u3000".getBytes("Shift_JIS");
            assertTrue(ShiftJis.isShiftJIS2ndByte(array[1]));
            // 「熙」
            array = "\u7199".getBytes("Shift_JIS");
            assertTrue(ShiftJis.isShiftJIS2ndByte(array[1]));
            // 「＂」
            array = "\uff02".getBytes("Windows-31J");
            assertTrue(ShiftJis.isShiftJIS2ndByte(array[1]));
        }catch(UnsupportedEncodingException e){
            fail();
        }

        return;
    }

    /**
     * Test of isShiftJIS method, of class ShiftJis.
     */
    @Test
    public void testIsShiftJIS(){
        System.out.println("isShiftJIS");

        byte[] array;
        try{
            // 全角スペース
            array = "\u3000".getBytes("Shift_JIS");
            assertTrue(ShiftJis.isShiftJIS(array[0], array[1]));
            // 「熙」
            array = "\u7199".getBytes("Shift_JIS");
            assertTrue(ShiftJis.isShiftJIS(array[0], array[1]));
            // 「＂」
            array = "\uff02".getBytes("Windows-31J");
            assertTrue(ShiftJis.isShiftJIS(array[0], array[1]));
        }catch(UnsupportedEncodingException e){
            fail();
        }

        return;
    }

}

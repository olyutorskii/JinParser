/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 */
public class DecodeErrorInfoTest {

    public DecodeErrorInfoTest() {
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
     * Test of Constructor
     */
    @Test
    public void testConstructor(){
        System.out.println("Constructor");

        new DecodeErrorInfo(99, (byte)0xfe);
        new DecodeErrorInfo(999, (byte)0x87, (byte)0x40);

        new DecodeErrorInfo(0, (byte)0xfe);
        new DecodeErrorInfo(0, (byte)0x87, (byte)0x40);

        try{
            new DecodeErrorInfo(-1, (byte)0xfe);
            fail();
        }catch(IndexOutOfBoundsException e){
        }catch(Throwable e){
            fail();
        }

        try{
            new DecodeErrorInfo(-1, (byte)0x87, (byte)0x40);
            fail();
        }catch(IndexOutOfBoundsException e){
        }catch(Throwable e){
            fail();
        }

        return;
    }

    /**
     * Test of getCharPosition method, of class DecodeErrorInfo.
     */
    @Test
    public void testGetCharPosition(){
        System.out.println("getCharPosition");

        DecodeErrorInfo info;

        info = new DecodeErrorInfo(99, (byte)0xfe);
        assertEquals(99, info.getCharPosition());

        info = new DecodeErrorInfo(999, (byte)0x87, (byte)0x40);
        assertEquals(999, info.getCharPosition());

        return;
    }

    /**
     * Test of has2nd method, of class DecodeErrorInfo.
     */
    @Test
    public void testHas2nd(){
        System.out.println("has2nd");

        DecodeErrorInfo info;

        info = new DecodeErrorInfo(99, (byte)0xfe);
        assertFalse(info.has2nd());

        info = new DecodeErrorInfo(999, (byte)0x87, (byte)0x40);
        assertTrue(info.has2nd());

        return;
    }

    /**
     * Test of getRawByte1st method, of class DecodeErrorInfo.
     */
    @Test
    public void testGetRawByte1st(){
        System.out.println("getRawByte1st");

        DecodeErrorInfo info;

        info = new DecodeErrorInfo(99, (byte)0xfe);
        assertEquals((byte)0xfe, info.getRawByte1st());

        info = new DecodeErrorInfo(999, (byte)0x87, (byte)0x40);
        assertEquals((byte)0x87, info.getRawByte1st());

        return;
    }

    /**
     * Test of getRawByte2nd method, of class DecodeErrorInfo.
     */
    @Test
    public void testGetRawByte2nd(){
        System.out.println("getRawByte2nd");

        DecodeErrorInfo info;

        info = new DecodeErrorInfo(99, (byte)0xfe);
        try{
            info.getRawByte2nd();
            fail();
        }catch(IllegalStateException e){
        }catch(Throwable e){
            fail();
        }

        info = new DecodeErrorInfo(999, (byte)0x87, (byte)0x40);
        assertEquals((byte)0x40, info.getRawByte2nd());

        return;
    }

    /**
     * Test of createGappedClone method, of class DecodeErrorInfo.
     */
    @Test
    public void testCreateGappedClone(){
        System.out.println("createGappedClone");

        DecodeErrorInfo info;

        info = new DecodeErrorInfo(99, (byte)0xfe);
        info = info.createGappedClone(1);
        assertEquals(98, info.getCharPosition());

        info = new DecodeErrorInfo(999, (byte)0x87, (byte)0x40);
        info = info.createGappedClone(1);
        assertEquals(998, info.getCharPosition());

        info = new DecodeErrorInfo(99, (byte)0xfe);
        info = info.createGappedClone(-1);
        assertEquals(100, info.getCharPosition());

        info = new DecodeErrorInfo(999, (byte)0x87, (byte)0x40);
        info = info.createGappedClone(-1);
        assertEquals(1000, info.getCharPosition());

        info = new DecodeErrorInfo(99, (byte)0xfe);
        info = info.createGappedClone(99);
        assertEquals(0, info.getCharPosition());

        info = new DecodeErrorInfo(999, (byte)0x87, (byte)0x40);
        info = info.createGappedClone(999);
        assertEquals(0, info.getCharPosition());

        info = new DecodeErrorInfo(99, (byte)0xfe);
        try{
            info = info.createGappedClone(100);
            fail();
        }catch(IndexOutOfBoundsException e){
        }catch(Throwable e){
            fail();
        }

        info = new DecodeErrorInfo(999, (byte)0x87, (byte)0x40);
        try{
            info = info.createGappedClone(1000);
            fail();
        }catch(IndexOutOfBoundsException e){
        }catch(Throwable e){
            fail();
        }

        return;
    }

    /**
     * Test of toString method, of class DecodeErrorInfo.
     */
    @Test
    public void testToString(){
        System.out.println("toString");

        DecodeErrorInfo info;

        info = new DecodeErrorInfo(99, (byte)0x09);
        assertEquals("start:99 09", info.toString());

        info = new DecodeErrorInfo(99, (byte)0xfe);
        assertEquals("start:99 fe", info.toString());

        info = new DecodeErrorInfo(999, (byte)0x08, (byte)0x09);
        assertEquals("start:999 08:09", info.toString());

        info = new DecodeErrorInfo(999, (byte)0x87, (byte)0x40);
        assertEquals("start:999 87:40", info.toString());

        return;
    }

    /**
     * Test of POS_COMPARATOR.
     */
    @Test
    public void testCompare(){
        System.out.println("POS_COMPARATOR");

        DecodeErrorInfo info;
        DecodeErrorInfo other;

        info  = new DecodeErrorInfo(99, (byte)0xfe);
        other = new DecodeErrorInfo(98, (byte)0xfe);
        assertTrue(DecodeErrorInfo.POS_COMPARATOR.compare(info, other) > 0);

        info  = new DecodeErrorInfo(99, (byte)0xfe);
        other = new DecodeErrorInfo(100, (byte)0xfe);
        assertTrue(DecodeErrorInfo.POS_COMPARATOR.compare(info, other) < 0);

        info  = new DecodeErrorInfo(99, (byte)0xfe);
        other = new DecodeErrorInfo(99, (byte)0xfe);
        assertTrue(DecodeErrorInfo.POS_COMPARATOR.compare(info, other) == 0);

        assertTrue(DecodeErrorInfo.POS_COMPARATOR.compare(null, other) < 0);
        assertTrue(DecodeErrorInfo.POS_COMPARATOR.compare(info, null) > 0);
        assertTrue(DecodeErrorInfo.POS_COMPARATOR.compare(null, null) == 0);

        return;
    }

}

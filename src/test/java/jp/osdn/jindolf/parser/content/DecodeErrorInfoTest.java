/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.osdn.jindolf.parser.content;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 */
public class DecodeErrorInfoTest {

    private static final byte B0 = (byte)0x00;


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

        DecodeErrorInfo info;

        info = new DecodeErrorInfo(99, (byte)0xfe);
        assertNotNull(info);

        info = new DecodeErrorInfo(999, (byte)0x87, (byte)0x40);
        assertNotNull(info);

        info = new DecodeErrorInfo(0, (byte)0xfe);
        assertNotNull(info);

        info = new DecodeErrorInfo(0, (byte)0x87, (byte)0x40);
        assertNotNull(info);

        try{
            info = new DecodeErrorInfo(-1, (byte)0xfe);
            fail();
            info.hashCode();
        }catch(IndexOutOfBoundsException e){
            // GOOD
        }

        try{
            info = new DecodeErrorInfo(-1, (byte)0x87, (byte)0x40);
            fail();
            info.hashCode();
        }catch(IndexOutOfBoundsException e){
            // GOOD
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

        info = new DecodeErrorInfo(0, (byte)0xfe);
        assertEquals(0, info.getCharPosition());

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
            // GOOD
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
            info.hashCode();
        }catch(IndexOutOfBoundsException e){
            // GOOD
        }

        info = new DecodeErrorInfo(999, (byte)0x87, (byte)0x40);
        try{
            info = info.createGappedClone(1000);
            fail();
            info.hashCode();
        }catch(IndexOutOfBoundsException e){
            // GOOD
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
     * Test of lsearchErrorIndex method, of class DecodedContent.
     */
    @Test
    public void testLsearchErrorIndex(){
        System.out.println("lsearchErrorIndex");

        List<DecodeErrorInfo> errList;
        int result;

        errList = new ArrayList<>();

        errList.clear();
        result = DecodeErrorInfo.lsearchErrorIndex(errList, -1);
        assertEquals(0, result);
        result = DecodeErrorInfo.lsearchErrorIndex(errList, 0);
        assertEquals(0, result);
        result = DecodeErrorInfo.lsearchErrorIndex(errList, 10);
        assertEquals(0, result);

        errList.clear();
        errList.add(new DecodeErrorInfo(10, B0));
        result = DecodeErrorInfo.lsearchErrorIndex(errList, 9);
        assertEquals(0, result);
        result = DecodeErrorInfo.lsearchErrorIndex(errList, 10);
        assertEquals(0, result);
        result = DecodeErrorInfo.lsearchErrorIndex(errList, 11);
        assertEquals(1, result);

        errList.clear();
        errList.add(new DecodeErrorInfo(10, B0));
        errList.add(new DecodeErrorInfo(20, B0));
        errList.add(new DecodeErrorInfo(30, B0));
        errList.add(new DecodeErrorInfo(40, B0));
        errList.add(new DecodeErrorInfo(50, B0));
        result = DecodeErrorInfo.lsearchErrorIndex(errList, 9);
        assertEquals(0, result);
        result = DecodeErrorInfo.lsearchErrorIndex(errList, 10);
        assertEquals(0, result);
        result = DecodeErrorInfo.lsearchErrorIndex(errList, 11);
        assertEquals(1, result);
        result = DecodeErrorInfo.lsearchErrorIndex(errList, 29);
        assertEquals(2, result);
        result = DecodeErrorInfo.lsearchErrorIndex(errList, 30);
        assertEquals(2, result);
        result = DecodeErrorInfo.lsearchErrorIndex(errList, 31);
        assertEquals(3, result);
        result = DecodeErrorInfo.lsearchErrorIndex(errList, 49);
        assertEquals(4, result);
        result = DecodeErrorInfo.lsearchErrorIndex(errList, 50);
        assertEquals(4, result);
        result = DecodeErrorInfo.lsearchErrorIndex(errList, 51);
        assertEquals(5, result);
        result = DecodeErrorInfo.lsearchErrorIndex(errList, 1000);
        assertEquals(5, result);

        return;
     }

    /**
     * Test of bsearchErrorIndex method, of class DecodedContent.
     */
    @Test
    public void testBsearchErrorIndex(){
        System.out.println("bsearchErrorIndex");

        List<DecodeErrorInfo> errList;
        int result;

        errList = new ArrayList<>();

        errList.clear();
        result = DecodeErrorInfo.bsearchErrorIndex(errList, -1);
        assertEquals(0, result);
        result = DecodeErrorInfo.bsearchErrorIndex(errList, 0);
        assertEquals(0, result);
        result = DecodeErrorInfo.bsearchErrorIndex(errList, 10);
        assertEquals(0, result);

        errList.clear();
        errList.add(new DecodeErrorInfo(10, B0));
        result = DecodeErrorInfo.bsearchErrorIndex(errList, 9);
        assertEquals(0, result);
        result = DecodeErrorInfo.bsearchErrorIndex(errList, 10);
        assertEquals(0, result);
        result = DecodeErrorInfo.bsearchErrorIndex(errList, 11);
        assertEquals(1, result);

        errList.clear();
        errList.add(new DecodeErrorInfo(10, B0));
        errList.add(new DecodeErrorInfo(20, B0));
        errList.add(new DecodeErrorInfo(30, B0));
        errList.add(new DecodeErrorInfo(40, B0));
        errList.add(new DecodeErrorInfo(50, B0));
        result = DecodeErrorInfo.bsearchErrorIndex(errList, 9);
        assertEquals(0, result);
        result = DecodeErrorInfo.bsearchErrorIndex(errList, 10);
        assertEquals(0, result);
        result = DecodeErrorInfo.bsearchErrorIndex(errList, 11);
        assertEquals(1, result);
        result = DecodeErrorInfo.bsearchErrorIndex(errList, 29);
        assertEquals(2, result);
        result = DecodeErrorInfo.bsearchErrorIndex(errList, 30);
        assertEquals(2, result);
        result = DecodeErrorInfo.bsearchErrorIndex(errList, 31);
        assertEquals(3, result);
        result = DecodeErrorInfo.bsearchErrorIndex(errList, 49);
        assertEquals(4, result);
        result = DecodeErrorInfo.bsearchErrorIndex(errList, 50);
        assertEquals(4, result);
        result = DecodeErrorInfo.bsearchErrorIndex(errList, 51);
        assertEquals(5, result);
        result = DecodeErrorInfo.bsearchErrorIndex(errList, 1000);
        assertEquals(5, result);

        return;
    }

    /**
     * Test of searchErrorIndex method, of class DecodedContent.
     */
    @Test
    public void testSearchErrorIndex(){
        System.out.println("searchErrorIndex");

        List<DecodeErrorInfo> errList;
        int result;

        errList = new ArrayList<>();

        errList.clear();
        for(int pos = 0; pos < 150; pos += 10){
            errList.add(new DecodeErrorInfo(pos, B0));
        }
        assertTrue(errList.size() < DecodeErrorInfo.BSEARCH_THRESHOLD);
        result = DecodeErrorInfo.searchErrorIndex(errList, 89);
        assertEquals(9, result);
        result = DecodeErrorInfo.searchErrorIndex(errList, 90);
        assertEquals(9, result);
        result = DecodeErrorInfo.searchErrorIndex(errList, 91);
        assertEquals(10, result);

        errList.clear();
        for(int pos = 0; pos < 1500; pos += 10){
            errList.add(new DecodeErrorInfo(pos, B0));
        }
        assertTrue(errList.size() >= DecodeErrorInfo.BSEARCH_THRESHOLD);
        result = DecodeErrorInfo.searchErrorIndex(errList, 899);
        assertEquals(90, result);
        result = DecodeErrorInfo.searchErrorIndex(errList, 900);
        assertEquals(90, result);
        result = DecodeErrorInfo.searchErrorIndex(errList, 901);
        assertEquals(91, result);

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

        info  = new DecodeErrorInfo(99, (byte)0xf1);
        other = new DecodeErrorInfo(98, (byte)0xf2);
        assertTrue(DecodeErrorInfo.POS_COMPARATOR.compare(info, other) > 0);

        info  = new DecodeErrorInfo(99, (byte)0xf3);
        other = new DecodeErrorInfo(100, (byte)0xf4);
        assertTrue(DecodeErrorInfo.POS_COMPARATOR.compare(info, other) < 0);

        info  = new DecodeErrorInfo(99, (byte)0xf5);
        other = new DecodeErrorInfo(99, (byte)0xf6);
        assertTrue(DecodeErrorInfo.POS_COMPARATOR.compare(info, other) == 0);

        assertTrue(DecodeErrorInfo.POS_COMPARATOR.compare(null, other) < 0);
        assertTrue(DecodeErrorInfo.POS_COMPARATOR.compare(info, null) > 0);
        assertTrue(DecodeErrorInfo.POS_COMPARATOR.compare(null, null) == 0);

        return;
    }

}

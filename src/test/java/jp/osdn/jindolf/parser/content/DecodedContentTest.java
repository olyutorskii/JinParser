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
public class DecodedContentTest {

    public DecodedContentTest() {
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
     * Test of Constructor, of class DecodedContent.
     */
    @Test
    public void testConstructor(){
        System.out.println("Constructor");

        DecodedContent content;

        content = new DecodedContent();
        assertEquals("", content.toString());

        content = new DecodedContent("abc");
        assertEquals("abc", content.toString());

        content = new DecodedContent(128);
        assertEquals("", content.toString());

        content = new DecodedContent(0);
        assertEquals("", content.toString());
        content.append("abc");
        assertEquals("abc", content.toString());

        try{
            Object o = new DecodedContent(-1);
            fail();
        }catch(NegativeArraySizeException e){
        }catch(Throwable e){
            fail();
        }

        return;
    }

    /**
     * Test of init method, of class DecodedContent.
     */
    @Test
    public void testInit(){
        System.out.println("init");

        DecodedContent content;

        content = new DecodedContent();
        content.append("abc");
        content.addDecodeError((byte)0xff);
        content.append("def");
        assertEquals("abc?def", content.toString());
        assertEquals(1, content.getDecodeErrorList().size());

        content.init();
        assertEquals("", content.toString());
        assertEquals(0, content.getDecodeErrorList().size());

        content.append('X');
        assertEquals("X", content.toString());

        return;
    }

    /**
     * Test of hasDecodeError method, of class DecodedContent.
     */
    @Test
    public void testHasDecodeError(){
        System.out.println("hasDecodeError");

        DecodedContent content;

        content = new DecodedContent();
        assertFalse(content.hasDecodeError());

        content.append("a");
        assertFalse(content.hasDecodeError());

        content.addDecodeError((byte)0xff);
        assertTrue(content.hasDecodeError());

        content.append("b");
        assertTrue(content.hasDecodeError());

        content.init();
        assertFalse(content.hasDecodeError());

        content.append("c");
        assertFalse(content.hasDecodeError());

        content = new DecodedContent();
        List<DecodeErrorInfo> list = content.getDecodeErrorList();
        assertEquals(0, list.size());
        assertFalse(content.hasDecodeError());

        return;
    }

    /**
     * Test of getDecodeErrorList method, of class DecodedContent.
     */
    @Test
    public void testGetDecodeErrorList(){
        System.out.println("getDecodeErrorList");

        DecodedContent content;
        List<DecodeErrorInfo> list;

        content = new DecodedContent();
        list = content.getDecodeErrorList();
        assertEquals(0, list.size());

        content.append("abc");
        list = content.getDecodeErrorList();
        assertEquals(0, list.size());

        content.addDecodeError((byte)0xff);
        list = content.getDecodeErrorList();
        assertEquals(1, list.size());

        content.append("def");
        list = content.getDecodeErrorList();
        assertEquals(1, list.size());

        content.addDecodeError((byte)0x03, (byte)0x04);
        list = content.getDecodeErrorList();
        assertEquals(2, list.size());

        return;
    }

    /**
     * Test of getRawContent method, of class DecodedContent.
     */
    @Test
    public void testGetRawContent(){
        System.out.println("getRawContent");

        DecodedContent content;

        content = new DecodedContent();
        assertEquals("", content.getRawContent().toString());

        content.append("a");
        assertEquals("a", content.getRawContent().toString());

        content.addDecodeError((byte)0xff);
        assertEquals("a?", content.getRawContent().toString());

        content.append("b");
        assertEquals("a?b", content.getRawContent().toString());

        assertEquals(content.toString(), content.getRawContent().toString());

        return;
    }

    /**
     * Test of charAt method, of class DecodedContent.
     */
    @Test
    public void testCharAt(){
        System.out.println("charAt");

        DecodedContent content;

        content = new DecodedContent();
        content.append("12345");
        assertEquals('1', content.charAt(0));
        assertEquals('3', content.charAt(2));
        assertEquals('5', content.charAt(4));

        try{
            content.charAt(-1);
            fail();
        }catch(IndexOutOfBoundsException e){
        }catch(Throwable e){
            fail();
        }

        try{
            content.charAt(5);
            fail();
        }catch(IndexOutOfBoundsException e){
        }catch(Throwable e){
            fail();
        }

        return;
    }

    /**
     * Test of length method, of class DecodedContent.
     */
    @Test
    public void testLength(){
        System.out.println("length");

        DecodedContent content;

        content = new DecodedContent();
        assertEquals(0, content.length());

        content.append("12345");
        assertEquals(5, content.length());

        content.addDecodeError((byte)0xff);
        assertEquals(6, content.length());

        content.init();
        assertEquals(0, content.length());

        return;
    }

    /**
     * Test of subSequence method, of class DecodedContent.
     */
    @Test
    public void testSubSequence(){
        System.out.println("subSequence");

        DecodedContent content;

        content = new DecodedContent();

        content.append("12345");
        assertEquals("234", content.subSequence(1, 4).toString());

        try{
            content.subSequence(-1, 4);
            fail();
        }catch(IndexOutOfBoundsException e){
        }catch(Throwable e){
            fail();
        }

        try{
            content.subSequence(1, 6);
            fail();
        }catch(IndexOutOfBoundsException e){
        }catch(Throwable e){
            fail();
        }

        try{
            content.subSequence(4, 1);
            fail();
        }catch(IndexOutOfBoundsException e){
        }catch(Throwable e){
            fail();
        }

        return;
    }

    /**
     * Test of subContent method, of class DecodedContent.
     */
    @Test
    public void testSubContent(){
        System.out.println("subContent");

        DecodedContent content;

        content = new DecodedContent();

        content.append("12345");
        assertEquals("234", content.subContent(1, 4).toString());

        try{
            content.subContent(-1, 4);
            fail();
        }catch(IndexOutOfBoundsException e){
        }catch(Throwable e){
            fail();
        }

        try{
            content.subContent(1, 6);
            fail();
        }catch(IndexOutOfBoundsException e){
        }catch(Throwable e){
            fail();
        }

        try{
            content.subContent(4, 1);
            fail();
        }catch(IndexOutOfBoundsException e){
        }catch(Throwable e){
            fail();
        }

        content = new DecodedContent();
        content.append("ab");
        content.addDecodeError((byte)0x01);
        content.append("de");
        content = content.subContent(1,4);
        assertEquals("b?d", content.toString());

        List<DecodeErrorInfo> list = content.getDecodeErrorList();
        assertEquals(1, list.size());
        assertEquals((byte)0x01, list.get(0).getRawByte1st());

        return;
    }

    /**
     * Test of append method, of class DecodedContent.
     */
    @Test
    public void testAppend_char(){
        System.out.println("append");

        DecodedContent content;

        content = new DecodedContent();
        content.append('a');
        assertEquals("a", content.toString());

        return;
    }

    /**
     * Test of append method, of class DecodedContent.
     */
    @Test
    public void testAppend_CharSequence(){
        System.out.println("append");

        DecodedContent content;

        content = new DecodedContent();
        CharSequence seq = "abc";
        content.append(seq);
        assertEquals("abc", content.toString());

        return;
    }

    /**
     * Test of append method, of class DecodedContent.
     */
    @Test
    public void testAppend_3args_1(){
        System.out.println("append");

        DecodedContent content;

        content = new DecodedContent();
        content.append("abc");
        assertEquals("abc", content.toString());

        CharSequence seq = "12345";
        content.append(seq, 1, 4);
        assertEquals("abc234", content.toString());

        return;
    }

    /**
     * Test of append method, of class DecodedContent.
     */
    @Test
    public void testAppend_3args_2(){
        System.out.println("append");

        DecodedContent content;

        content = new DecodedContent();
        content.append("abc");

        DecodedContent other;
        other = new DecodedContent();
        other.append("12345");

        content.append(other, 1, 4);
        assertEquals("abc234", content.toString());

        content = new DecodedContent();
        content.append("abc");

        other = new DecodedContent();
        other.addDecodeError((byte)0x01);
        other.addDecodeError((byte)0x02);
        other.addDecodeError((byte)0x03);
        other.addDecodeError((byte)0x04);
        other.addDecodeError((byte)0x05);

        content.append(other, 1, 4);
        assertEquals("abc???", content.toString());

        List<DecodeErrorInfo> list = content.getDecodeErrorList();
        assertEquals(3, list.size());

        DecodeErrorInfo info;

        info = list.get(0);
        assertEquals(3, info.getCharPosition());
        assertEquals((byte)0x02, info.getRawByte1st());
        info = list.get(1);
        assertEquals(4, info.getCharPosition());
        assertEquals((byte)0x03, info.getRawByte1st());
        info = list.get(2);
        assertEquals(5, info.getCharPosition());
        assertEquals((byte)0x04, info.getRawByte1st());

        return;
    }

    /**
     * Test of append method, of class DecodedContent.
     */
    @Test
    public void testAppend_3args_3(){
        System.out.println("append");

        DecodedContent content;

        content = new DecodedContent();
        content.append("abc");
        assertEquals("abc", content.toString());

        char[] seq = {'1','2','3','4','5',};
        content.append(seq, 1, 3);
        assertEquals("abc234", content.toString());

        return;
    }

    /**
     * Test of addDecodeError method, of class DecodedContent.
     */
    @Test
    public void testAddDecodeError_byte(){
        System.out.println("addDecodeError");

        DecodedContent content;

        content = new DecodedContent();
        content.append("abc");
        content.addDecodeError((byte)0xfe);
        content.append("def");
        content.addDecodeError((byte)0xff);

        assertEquals("abc?def?", content.toString());
        List<DecodeErrorInfo> list = content.getDecodeErrorList();
        assertEquals(2, list.size());

        DecodeErrorInfo info;

        info = list.get(0);
        assertEquals(3, list.get(0).getCharPosition());
        assertFalse(info.has2nd());
        assertEquals((byte)0xfe, info.getRawByte1st());

        info = list.get(1);
        assertEquals(7, info.getCharPosition());
        assertFalse(info.has2nd());
        assertEquals((byte)0xff, info.getRawByte1st());

        return;
    }

    /**
     * Test of addDecodeError method, of class DecodedContent.
     */
    @Test
    public void testAddDecodeError_byte_byte(){
        System.out.println("addDecodeError");

        DecodedContent content;

        content = new DecodedContent();
        content.append("abc");
        content.addDecodeError((byte)0x01, (byte)0x02);
        content.append("def");
        content.addDecodeError((byte)0xfe, (byte)0xff);

        assertEquals("abc?def?", content.toString());
        List<DecodeErrorInfo> list = content.getDecodeErrorList();
        assertEquals(2, list.size());

        DecodeErrorInfo info;

        info = list.get(0);
        assertEquals(3, list.get(0).getCharPosition());
        assertTrue(info.has2nd());
        assertEquals((byte)0x01, info.getRawByte1st());
        assertEquals((byte)0x02, info.getRawByte2nd());

        info = list.get(1);
        assertEquals(7, info.getCharPosition());
        assertTrue(info.has2nd());
        assertEquals((byte)0xfe, info.getRawByte1st());
        assertEquals((byte)0xff, info.getRawByte2nd());

        return;
    }

    /**
     * Test of toString method, of class DecodedContent.
     */
    @Test
    public void testToString(){
        System.out.println("toString");

        DecodedContent content;

        content = new DecodedContent();
        content.append("abc");
        content.addDecodeError((byte)0x01, (byte)0x02);
        content.append("def");
        content.addDecodeError((byte)0xfe, (byte)0xff);

        assertEquals("abc?def?", content.toString());
        assertEquals(content.getRawContent().toString(), content.toString());

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
        result = DecodedContent.lsearchErrorIndex(errList, 10);
        assertEquals(0, result);

        errList.clear();
        errList.add(new DecodeErrorInfo(5, (byte)0x00));
        result = DecodedContent.lsearchErrorIndex(errList, 10);
        assertEquals(1, result);

        errList.clear();
        errList.add(new DecodeErrorInfo(10, (byte)0x00));
        result = DecodedContent.lsearchErrorIndex(errList, 10);
        assertEquals(0, result);

        errList.clear();
        errList.add(new DecodeErrorInfo(15, (byte)0x00));
        result = DecodedContent.lsearchErrorIndex(errList, 10);
        assertEquals(0, result);

        errList.clear();
        errList.add(new DecodeErrorInfo(4, (byte)0x00));
        errList.add(new DecodeErrorInfo(5, (byte)0x00));
        errList.add(new DecodeErrorInfo(14, (byte)0x00));
        errList.add(new DecodeErrorInfo(15, (byte)0x00));
        result = DecodedContent.lsearchErrorIndex(errList, 10);
        assertEquals(2, result);

        errList.clear();
        errList.add(new DecodeErrorInfo(4, (byte)0x00));
        errList.add(new DecodeErrorInfo(5, (byte)0x00));
        errList.add(new DecodeErrorInfo(10, (byte)0x00));
        errList.add(new DecodeErrorInfo(14, (byte)0x00));
        errList.add(new DecodeErrorInfo(15, (byte)0x00));
        result = DecodedContent.lsearchErrorIndex(errList, 10);
        assertEquals(2, result);

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
        result = DecodedContent.bsearchErrorIndex(errList, 10);
        assertEquals(0, result);

        errList.clear();
        errList.add(new DecodeErrorInfo(5, (byte)0x00));
        result = DecodedContent.bsearchErrorIndex(errList, 10);
        assertEquals(1, result);

        errList.clear();
        errList.add(new DecodeErrorInfo(10, (byte)0x00));
        result = DecodedContent.bsearchErrorIndex(errList, 10);
        assertEquals(0, result);

        errList.clear();
        errList.add(new DecodeErrorInfo(15, (byte)0x00));
        result = DecodedContent.bsearchErrorIndex(errList, 10);
        assertEquals(0, result);

        errList.clear();
        errList.add(new DecodeErrorInfo(4, (byte)0x00));
        errList.add(new DecodeErrorInfo(5, (byte)0x00));
        errList.add(new DecodeErrorInfo(14, (byte)0x00));
        errList.add(new DecodeErrorInfo(15, (byte)0x00));
        result = DecodedContent.bsearchErrorIndex(errList, 10);
        assertEquals(2, result);

        errList.clear();
        errList.add(new DecodeErrorInfo(4, (byte)0x00));
        errList.add(new DecodeErrorInfo(5, (byte)0x00));
        errList.add(new DecodeErrorInfo(10, (byte)0x00));
        errList.add(new DecodeErrorInfo(14, (byte)0x00));
        errList.add(new DecodeErrorInfo(15, (byte)0x00));
        result = DecodedContent.bsearchErrorIndex(errList, 10);
        assertEquals(2, result);
        result = DecodedContent.bsearchErrorIndex(errList, 9);
        assertEquals(2, result);
        result = DecodedContent.bsearchErrorIndex(errList, 11);
        assertEquals(3, result);

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
        for(int pos = 0; pos <= 1000; pos += 10){
            errList.add(new DecodeErrorInfo(pos, (byte)0x00));
        }
        result = DecodedContent.searchErrorIndex(errList, 503);
        assertEquals(51, result);

        errList.clear();
        for(int pos = 0; pos <= 50; pos += 10){
            errList.add(new DecodeErrorInfo(pos, (byte)0x00));
        }
        result = DecodedContent.searchErrorIndex(errList, 23);
        assertEquals(3, result);

        return;
    }

    /**
     * Test of appendGappedErrorInfo method, of class DecodedContent.
     */
    @Test
    public void testAppendGappedErrorInfo(){
        System.out.println("appendGappedErrorInfo");

        DecodedContent sourceContent;
        sourceContent = new DecodedContent();
        for(int pos = 0; pos <= 50; pos += 10){
            sourceContent.append("123456789");
            sourceContent.addDecodeError((byte)0x00);
        }

        List<DecodeErrorInfo> result;
        result = DecodedContent.appendGappedErrorInfo(sourceContent, 15, 35, null, -100);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(119, result.get(0).getCharPosition());
        assertEquals(129, result.get(1).getCharPosition());

        return;
    }

    /**
     * Test of ensureCapacity method, of class DecodedContent.
     */
    @Test
    public void testEnsureCapacity(){
        System.out.println("ensureCapacity");

        DecodedContent content;

        content = new DecodedContent("abc");
        content.ensureCapacity(-1);
        content.ensureCapacity(0);
        content.ensureCapacity(1);
        content.ensureCapacity(5);
        content.append("def");
        assertEquals("abcdef", content.toString());

        content = new DecodedContent();
        content.ensureCapacity(5);
        content.append("abc");
        assertEquals("abc", content.toString());

        return;
    }

    /**
     * Test of setCharAt method, of class DecodedContent.
     */
    @Test
    public void testSetCharAt(){
        System.out.println("setCharAt");

        DecodedContent content;

        content = new DecodedContent("abc");
        content.setCharAt(1, 'B');
        assertEquals("aBc", content.toString());

        content = new DecodedContent("a");
        content.addDecodeError((byte)0xff);
        content.append('c');
        assertEquals("a?c", content.toString());
        content.setCharAt(1, 'B');
        assertEquals("aBc", content.toString());
        assertEquals(1, content.getDecodeErrorList().size());
        assertEquals(1, content.getDecodeErrorList().get(0).getCharPosition());
        assertEquals((byte)0xff, content.getDecodeErrorList().get(0).getRawByte1st());

        content = new DecodedContent("abc");
        try{
            content.setCharAt(-1, 'B');
            fail();
        }catch(IndexOutOfBoundsException e){
            // NOTHING
        }
        try{
            content.setCharAt(10, 'B');
            fail();
        }catch(IndexOutOfBoundsException e){
            // NOTHING
        }

        return;
    }

}

/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.osdn.jindolf.parser.content;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 */
public class DecodedContentTest {

    public DecodedContentTest() {
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

        content = new DecodedContent(null);
        assertEquals("null", content.toString());

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
     * @see DecodedContent#append(char)
     */
    @Test
    public void testAppend_char(){
        System.out.println("append");

        DecodedContent content;

        content = new DecodedContent();
        assertEquals("", content.toString());
        content.append('a');
        assertEquals("a", content.toString());
        content.append('b');
        assertEquals("ab", content.toString());

        return;
    }

    /**
     * Test of append method, of class DecodedContent.
     * @see DecodedContent#append(CharSequence)
     */
    @Test
    public void testAppend_CharSequence(){
        System.out.println("append");

        DecodedContent content;
        CharSequence seq;

        content = new DecodedContent();
        assertEquals("", content.toString());
        seq = "abc";
        content.append(seq);
        assertEquals("abc", content.toString());
        seq = "def";
        content.append(seq);
        assertEquals("abcdef", content.toString());
        seq = null;
        content.append(seq);
        assertEquals("abcdefnull", content.toString());
        content.append(new DecodedContent("dec"));
        assertEquals("abcdefnulldec", content.toString());

        return;
    }

    /**
     * Test of append method, of class DecodedContent.
     * @see DecodedContent#append(CharSequence txt, int start, int end)
     */
    @Test
    public void testAppend_3args_CharSeqintint(){
        System.out.println("append");

        DecodedContent content;
        CharSequence seq;

        content = new DecodedContent();

        seq = "12345";

        content.init();
        try{
            content.append(seq, -1, 3);
            fail();
        }catch(IndexOutOfBoundsException e){
            // GOOD
        }

        content.init();
        content.append(seq, 0, 3);
        assertEquals("123", content.toString());

        content.init();
        content.append(seq, 1, 3);
        assertEquals("23", content.toString());

        content.init();
        content.append((CharSequence)new DecodedContent("PQR"), 1, 3);
        assertEquals("QR", content.toString());

        content.init();
        try{
            content.append(seq, 3, 1);
            fail();
        }catch(IndexOutOfBoundsException e){
            // GOOD
        }

        content.init();
        content.append(seq, 3, 3);
        assertEquals("", content.toString());

        content.init();
        content.append(seq, 3, 5);
        assertEquals("45", content.toString());

        content.init();
        try{
            content.append(seq, 3, 6);
            fail();
        }catch(IndexOutOfBoundsException e){
            // GOOD
        }

        content.init();
        try{
            content.append(seq, 10, 10);
            fail();
        }catch(IndexOutOfBoundsException e){
            // GOOD
        }

        // test runtime
        StringBuilder sb = new StringBuilder("");
        seq = null;
        sb.append(seq, 1, 2);
        assertEquals("u", sb.toString());

        content.init();
        seq = null;
        content.append(seq, 1, 2);
        assertEquals("u", content.toString());

        return;
    }

    /**
     * Test of append method, of class DecodedContent.
     * @see DecodedContent#append(DecodedContent txt, int start, int end)
     */
    @Test
    public void testAppend_3args_Decodeintint(){
        System.out.println("append");

        DecodedContent content;
        List<DecodeErrorInfo> errList;
        DecodeErrorInfo info;

        content = new DecodedContent();
        content.append("abc");

        DecodedContent other;
        other = new DecodedContent();
        other.append("12345");

        content.append(other, 1, 4);
        assertEquals("abc234", content.toString());

        content.init();
        try{
            content.append(other, -1, 3);
            fail();
        }catch(IndexOutOfBoundsException e){
            // GOOD
        }

        content.init();
        content.append(other, 0, 3);
        assertEquals("123", content.toString());

        content.init();
        content.append(other, 1, 3);
        assertEquals("23", content.toString());

        content.init();
        try{
            content.append(other, 3, 1);
            fail();
        }catch(IndexOutOfBoundsException e){
            // GOOD
        }

        content.init();
        content.append(other, 3, 3);
        assertEquals("", content.toString());

        content.init();
        content.append(other, 3, 5);
        assertEquals("45", content.toString());

        content.init();
        try{
            content.append(other, 3, 6);
            fail();
        }catch(IndexOutOfBoundsException e){
            // GOOD
        }

        content.init();
        try{
            content.append(other, 10, 10);
            fail();
        }catch(IndexOutOfBoundsException e){
            // GOOD
        }

        content.init();
        content.append("abc");

        other = new DecodedContent();
        other.append('A');
        other.addDecodeError((byte)0x01);
        other.append('B');
        other.addDecodeError((byte)0x02);
        other.append('C');
        assertEquals("A?B?C", other.toString());

        content.append(other, 1, 4);
        assertEquals("abc?B?", content.toString());

        errList = content.getDecodeErrorList();
        assertEquals(2, errList.size());

        info = errList.get(0);
        assertEquals(3, info.getCharPosition());
        assertEquals((byte)0x01, info.getRawByte1st());
        info = errList.get(1);
        assertEquals(5, info.getCharPosition());
        assertEquals((byte)0x02, info.getRawByte1st());

        content.init();
        content.append(other, 0, 5);
        assertEquals("A?B?C", content.toString());

        errList = content.getDecodeErrorList();
        assertEquals(2, errList.size());

        info = errList.get(0);
        assertEquals(1, info.getCharPosition());
        assertEquals((byte)0x01, info.getRawByte1st());
        info = errList.get(1);
        assertEquals(3, info.getCharPosition());
        assertEquals((byte)0x02, info.getRawByte1st());

        content.init();
        content.append("ABCDE");
        content.append(content, 1, 3);
        assertEquals("ABCDEBC", content.toString());

        content.init();
        content.append('A');
        content.addDecodeError((byte)0x00);
        content.append('B');
        content.addDecodeError((byte)0x01);
        content.append('C');
        content.append(content, 1, 3);
        assertEquals("A?B?C?B", content.toString());
        errList = content.getDecodeErrorList();
        assertEquals(3, errList.size());
        info = errList.get(0);
        assertEquals(1, info.getCharPosition());
        assertEquals((byte)0x00, info.getRawByte1st());
        info = errList.get(1);
        assertEquals(3, info.getCharPosition());
        assertEquals((byte)0x01, info.getRawByte1st());
        info = errList.get(2);
        assertEquals(5, info.getCharPosition());
        assertEquals((byte)0x00, info.getRawByte1st());

        CharSequence seq;
        // test runtime
        StringBuilder sb = new StringBuilder("");
        seq = null;
        sb.append(seq, 1, 2);
        assertEquals("u", sb.toString());

        content.init();
        other = null;
        content.append(other, 1, 2);
        assertEquals("u", content.toString());

        return;
    }

    /**
     * Test of append method, of class DecodedContent.
     * @see DecodedContent#append(char[], int, int)
     */
    @Test
    public void testAppend_3args_charintint(){
        System.out.println("append");

        DecodedContent content;

        char[] seq = {'1','2','3','4','5',};

        content = new DecodedContent();


        content.init();
        try{
            content.append(seq, -1, 3);
            fail();
        }catch(IndexOutOfBoundsException e){
            // GOOD
        }

        content.init();
        content.append(seq, 0, 3);
        assertEquals("123", content.toString());

        content.init();
        content.append(seq, 1, 3);
        assertEquals("234", content.toString());


        try{
            content.append(seq, 3, -1);
            fail();
        }catch(IndexOutOfBoundsException e){
            // GOOD
        }

        content.init();
        content.append(seq, 3, 0);
        assertEquals("", content.toString());

        content.init();
        content.append(seq, 3, 1);
        assertEquals("4", content.toString());

        content.init();
        content.append(seq, 3, 2);
        assertEquals("45", content.toString());

        content.init();
        try{
            content.append(seq, 3, 3);
            fail();
        }catch(IndexOutOfBoundsException e){
            // GOOD
        }

        // test runtime
        StringBuilder sb = new StringBuilder("A");
        try{
            sb.append((char[])null, 1, 2);
            fail();
        }catch(NullPointerException e){
            // GOOD
        }

        try{
            content.append((char[])null, 1, 2);
            fail();
        }catch(NullPointerException e){
            // GOOD
        }

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
     * Test of appendGappedErrorInfo method, of class DecodedContent.
     */
    @Test
    public void testAppendGappedErrorInfo(){
        System.out.println("appendGappedErrorInfo");

        List<DecodeErrorInfo> srcErrList = new ArrayList<>();
        for(int pos = 0; pos <= 50; pos += 10){
            DecodeErrorInfo info = new DecodeErrorInfo(pos, (byte)0x00);
            srcErrList.add(info);
        }

        List<DecodeErrorInfo> result;
        List<DecodeErrorInfo> target;

        result = DecodedContent.appendGappedErrorInfo(srcErrList, 15, 35, null, -100);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(120, result.get(0).getCharPosition());
        assertEquals(130, result.get(1).getCharPosition());

        target = new ArrayList<>();
        result = DecodedContent.appendGappedErrorInfo(srcErrList, 15, 35, target, -100);
        assertSame(target, result);
        assertEquals(2, result.size());
        assertEquals(120, result.get(0).getCharPosition());
        assertEquals(130, result.get(1).getCharPosition());

        try{
            DecodedContent.appendGappedErrorInfo(srcErrList, 15, 35, srcErrList, -100);
            fail();
        }catch(IllegalArgumentException e){
            // GOOD
        }


        result = DecodedContent.appendGappedErrorInfo(srcErrList, 10, 40, null, -100);
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(110, result.get(0).getCharPosition());
        assertEquals(120, result.get(1).getCharPosition());
        assertEquals(130, result.get(2).getCharPosition());

        result = DecodedContent.appendGappedErrorInfo(srcErrList, 10, 41, null, -100);
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(110, result.get(0).getCharPosition());
        assertEquals(120, result.get(1).getCharPosition());
        assertEquals(130, result.get(2).getCharPosition());
        assertEquals(140, result.get(3).getCharPosition());

        result = DecodedContent.appendGappedErrorInfo(srcErrList, 11, 40, null, -100);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(120, result.get(0).getCharPosition());
        assertEquals(130, result.get(1).getCharPosition());

        result = DecodedContent.appendGappedErrorInfo(srcErrList, 9, 40, null, -100);
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(110, result.get(0).getCharPosition());
        assertEquals(120, result.get(1).getCharPosition());
        assertEquals(130, result.get(2).getCharPosition());

        result = DecodedContent.appendGappedErrorInfo(srcErrList, 10, 50, null, -100);
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(110, result.get(0).getCharPosition());
        assertEquals(120, result.get(1).getCharPosition());
        assertEquals(130, result.get(2).getCharPosition());
        assertEquals(140, result.get(3).getCharPosition());

        result = DecodedContent.appendGappedErrorInfo(srcErrList, 10, 51, null, -100);
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals(110, result.get(0).getCharPosition());
        assertEquals(120, result.get(1).getCharPosition());
        assertEquals(130, result.get(2).getCharPosition());
        assertEquals(140, result.get(3).getCharPosition());
        assertEquals(150, result.get(4).getCharPosition());

        result = DecodedContent.appendGappedErrorInfo(srcErrList, 0, 0, null, -100);
        assertNull(result);

        result = DecodedContent.appendGappedErrorInfo(srcErrList, 0, 1, null, -100);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getCharPosition());

        result = DecodedContent.appendGappedErrorInfo(srcErrList, 10, 10, null, -100);
        assertNull(result);

        result = DecodedContent.appendGappedErrorInfo(srcErrList, 15, 15, null, -100);
        assertNull(result);

        result = DecodedContent.appendGappedErrorInfo(srcErrList, 15, 16, null, -100);
        assertNull(result);

        result = DecodedContent.appendGappedErrorInfo(srcErrList, 60, 70, null, -100);
        assertNull(result);

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

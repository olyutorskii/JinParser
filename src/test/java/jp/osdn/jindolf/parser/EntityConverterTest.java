/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.osdn.jindolf.parser;

import java.util.List;
import jp.osdn.jindolf.parser.content.DecodeErrorInfo;
import jp.osdn.jindolf.parser.content.DecodedContent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 */
public class EntityConverterTest {

    public EntityConverterTest() {
    }

    /**
     * Test of convert method, of class EntityConverter.
     */
    @Test
    public void testConvert(){
        System.out.println("convert");

        EntityConverter converter = new EntityConverter();

        DecodedContent from;
        DecodedContent result;

        from = new DecodedContent();
        from.append("a&gt;b&lt;c&quot;d&amp;e");
        result = converter.convert(from, 0, from.length());
        assertEquals("a>b<c\"d&e", result.toString());

        from = new DecodedContent();
        from.append("&gt;&lt;&quot;&amp;");
        result = converter.convert(from, 0, from.length());
        assertEquals("><\"&", result.toString());

        from = new DecodedContent();
        from.append("12345");
        result = converter.convert(from, 1, 3);
        assertEquals("23", result.toString());

        from = new DecodedContent();
        from.append("12&gt;45");
        result = converter.convert(from, 1, 7);
        assertEquals("2>4", result.toString());

        from = new DecodedContent();
        from.append("12&gt;45");
        result = converter.convert(from, 3, 7);
        assertEquals("gt;4", result.toString());

        from = new DecodedContent();
        from.append("&amp;gt;");
        result = converter.convert(from, 0, from.length());
        assertEquals("&gt;", result.toString());

        from = new DecodedContent();
        from.append("a&gt;b");
        result = converter.convert(from);
        assertEquals("a>b", result.toString());

        from = new DecodedContent();
        from.append("a&gt;b");
        from.addDecodeError((byte)0x03);
        from.append("c");
        result = converter.convert(from);
        assertEquals("a>b?c", result.toString());
        assertTrue(result.hasDecodeError());
        List<DecodeErrorInfo> list = result.getDecodeErrorList();
        assertEquals(1, list.size());
        assertEquals((byte)0x03, list.get(0).getRawByte1st());

        from = new DecodedContent();
        from.append("");
        result = converter.convert(from, 0, 0);
        assertEquals("", result.toString());

        from = new DecodedContent();
        from.append("a\\b");
        result = converter.convert(from, 0, from.length());
        assertEquals("a¥b", result.toString());

        from = new DecodedContent();
        from.append("abcde");
        SeqRange range = new SeqRange(1,4);
        result = converter.convert(from, range);
        assertEquals("bcd", result.toString());

        from = new DecodedContent();
        from.append("abcde");
        try{
            converter.convert(from, 4, 1);
            fail();
        }catch(IndexOutOfBoundsException e){
            // OK
        }

        from = new DecodedContent();
        from.append("abcde");
        try{
            converter.convert(from, -1, 4);
            fail();
        }catch(IndexOutOfBoundsException e){
            // OK
        }

        from = new DecodedContent();
        from.append("abcde");
        try{
            converter.convert(from, 1, 6);
            fail();
        }catch(IndexOutOfBoundsException e){
            // OK
        }

        from = new DecodedContent();
        from.append("a\ud83d\udc11b"); // 🐑
        result = converter.convert(from);
        assertEquals("a\ud83d\udc11b", result.toString());

        from = new DecodedContent();
        from.append("a\ud83d\udc11b"); // 🐑
        EntityConverter repConverter = new EntityConverter(true);
        result = repConverter.convert(from);
        assertEquals("a?b", result.toString());

        return;
    }

    /**
     * Test of append method, of class EntityConverter.
     */
    @Test
    public void testAppend(){
        System.out.println("append");

        EntityConverter converter = new EntityConverter();
        DecodedContent target;
        DecodedContent from;
        DecodedContent result;

        target = new DecodedContent("abc");
        from = new DecodedContent("d&gt;f");
        result = converter.append(target,from);
        assertEquals("abcd>f", result.toString());

        target = new DecodedContent("abc");
        from = new DecodedContent("d&gt;fg&lt;i");
        result = converter.append(target, from, 6, 12);
        assertEquals("abcg<i", result.toString());

        target = new DecodedContent("abc");
        from = new DecodedContent("d&gt;fg&lt;i");
        result = converter.append(target, from, new SeqRange(6, 12));
        assertEquals("abcg<i", result.toString());

        target = new DecodedContent();
        target.append('a');
        target.addDecodeError((byte)0xff);
        target.append('c');
        from = new DecodedContent();
        from.append('d');
        from.addDecodeError((byte)0xfe);
        from.append('f');
        result = converter.append(target, from);
        assertEquals("a?cd?f", result.toString());
        assertTrue(result.hasDecodeError());
        List<DecodeErrorInfo> list = result.getDecodeErrorList();
        assertEquals(2, list.size());
        assertEquals((byte)0xff, list.get(0).getRawByte1st());
        assertEquals((byte)0xfe, list.get(1).getRawByte1st());
        assertEquals(1, list.get(0).getCharPosition());
        assertEquals(4, list.get(1).getCharPosition());

        return;
    }

}

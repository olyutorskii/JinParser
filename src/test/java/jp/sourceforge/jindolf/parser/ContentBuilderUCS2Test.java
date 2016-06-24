/*
 * License : The MIT License
 * Copyright(c) 2010 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class ContentBuilderUCS2Test {

    public ContentBuilderUCS2Test() {
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

    public static byte[] byteArray(CharSequence seq){
        byte[] result;

        List<Byte> byteList = new ArrayList<>();

        int length = seq.length();
        for(int pos = 0; pos < length; pos++){
            int val = 0;

            char ch = seq.charAt(pos);

            if('0' <= ch && ch <= '9'){
                val += ch - '0';
            }else if('a' <= ch && ch <= 'f'){
                val += ch - 'a' + 10;
            }else if('A' <= ch && ch <= 'F'){
                val += ch - 'A' + 10;
            }else{
                continue;
            }

            pos++;
            if(pos >= length) break;

            val *= 16;
            ch = seq.charAt(pos);

            if('0' <= ch && ch <= '9'){
                val += ch - '0';
            }else if('a' <= ch && ch <= 'f'){
                val += ch - 'a' + 10;
            }else if('A' <= ch && ch <= 'F'){
                val += ch - 'A' + 10;
            }else{
                continue;
            }

            byteList.add((byte)val);
        }

        result = new byte[byteList.size()];

        for(int pos = 0; pos < result.length; pos++){
            result[pos] = byteList.get(pos);
        }

        return result;
    }

    /**
     * Test of UTF8
     */
    @Test
    public void testUTF8() throws Exception {
        Charset cs = Charset.forName("UTF-8");

        CharsetDecoder cd;
        ContentBuilderUCS2 cb;
        StreamDecoder decoder;
        byte[] bdata;
        InputStream is;
        DecodedContent content;
        List<DecodeErrorInfo> errList;
        DecodeErrorInfo einfo;


        cd = cs.newDecoder();
        decoder = new StreamDecoder(cd);
        cb = new ContentBuilderUCS2();
        decoder.setDecodeHandler(cb);
        bdata = byteArray("41:42:43");
        is = new ByteArrayInputStream(bdata);
        decoder.decode(is);
        content = cb.getContent();

        assertEquals(3, content.length());
        assertEquals("ABC", content.toString());
        assertFalse(content.hasDecodeError());


        cd = cs.newDecoder();
        decoder = new StreamDecoder(cd);
        cb = new ContentBuilderUCS2();
        decoder.setDecodeHandler(cb);
        bdata = byteArray("41:EFBCA2:43");
        is = new ByteArrayInputStream(bdata);
        decoder.decode(is);
        content = cb.getContent();

        assertEquals(3, content.length());
        assertEquals("AＢC", content.toString());
        assertFalse(content.hasDecodeError());


        cd = cs.newDecoder();
        decoder = new StreamDecoder(cd);
        cb = new ContentBuilderUCS2();
        decoder.setDecodeHandler(cb);
        bdata = byteArray("41:FF:43");
        is = new ByteArrayInputStream(bdata);
        decoder.decode(is);
        content = cb.getContent();

        assertEquals(3, content.length());
        assertEquals("A?C", content.toString());
        assertTrue(content.hasDecodeError());
        errList = content.getDecodeErrorList();
        assertEquals(1, errList.size());
        einfo = errList.get(0);
        assertFalse(einfo.has2nd());
        assertEquals((byte)0xff, einfo.getRawByte1st());
        assertEquals(1, einfo.getCharPosition());

        return;
    }

    /**
     * Test of UTF16
     */
    @Test
    public void testUTF16() throws Exception {
        Charset cs = Charset.forName("UTF-16");

        CharsetDecoder cd;
        ContentBuilderUCS2 cb;
        StreamDecoder decoder;
        byte[] bdata;
        InputStream is;
        DecodedContent content;


        cd = cs.newDecoder();
        decoder = new StreamDecoder(cd);
        cb = new ContentBuilderUCS2();
        decoder.setDecodeHandler(cb);
        bdata = byteArray("0041:0042:0043");
        is = new ByteArrayInputStream(bdata);
        decoder.decode(is);
        content = cb.getContent();

        assertEquals(3, content.length());
        assertEquals("ABC", content.toString());
        assertFalse(content.hasDecodeError());


        cd = cs.newDecoder();
        decoder = new StreamDecoder(cd);
        cb = new ContentBuilderUCS2();
        decoder.setDecodeHandler(cb);
        bdata = byteArray("0041:FF22:0043");
        is = new ByteArrayInputStream(bdata);
        decoder.decode(is);
        content = cb.getContent();

        assertEquals(3, content.length());
        assertEquals("AＢC", content.toString());
        assertFalse(content.hasDecodeError());


        return;
    }

    /**
     * Test of UTF16 sequence error
     */
    @Test
    public void testUTF16_seq() throws Exception {
        Charset cs = Charset.forName("UTF-16");

        CharsetDecoder cd;
        ContentBuilderUCS2 cb;
        StreamDecoder decoder;
        byte[] bdata;
        InputStream is;
        DecodedContent content;
        List<DecodeErrorInfo> errList;
        DecodeErrorInfo einfo;

        cd = cs.newDecoder();
        decoder = new StreamDecoder(cd);
        cb = new ContentBuilderUCS2();
        decoder.setDecodeHandler(cb);
        bdata = byteArray("0041:d800:0043:0044");
        is = new ByteArrayInputStream(bdata);
        decoder.decode(is);
        content = cb.getContent();

        assertEquals(6, content.length());
        assertEquals("A????D", content.toString());
        assertTrue(content.hasDecodeError());
        errList = content.getDecodeErrorList();
        assertEquals(4, errList.size());
        einfo = errList.get(0);
        assertFalse(einfo.has2nd());
        assertEquals((byte)0xd8, einfo.getRawByte1st());
        assertEquals(1, einfo.getCharPosition());
        einfo = errList.get(1);
        assertFalse(einfo.has2nd());
        assertEquals((byte)0x00, einfo.getRawByte1st());
        assertEquals(2, einfo.getCharPosition());
        einfo = errList.get(2);
        assertFalse(einfo.has2nd());
        assertEquals((byte)0x00, einfo.getRawByte1st());
        assertEquals(3, einfo.getCharPosition());
        einfo = errList.get(3);
        assertFalse(einfo.has2nd());
        assertEquals((byte)0x43, einfo.getRawByte1st());
        assertEquals(4, einfo.getCharPosition());


        cd = cs.newDecoder();
        decoder = new StreamDecoder(cd);
        cb = new ContentBuilderUCS2();
        decoder.setDecodeHandler(cb);
        bdata = byteArray("0041:0042:dc00:0044");
        is = new ByteArrayInputStream(bdata);
        decoder.decode(is);
        content = cb.getContent();

        assertEquals(5, content.length());
        assertEquals("AB??D", content.toString());
        errList = content.getDecodeErrorList();
        assertEquals(2, errList.size());
        einfo = errList.get(0);
        assertFalse(einfo.has2nd());
        assertEquals((byte)0xdc, einfo.getRawByte1st());
        assertEquals(2, einfo.getCharPosition());
        einfo = errList.get(1);
        assertFalse(einfo.has2nd());
        assertEquals((byte)0x00, einfo.getRawByte1st());
        assertEquals(3, einfo.getCharPosition());


        cd = cs.newDecoder();
        decoder = new StreamDecoder(cd);
        cb = new ContentBuilderUCS2();
        decoder.setDecodeHandler(cb);
        bdata = byteArray("0041:d800");
        is = new ByteArrayInputStream(bdata);
        decoder.decode(is);
        content = cb.getContent();

        assertEquals(3, content.length());
        assertEquals("A??", content.toString());
        assertTrue(content.hasDecodeError());
        errList = content.getDecodeErrorList();
        assertEquals(2, errList.size());
        einfo = errList.get(0);
        assertFalse(einfo.has2nd());
        assertEquals((byte)0xd8, einfo.getRawByte1st());
        assertEquals(1, einfo.getCharPosition());
        einfo = errList.get(1);
        assertFalse(einfo.has2nd());
        assertEquals((byte)0x00, einfo.getRawByte1st());
        assertEquals(2, einfo.getCharPosition());

        return;
    }

    /**
     * Test of UTF16 mapping error
     */
    @Test
    public void testUTF16_nomap() throws Exception {
        Charset cs = Charset.forName("UTF-16");

        CharsetDecoder cd;
        ContentBuilderUCS2 cb;
        StreamDecoder decoder;
        byte[] bdata;
        InputStream is;
        DecodedContent content;

        cd = cs.newDecoder();
        decoder = new StreamDecoder(cd);
        cb = new ContentBuilderUCS2();
        decoder.setDecodeHandler(cb);
        bdata = byteArray("0041:d83d:dc11:0042");
        is = new ByteArrayInputStream(bdata);
        decoder.decode(is);
        content = cb.getContent();

        assertEquals(4, content.length());
        assertEquals("A\ud83d\udc11B", content.toString());

        return;
    }

}

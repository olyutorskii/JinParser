/*
 * License : The MIT License
 * Copyright(c) 2010 olyutorskii
 */

package jp.osdn.jindolf.parser.content;

import io.bitbucket.olyutorskii.jiocema.DecodeBreakException;
import io.bitbucket.olyutorskii.jiocema.DecodeNotifier;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
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
public class ContentBuilderTest {

    public ContentBuilderTest() {
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
     * Test of UTF8
     * @throws Exception
     */
    @Test
    public void testUTF8() throws Exception {
        Charset cs = Charset.forName("UTF-8");

        CharsetDecoder cd;
        ContentBuilder cb;
        DecodeNotifier decoder;
        byte[] bdata;
        InputStream is;
        DecodedContent content;
        List<DecodeErrorInfo> errList;
        DecodeErrorInfo einfo;


        cd = cs.newDecoder();
        decoder = new DecodeNotifier(cd);
        cb = new ContentBuilder();
        decoder.setCharDecodeListener(cb);
        bdata = Bseq.byteArray("41:42:43");
        is = new ByteArrayInputStream(bdata);
        decoder.decode(is);
        content = cb.getContent();

        assertEquals(3, content.length());
        assertEquals("ABC", content.toString());
        assertFalse(content.hasDecodeError());


        cd = cs.newDecoder();
        decoder = new DecodeNotifier(cd);
        cb = new ContentBuilder();
        decoder.setCharDecodeListener(cb);
        bdata = Bseq.byteArray("41:EFBCA2:43");
        is = new ByteArrayInputStream(bdata);
        decoder.decode(is);
        content = cb.getContent();

        assertEquals(3, content.length());
        assertEquals("AＢC", content.toString());
        assertFalse(content.hasDecodeError());


        cd = cs.newDecoder();
        decoder = new DecodeNotifier(cd);
        cb = new ContentBuilder();
        decoder.setCharDecodeListener(cb);
        bdata = Bseq.byteArray("41:FF:43");
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
     * @throws Exception
     */
    @Test
    public void testUTF16() throws Exception {
        Charset cs = Charset.forName("UTF-16");

        CharsetDecoder cd;
        ContentBuilder cb;
        DecodeNotifier decoder;
        byte[] bdata;
        InputStream is;
        DecodedContent content;


        cd = cs.newDecoder();
        decoder = new DecodeNotifier(cd);
        cb = new ContentBuilder();
        decoder.setCharDecodeListener(cb);
        bdata = Bseq.byteArray("0041:0042:0043");
        is = new ByteArrayInputStream(bdata);
        decoder.decode(is);
        content = cb.getContent();

        assertEquals(3, content.length());
        assertEquals("ABC", content.toString());
        assertFalse(content.hasDecodeError());


        cd = cs.newDecoder();
        decoder = new DecodeNotifier(cd);
        cb = new ContentBuilder();
        decoder.setCharDecodeListener(cb);
        bdata = Bseq.byteArray("0041:FF22:0043");
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
     * @throws Exception
     */
    @Test
    public void testUTF16_seq() throws Exception {
        Charset cs = Charset.forName("UTF-16");

        CharsetDecoder cd;
        ContentBuilder cb;
        DecodeNotifier decoder;
        byte[] bdata;
        InputStream is;
        DecodedContent content;
        List<DecodeErrorInfo> errList;
        DecodeErrorInfo einfo;

        cd = cs.newDecoder();
        decoder = new DecodeNotifier(cd);
        cb = new ContentBuilder();
        decoder.setCharDecodeListener(cb);
        bdata = Bseq.byteArray("0041:d800:0043:0044");
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
        decoder = new DecodeNotifier(cd);
        cb = new ContentBuilder();
        decoder.setCharDecodeListener(cb);
        bdata = Bseq.byteArray("0041:0042:dc00:0044");
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
        decoder = new DecodeNotifier(cd);
        cb = new ContentBuilder();
        decoder.setCharDecodeListener(cb);
        bdata = Bseq.byteArray("0041:d800");
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
     * @throws Exception
     */
    @Test
    public void testUTF16_nomap() throws Exception {
        Charset cs = Charset.forName("UTF-16");

        CharsetDecoder cd;
        ContentBuilder cb;
        DecodeNotifier decoder;
        byte[] bdata;
        InputStream is;
        DecodedContent content;

        cd = cs.newDecoder();
        decoder = new DecodeNotifier(cd);
        cb = new ContentBuilder();
        decoder.setCharDecodeListener(cb);
        bdata = Bseq.byteArray("0041:d83d:dc11:0042");
        is = new ByteArrayInputStream(bdata);
        decoder.decode(is);
        content = cb.getContent();

        assertEquals(4, content.length());
        assertEquals("A\ud83d\udc11B", content.toString());

        return;
    }

    @Test
    public void testSheep() throws IOException, DecodeBreakException {
        System.out.println("sheep");

        Charset cs;
        CharsetDecoder decoder;
        ContentBuilder listener;

        DecodeNotifier sd;
        InputStream is;

        cs = Charset.forName("UTF-8");
        decoder = cs.newDecoder();

        sd = new DecodeNotifier(decoder);

        listener = new ContentBuilder();
        sd.setCharDecodeListener(listener);

        // SMP character U+1F411 [SHEEP]
        // see https://ja.osdn.net/projects/jindolf/ticket/36356
        is = Bseq.byteStream(0xf0, 0x9f, 0x90, 0x91);
        sd.decode(is);
        assertEquals("\ud83d\udc11", listener.getContent().toString());

        return;
    }

}

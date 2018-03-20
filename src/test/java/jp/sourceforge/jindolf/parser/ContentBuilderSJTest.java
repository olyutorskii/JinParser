/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import io.bitbucket.olyutorskii.jiocema.DecodeBreakException;
import io.bitbucket.olyutorskii.jiocema.DecodeNotifier;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 */
public class ContentBuilderSJTest {

    public ContentBuilderSJTest() {
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
     * Test of SjisDecoder & ContentBuilder.
     * @throws java.io.IOException
     * @throws DecodeBreakException
     */
    @Test
    public void testDecoding() throws IOException, DecodeBreakException{
        System.out.println("Decoding");

        DecodeNotifier decoder;
        ContentBuilderSJ builder;
        byte[] bdata;
        InputStream istream;
        DecodedContent content;

        decoder = new SjisNotifier();
        builder = new ContentBuilderSJ();
        decoder.setCharDecodeListener(builder);

        bdata = Bseq.byteArray("20:41:42:43:7e");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals(" ABC~", content.toString());
        assertFalse(content.hasDecodeError());

        bdata = Bseq.byteArray("");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("", content.toString());
        assertFalse(content.hasDecodeError());

        bdata = Bseq.byteArray("00:0A:0D:1F");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("\u0000\n\r\u001f", content.toString());
        assertFalse(content.hasDecodeError());

        bdata = Bseq.byteArray("A1:B1:B2:B3:DF");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("｡ｱｲｳﾟ", content.toString());
        assertFalse(content.hasDecodeError());

        bdata = Bseq.byteArray("8140:82A0:82A2:82A4:889F:EAA4");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("\u3000あいう亜熙", content.toString());
        assertFalse(content.hasDecodeError());

        bdata = Bseq.byteArray("5c");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("\\", content.toString());
        assertNotSame("\u00a5", content.toString());
        assertFalse(content.hasDecodeError());

        bdata = Bseq.byteArray("8d5c");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("構", content.toString());
        assertFalse(content.hasDecodeError());

        return;
    }

    private void assertUnmapError(DecodeErrorInfo einfo,
                                    int charPos,
                                    int b1, int b2 ){
        assertEquals(charPos, einfo.getCharPosition());
        assertTrue(einfo.has2nd());
        assertEquals((byte)b1, einfo.getRawByte1st());
        assertEquals((byte)b2, einfo.getRawByte2nd());
        return;
    }

    /**
     * Test of unmappable character.
     * @throws java.io.IOException
     * @throws DecodeBreakException
     */
    @Test
    public void testUnmap() throws IOException, DecodeBreakException{
        System.out.println("Unmap");

        SjisNotifier decoder;
        ContentBuilderSJ builder;
        byte[] bdata;
        InputStream istream;
        DecodedContent content;
        DecodeErrorInfo einfo;

        decoder = new SjisNotifier();
        builder = new ContentBuilderSJ();
        decoder.setCharDecodeListener(builder);

        bdata = Bseq.byteArray("41:8540:42"); // 9区
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("A?B", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertUnmapError(einfo, 1, 0x85, 0x40);

        bdata = Bseq.byteArray("41:8740:42"); // 13区
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("A?B", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertUnmapError(einfo, 1, 0x87, 0x40);

        bdata = Bseq.byteArray("41:8840:42"); // 15区
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("A?B", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertUnmapError(einfo, 1, 0x88, 0x40);

        bdata = Bseq.byteArray("41:EB40:42"); // 85区
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("A?B", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertUnmapError(einfo, 1, 0xEB, 0x40);

        bdata = Bseq.byteArray("41:ED40:42"); // 89区
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("A?B", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertUnmapError(einfo, 1, 0xED, 0x40);

        bdata = Bseq.byteArray("41:EEFC:42"); // 92区
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("A?B", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertUnmapError(einfo, 1, 0xEE, 0xFC);

        bdata = Bseq.byteArray("41:EF9F:42"); // 94区
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("A?B", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertUnmapError(einfo, 1, 0xEF, 0x9F);

        return;
    }

    private void assertMalformError(DecodeErrorInfo einfo,
                                      int charPos,
                                      int b1 ){
        assertEquals(charPos, einfo.getCharPosition());
        assertFalse(einfo.has2nd());
        assertEquals((byte)b1, einfo.getRawByte1st());
        return;
    }

    /**
     * Test of malformed character.
     * @throws java.io.IOException
     * @throws DecodeBreakException
     */
    @Test
    public void testMalform() throws IOException, DecodeBreakException{
        System.out.println("Malform");

        SjisNotifier decoder;
        ContentBuilderSJ builder;
        byte[] bdata;
        InputStream istream;
        DecodedContent content;
        DecodeErrorInfo einfo;

        decoder = new SjisNotifier();
        builder = new ContentBuilderSJ();
        decoder.setCharDecodeListener(builder);

        bdata = Bseq.byteArray("31:FD:FE:FF:32");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("1???2", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(3, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertMalformError(einfo, 1, 0xfd);
        einfo = content.getDecodeErrorList().get(1);
        assertMalformError(einfo, 2, 0xfe);
        einfo = content.getDecodeErrorList().get(2);
        assertMalformError(einfo, 3, 0xff);

        bdata = Bseq.byteArray("31:82:32:33");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("1?23", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertMalformError(einfo, 1, 0x82);

        bdata = Bseq.byteArray("31:32:33:82");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("123?", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertMalformError(einfo, 3, 0x82);

        return;
    }

    /**
     * Test of Bounds buffering.
     * @throws java.io.IOException
     * @throws DecodeBreakException
     */
    @Test
    public void testBounds() throws IOException, DecodeBreakException{
        System.out.println("Bounds");

        SjisNotifier decoder;
        ContentBuilderSJ builder;
        byte[] bdata;
        InputStream istream;
        DecodedContent content;
        DecodeErrorInfo einfo;

        decoder = new SjisNotifier(5, 5);
        builder = new ContentBuilderSJ();
        decoder.setCharDecodeListener(builder);

        bdata = Bseq.byteArray("31:32:33:34:88" + "9F:35");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("1234亜5", content.toString());
        assertFalse(content.hasDecodeError());
        assertEquals(0, content.getDecodeErrorList().size());

        bdata = Bseq.byteArray("31:32:33:34:82" + "35:36");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("1234?56", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertMalformError(einfo, 4, 0x82);

        bdata = Bseq.byteArray("31:32:33:34:87" + "40:35");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("1234?5", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertUnmapError(einfo, 4, 0x87, 0x40);

        decoder = new SjisNotifier(5, 3);
        builder = new ContentBuilderSJ();
        decoder.setCharDecodeListener(builder);

        bdata = Bseq.byteArray("31:32:33:34:35:36");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("123456", content.toString());
        assertFalse(content.hasDecodeError());
        assertEquals(0, content.getDecodeErrorList().size());

        return;
    }

}

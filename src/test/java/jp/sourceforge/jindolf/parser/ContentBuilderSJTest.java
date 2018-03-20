/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public static byte[] byteArray(CharSequence seq){
        byte[] result;

        List<Byte> byteList = new ArrayList<Byte>();

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
     * Test of SjisDecoder & ContentBuilder.
     * @throws java.io.IOException
     * @throws jp.sourceforge.jindolf.parser.DecodeException
     */
    @Test
    public void testDecoding() throws IOException, DecodeException{
        System.out.println("Decoding");

        SjisDecoder decoder;
        ContentBuilderSJ builder;
        byte[] bdata;
        InputStream istream;
        DecodedContent content;

        decoder = new SjisDecoder();
        builder = new ContentBuilderSJ();
        decoder.setDecodeHandler(builder);

        bdata = byteArray("20:41:42:43:7e");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals(" ABC~", content.toString());
        assertFalse(content.hasDecodeError());

        bdata = byteArray("");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("", content.toString());
        assertFalse(content.hasDecodeError());

        bdata = byteArray("00:0A:0D:1F");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("\u0000\n\r\u001f", content.toString());
        assertFalse(content.hasDecodeError());

        bdata = byteArray("A1:B1:B2:B3:DF");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("｡ｱｲｳﾟ", content.toString());
        assertFalse(content.hasDecodeError());

        bdata = byteArray("8140:82A0:82A2:82A4:889F:EAA4");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("\u3000あいう亜熙", content.toString());
        assertFalse(content.hasDecodeError());

        bdata = byteArray("5c");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("\\", content.toString());
        assertNotSame("\u00a5", content.toString());
        assertFalse(content.hasDecodeError());

        bdata = byteArray("8d5c");
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
     * @throws jp.sourceforge.jindolf.parser.DecodeException
     */
    @Test
    public void testUnmap() throws IOException, DecodeException{
        System.out.println("Unmap");

        SjisDecoder decoder;
        ContentBuilderSJ builder;
        byte[] bdata;
        InputStream istream;
        DecodedContent content;
        DecodeErrorInfo einfo;

        decoder = new SjisDecoder();
        builder = new ContentBuilderSJ();
        decoder.setDecodeHandler(builder);

        bdata = byteArray("41:8540:42"); // 9区
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("A?B", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertUnmapError(einfo, 1, 0x85, 0x40);

        bdata = byteArray("41:8740:42"); // 13区
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("A?B", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertUnmapError(einfo, 1, 0x87, 0x40);

        bdata = byteArray("41:8840:42"); // 15区
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("A?B", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertUnmapError(einfo, 1, 0x88, 0x40);

        bdata = byteArray("41:EB40:42"); // 85区
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("A?B", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertUnmapError(einfo, 1, 0xEB, 0x40);

        bdata = byteArray("41:ED40:42"); // 89区
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("A?B", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertUnmapError(einfo, 1, 0xED, 0x40);

        bdata = byteArray("41:EEFC:42"); // 92区
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("A?B", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertUnmapError(einfo, 1, 0xEE, 0xFC);

        bdata = byteArray("41:EF9F:42"); // 94区
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
     * @throws jp.sourceforge.jindolf.parser.DecodeException
     */
    @Test
    public void testMalform() throws IOException, DecodeException{
        System.out.println("Malform");

        SjisDecoder decoder;
        ContentBuilderSJ builder;
        byte[] bdata;
        InputStream istream;
        DecodedContent content;
        DecodeErrorInfo einfo;

        decoder = new SjisDecoder();
        builder = new ContentBuilderSJ();
        decoder.setDecodeHandler(builder);

        bdata = byteArray("31:FD:FE:FF:32");
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

        bdata = byteArray("31:82:32:33");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("1?23", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertMalformError(einfo, 1, 0x82);

        bdata = byteArray("31:32:33:82");
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
     * @throws jp.sourceforge.jindolf.parser.DecodeException
     */
    @Test
    public void testBounds() throws IOException, DecodeException{
        System.out.println("Bounds");

        SjisDecoder decoder;
        ContentBuilderSJ builder;
        byte[] bdata;
        InputStream istream;
        DecodedContent content;
        DecodeErrorInfo einfo;

        decoder = new SjisDecoder(5, 5);
        builder = new ContentBuilderSJ();
        decoder.setDecodeHandler(builder);

        bdata = byteArray("31:32:33:34:88" + "9F:35");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("1234亜5", content.toString());
        assertFalse(content.hasDecodeError());
        assertEquals(0, content.getDecodeErrorList().size());

        bdata = byteArray("31:32:33:34:82" + "35:36");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("1234?56", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertMalformError(einfo, 4, 0x82);

        bdata = byteArray("31:32:33:34:87" + "40:35");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("1234?5", content.toString());
        assertTrue(content.hasDecodeError());
        assertEquals(1, content.getDecodeErrorList().size());
        einfo = content.getDecodeErrorList().get(0);
        assertUnmapError(einfo, 4, 0x87, 0x40);

        decoder = new SjisDecoder(5, 3);
        builder = new ContentBuilderSJ();
        decoder.setDecodeHandler(builder);

        bdata = byteArray("31:32:33:34:35:36");
        istream = new ByteArrayInputStream(bdata);
        decoder.decode(istream);
        content = builder.getContent();
        assertEquals("123456", content.toString());
        assertFalse(content.hasDecodeError());
        assertEquals(0, content.getDecodeErrorList().size());

        return;
    }

}

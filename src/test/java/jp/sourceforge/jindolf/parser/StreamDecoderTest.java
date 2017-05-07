/*
 */

package jp.sourceforge.jindolf.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class StreamDecoderTest {

    public StreamDecoderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of decode method, of class StreamDecoder.
     * @throws IOException
     * @throws DecodeException
     */
    @Test
    public void testDecode() throws IOException, DecodeException {
        System.out.println("decode");

        Charset cs;
        CharsetDecoder decoder;

        StreamDecoder sd;
        InputStream is;
        TestHandler handler;

        cs = Charset.forName("US-ASCII");

        decoder = cs.newDecoder();
        sd = new StreamDecoder(decoder);
        is = new ByteArrayInputStream(new byte[]{});

        try{
            sd.decode(is);
            fail();
        }catch(NullPointerException e){
            // GOOD
        }

        handler = new TestHandler();
        sd.setDecodeHandler(handler);

        is = byteStream();
        handler.clear();
        sd.decode(is);
        assertEquals("[ST][EN]", handler.toString());

        is = byteStream(0x41, 0x42, 0x43);
        handler.clear();
        sd.decode(is);
        assertEquals("[ST][CH]ABC[EN]", handler.toString());

        is = byteStream(0x0d, 0x0a, 0x7f);
        handler.clear();
        sd.decode(is);
        assertEquals("[ST][CH]\r\n\u007f[EN]", handler.toString());

        is = byteStream(0x7e, 0x7f, 0x80, 0xfe, 0xff);
        handler.clear();
        sd.decode(is);
        assertEquals("[ST][CH]\u007e\u007f[ER]80[ER]fe[ER]ff[EN]", handler.toString());

        is = byteStream(0x41, 0x42, 0x80, 0x43);
        handler.clear();
        sd.decode(is);
        assertEquals("[ST][CH]AB[ER]80[CH]C[EN]", handler.toString());

        decoder = cs.newDecoder();
        sd = new StreamDecoder(decoder, 4, 100);
        sd.setDecodeHandler(handler);
        is = byteStream(0x41, 0x42, 0x43, 0x44, 0x45);
        handler.clear();
        sd.decode(is);
        assertEquals("[ST][CH]ABCDE[EN]", handler.toString());

        decoder = cs.newDecoder();
        sd = new StreamDecoder(decoder, 100, 4);
        sd.setDecodeHandler(handler);
        is = byteStream(0x41, 0x42, 0x43, 0x44, 0x45);
        handler.clear();
        sd.decode(is);
        assertEquals("[ST][CH]ABCDE[EN]", handler.toString());

        return;
    }

    static ByteArrayInputStream byteStream(int... array){
        byte[] ba = new byte[array.length];

        int idx = 0;
        for(int iVal : array){
            byte bVal = (byte)(iVal & 0xff);
            ba[idx++] = bVal;
        }

        return new ByteArrayInputStream(ba);
    }

    static class TestHandler implements DecodeHandler{

        private final StringBuilder text = new StringBuilder();
        private boolean notch = true;

        @Override
        public void startDecoding(CharsetDecoder decoder) throws DecodeException {
            this.text.append("[ST]");
            this.notch = true;
        }

        @Override
        public void endDecoding() throws DecodeException {
            this.text.append("[EN]");
            this.notch = true;
        }

        @Override
        public void charContent(CharSequence seq) throws DecodeException {
            if(this.notch){
                this.text.append("[CH]");
            }
            this.text.append(seq);
            this.notch = false;
        }

        @Override
        public void decodingError(byte[] errorArray, int offset, int length) throws DecodeException {
            for(int ct = 0; ct < length;ct++){
                this.text.append("[ER]");
                int val = errorArray[offset + ct] & 0xff;
                if(val <= 0xf) this.text.append('0');
                this.text.append(Integer.toHexString(val));
            }
            this.notch = true;
        }

        public void clear(){
            text.setLength(0);
            this.notch = true;
        }

        @Override
        public String toString(){
            return text.toString();
        }

    }

}

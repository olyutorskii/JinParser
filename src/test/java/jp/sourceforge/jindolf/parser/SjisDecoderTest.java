/*
 */

package jp.sourceforge.jindolf.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
public class SjisDecoderTest {

    public SjisDecoderTest() {
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

    public static ByteArrayInputStream byteIs(CharSequence seq){
        byte[] bs = byteArray(seq);
        ByteArrayInputStream result = new ByteArrayInputStream(bs);
        return result;
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
     * Test of class SjisDecoder.
     * @throws Exception
     */
    @Test
    public void testSjisDecoder() throws Exception {
        SjisDecoder sjd;
        InputStream is;
        TestHandler handler;

        handler = new TestHandler();

        sjd = new SjisDecoder(10, 10);
        sjd.setDecodeHandler(handler);
        is = byteIs("414243");
        handler.clear();
        sjd.decode(is);
        assertEquals("[ST][CH]ABC[EN]", handler.toString());

        sjd = new SjisDecoder(10, 2);
        sjd.setDecodeHandler(handler);
        is = byteIs("414243");
        handler.clear();
        sjd.decode(is);
        assertEquals("[ST][CH]AB[CH]C[EN]", handler.toString());

        sjd = new SjisDecoder(2, 10);
        sjd.setDecodeHandler(handler);
        is = byteIs("82a0:82a1");
        handler.clear();
        sjd.decode(is);
        assertEquals("[ST][CH]あぃ[EN]", handler.toString());

        sjd = new SjisDecoder(2, 10);
        sjd.setDecodeHandler(handler);
        is = byteIs("41:82a0:82a1");
        handler.clear();
        sjd.decode(is);
        assertEquals("[ST][CH]Aあぃ[EN]", handler.toString());

        sjd = new SjisDecoder(1, 10);
        sjd.setDecodeHandler(handler);
        is = byteIs("82a0:82a1");
        handler.clear();
        try{
            sjd.decode(is);
            fail();
        }catch(DecodeException e){
            // GOOD
        }

        return;
    }

    static class TestHandler implements DecodeHandler{

        private final StringBuilder text = new StringBuilder();

        @Override
        public void startDecoding(CharsetDecoder decoder) throws DecodeException {
            this.text.append("[ST]");
        }

        @Override
        public void endDecoding() throws DecodeException {
            this.text.append("[EN]");
        }

        @Override
        public void charContent(CharSequence seq) throws DecodeException {
            this.text.append("[CH]");
            this.text.append(seq);
        }

        @Override
        public void decodingError(byte[] errorArray, int offset, int length) throws DecodeException {
            this.text.append("[ER]");
            for(int ct = 0; ct < length;ct++){
                int val = errorArray[offset + ct] & 0xff;
                if(val <= 0xf) this.text.append('0');
                this.text.append(Integer.toHexString(val));
            }
        }

        public void clear(){
            text.setLength(0);
        }

        @Override
        public String toString(){
            return text.toString();
        }

    }

}

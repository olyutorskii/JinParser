/*
 */

package jp.osdn.jindolf.parser.content;

import io.bitbucket.olyutorskii.jiocema.DecodeNotifier;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
public class SjisNotifierTest {



    public SjisNotifierTest() {
    }

    @Test
    public void testConstructor() throws Exception{
        DecodeNotifier decoder;

        decoder = new SjisNotifier();
        assert decoder == decoder;

        decoder = new SjisNotifier(2, 2);
        assert decoder == decoder;

        try{
            decoder = new SjisNotifier(1, 2);
            fail();
        }catch(IllegalArgumentException e){
            assertEquals("input buffer length must be 2 or more for Shift_JIS", e.getMessage());
        }

        try{
            decoder = new SjisNotifier(2, 1);
            fail();
        }catch(IllegalArgumentException e){
            assertEquals("output buffer length must be 2 or more for surrogate pair", e.getMessage());
        }

        assert decoder == decoder;

        return;
    }

    @Test
    public void testJisX0201() throws Exception{
        DecodeNotifier decoder;
        TestListener lst;
        InputStream is;

        decoder = new SjisNotifier();

        lst = new TestListenerRW();
        decoder.setCharDecodeListener(lst);

        // test JISX0201 ASCII decoding

        lst.clear();
        is = Bseq.byteStream(0x00, 0x1f, 0x20, 0x21, 0x7f);
        decoder.decode(is);
        assertEquals("[ST][RW]001f20217f[CH]\u0000\u001f\u0020\u0021\u007f[EN]", lst.toString());

        // test JISX0201 Hankaku-Katakana decoding

        lst.clear();
        is = Bseq.byteStream(0xa1, 0xb1, 0xdf);
        decoder.decode(is);
        assertEquals("[ST][RW]a1b1df[CH]\uff61\uff71\uff9f[EN]", lst.toString());

        return;
    }

    @Test
    public void testJisX0208() throws Exception{
        DecodeNotifier decoder;
        TestListener lst;
        InputStream is;

        decoder = new SjisNotifier();

        lst = new TestListenerRW();
        decoder.setCharDecodeListener(lst);

        // test JISX0208 ASCII decoding

        lst.clear();
        is = Bseq.byteStream(0x88, 0x9f, 0xea, 0xa4);
        decoder.decode(is);
        assertEquals("[ST][RW]889feaa4[CH]\u4e9c\u7199[EN]", lst.toString());

        return;
    }

    @Test
    public void testInvalid1st() throws Exception{
        DecodeNotifier decoder;
        TestListener lst;
        InputStream is;

        decoder = new SjisNotifier();

        lst = new TestListenerRW();
        decoder.setCharDecodeListener(lst);

        // test invalid 1st character decoding
        // 0x80, 0xa0, 0xf0 - 0xff

        lst.clear();
        is = Bseq.byteStream(0x80);
        decoder.decode(is);
        assertEquals("[ST][ME]80[EN]", lst.toString());

        lst.clear();
        is = Bseq.byteStream(0x80, 0x41);
        decoder.decode(is);
        assertEquals("[ST][ME]80[RW]41[CH]A[EN]", lst.toString());

        // 0x80+あ
        lst.clear();
        is = Bseq.byteStream(0x80, 0x82, 0xa0);
        decoder.decode(is);
        assertEquals("[ST][ME]80[RW]82a0[CH]\u3042[EN]", lst.toString());

        lst.clear();
        is = Bseq.byteStream(0x80, 0x80);
        decoder.decode(is);
        assertEquals("[ST][ME]80[ME]80[EN]", lst.toString());

        lst.clear();
        is = Bseq.byteStream(0xa0);
        decoder.decode(is);
        assertEquals("[ST][ME]a0[EN]", lst.toString());

        lst.clear();
        is = Bseq.byteStream(0xf0);
        decoder.decode(is);
        assertEquals("[ST][ME]f0[EN]", lst.toString());

        lst.clear();
        is = Bseq.byteStream(0xff);
        decoder.decode(is);
        assertEquals("[ST][ME]ff[EN]", lst.toString());

        lst.clear();
        is = Bseq.byteStream(0xfd, 0xfe, 0xff);
        decoder.decode(is);
        assertEquals("[ST][ME]fd[ME]fe[ME]ff[EN]", lst.toString());

        lst.clear();
        is = Bseq.byteStream(0xff, 0x32);
        decoder.decode(is);
        assertEquals("[ST][ME]ff[RW]32[CH]2[EN]", lst.toString());

        lst.clear();
        is = Bseq.byteStream(0x41, 0x42, 0x43, 0xff, 0x32);
        decoder.decode(is);
        assertEquals("[ST][RW]414243[CH]ABC[ME]ff[RW]32[CH]2[EN]", lst.toString());

        return;
    }

    @Test
    public void testInvalid2nd() throws Exception{
        DecodeNotifier decoder;
        TestListener lst;
        InputStream is;

        decoder = new SjisNotifier();

        lst = new TestListenerRW();
        decoder.setCharDecodeListener(lst);

        // test invalid 2nd character decoding
        // 0x00 - 0x3f, 0x7f, 0xfd - 0xff

        lst.clear();
        is = Bseq.byteStream(0x81, 0x00);
        decoder.decode(is);
        assertEquals("[ST][ME]81[RW]00[CH]\u0000[EN]", lst.toString());

        lst.clear();
        is = Bseq.byteStream(0x81, 0x3f);
        decoder.decode(is);
        assertEquals("[ST][ME]81[RW]3f[CH]\u003f[EN]", lst.toString());

        lst.clear();
        is = Bseq.byteStream(0x81, 0x7f);
        decoder.decode(is);
//        assertEquals("[ST][RW]817f[CH]\u00f7[EN]", lst.toString()); // 1.7
//        assertEquals("[ST][ME]81[RW]7f[CH]\u007f[EN]", lst.toString()); // 1.8

        lst.clear();
        is = Bseq.byteStream(0x81, 0xfd);
        decoder.decode(is);
        assertEquals("[ST][ME]81[ME]fd[EN]", lst.toString());

        lst.clear();
        is = Bseq.byteStream(0x81, 0xfe);
        decoder.decode(is);
        assertEquals("[ST][ME]81[ME]fe[EN]", lst.toString());

        lst.clear();
        is = Bseq.byteStream(0x81, 0xff);
        decoder.decode(is);
        assertEquals("[ST][ME]81[ME]ff[EN]", lst.toString());

        return;
    }

    @Test
    public void testUnmap() throws Exception{
        DecodeNotifier decoder;
        TestListener lst;
        InputStream is;

        decoder = new SjisNotifier(4, 4);

        lst = new TestListenerRW();
        decoder.setCharDecodeListener(lst);

        // test unmap error

        lst.clear();
        is = Bseq.byteStream(0x85, 0x40);
        decoder.decode(is);
        assertEquals("[ST][UE]8540[EN]", lst.toString());

        lst.clear();
        is = Bseq.byteStream(0x41, 0x42, 0x43, 0x85, 0x40);
        decoder.decode(is);
        assertEquals("[ST][RW]414243[CH]ABC[UE]8540[EN]", lst.toString());

        return;
    }

    @Test
    public void testSomeMethod() throws Exception{
        DecodeNotifier decoder;
        TestListener lst;
        InputStream is;

        // test decoding

        decoder = new SjisNotifier();

        lst = new TestListenerRW();
        decoder.setCharDecodeListener(lst);

        lst.clear();
        is = Bseq.byteStream();
        decoder.decode(is);
        assertEquals("[ST][EN]", lst.toString());

        lst.clear();
        is = Bseq.byteStream(0x41);
        decoder.decode(is);
        assertEquals("[ST][RW]41[CH]A[EN]", lst.toString());

        return;
    }

}

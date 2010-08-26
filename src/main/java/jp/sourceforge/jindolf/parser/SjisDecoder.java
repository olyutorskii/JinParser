/*
 * stream decoder for Shift_JIS
 *
 * Copyright(c) 2009 olyutorskii
 * $Id: SjisDecoder.java 894 2009-11-04 07:26:59Z olyutorskii $
 */

package jp.sourceforge.jindolf.parser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CoderResult;

/**
 * Shift_JISバイト列のデコードに特化した、{@link StreamDecoder}の派生クラス。
 * Java実行系の細かな仕様差異による
 * デコードエラー出現パターンゆらぎの正規化も行う。
 * 0x5Cが{@literal U+005C}にデコードされるか
 * {@literal U+00A5}にデコードされるかはJava実行系の実装依存。
 * @see <a href="http://www.iana.org/assignments/character-sets">
 * CHARACTER SETS</a>
 */
public class SjisDecoder extends StreamDecoder{

    /** 入力バッファに必要な最小サイズ(={@value})。 */
    public static final int MIN_INBUFSZ = ShiftJis.MAX_BYTES_PER_CHAR * 2 + 1;

    static{
        assert MIN_INBUFSZ <= BYTEBUF_DEFSZ;
    }

    /**
     * コンストラクタ。
     */
    public SjisDecoder(){
        this(BYTEBUF_DEFSZ, CHARBUF_DEFSZ);
        return;
    }

    /**
     * コンストラクタ。
     * @param inbuf_sz 入力バッファサイズ
     * @param outbuf_sz 出力バッファサイズ
     * @throws IllegalArgumentException バッファサイズが小さすぎる。
     */
    public SjisDecoder(int inbuf_sz, int outbuf_sz)
            throws IllegalArgumentException{
        super(ShiftJis.CHARSET.newDecoder(), inbuf_sz, outbuf_sz);
        if(inbuf_sz < MIN_INBUFSZ){
            throw new IllegalArgumentException();
        }
        return;
    }

    /**
     * 1バイトのエラーを2バイトに統合できないか試す。
     * @param result デコード異常系
     * @return 修正されたデコード異常系。修正がなければ引数と同じものを返す。
     * @throws IOException 入力エラー
     */
    private CoderResult modify1ByteError(CoderResult result)
            throws IOException {
        ByteBuffer inbuffer = getByteBuffer();

        int currPos;
        int nextPos;

        currPos = inbuffer.position();
        nextPos = currPos + 1;
        if(nextPos >= inbuffer.limit()){
            readByteBuffer();
            currPos = inbuffer.position();
            nextPos = currPos + 1;
        }

        // 入力バイト列の最後がこのデコードエラーだった場合。
        if(nextPos >= inbuffer.limit()) return result;

        byte curr = inbuffer.get(currPos);    // 絶対的get
        byte next = inbuffer.get(nextPos);
        if( ShiftJis.isShiftJIS(curr, next) ){
            return CoderResult.unmappableForLength(2);
        }

        return result;
    }

    /**
     * 2バイトのエラーを1バイトに分割できないか試す。
     * ※ バイト列"FF:32" のShift_JISデコードに際して、
     * 2バイト長のデコードエラーを返す1.6系実行系が存在する。
     * @param result デコード異常系
     * @return 修正されたデコード異常系。修正がなければ引数と同じものを返す。
     */
    private CoderResult modify2ByteError(CoderResult result){
        ByteBuffer inbuffer = getByteBuffer();

        int currPos;
        int nextPos;

        currPos = inbuffer.position();
        nextPos = currPos + 1;

        byte curr = inbuffer.get(currPos);    // 絶対的get
        byte next = inbuffer.get(nextPos);
        if( ! ShiftJis.isShiftJIS(curr, next) ){
            return CoderResult.malformedForLength(1);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * ライブラリ実装によるシフトJISデコードエラー出現パターンの
     * 差異を吸収する。
     * @param result {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    protected int chopErrorSequence(CoderResult result)
            throws IOException{
        int errorLength = result.length();

        CoderResult newResult;
        if(errorLength == 1){
            newResult = modify1ByteError(result);
        }else if(errorLength == 2){
            newResult = modify2ByteError(result);
        }else{
            assert false;
            return -1;
        }

        return super.chopErrorSequence(newResult);
    }

}

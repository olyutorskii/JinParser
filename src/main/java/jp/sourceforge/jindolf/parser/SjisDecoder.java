/*
 * stream decoder for Shift_JIS
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CoderResult;

/**
 * Shift_JISバイト列のデコードエラーに特化した、
 * {@link StreamDecoder}の派生クラス。
 *
 * <p>Java実行系の細かな仕様差異による
 * デコードエラー出現パターンゆらぎの正規化を行う。
 *
 * <p>0x5Cが{@literal U+005C}にデコードされるか
 * {@literal U+00A5}にデコードされるかはJava実行系の実装依存。
 *
 * @see <a href="http://www.iana.org/assignments/character-sets">
 * CHARACTER SETS</a>
 */
public class SjisDecoder extends StreamDecoder{

    /**
     * コンストラクタ。
     */
    public SjisDecoder(){
        this(BYTEBUF_DEFSZ, CHARBUF_DEFSZ);
        return;
    }

    /**
     * コンストラクタ。
     *
     * @param inbufSz 入力バッファサイズ
     * @param outbufSz 出力バッファサイズ
     * @throws IllegalArgumentException バッファサイズが小さすぎる。
     */
    public SjisDecoder(int inbufSz, int outbufSz)
            throws IllegalArgumentException{
        super(ShiftJis.CHARSET.newDecoder(), inbufSz, outbufSz);
        return;
    }


    /**
     * 1バイトのエラーをUnmap系2バイトエラーに統合できないか試す。
     *
     * <p>必要に応じて1バイト以上の追加先読みを行う。
     *
     * <p>バイト列 {0x85,0x40}(未割り当て9区の文字) を、
     * 1バイトのエラーと文字@に分離するJava実行系への対処。
     *
     * @param result デコード異常系
     * @return 修正されたデコード異常系。修正がなければ引数と同じものを返す。
     * @throws IOException 追加入力エラー
     */
    private CoderResult modify1ByteError(CoderResult result)
            throws IOException {
        assert result.length() == 1;

        ByteBuffer inbuffer = getByteBuffer();

        if(inbuffer.remaining() < 2){
            fillByteBuffer();
        }
        // 入力バイト列の最後がこの1バイトエラーだった場合。
        if(inbuffer.remaining() < 2) return result;

        int currPos = inbuffer.position();
        int nextPos = currPos + 1;

        // 絶対的get
        byte curr = inbuffer.get(currPos);
        byte next = inbuffer.get(nextPos);

        CoderResult newResult;
        if( ShiftJis.isShiftJIS(curr, next) ){
            newResult = CoderResult.unmappableForLength(2);
        }else{
            newResult = result;
        }

        return newResult;
    }

    /**
     * 2バイトのエラーを1バイトに分割できないか試す。
     *
     * <p>※ バイト列"FF:32" のShift_JISデコードに際して、
     * 2バイト長のデコードエラーを返す1.6系実行系が存在する。
     *
     * @param result デコード異常系
     * @return 修正されたデコード異常系。修正がなければ引数と同じものを返す。
     */
    private CoderResult modify2ByteError(CoderResult result){
        assert result.length() == 2;

        ByteBuffer inbuffer = getByteBuffer();
        assert inbuffer.remaining() >= 2;

        int currPos = inbuffer.position();
        int nextPos = currPos + 1;

        byte curr = inbuffer.get(currPos);    // 絶対的get
        byte next = inbuffer.get(nextPos);

        CoderResult newResult;
        if( ShiftJis.isShiftJIS(curr, next) ){
            newResult = result;
        }else{
            newResult = CoderResult.malformedForLength(1);
        }

        return newResult;
    }

    /**
     * {@inheritDoc}
     *
     * <p>シフトJISデコードエラー出現パターンの
     * ランタイム実装による差異を吸収する。
     *
     * @param result {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    protected CoderResult modifyErrorLength(CoderResult result)
            throws IOException{
        int errorLength = result.length();

        CoderResult newResult;
        switch (errorLength) {
        case 1:
            newResult = modify1ByteError(result);
            break;
        case 2:
            newResult = modify2ByteError(result);
            break;
        default:
            newResult = result;
            break;
        }

        return newResult;
    }

}

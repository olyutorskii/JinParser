/*
 * Shift_JIS decode notifier
 *
 * License : The MIT License
 * Copyright(c) 2018 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import io.bitbucket.olyutorskii.jiocema.DecodeNotifier;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CoderResult;

/**
 * Shift_JISバイト列のデコードエラーに特化した、
 * {@link DecodeNotifier}の派生クラス。
 *
 * <p>Javaランタイムの細かな仕様差異による
 * デコードエラー出現パターンゆらぎの正規化を行う。
 *
 * <ul>
 *
 * <li>バイト列 [0xff:0x32]や[0x81:0xfd]を
 * 2バイト長Unmapエラーとして検出してしまう
 * Javaランタイムへの対処。
 *
 * <li>バイト列 [0x85,0x40](未割り当て9区の文字) を、
 * 1バイトのエラーと文字@に分離してしまうJavaランタイムへの対処。
 *
 * <li>バイト列 [0x80:0x41]先頭を1バイト長Unmapエラーとして検出してしまう
 * Javaランタイムへの対処。
 *
 * </ul>
 *
 * <p>TODO: 1.7系ランタイムによっては
 * [0x81, 0x7f]が「÷」にデコードされる場合がある問題が未解決。
 *
 * @see <a href="https://en.wikipedia.org/wiki/Shift_JIS">Shift_JIS</a>
 * @see sun.nio.cs.ext.SJIS
 */
public class SjisNotifier extends DecodeNotifier{

    private static final String MSGFORM_SJBUFLEN =
            "input buffer length must be 2 or more for Shift_JIS";


    /**
     * コンストラクタ。
     *
     * <p>デコーダにはShift_JIS用ランタイムが用いられる。
     *
     * <p>バッファサイズはデフォルト値が用いられる。
     *
     * @see DecodeNotifier#DEFSZ_BYTEBUF
     * @see DecodeNotifier#DEFSZ_CHARBUF
     */
    public SjisNotifier(){
        this(DEFSZ_BYTEBUF, DEFSZ_CHARBUF);
        return;
    }

    /**
     * コンストラクタ。
     *
     * <p>デコーダにはShift_JIS用ランタイムが用いられる。
     *
     * @param inbufSz 入力バッファサイズ。
     *     シフトJIS上位下位のため2以上を指定しなければならない。
     * @param outbufSz 出力バッファサイズ。
     *     サロゲートペア格納のため2以上を指定しなければならない。
     * @throws IllegalArgumentException 不適切なバッファサイズ
     */
    public SjisNotifier(int inbufSz, int outbufSz)
            throws IllegalArgumentException {
        super(ShiftJis.CHARSET.newDecoder(), inbufSz, outbufSz);

        if(inbufSz < 2){
            throw new IllegalArgumentException(MSGFORM_SJBUFLEN);
        }

        return;
    }


    /**
     * Javaランタイムの差異によるシフトJISデコードエラーの揺らぎを正規化する。
     *
     * <ul>
     *
     * <li>2バイト長のUnmapエラーがシフトJISの形式を満たさない場合、
     * 1バイト長のMalformedエラーに修正する。
     *
     * <li>2バイト長でない、もしくはUnmapでないエラー時に、
     * 入力バッファ未読部先頭がシフトJISの形式を満たす場合、
     * 2バイト長のUnmapエラーに修正する。
     *
     * <li>1バイト長のUnmapエラーで
     * 入力バッファ未読部先頭がシフトJISの形式を満たさない場合、
     * 1バイト長のMalformedエラーに修正する。
     *
     * </ul>
     *
     * <p>必要に応じて1バイト以上の追加先読みを行う。
     *
     * <p>{@inheritDoc}
     *
     * @param errInfo {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    protected CoderResult modifyErrorResult(CoderResult errInfo)
            throws IOException {
        boolean unmapSingle = false;
        boolean unmapDouble = false;
        if(errInfo.isUnmappable()){
            int errorLength = errInfo.length();
            switch(errorLength){
            case 1: unmapSingle = true; break;
            case 2: unmapDouble = true; break;
            default:                    break;
            }
        }

        if(unmapDouble){
            return modifyUnmapDoubleError(errInfo);
        }

        boolean detectSjis = false;
        if(fillDoubleBytes()){
            if(isSjisHeadErr()){
                detectSjis = true;
            }
        }

        CoderResult newResult;
        if(detectSjis){
            newResult = CoderResult.unmappableForLength(2);
        }else if(unmapSingle){
            newResult = CoderResult.malformedForLength(1);
        }else{
            newResult = errInfo;
        }

        return newResult;
    }

    /**
     * modify 2-byte unmap decode error.
     *
     * <p>if 2-bytes sequence is invalid Shift_JIS,
     * single-byte malformed error will be return.
     *
     * <p>Yes, [81:fd] must not be Unmap-error.
     *
     * @param errInfo original error information
     * @return modified error information
     */
    private CoderResult modifyUnmapDoubleError(CoderResult errInfo){
        assert errInfo.isUnmappable();
        assert errInfo.length() == 2;

        CoderResult newResult;
        if(isSjisHeadErr()){
            newResult = errInfo;
        }else{
            newResult = CoderResult.malformedForLength(1);
        }

        return newResult;
    }

    /**
     * 入力バッファ未読部長が2バイト以上になるまで入力を進める。
     *
     * @return 未読部長が2バイト未満の段階で入力が終了したらfalse
     * @throws IOException 入力エラー
     */
    private boolean fillDoubleBytes() throws IOException{
        ByteBuffer inbuffer = getByteBuffer();
        while(inbuffer.remaining() < 2){
            if( ! hasMoreInput()) return false;
            supplyInputBytes();
        }
        return true;
    }

    /**
     * 入力バッファ未読部先頭がシフトJISの2バイト長文字形式か判定する。
     *
     * <p>入力バッファ未読部の長さが2未満の場合は常に偽となる。
     *
     * <p>文字集合がJIS X0208に収まるか否かのUnmap判定は行わない。
     *
     * @return シフトJISの2バイト長文字形式ならtrue
     */
    private boolean isSjisHeadErr(){
        ByteBuffer inbuffer = getByteBuffer();

        if(inbuffer.remaining() < 2) return false;
        int currPos = inbuffer.position();
        int nextPos = currPos + 1;

        byte curr = inbuffer.get(currPos);
        byte next = inbuffer.get(nextPos);

        boolean result;
        result = ShiftJis.isShiftJIS(curr, next);

        return result;
    }

}

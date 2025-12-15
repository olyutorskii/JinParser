/*
 * content builder
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.osdn.jindolf.parser.content;

import io.bitbucket.olyutorskii.jiocema.CharDecodeListener;
import io.bitbucket.olyutorskii.jiocema.DecodeBreakException;
import java.nio.charset.CharsetDecoder;

/**
 * 文字列デコード通知から
 * {@link DecodedContent}を生成するためのデコードリスナ。
 */
public class ContentBuilder implements CharDecodeListener{

    private static final int DEF_CAPA_SZ = 128;


    /** 文字列内容。 */
    private final DecodedContent content;


    /**
     * コンストラクタ。
     */
    public ContentBuilder(){
        this(DEF_CAPA_SZ);
    }

    /**
     * コンストラクタ。
     *
     * @param capacity 初期容量
     * @throws NegativeArraySizeException 容量指定が負。
     */
    public ContentBuilder(int capacity) throws NegativeArraySizeException{
        super();
        this.content = new DecodedContent(capacity);
        return;
    }


    /**
     * デコード結果の{@link DecodedContent}を取得する。
     *
     * @return デコード結果文字列
     */
    public DecodedContent getContent(){
        return this.content;
    }

    /**
     * Receive notification of the beginning of decoding.
     *
     * @param decoder character decoder
     * @throws DecodeBreakException DecodeBreakException Throw
     *     if you want to break decoding immediately.
     */
    @Override
    public void startDecoding(CharsetDecoder decoder)
            throws DecodeBreakException{
        this.content.init();
        return;
    }

    /**
     * Receive notification of the end of decoding.
     *
     * @throws DecodeBreakException Throw if you want to break
     *     decoding immediately.
     */
    @Override
    public void endDecoding()
            throws DecodeBreakException{
        // NOTHING
        return;
    }

    /**
     * Receive notification
     * that raw bytes on input-byte-buffer has been consumed.
     *
     * <p>Error byte sequence is not included.
     *
     * <p>If you want to use byte sequence later,
     * you must copy it before return.
     *
     * @param byteArray byte array containing raw bytes
     * @param offset raw bytes start position
     * @param length raw bytes length
     */
    @Override
    public void rawBytes(byte[] byteArray, int offset, int length){
        // NOTHING
        return;
    }

    /**
     * Receive notification of decoded character sequence.
     *
     * <p>If you want to use character sequence later,
     * you must copy it before return.
     *
     * @param charArray char array containing character sequence
     * @param offset character sequence start position
     * @param length character sequence length
     * @throws DecodeBreakException Throw if you want to break
     *      decoding immediately.
     */
    @Override
    public void charContent(char[] charArray, int offset, int length)
            throws DecodeBreakException{
        if(length > 0){
            char ch1st  = charArray[0];
            assert ! Character.isLowSurrogate(ch1st);

            char chLast = charArray[length - 1];
            assert ! Character.isHighSurrogate(chLast);
        }

        getContent().append(charArray, offset, length);

        return;
    }

    /**
     * Receive notification of malformed-sequence error.
     *
     * <p>If you want to use error sequence later,
     * you must copy it before return.
     *
     * @param errorArray byte array containing error sequence
     * @param offset error sequence start position
     * @param length error sequence length
     * @throws DecodeBreakException Throw if you want to break
     *      decoding immediately.
     */
    @Override
    public void malformedError(byte[] errorArray, int offset, int length)
            throws DecodeBreakException {
        decodingError(errorArray, offset, length);
        return;
    }

    /**
     * Receive notification of character-set unmapping error.
     *
     * <p>If you want to use error sequence later,
     * you must copy it before return.
     *
     * @param errorArray byte array containing error sequence
     * @param offset error sequence start position
     * @param length error sequence length
     * @throws DecodeBreakException Throw if you want to break
     *      decoding immediately.
     */
    @Override
    public void unmapError(byte[] errorArray, int offset, int length)
            throws DecodeBreakException {
        decodingError(errorArray, offset, length);
        return;
    }

    /**
     * デコードエラーの受信。
     *
     * <p>エラーを構成する各バイトから一つずつエラー情報を生成する。
     *
     * @param errorArray エラーの含まれるバイト倍列
     * @param offset オフセット
     * @param length 長さ
     */
    private void decodingError(byte[] errorArray, int offset, int length){
        DecodedContent text = getContent();

        int limit = offset + length;
        for(int bpos = offset; bpos < limit; bpos++){
            byte bVal = errorArray[bpos];
            text.addDecodeError(bVal);
        }

        return;
    }

}

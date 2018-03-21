/*
 * content builder
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

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
     * {@inheritDoc}
     *
     * @param decoder {@inheritDoc}
     * @throws DecodeBreakException {@inheritDoc}
     */
    @Override
    public void startDecoding(CharsetDecoder decoder)
            throws DecodeBreakException{
        this.content.init();
        return;
    }

    /**
     * {@inheritDoc}
     *
     * @throws DecodeBreakException {@inheritDoc}
     */
    @Override
    public void endDecoding()
            throws DecodeBreakException{
        // NOTHING
        return;
    }

    /**
     * {@inheritDoc}
     *
     * @param byteArray {@inheritDoc}
     * @param offset {@inheritDoc}
     * @param length {@inheritDoc}
     */
    @Override
    public void rawBytes(byte[] byteArray, int offset, int length){
        // NOTHING
        return;
    }

    /**
     * {@inheritDoc}
     *
     * @param charArray {@inheritDoc}
     * @param offset {@inheritDoc}
     * @param length {@inheritDoc}
     * @throws DecodeBreakException {@inheritDoc}
     */
    @Override
    public void charContent(char[] charArray, int offset, int length)
            throws DecodeBreakException{
        getContent().append(charArray, offset, length);
        return;
    }

    /**
     * {@inheritDoc}
     *
     * @param errorArray {@inheritDoc}
     * @param offset {@inheritDoc}
     * @param length {@inheritDoc}
     * @throws DecodeBreakException {@inheritDoc}
     */
    @Override
    public void malformedError(byte[] errorArray, int offset, int length)
            throws DecodeBreakException {
        decodingError(errorArray, offset, length);
        return;
    }

    /**
     * {@inheritDoc}
     *
     * @param errorArray {@inheritDoc}
     * @param offset {@inheritDoc}
     * @param length {@inheritDoc}
     * @throws DecodeBreakException {@inheritDoc}
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

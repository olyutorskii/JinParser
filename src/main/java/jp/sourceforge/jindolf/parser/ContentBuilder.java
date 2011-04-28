/*
 * abstract content builder
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import java.nio.charset.CharsetDecoder;

/**
 * {@link DecodedContent}取得用抽象デコードハンドラ。
 */
public abstract class ContentBuilder implements DecodeHandler{

    /** 文字列内容。 */
    protected DecodedContent content;

    /**
     * コンストラクタ。
     * 長さ0で空の{@link DecodedContent}がセットされる。
     * @param capacity 初期容量
     * @throws NegativeArraySizeException 容量指定が負。
     */
    protected ContentBuilder(int capacity) throws NegativeArraySizeException{
        super();
        this.content = new DecodedContent(capacity);
        return;
    }

    /**
     * デコード処理の初期化。
     */
    protected void init(){
        return;
    }

    /**
     * エラー情報をフラッシュする。
     */
    protected void flushError(){
        return;
    }

    /**
     * {@inheritDoc}
     * @param decoder {@inheritDoc}
     * @throws DecodeException {@inheritDoc}
     */
    @Override
    public void startDecoding(CharsetDecoder decoder)
            throws DecodeException{
        init();
        return;
    }

    /**
     * {@inheritDoc}
     * @throws DecodeException {@inheritDoc}
     */
    @Override
    public void endDecoding()
            throws DecodeException{
        flushError();
        return;
    }

    /**
     * デコード結果の{@link DecodedContent}を取得する。
     * @return デコード結果文字列
     */
    public DecodedContent getContent(){
        return this.content;
    }

}

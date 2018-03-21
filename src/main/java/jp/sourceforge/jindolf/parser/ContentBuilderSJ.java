/*
 * content builder for Shift_JIS
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import io.bitbucket.olyutorskii.jiocema.DecodeBreakException;

/**
 * Shift_JIS 文字列のデコード通知から
 * {@link DecodedContent}を生成するためのデコードリスナ。
 *
 * <p>2バイト系Unmapエラーを普通のデコードエラーと区別する。
 */
public class ContentBuilderSJ extends ContentBuilder{

    private static final int DEF_BUF_SZ = 128;


    /**
     * コンストラクタ。
     */
    public ContentBuilderSJ(){
        this(DEF_BUF_SZ);
        return;
    }

    /**
     * コンストラクタ。
     *
     * @param capacity 初期容量
     * @throws NegativeArraySizeException 容量指定が負。
     */
    public ContentBuilderSJ(int capacity) throws NegativeArraySizeException{
        super(capacity);
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
        assert length == 2;

        byte b0 = errorArray[offset];
        byte b1 = errorArray[offset + 1];

        DecodedContent text = getContent();
        text.addDecodeError(b0, b1);

        return;
    }

}

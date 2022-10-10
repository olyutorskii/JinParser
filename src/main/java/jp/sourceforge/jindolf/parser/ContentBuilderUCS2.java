/*
 * content builder for UTF-8
 *
 * License : The MIT License
 * Copyright(c) 2010 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

/**
 * "UTF-8"エンコーディング用デコードハンドラ。
 * {@link StreamDecoder}からの通知に従い、
 * {@link DecodedContent}へとデコードする。
 */
public class ContentBuilderUCS2 extends ContentBuilder{

    private static final int DEF_BUF_SZ = 128;


    /**
     * コンストラクタ。
     * 長さ0で空の{@link DecodedContent}がセットされる。
     */
    public ContentBuilderUCS2(){
        this(DEF_BUF_SZ);
        return;
    }

    /**
     * コンストラクタ。
     * 長さ0で空の{@link DecodedContent}がセットされる。
     * @param capacity 初期容量
     * @throws NegativeArraySizeException 容量指定が負。
     */
    public ContentBuilderUCS2(int capacity)
            throws NegativeArraySizeException{
        super(capacity);
        initImpl();
        return;
    }


    /**
     * デコード処理の初期化下請。
     */
    private void initImpl(){
        this.getContent().init();
        return;
    }

    /**
     * デコード処理の初期化。
     */
    @Override
    protected void init(){
        initImpl();
        return;
    }

    /**
     * {@inheritDoc}
     * @param seq {@inheritDoc}
     * @throws DecodeException {@inheritDoc}
     */
    @Override
    public void charContent(CharSequence seq)
            throws DecodeException{
        flushError();
        getContent().append(seq);
        return;
    }

    /**
     * {@inheritDoc}
     * @param errorArray {@inheritDoc}
     * @param offset {@inheritDoc}
     * @param length {@inheritDoc}
     * @throws DecodeException {@inheritDoc}
     */
    @Override
    public void decodingError(byte[] errorArray, int offset, int length)
            throws DecodeException{
        int limit = offset + length;

        for(int bpos = offset; bpos < limit; bpos++){
            byte bval = errorArray[bpos];
            getContent().addDecodeError(bval);
        }

        return;
    }

}

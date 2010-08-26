/*
 * content builder for UTF-8 (UCS2 only)
 *
 * License : The MIT License
 * Copyright(c) 2010 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

/**
 * "UTF-8"エンコーディング用デコードハンドラ。
 * {@link StreamDecoder}からの通知に従い、
 * {@link DecodedContent}へとデコードする。
 * UCS-4はUTF-16エラー扱い。
 */
public class ContentBuilderUCS2 extends ContentBuilder{

    /**
     * サロゲートペア文字(上位,下位)をUTF-16BEバイト列に変換する。
     * @param ch 文字
     * @return UTF-8バイト列
     */
    public static byte[] charToUTF16(char ch){
        byte[] result = new byte[2];
        result[0] = (byte)(ch >> 8);
        result[1] = (byte)(ch & 0xff);

        return result;
    }

    /**
     * コンストラクタ。
     * 長さ0で空の{@link DecodedContent}がセットされる。
     */
    public ContentBuilderUCS2(){
        this(128);
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
        this.content.init();
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
    public void charContent(CharSequence seq)
            throws DecodeException{
        flushError();

        int length = seq.length();
        int startPos = 0;

        for(int pos = 0; pos < length; pos++){
            char ch = seq.charAt(pos);

            if(   ! Character.isHighSurrogate(ch)
               && ! Character.isLowSurrogate (ch) ){
                continue;
            }

            if(startPos < pos){
                CharSequence chopped = seq.subSequence(startPos, pos);
                this.content.append(chopped);
                startPos = pos + 1;
            }

            byte[] barr = charToUTF16(ch);
            for(byte bval : barr){
                this.content.addDecodeError(bval);
            }
        }

        if(startPos < length){
            CharSequence chopped = seq.subSequence(startPos, length);
            this.content.append(chopped);
        }

        return;
    }

    /**
     * {@inheritDoc}
     * @param errorArray {@inheritDoc}
     * @param offset {@inheritDoc}
     * @param length {@inheritDoc}
     * @throws DecodeException {@inheritDoc}
     */
    public void decodingError(byte[] errorArray, int offset, int length)
            throws DecodeException{
        int limit = offset + length;

        for(int bpos = offset; bpos < limit; bpos++){
            byte bval = errorArray[bpos];
            this.content.addDecodeError(bval);
        }

        return;
    }

}

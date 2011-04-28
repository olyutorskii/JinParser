/*
 * content builder for Shift_JIS
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

/**
 * "Shift_JIS"エンコーディング用デコードハンドラ。
 * {@link SjisDecoder}からの通知に従い、
 * {@link DecodedContent}へとデコードする。
 */
public class ContentBuilderSJ extends ContentBuilder{

    private static final int DEF_BUF_SZ = 128;


    private boolean hasByte1st;
    private byte byte1st;


    /**
     * コンストラクタ。
     * 長さ0で空の{@link DecodedContent}がセットされる。
     */
    public ContentBuilderSJ(){
        this(DEF_BUF_SZ);
        return;
    }

    /**
     * コンストラクタ。
     * 長さ0で空の{@link DecodedContent}がセットされる。
     * @param capacity 初期容量
     * @throws NegativeArraySizeException 容量指定が負。
     */
    public ContentBuilderSJ(int capacity) throws NegativeArraySizeException{
        super(capacity);
        initImpl();
        return;
    }

    /**
     * デコード処理の初期化下請。
     */
    private void initImpl(){
        getContent().init();
        this.hasByte1st = false;
        this.byte1st = 0x00;
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
     * エラー情報をフラッシュする。
     */
    @Override
    protected void flushError(){
        if(this.hasByte1st){
            getContent().addDecodeError(this.byte1st);
            this.hasByte1st = false;
        }
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
            if( ! this.hasByte1st){
                if(ShiftJis.isShiftJIS1stByte(bval)){
                    this.byte1st = bval;
                    this.hasByte1st = true;
                }else{
                    getContent().addDecodeError(bval);
                }
            }else{
                if(ShiftJis.isShiftJIS2ndByte(bval)){   // 文字集合エラー
                    getContent().addDecodeError(this.byte1st, bval);
                    this.hasByte1st = false;
                }else if(ShiftJis.isShiftJIS1stByte(bval)){
                    getContent().addDecodeError(this.byte1st);
                    this.byte1st = bval;
                    this.hasByte1st = true;
                }else{
                    getContent().addDecodeError(this.byte1st);
                    getContent().addDecodeError(bval);
                    this.hasByte1st = false;
                }
            }
        }

        return;
    }

}

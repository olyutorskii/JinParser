/*
 * stream decoder
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

/**
 * バイトストリームからの文字デコーダ。
 * 入力バイトストリームをデコードし、デコード結果およびデコードエラーを
 * 文字デコードハンドラ{@link DecodeHandler}に通知する。
 * このクラスは、
 * デコードエラー詳細を察知できない{@link java.io.InputStreamReader}の
 * 代替品として設計された。
 * マルチスレッド対応はしていない。
 */
public class StreamDecoder{

    /** デフォルト入力バッファサイズ(={@value}bytes)。 */
    public static final int BYTEBUF_DEFSZ = 4 * 1024;
    /** デフォルト出力バッファサイズ(={@value}chars)。 */
    public static final int CHARBUF_DEFSZ = 4 * 1024;


    private final CharsetDecoder decoder;

    private ReadableByteChannel channel;
    private final ByteBuffer byteBuffer;
    private final CharBuffer charBuffer;

    private boolean isEndOfInput;
    private boolean isFlushing;

    private DecodeHandler decodeHandler;

    // エンコーディングによっては長さに見直しが必要
    private byte[] errorData = new byte[4];


    /**
     * コンストラクタ。
     * @param decoder デコーダ
     */
    public StreamDecoder(CharsetDecoder decoder){
        this(decoder, BYTEBUF_DEFSZ, CHARBUF_DEFSZ);
        return;
    }

    /**
     * コンストラクタ。
     * @param decoder デコーダ
     * @param inbuf_sz 入力バッファサイズ
     * @param outbuf_sz 出力バッファサイズ
     * @throws NullPointerException デコーダにnullを渡した。
     * @throws IllegalArgumentException バッファサイズが負。
     */
    public StreamDecoder(CharsetDecoder decoder,
                           int inbuf_sz,
                           int outbuf_sz )
            throws NullPointerException,
                   IllegalArgumentException {
        super();

        if(decoder == null) throw new NullPointerException();

        if(inbuf_sz <= 0 || outbuf_sz <= 0){
            throw new IllegalArgumentException();
        }

        this.decoder = decoder;
        this.byteBuffer = ByteBuffer.allocate(inbuf_sz);
        this.charBuffer = CharBuffer.allocate(outbuf_sz);
        this.channel = null;

        initDecoderImpl();

        return;
    }

    /**
     * デコーダの初期化下請。
     */
    private void initDecoderImpl(){
        this.byteBuffer.clear().flip();
        this.charBuffer.clear();

        this.decoder.onMalformedInput     (CodingErrorAction.REPORT);
        this.decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        this.decoder.reset();

        this.isEndOfInput = false;
        this.isFlushing = false;

        Arrays.fill(this.errorData, (byte)0x00);

        return;
    }

    /**
     * デコーダの初期化。
     */
    protected void initDecoder(){
        initDecoderImpl();
        return;
    }

    /**
     * 入力バッファを返す。
     * @return 入力バッファ
     */
    protected ByteBuffer getByteBuffer(){
        return this.byteBuffer;
    }

    /**
     * 出力バッファを返す。
     * @return 出力バッファ
     */
    protected CharBuffer getCharBuffer(){
        return this.charBuffer;
    }

    /**
     * デコードハンドラの設定。
     * nullオブジェクトを指定しても構わないが、
     * その場合パース時に例外を起こす。
     * @param decodeHandler デコードハンドラ
     */
    public void setDecodeHandler(DecodeHandler decodeHandler){
        this.decodeHandler = decodeHandler;
        return;
    }

    /**
     * デコードエラー格納配列の再アサイン。
     * 配列の内容は保持される。
     * 決して縮小することは無い。
     * メモ：java.util.Arrays#copyOf()はJRE1.5にない。
     * @param size 再アサイン量。バイト長。
     */
    protected void reassignErrorData(int size){
        int oldLength = this.errorData.length;
        if(oldLength >= size) return;
        int newSize = size;
        if(oldLength * 2 > newSize) newSize = oldLength * 2;
        byte[] newData = new byte[newSize];
        System.arraycopy(this.errorData, 0, newData, 0, oldLength);
        this.errorData = newData;
        return;
    }

    /**
     * デコードハンドラに文字列を渡す。
     * @throws DecodeException デコードエラー
     */
    protected void flushContent() throws DecodeException{
        if(this.charBuffer.position() <= 0){
            return;
        }

        this.charBuffer.flip();
        this.decodeHandler.charContent(this.charBuffer);
        this.charBuffer.clear();

        return;
    }

    /**
     * デコードハンドラにデコードエラーを渡す。
     * @param result デコード結果
     * @throws DecodeException デコードエラー
     * @throws IOException 入力エラー
     */
    protected void putDecodeError(CoderResult result)
            throws IOException,
                   DecodeException{
        int length = chopErrorSequence(result);
        this.decodeHandler.decodingError(this.errorData, 0, length);
        return;
    }

    /**
     * デコードエラーの原因バイト列を抽出する。
     * {@link #errorData}の先頭にバイト列が格納され、バイト長が返される。
     * @param result デコード結果
     * @return 原因バイト列の長さ
     * @throws IOException 入力エラー。
     * ※このメソッドを継承する場合、必要に応じて先読みをしてもよいし、
     * その結果生じたIO例外を投げてもよい。
     */
    protected int chopErrorSequence(CoderResult result) throws IOException{
        int errorLength = result.length();
        reassignErrorData(errorLength);
        this.byteBuffer.get(this.errorData, 0, errorLength);  // 相対get
        return errorLength;
    }

    /**
     * チャンネルからの入力を読み進める。
     * 前回の読み残しはバッファ前方に詰め直される。
     * @return 入力バイト数。
     * @throws java.io.IOException 入出力エラー
     */
    protected int readByteBuffer() throws IOException{
        this.byteBuffer.compact();

        int length = this.channel.read(this.byteBuffer);
        if(length <= 0){
            this.isEndOfInput = true;
        }

        this.byteBuffer.flip();

        return length;
    }

    /**
     * バイトストリームのデコードを開始する。
     * @param istream 入力ストリーム
     * @throws IOException 入出力エラー
     * @throws DecodeException デコードエラー
     */
    public void decode(InputStream istream)
            throws IOException,
                   DecodeException {
        this.channel = Channels.newChannel(istream);

        try{
            decodeChannel();
        }finally{
            this.channel.close();
            this.channel = null;
            istream.close();
        }

        return;
    }

    /**
     * 内部チャネルのデコードを開始する。
     * @throws IOException 入出力エラー
     * @throws DecodeException デコードエラー
     */
    protected void decodeChannel()
            throws IOException,
                   DecodeException {
        initDecoder();

        this.decodeHandler.startDecoding(this.decoder);

        for(;;){
            CoderResult result;
            if(this.isFlushing){
                result = this.decoder.flush(this.charBuffer);
            }else{
                result = this.decoder.decode(this.byteBuffer,
                                             this.charBuffer,
                                             this.isEndOfInput);
            }

            if(result.isError()){
                flushContent();
                putDecodeError(result);
            }else if(result.isOverflow()){      // 出力バッファが一杯
                flushContent();
            }else if(result.isUnderflow()){     // 入力バッファが空
                if( ! this.isEndOfInput ){
                    readByteBuffer();
                    continue;
                }

                if( ! this.isFlushing ){
                    this.isFlushing = true;
                    continue;
                }

                flushContent();
                break;
            }else{
                assert false;
            }
        }

        this.decodeHandler.endDecoding();

        return;
    }

}

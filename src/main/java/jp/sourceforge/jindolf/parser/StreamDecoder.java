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
 * バイトストリームを入力とする文字列デコーダ。
 *
 * <p>入力バイトストリームから文字列(charシーケンス)をデコードし、
 * デコード結果およびデコード異常系を
 * 文字デコードハンドラ<code>{@link DecodeHandler}</code>に通知する。
 *
 * <p>このクラスは、「制御の反転」(Inversion of Control)を用いて
 * <code>{@link java.nio.charset.CharsetDecoder}</code>呼び出しの
 * 煩雑さを隠蔽するために設計された。
 *
 * <p>このクラスは、
 * デコード異常系詳細を察知できない
 * <code>{@link java.io.InputStreamReader}</code>の
 * 代替品として設計された。
 *
 * <p>マルチスレッドからの同一インスタンスへの操作は非対応。
 *
 * @see java.nio.charset.CharsetDecoder
 */
public class StreamDecoder{

    /** デフォルト入力バッファサイズ(={@value}bytes)。 */
    public static final int BYTEBUF_DEFSZ = 4 * 1024;
    /** デフォルト出力バッファサイズ(={@value}chars)。 */
    public static final int CHARBUF_DEFSZ = 4 * 1024;

    private static final int DEF_ERRBUFLEN = 4;


    private final CharsetDecoder decoder;

    private final ByteBuffer byteBuffer;
    private final CharBuffer charBuffer;

    private ReadableByteChannel channel;

    private DecodeHandler decodeHandler;

    private byte[] errorData = new byte[DEF_ERRBUFLEN];


    /**
     * コンストラクタ。
     *
     * バッファサイズは入出力ともデフォルト値が用いられる。
     *
     * @param decoder 文字列デコーダ。
     * 異常系に関するアクション応答設定は変更される。
     * @throws NullPointerException デコーダにnullを渡した。
     */
    public StreamDecoder(CharsetDecoder decoder) throws NullPointerException{
        this(decoder, BYTEBUF_DEFSZ, CHARBUF_DEFSZ);
        return;
    }

    /**
     * コンストラクタ。
     *
     * @param decoder 文字列デコーダ。
     * 異常系に関するアクション応答設定は変更される。
     * @param inbufSz 入力バッファサイズ(byte単位)
     * @param outbufSz 出力バッファサイズ(char単位)
     * @throws NullPointerException デコーダにnullを渡した。
     * @throws IllegalArgumentException バッファサイズが0以下。
     */
    public StreamDecoder(CharsetDecoder decoder,
                           int inbufSz,
                           int outbufSz )
            throws NullPointerException,
                   IllegalArgumentException {
        super();

        if(decoder == null) throw new NullPointerException();

        if(inbufSz <= 0 || outbufSz <= 0){
            throw new IllegalArgumentException();
        }

        this.decoder = decoder;
        this.byteBuffer = ByteBuffer.allocate(inbufSz);
        this.charBuffer = CharBuffer.allocate(outbufSz);
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

        Arrays.fill(this.errorData, (byte) 0x00);

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
     * デコードハンドラの設定。
     *
     * <p>デコード結果はここで指定したハンドラに通知される。
     *
     * <p>nullオブジェクトを指定することも可能だが、
     * その場合デコード時に例外を起こす。
     *
     * @param decodeHandler デコードハンドラ
     */
    public void setDecodeHandler(DecodeHandler decodeHandler){
        this.decodeHandler = decodeHandler;
        return;
    }

    /**
     * 入力バッファを返す。
     *
     * @return 入力バッファ
     */
    protected ByteBuffer getByteBuffer(){
        return this.byteBuffer;
    }

    /**
     * 出力バッファを返す。
     *
     * @return 出力バッファ
     */
    protected CharBuffer getCharBuffer(){
        return this.charBuffer;
    }

    /**
     * チャネルからの入力を読み進め入力バッファに詰め込む。
     *
     * <p>前回デコード処理の読み残しはバッファ前方に詰め直される。
     *
     * <p>入力バッファに空きがない状態で呼んではいけない。
     *
     * @return 入力バイト数。
     *     入力末端に達したときは負の値。
     *     ※入力バッファに空きがありチャネルがブロックモードの場合、
     *     返り値0はありえない。
     * @throws java.io.IOException 入力エラー
     */
    protected int fillByteBuffer() throws IOException{
        this.byteBuffer.compact();
        assert this.byteBuffer.hasRemaining();

        int length = this.channel.read(this.byteBuffer);
        assert length != 0;

        this.byteBuffer.flip();

        return length;
    }

    /**
     * 出力バッファの全出力を読み進め、
     * デコードハンドラに文字列を通知する。
     *
     * <p>出力バッファはクリアされる。
     *
     * <p>既に出力バッファが空だった場合、何もしない。
     *
     * @throws DecodeException ハンドラによるデコードエラー
     */
    protected void notifyText() throws DecodeException{
        if(this.charBuffer.position() <= 0){
            return;
        }

        this.charBuffer.flip();
        this.decodeHandler.charContent(this.charBuffer);
        this.charBuffer.clear();

        return;
    }

    /**
     * デコードハンドラにデコードエラーを通知する。
     *
     * @param result デコード結果
     * @throws DecodeException ハンドラによるデコードエラー
     * @throws IOException 入力エラー
     */
    protected void notifyError(CoderResult result)
            throws IOException,
                   DecodeException{
        int length = chopErrorSequence(result);
        this.decodeHandler.decodingError(this.errorData, 0, length);
        return;
    }

    /**
     * 入力バッファからデコードエラーの原因となったバイト列を読み進める。
     *
     * <p>{@link #errorData}の先頭にバイト列がコピーされ、
     * バイト長が返される。
     *
     * <p>入力バッファはエラーの長さの分だけ読み進められる。
     *
     * @param result デコードエラー
     * @return 原因バイト列の長さ
     * @throws IOException 入力エラー。
     *     ※このメソッドを継承する場合、必要に応じて先読みをしてもよいし、
     *     その結果生じたIO例外を投げてもよい。
     */
    protected int chopErrorSequence(CoderResult result) throws IOException{
        int errorLength = result.length();
        reassignErrorData(errorLength);
        this.byteBuffer.get(this.errorData, 0, errorLength);  // 相対get
        return errorLength;
    }

    /**
     * デコードエラー格納配列の再アサイン。
     *
     * <p>旧配列の内容は保持される。
     * 決して縮小することは無い。
     *
     * @param size 再アサイン量。バイト長。
     */
    protected void reassignErrorData(int size){
        int oldLength = this.errorData.length;
        if(oldLength >= size) return;

        int newSize = size;
        if(oldLength * 2 > newSize) newSize = oldLength * 2;

        byte[] newData = Arrays.copyOf(this.errorData, newSize);
        this.errorData = newData;

        return;
    }

    /**
     * バイト入力ストリームを文字列デコードする。
     *
     * <p>デコード作業の状況に応じてハンドラへの各種通知が行われる。
     *
     * <p>入力ストリーム末端に到達するとデコード作業は終わり、
     * 入力ストリームは閉じられる。
     *
     * @param istream 入力ストリーム
     * @throws IOException 入力エラー
     * @throws DecodeException ハンドラによるデコードエラー
     */
    public void decode(InputStream istream)
            throws IOException,
                   DecodeException {
        // このチャネルは必ずブロックモードのはず
        this.channel = Channels.newChannel(istream);

        try{
            decodeChannel();
        }finally{
            this.channel.close();
            this.channel = null;
        }

        return;
    }

    /**
     * 内部チャネルのデコードを開始する。
     *
     * @throws IOException 入力エラー
     * @throws DecodeException ハンドラによるデコードエラー
     */
    protected void decodeChannel()
            throws IOException,
                   DecodeException {
        initDecoder();

        this.decodeHandler.startDecoding(this.decoder);

        int ioLength;
        boolean isEndOfInput;

        ioLength = fillByteBuffer();
        isEndOfInput = ioLength < 0;

        for(;;){
            CoderResult decodeResult =
                    this.decoder.decode(this.byteBuffer,
                                        this.charBuffer,
                                        isEndOfInput);
            // デコードエラー出現
            if(decodeResult.isError()){
                notifyText();
                decodeResult = modifyErrorLength(decodeResult);
                notifyError(decodeResult);
                continue;
            }

            // 出力バッファが一杯
            if(decodeResult.isOverflow()){
                notifyText();
                continue;
            }

            assert decodeResult.isUnderflow();

            // デコード掃き出し開始
            if(isEndOfInput){
                break;
            }

            // 入力バッファのデータが不足
            checkInfLoop();
            ioLength = fillByteBuffer();
            isEndOfInput = ioLength < 0;
        }

        notifyText();

        CoderResult flushResult;
        do{
            flushResult = this.decoder.flush(this.charBuffer);
            assert ! flushResult.isError();
            notifyText();
        }while( ! flushResult.isUnderflow() );

        this.decodeHandler.endDecoding();

        return;
    }

    /**
     * エラーの長さ(バイト列長)を修正する。
     *
     * <p>文字コード毎の事情に特化した異常系実装を目的とする。
     * デフォルト実装では引数をそのまま返す。
     *
     * <p>バイト列を先読みすることで、
     * さらに長いエラー情報を再構成してもよい。
     *
     * <p>エラー情報を前方に縮小することで、
     * エラーとして扱われるはずだったバイト列後部は
     * 再度デコード処理の対象として扱われる。
     *
     * @param result 修正元エラー情報。
     * @return 修正後エラー情報。引数と同じ場合もありうる。
     * (修正がない場合など)
     * @throws IOException バイト列読み込みエラー
     */
    protected CoderResult modifyErrorLength(CoderResult result)
            throws IOException{
        CoderResult newResult = result;
        return newResult;
    }

    /**
     * 不適切な入力バッファサイズ由来の無限ループを検出し例外を投げる。
     *
     * <p>検出しなければ何もしない。
     *
     * @throws DecodeException 無限ループが検出された
     */
    private void checkInfLoop() throws DecodeException{
        if(this.byteBuffer.position() == 0){
            int bufSz = this.byteBuffer.capacity();
            String csName = this.decoder.charset().name();

            StringBuilder text = new StringBuilder();
            text.append("too small input buffer (");
            text.append(bufSz).append("bytes) for ");
            text.append(csName);

            throw new DecodeException(text.toString());
        }
        return;
    }

}

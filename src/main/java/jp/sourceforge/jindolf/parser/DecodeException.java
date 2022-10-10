/*
 * decode exception
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

/**
 * デコード異常系情報。
 * {@link DecodeHandler}の各メソッドは、この例外をスローすることで
 * デコード処理の即時停止を{@link StreamDecoder}に指示することができる。
 * デコード元(バイトストリーム)の中のエラー発生位置と
 * デコード先(CharSequence)の中のエラー発生位置を保持することができる。
 * いずれの値も、エラー発生位置が不明な場合は負の値が設定される。
 */
@SuppressWarnings("serial")
public class DecodeException extends Exception{

    private final int bytePos;
    private final int charPos;

    /**
     * コンストラクタ。
     */
    public DecodeException(){
        this(null);
        return;
    }

    /**
     * コンストラクタ。
     * @param message メッセージ
     */
    public DecodeException(String message){
        this(message, -1, -1);
        return;
    }

    /**
     * コンストラクタ。
     * 位置情報が不明な場合は負の値を渡す。
     * @param bytePos デコード元エラー発生バイト位置
     * @param charPos デコード先エラー発生文字位置
     */
    public DecodeException(int bytePos, int charPos){
        this(null, bytePos, charPos);
        return;
    }

    /**
     * コンストラクタ。
     * 位置情報が不明な場合は負の値を渡す。
     * @param message メッセージ
     * @param bytePos デコード元エラー発生バイト位置
     * @param charPos デコード先エラー発生文字位置
     */
    public DecodeException(String message, int bytePos, int charPos){
        super(message);
        this.bytePos = bytePos;
        this.charPos = charPos;
        return;
    }

    /**
     * デコード元エラー発生位置を返す。
     * 単位はbyte単位。
     * @return エラー発生位置。不明な場合は負の値。
     */
    public int getBytePos(){
        return this.bytePos;
    }

    /**
     * デコード先エラー発生位置を返す。
     * 単位はchar単位。
     * @return エラー発生位置。不明な場合は負の値。
     */
    public int getCharPos(){
        return this.charPos;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String getMessage(){
        StringBuilder result = new StringBuilder();

        String message = super.getMessage();
        if(message != null && message.length() > 0){
            result.append(message).append(' ');
        }

        result.append("bytePos=").append(this.bytePos);
        result.append(' ');
        result.append("charPos=").append(this.charPos);

        return result.toString();
    }

}

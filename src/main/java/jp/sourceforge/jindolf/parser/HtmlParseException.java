/*
 * html parse exception
 *
 * Copyright(c) 2009 olyutorskii
 * $Id: HtmlParseException.java 894 2009-11-04 07:26:59Z olyutorskii $
 */

package jp.sourceforge.jindolf.parser;

/**
 * XHTMLパースの異常系情報。
 * {@link HtmlParser}の各ハンドラは、この例外をスローすることで
 * パース処理の即時停止を{@link HtmlParser}に指示することができる。
 * パース対象({@link DecodedContent})内のパース中断位置を
 * 保持することができる。
 * 中断位置が不明な場合は負の値が設定される。
 */
@SuppressWarnings("serial")
public class HtmlParseException extends Exception{

    private final int charPos;

    /**
     * コンストラクタ。
     */
    public HtmlParseException(){
        this(null, -1);
        return;
    }

    /**
     * コンストラクタ。
     * @param message メッセージ
     */
    public HtmlParseException(String message){
        this(message, -1);
        return;
    }

    /**
     * コンストラクタ。
     * @param charPos パース中断位置
     */
    public HtmlParseException(int charPos){
        this(null, charPos);
        return;
    }

    /**
     * コンストラクタ。
     * @param message メッセージ
     * @param charPos パース中断位置
     */
    public HtmlParseException(String message, int charPos){
        super(message);
        this.charPos = charPos;
        return;
    }

    /**
     * パース中断位置を返す。
     * @return パース中断位置
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

        result.append("charPos=").append(this.charPos);

        return result.toString();
    }

}

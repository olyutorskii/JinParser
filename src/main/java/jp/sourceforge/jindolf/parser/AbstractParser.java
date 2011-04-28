/*
 * abstract XHTML parser
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.sourceforge.jindolf.corelib.GameRole;

/**
 * 人狼BBS生成のXHTML文書を解釈するパーサの抽象基底クラス。
 * {@link DecodedContent}の内容をパースし、
 * 各種ハンドラへ通知する処理の基盤を構成する。
 * 正規表現エンジンを実装基盤とする。
 * 親パーサを指定することにより、検索対象文字列とマッチエンジンを
 * 親パーサと共有することができる。
 * @see Matcher
 */
public abstract class AbstractParser implements ChainedParser{

    /** ホワイトスペース。 */
    protected static final String SPCHAR = "\u0020\\t\\n\\r";
    /** 0回以上連続するホワイトスペースの正規表現。 */
    protected static final String SP_I = "[" +SPCHAR+ "]*";

    private static final Pattern DUMMY_PATTERN = compile("\u0000");

    /**
     * 正規表現のコンパイルを行う。
     * デフォルトで{@link java.util.regex.Pattern#DOTALL}が
     * オプション指定される。
     * @param regex 正規表現文字列
     * @return マッチエンジン
     */
    protected static Pattern compile(CharSequence regex){
        Pattern result = Pattern.compile(regex.toString(), Pattern.DOTALL);
        return result;
    }

    private final ChainedParser parent;

    private DecodedContent content;
    private Matcher matcher;
    private String contextErrorMessage;

    /**
     * コンストラクタ。
     */
    protected AbstractParser(){
        this(null);
        return;
    }

    /**
     * コンストラクタ。
     * @param parent 親パーサ
     */
    protected AbstractParser(ChainedParser parent){
        super();
        this.parent = parent;
        resetImpl();
        return;
    }

    /**
     * パーサの状態をコンストラクタ直後の状態にリセットする。
     * ※コンストラクタから呼ばせるためにオーバーライド不可
     */
    private void resetImpl(){
        this.content = null;
        this.matcher = null;
        this.contextErrorMessage = null;
        return;
    }

    /**
     * パーサの状態をリセットする。
     */
    public void reset(){
        if(this.parent != null){
            throw new UnsupportedOperationException();
        }
        resetImpl();
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     */
    /**
     * パース対象文字列をセットする。
     * パースが終わるまでこの文字列の内容を変更してはならない。
     * @param content パース対象文字列
     */
    public void setContent(DecodedContent content){
        if(this.parent != null){
            throw new UnsupportedOperationException();
        }

        CharSequence rawContent = content.getRawContent();

        this.content = content;
        this.matcher = DUMMY_PATTERN.matcher(rawContent);

        return;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public DecodedContent getContent(){
        if(this.parent != null){
            return this.parent.getContent();
        }

        return this.content;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Matcher getMatcher(){
        if(this.parent != null){
            return this.parent.getMatcher();
        }

        return this.matcher;
    }

    /**
     * 文脈依存のエラーメッセージを設定する。
     * {@link #buildParseException}で利用される。
     * 設定内容は親へ委譲されない。
     * @param errorMessage エラーメッセージ。nullも可能。
     */
    protected void setContextErrorMessage(String errorMessage){
        this.contextErrorMessage = errorMessage;
        return;
    }

    /**
     * 文脈状況に応じたパース例外を生成する。
     * 例外にはリージョン開始位置が埋め込まれる。
     * @return パース例外
     */
    protected HtmlParseException buildParseException(){
        HtmlParseException result;
        result = new HtmlParseException(this.contextErrorMessage,
                                        regionStart() );
        return result;
    }

    /**
     * パースに使う正規表現パターンを切り替える。
     * @param pattern 正規表現パターン
     */
    protected void switchPattern(Pattern pattern){
        getMatcher().usePattern(pattern);
        return;
    }

    /**
     * 最後のマッチに成功した文字領域以前をパース対象から外す。
     */
    protected void shrinkRegion(){
        int lastMatchedEnd;
        try{
            lastMatchedEnd = matchEnd();
        }catch(IllegalStateException e){
            return;
        }

        int regionEnd   = regionEnd();

        getMatcher().region(lastMatchedEnd, regionEnd);

        return;
    }

    /**
     * 検査対象の一部が指定パターンにマッチするか判定する。
     * @param pattern 指定パターン
     * @return マッチすればtrue
     */
    protected boolean findProbe(Pattern pattern){
        switchPattern(pattern);
        if( getMatcher().find() ) return true;
        return false;
    }

    /**
     * 検査対象先頭が指定パターンにマッチするか判定する。
     * @param pattern 指定パターン
     * @return マッチすればtrue
     */
    protected boolean lookingAtProbe(Pattern pattern){
        switchPattern(pattern);
        if( getMatcher().lookingAt() ) return true;
        return false;
    }

    /**
     * 検査対象全体が指定パターンにマッチするか判定する。
     * @param pattern 指定パターン
     * @return マッチすればtrue
     */
    protected boolean matchesProbe(Pattern pattern){
        switchPattern(pattern);
        if( getMatcher().matches() ) return true;
        return false;
    }

    /**
     * 残りの検索対象領域からパターンがマッチする部分を探す。
     * 見つからなければ例外をスローする。
     * @param pattern 正規表現パターン
     * @throws HtmlParseException
     * マッチしなかった
     */
    protected void findAffirm(Pattern pattern)
            throws HtmlParseException{
        if( ! findProbe(pattern) ){
            throw buildParseException();
        }
        return;
    }

    /**
     * 残りの検索対象領域先頭からパターンがマッチする部分を探す。
     * 見つからなければ例外をスローする。
     * @param pattern 正規表現パターン
     * @throws HtmlParseException
     * マッチしなかった
     */
    protected void lookingAtAffirm(Pattern pattern)
            throws HtmlParseException{
        if( ! lookingAtProbe(pattern) ){
            throw buildParseException();
        }
        return;
    }

    /**
     * 残りの検索対象領域全体がパターンにマッチするか調べる。
     * マッチしなければ例外をスローする。
     * @param pattern 正規表現パターン
     * @throws HtmlParseException
     * マッチしなかった
     */
    protected void matchesAffirm(Pattern pattern)
            throws HtmlParseException{
        if( ! matchesProbe(pattern) ){
            throw buildParseException();
        }
        return;
    }

    /**
     * 最後のマッチで任意の前方参照グループがヒットしたか判定する。
     * @param group グループ番号
     * @return ヒットしていたらtrue
     */
    protected boolean isGroupMatched(int group){
        if(matchStart(group) >= 0) return true;
        return false;
    }

    /**
     * 最後にマッチした前方参照グループを数値化する。
     * 0以上の整数のみサポート。
     * @param group グループ番号
     * @return 数値
     */
    protected int parseGroupedInt(int group){
        int result = 0;

        CharSequence rawContent = getContent().getRawContent();
        int start = matchStart(group);
        int end   = matchEnd(group);
        for(int pos = start; pos < end; pos++){
            char letter = rawContent.charAt(pos);
            int digit = Character.digit(letter, 10);
            result = result * 10 + digit;
        }

        return result;
    }

    /**
     * 最後にマッチした前方参照グループの開始位置を得る。
     * @param group 前方参照識別番号
     * @return 開始位置
     */
    protected int matchStart(int group){
        return getMatcher().start(group);
    }

    /**
     * 最後にマッチした前方参照グループの終了位置を得る。
     * @param group 前方参照識別番号
     * @return 終了位置
     */
    protected int matchEnd(int group){
        return getMatcher().end(group);
    }

    /**
     * 最後にマッチした全領域の開始位置を得る。
     * @return 開始位置
     */
    protected int matchStart(){
        return getMatcher().start();
    }

    /**
     * 最後にマッチした全領域の終了位置を得る。
     * @return 終了位置
     */
    protected int matchEnd(){
        return getMatcher().end();
    }

    /**
     * 検索領域の先頭位置を返す。
     * @return 先頭位置
     */
    protected int regionStart(){
        return getMatcher().regionStart();
    }

    /**
     * 検索領域の末尾位置を返す。
     * @return 末尾位置
     */
    protected int regionEnd(){
        return getMatcher().regionEnd();
    }

    /**
     * 0個以上のホワイトスペースを読み飛ばす。
     * 具体的には検索対象領域の先頭が進むだけ。
     */
    protected void sweepSpace(){
        CharSequence rawContent = getContent().getRawContent();

        boolean hasSpace = false;
        int regionStart = regionStart();
        int regionEnd   = regionEnd();

        for( ; regionStart < regionEnd; regionStart++){
            char letter = rawContent.charAt(regionStart);

            switch(letter){
            case '\u0020':
            case '\t':
            case '\n':
            case '\r':
                hasSpace = true;
                continue;
            default:
                break;
            }

            break;
        }

        if(hasSpace){
            getMatcher().region(regionStart, regionEnd);
        }

        return;
    }

    /**
     * 検索領域の先頭から各種役職名のマッチを試みる。
     * @return 役職。何もマッチしなければnullを返す。
     */
    protected GameRole lookingAtRole(){
        GameRole role = GameRole.lookingAtRole(getMatcher());
        return role;
    }

}

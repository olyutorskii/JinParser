/*
 * chained parser interface
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import java.util.regex.Matcher;

/**
 * 連結パーサの基本インタフェース。
 */
public interface ChainedParser{

    /**
     * パース対象文字列を取得する。
     * このクラスおよびこのクラスを継承するものは、
     * 全てこのメソッドを介してパース対象文字列にアクセスしなければならない。
     * @return パース対象文字列
     */
    DecodedContent getContent();

    /**
     * 現時点での正規表現マッチャを得る。
     * このクラスおよびこのクラスを継承するものは、
     * 全てこのメソッドを介してマッチャにアクセスしなければならない。
     * @return 正規表現マッチャ
     */
    Matcher getMatcher();

}

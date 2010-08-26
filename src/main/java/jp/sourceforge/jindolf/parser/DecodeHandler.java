/*
 * decode handler
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import java.nio.charset.CharsetDecoder;

/**
 * 文字デコードハンドラ。
 * {@link StreamDecoder}により呼ばれる。
 * メソッドが呼ばれる順番は
 * {@link #startDecoding}が最初で
 * {@link #endDecoding}が最後。
 * その間、{@link #charContent}
 * または{@link #decodingError}が複数回呼ばれる。
 * 各メソッドは、{@link DecodeException}をスローすることで
 * デコード処理を中止させることができる。
 */
public interface DecodeHandler{

    /**
     * デコード開始の通知を受け取る。
     * @param decoder デコーダ
     * @throws DecodeException デコードエラー
     */
    void startDecoding(CharsetDecoder decoder)
            throws DecodeException;

    /**
     * 正常にデコードした文字列の通知を受け取る。
     * seqの内容は、ハンドラ呼び出し元で随時変更されうる。
     * seqの内容を後々再利用するつもりなら、
     * 制御を呼び出し元に戻すまでの間に必要な箇所をコピーする必要がある。
     * @param seq 文字列
     * @throws DecodeException デコードエラー
     */
    void charContent(CharSequence seq)
            throws DecodeException;

    /**
     * デコードエラーの通知を受け取る。
     * errorArrayの内容は、ハンドラ呼び出し元で随時変更されうる。
     * errorArrayの内容を後々再利用するつもりなら、
     * 制御を呼び出し元に戻すまでの間に必要な箇所をコピーする必要がある。
     * @param errorArray エラーを引き起こした入力バイトシーケンス。
     * @param offset errorArrayに含まれるエラーの開始位置。
     * @param length errorArrayに含まれるエラーのバイト長。
     * @throws DecodeException デコードエラー
     */
    void decodingError(byte[] errorArray, int offset, int length)
            throws DecodeException;

    /**
     * デコード終了の通知を受け取る。
     * @throws DecodeException デコードエラー
     */
    void endDecoding()
            throws DecodeException;

}

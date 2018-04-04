/*
 * JinParser (content) パッケージコメント
 *
 * License : The MIT License
 * Copyright(c) 2018 olyutorskii
 */

/**
 * このパッケージは、人狼BBS用パーサライブラリ「JinParser」から
 * XHTMLに依存しない部分のみを抽出したライブラリである。
 *
 * <p>
 * 任意のバイトストリームから、
 * デコードエラー情報付き文字列{@code DecodedContent}を得るには、
 * 次のように行う。
 * <pre>
 * {@code
 * InputStream is = .....
 * DecodeNotifier decoder = new DecodeNotifier(...);
 * ContentBuilder builder = new ContentBuilder();
 * decoder.setCharDecodeListener(builder);
 * try{
 *     decoder.decode(is);
 * }catch(IOException e){
 *     // ERROR!
 * }catch(DecodeBreakException e){
 *     // ABORT!
 * }
 * DecodedContent content = builder.getContent();
 * }
 * </pre>
 *
 * <hr>
 *
 * <p>
 * The MIT License
 * <p>
 * Copyright(c) 2018 olyutorskii
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * </p>
 *
 * <hr>
 *
 * @see <a href="http://jindolf.osdn.jp/">
 * Jindolfポータルサイト</a>
 * @see <a href="https://osdn.jp/projects/jindolf/devel/">
 * Jindolf開発プロジェクト</a>
 */

package jp.osdn.jindolf.parser.content;

/* EOF */

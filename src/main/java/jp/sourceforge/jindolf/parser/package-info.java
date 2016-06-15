/*
 * JinParser パッケージコメント
 *
 * このファイルは、SunJDK5.0以降に含まれるJavadoc用に用意された、
 * 特別な名前を持つソースファイルです。
 * このファイルはソースコードを含まず、
 * パッケージコメントとパッケージ宣言のみが含まれます。
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

/**
 * これは Jindolf プロジェクトにおける、
 * XHTML文書のパース部分を構成するパッケージです。
 *
 * <p>
 * JinParserライブラリは、CGIゲーム「人狼BBS」のクライアント制作者向けに
 * 作られたJavaライブラリです。
 * JinParserライブラリは、人狼BBSの専用クライアント開発プロジェクト
 * 「Jindolf」から派生しました。
 * </p>
 *
 * <hr>
 *
 * <p>
 * 任意のバイトストリームから、
 * デコードエラー情報付き文字列{@code DecodedContent}を得るには、
 * 次のようにします。
 * <pre>
 * {@code
 * InputStream is = .....
 * StreamDecoder decoder = new SjisDecoder();
 * ContentBuilder builder = new ContentBuilder();
 * decoder.setDecodeHandler(builder);
 * try{
 *     decoder.decode(is);
 * }catch(IOException e){
 *     // ERROR!
 * }catch(DecodeException e){
 *     // ERROR!
 * }
 * DecodedContent content = builder.getContent();
 * }
 * </pre>
 * </p>
 *
 * <p>
 * このようにして得られた文字列をパースして、
 * あなたの実装したハンドラ{@code YourHandler}に通知するには、
 * 以下のようにします。
 * <pre>
 * {@code
 * HtmlParser parser = new HtmlParser();
 * HtmlHandler handler = new YourHandler();
 * parser.setBasicHandler(handler);
 * parser.setTalkHandler(handler);
 * parser.setSysEventHandler(handler);
 * try{
 *     parser.parseAutomatic(content);
 * }catch(HtmlParseException e){
 *     // ERROR!
 * }
 * }
 * </pre>
 * </p>
 *
 * <p>
 * ハンドラ内部で、パース元となった文字列の一部を切り出したい場合は、
 * {@code EntityConverter}を使うのが便利です。
 * </p>
 *
 * <hr>
 *
 * <p>
 * The MIT License
 * </p>
 * <p>
 * Copyright(c) 2009 olyutorskii
 * </p>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * </p>
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * </p>
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
 * @see <a href="http://jindolf.sourceforge.jp/">
 * Jindolfポータルサイト</a>
 * @see <a href="http://sourceforge.jp/projects/jindolf/">
 * Jindolf開発プロジェクト</a>
 */

package jp.sourceforge.jindolf.parser;

/* EOF */

/*
 * JinParser パッケージコメント
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

/**
 * これは Jindolf プロジェクトにおける、
 * XHTML文書のパース部分を構成するパッケージである。
 *
 * <p>
 * JinParserライブラリは、CGIゲーム「人狼BBS」のクライアント制作者向けに
 * 作られたJavaライブラリである。
 * JinParserライブラリは、人狼BBSの専用クライアント開発プロジェクト
 * 「Jindolf」から派生した。
 *
 * <hr>
 *
 * <p>
 * 文字列と文字デコードエラーが混在した{@code DecodedContent}をパースして、
 * あなたの実装したハンドラ{@code YourHandler}に通知するには、
 * 以下のように行う。
 * <pre>
 * {@code
 * DecodedContent content = ...;
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
 *
 * <p>
 * ハンドラ内部で、パース元となった文字列の一部を切り出したい場合は、
 * {@code EntityConverter}を使うのが便利である。
 *
 * <hr>
 *
 * <p>
 * The MIT License
 * <p>
 * Copyright(c) 2009 olyutorskii
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

package jp.osdn.jindolf.parser;

/* EOF */

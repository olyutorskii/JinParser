# JinParser #

[![CodeQL](https://github.com/olyutorskii/JinParser/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/olyutorskii/JinParser/actions/workflows/codeql-analysis.yml)
-----------------------------------------------------------------------


## JinParserとは ? ##

* **JinParser**ライブラリは、[Jindolf][JINDOLF] プロジェクトを構成する
Javaライブラリです。

* Jindolfは、CGIゲーム「[人狼BBS][BBS]」の専用クライアント開発プロジェクトです。
JinParserは、Jindolf以外の人狼BBSクライアント製作者向けに、
Jindolfの機能の一部を提供することを目的に発足した、派生プロジェクトです。

* JinParser is one of the Java libraries
that make up the Jindolf chat game browser application for 人狼BBS.
人狼BBS and Jindolf players belonged to the Japanese-speaking community.
Therefore, JinParser documents and comments are heavily written in Japanese.

* JinParserは2023年10月頃まで [OSDN][OSDN](旧称 SourceForge.jp)
でホスティングされていました。
OSDNの可用性に関する問題が長期化しているため、GitHubへと移転してきました。

* ※ 人狼BBSを主催するninjin氏は、JinParserの製作に一切関与していません。
JinParserに関する問い合わせををninjin氏へ投げかけないように！約束だよ！


## API ドキュメント ##
* [API docs](https://olyutorskii.github.io/JinParser/apidocs/index.html)
* [Maven report](https://olyutorskii.github.io/JinParser/)


## ビルド方法 ##

* JinParserはビルドに際して [Maven 3.3.9+](https://maven.apache.org/)
と JDK 1.8+ を要求します。

* JinParserはビルドに際してJinCore、JioCema両ライブラリを必要とします。
開発時はMaven等を用いてこれらのライブラリを用意してください。

* Mavenを使わずとも `src/main/java/` 配下のソースツリーをコンパイルすることで
ライブラリを構成することが可能です。


## ライセンス ##

* JinParser独自のソフトウェア資産には [The MIT License][MIT] が適用されます.


## プロジェクト創設者 ##

* 2009年に [Olyutorskii](https://github.com/olyutorskii) によってプロジェクトが発足しました。


[JINDOLF]: http://jindolf.sourceforge.jp/
[BBS]: http://ninjinix.com/
[OSDN]: https://ja.osdn.net/projects/jindolf/scm/git/JinCore/
[MIT]: https://opensource.org/licenses/MIT


--- EOF ---

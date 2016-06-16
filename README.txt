[UTF-8 Japanese]

                             J i n P a r s e r
                                  README

                                              Copyright(c) 2009 olyutorskii


=== JinParserとは ===

 JinParserライブラリは、CGIゲーム「人狼BBS」のクライアント制作者向けに作られた
Javaライブラリです。
 このアーカイブは、JinParserライブラリの開発資産を、ある時点で凍結したものです。

 Jindolfは、CGIゲーム「人狼BBS」の専用クライアント開発プロジェクトです。
 JinParserは、Jindolf以外の人狼BBSクライアント製作者向けに、
JindolfのXHTML文書パース機能を提供することを目的に発足した、
派生プロジェクトです。

※ このアーカイブにはJindolfの実行バイナリは含まれていません。
　 Jindolfを動かしたい方は、jindolfで始まり拡張子が*.jarであるファイルを
　 別途入手してください。
※ 人狼BBSのURLは [ http://ninjinix.com/ ] まで
※ 人狼BBSを主催するninjin氏は、JinParserの製作に一切関与していません。
　 JinParserに関する問い合わせををninjin氏へ投げかけないように！約束だよ！


=== ソースコードに関して ===

 - JinParserはJava言語(JavaSE7)で記述されたプログラムです。
 - JinParserは他のプログラムに組み込まれて利用されるライブラリです。
   JARファイルによるライブラリ提供や、他プロジェクトのソースツリーへの
   マージの形で利用される事を想定しています。
 - JinParserはJRE1.7に準拠したJava実行環境で利用できるように作られています。
   原則として、JRE1.7に準拠した実行系であれば、プラットフォームを選びません。


=== 依存ライブラリ ===

 - JinParserはビルドに際してJinCoreライブラリを必要とします。
   開発時はMaven等を用いてJinCoreライブラリを用意してください。


=== 開発プロジェクト運営元 ===

  https://osdn.jp/projects/jindolf/devel/ まで。


=== ディレクトリ内訳構成 ===

基本的にはMaven3のmaven-archetype-quickstart構成に準じます。

./README.txt
    あなたが今見てるこれ。

./CHANGELOG.txt
    変更履歴。

./LICENSE.txt
    ライセンスに関して。

./SCM.txt
    ソースコード管理に関して。

./pom.xml
    Maven3用プロジェクト構成定義ファイル。

./src/main/java/
    Javaのソースコード。

./src/main/resources/
    プロパティファイルなどの各種リソース。

./src/test/java/
    JUnit 4.* 用のユニットテストコード。

./src/test/java/sample/
    サンプルのパーサ実装。

./src/main/config/
    各種ビルド・構成管理に必要なファイル群。

./src/main/config/checks.xml
    Checkstyle用configファイル。

./src/main/config/pmdrules.xml
    PMD用ルール定義ファイル。

./src/main/assembly/descriptor.xml
    ソースアーカイブ構成定義ファイル。


--- EOF ---

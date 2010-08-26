[UTF-8 Japanese]

                             JinParserライブラリ
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
※ 人狼BBSのURLは [ http://homepage2.nifty.com/ninjinia/ ] まで
※ 人狼BBSを主催するninjin氏は、JinParserの製作に一切関与していません。
　 JinParserに関する問い合わせををninjin氏へ投げかけないように！約束だよ！


=== ソースコードに関して ===

 - JinParserはJava言語(JLS3)で記述されたプログラムです。
 - JinParserは他のプログラムに組み込まれて利用されるライブラリです。
   JARファイルによるライブラリ提供や、他プロジェクトのソースツリーへの
   マージの形で利用される事を想定しています。
 - JinParserはJRE1.5に準拠したJava実行環境で利用できるように作られています。
   原則として、JRE1.5に準拠した実行系であれば、プラットフォームを選びません。


=== 依存ライブラリ ===

 - JinParserはビルドに際してJinCoreライブラリを必要とします。
   開発時はMaven等を用いてJinCoreライブラリを用意してください。


=== アーカイブ管理体制 ===

  このアーカイブは、UTF-8による開発環境を前提として構成されています。
  このアーカイブの原本となる開発資産は、
      http://hg.sourceforge.jp/view/jindolf/JinParser/
  を上位に持つMercurialリポジトリで管理されています。


=== 開発プロジェクト運営元 ===

  http://sourceforge.jp/projects/jindolf/devel/ まで。


=== ディレクトリ内訳構成 ===

基本的にはMaven2のmaven-archetype-quickstart構成に準じます。

./README.txt
    あなたが今見てるこれ。

./CHANGELOG.txt
    変更履歴。

./LICENSE.txt
    ライセンスに関して。

./pom.xml
    Maven2用プロジェクト構成定義ファイル。

./build.xml
    Ant用追加タスク。

./src/main/java/
    Javaのソースコード。

./src/main/resources/
    プロパティファイルなどの各種リソース。

./src/test/java/
    JUnit 4.* 用のユニットテストコード。

./src/test/java/sample/
    サンプルのパーサ実装。

./src/main/config/checks.xml
    Checkstyle用configファイル。

./src/main/config/pmdrules.xml
    PMD用ルール定義ファイル。

./src/main/assembly/descriptor.xml
    ソースアーカイブ構成定義ファイル。


--- EOF ---

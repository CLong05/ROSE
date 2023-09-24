# 面向 Oberon-0 的逆向工程工具  ROSE

本仓库内为2022年春季学期中山大学编译原理课程的实验项目5 ROSE。

开发一个面向 Oberon-0 的逆向工程工具，根据一个输入的 Oberon-0 源程序自动绘制对应的设计图，这些设计图
包括子程序调用关系图和程序控制流设计图（例如程序流程图、 PAD 图、 N_S 图等）， 本实验项目命名为ROSE，意为 Reverse Oberon Software Engineering。  设计过程涉及词法分析、语法分析、语法制导翻译、自动生成工具等重要环节。    

文件说明：

- ex1
  - Oberon-0.pdf  ex1实验报告，其中包括 Oberon-0 语言特点介绍、文法二义性讨论、实验心得体会等内容  
  - ex1\testcase 文件夹中存放回归测试用例
- ex2
  -  lexgen.pdf  ex2实验报告，其中包括
    - 以表格形式列出 Oberon-0 语言的词汇表
    - 用正则定义式描述 Oberon-0 语言词法规则
    - 关于 Oberon-0 语言与其他高级语言的词法规则的异同比较。
    - 讨论 3 种不同 lex 族软件工具的输入文件中， 词法规则定义的差异或特点。  
  - gen.bat：根据输入文件生成词法分析程序的脚本 
  - build.bat：编译词法分析程序的脚本 
  - run.bat：运行词法分析程序扫描编写的正确 Oberon-0 例子程序的脚本
  - test.bat：运行所有由实验软装置提供的测试用例的脚本 
  - ex2\bin 中存放根据 Oberon-0 词法分析程序源代码编译得到的字节码文件OberonScanner.class 以及其他相关的字节码文件
  - ex2\doc 中存放根据源程序中的注释自动生成的 javadoc 文档
  - ex2\jflex 中存放所使用的 JFlex 工具  
  - ex2\testcase 文件夹中存放测试用例
  - ex2\src 文件夹中存放源代码文件

- ex3
  - gen.bat 运行 JavaCUP 根据输入文件生成语法分析程序的脚本 
  - build.bat 编译语法分析程序的脚本 
  - run.bat 运行语法分析程序处理编写的正确 Oberon-0 例子程序的脚本
  - test.bat 运行所有由实验软装置提供的测试用例的脚本 
  - doc.bat 生成javadoc 文档的脚本
  - yaccgen.pdf ex3实验报告，讨论两个不同 yacc 族工具语法规则定义差异的文档  
  - ex3\src 文件夹中存放源代码文件
  - ex3\bin 中存放根据 Oberon-0 语法分析程序源代码编译得到的字节码文件Parser.class 以及其他相关的字节码文件
  - ex3\doc 中存放根据源程序中的注释自动生成的 javadoc 文档
  - ex3\javacup 中存放使用的 JavaCUP 工具
  - ex2\testcase 文件夹中存放测试用例
- ex4
  - build.bat 编译语法分析程序的脚本
  - run.bat 运行语法分析程序扫描编写的正确 Oberon-0 例子程序的脚本
  - test.bat 运行所有由实验软装置提供的测试用例的脚本 
  - doc.bat 生成javadoc 文档的脚本
  - ex4\src 文件夹中存放源代码文件
  - ex4\bin 中存放根据词法分析程序源代码编译得到的 Java 字节码文件OberonParser.class 以及其它相关的字节码文件。  
  - ex4\doc 中存放根据源程序中的注释自动生成的 javadoc 文档
  - ex4\testcase 文件夹中存放测试用例
  - scheme.pdf ex4实验报告，描述 Oberon-0 语言翻译模式 
-   ROSE：本次实验要求以及相应的实验软装置

---

This is the experimental project 5 ROSE for the Compiling principle course of Sun Yat-Sen University in the spring semester of 2022.

Develop a reverse engineering tool for Oberon-0 to automatically draw corresponding design drawings according to an input Oberon-0 source program. It includes subroutine call relationship diagram and program control flow design diagram (such as program flow diagram, PAD diagram, N_S diagram, etc.). This experimental project is named ROSE, which means Reverse Oberon Software Engineering. The design process involves lexical analysis, syntax analysis, syntax guidance translation, automatic generation tools and so on.

Document description:

- ex1
  - Oberon-0.pdf  ex1 experiment report, including Oberon-0 language features, grammar ambiguity discussion, experimental experience and other content
  - ex1\testcase folder contains regression test cases
- ex2
  - lexgen.pdf  ex2 experiment report, which includes
    - Lists the vocabulary of the Oberon-0 language in tabular form
    - Use regular definitions to describe the Oberon-0 language lexical rules
    - Comparison of lexical rules between Oberon-0 language and other high-level languages.
    - Discuss the differences or characteristics of lexical rule definitions in the input files of three different lex family software tools.
  - gen. bat: generates the script of the lexical analyzer based on the input file
  - build.bat: indicates the script that compiles the lexical analyzer
  - run.bat: Run the lexicon to scan the script written for the correct Oberon-0 example program
  - test.bat: scripts that run all test cases provided by the experimental software device
  - ex2 \bin contains the OberonScanner.class bytecode file compiled from the Oberon-0 lexical analyzer source code and other related bytecode files
  - ex2 \doc contains javadoc documents that are automatically generated based on comments in the source program
  - ex2\jflex stores the JFlex tools used
  - ex2\testcase folder stores test cases
  - ex2\src folder stores source code files

- ex3
  - gen.bat Runs the JavaCUP script to generate the parser based on the input file
  - build.bat Specifies the script for compiling the parser
  - run.bat runs the parser to process the script written for the correct Oberon-0 example programz	
  - test.bat Runs the scripts of all test cases provided by the experimental software device
  -doc. bat Specifies the script for generating the javadoc document
  - yaccgen.pdf  ex3 lab report, a document that discusses differences in syntax rule definitions for two different yacc family tools
  - ex3\src folder stores source code files
  - ex3 \bin contains the bytecode file parser.class compiled from Oberon-0 Parser source code and other related bytecode files
  - ex3 \doc contains javadoc documents that are automatically generated based on comments in the source program
  - ex3\javacup stores the JavaCUP tool
  - ex2\testcase folder stores test cases
- ex4
  - build.bat Specifies the script for compiling the parser
  - run.bat runs the parser to scan the script written for the correct Oberon-0 example program
  - test.bat Runs the scripts of all test cases provided by the experimental software device
  - doc. bat Specifies the script for generating the javadoc document
  - ex4\src folder stores source code files
  - ex4 \bin contains the Java bytecode file OberonParser.class and other related bytecode files compiled from the source code of the lexical parser.
  - ex4 \doc contains javadoc documents that are automatically generated based on comments in the source program
  - ex4\testcase folder stores test cases
  - scheme.pdf  ex4 Experiment report describing the Oberon-0 language translation model
- ROSE: The requirements of this experiment and the corresponding experimental software device

---

package io.github.pengxianggui.bak;

import io.github.pengxianggui.crud.autogenerate.CodeAutoGenerator;

public class CodeGenerator {

    public static void main(String[] args) {
        CodeAutoGenerator.builder()
                .author("pengxg")
                .module("bak-server")//模块名
                .url("jdbc:mysql://127.0.0.1:3306/bak_server")
                .username("root")
                .password("123456")
                .parentPkg("io.github.pengxianggui.bak")
                .build()
                .generate();

    }
}

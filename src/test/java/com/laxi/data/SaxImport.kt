package com.laxi.data

import com.laxi.data.xlsx.ExcelParser

/**
 * Created by Chendk on 2018/10/15
 */

fun main(args: Array<String>) {

    ExcelParser(
            "C:\\Users\\Capc\\Desktop\\test.xlsx",
            0,
            60
    ).myDataList.let {
        println(it)
    }

}
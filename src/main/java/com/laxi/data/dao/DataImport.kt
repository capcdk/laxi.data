package com.laxi.data.dao

import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.extension.activerecord.Model

/**
 * Created by Chendk on 2018/10/16
 */
@TableName("import_data")
class DataImport : Model<DataImport>() {
    var id: Long? = null
    var title: String? = null
}
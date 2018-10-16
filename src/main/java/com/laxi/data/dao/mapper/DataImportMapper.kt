package com.laxi.data.dao.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.laxi.data.dao.DataImport
import org.springframework.stereotype.Component

/**
 * Created by Chendk on 2018/10/16
 */
@Component
interface DataImportMapper : BaseMapper<DataImport> {

    fun listTitle(): List<String?>
}
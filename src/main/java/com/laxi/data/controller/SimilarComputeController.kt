package com.laxi.data.controller

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.laxi.data.algorithm.SimilarAlgorithm
import com.laxi.data.dao.DataImport
import com.laxi.data.dao.mapper.DataImportMapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import kotlin.system.measureNanoTime

/**
 * Created by Chendk on 2018/10/15
 */
@RestController
@RequestMapping("/api/similar")
class SimilarComputeController(
        val dataImportMapper: DataImportMapper
) {

    @GetMapping("/getSimilar")
    fun getSimilar(
            @RequestParam("alg") alg: String,
            @RequestParam("originTitle") originTitle: String,
            @RequestParam("threshold", required = false) threshold: Float?
    ): Mono<*> {
        val originTitleList = dataImportMapper.selectList(QueryWrapper<DataImport>().eq("title", originTitle))
        if (originTitleList.isEmpty()) {
            return Mono.just("不存在指定产品")
        }

        val titleList = dataImportMapper.listTitle().filterNotNull()
        val computeMills = measureNanoTime {
            val similarAlgorithm = SimilarAlgorithm.Factory(alg)
                    ?: return Mono.just("未实现指定算法")
            titleList.forEach {
                similarAlgorithm.getSimilarity(originTitle, it)
            }
        } / (1000 * 1000)
        return Mono.just(mapOf(
                "computeMills" to computeMills
        ))
    }
}
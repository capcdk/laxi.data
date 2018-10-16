package com.laxi.data.algorithm

/**
 * Created by Chendk on 2018/10/16
 */
interface SimilarAlgorithm {

    fun getSimilarity(source: String, target: String): Float

    companion object Factory {
        operator fun invoke(algorithm: String): SimilarAlgorithm? {
            return when (algorithm.toLowerCase()) {
                "dyn" -> DynamicProgramming()
                "lev" -> Levenshtein()
                "smi" -> SmithWaterMan()
                else -> null
            }
        }
    }
}
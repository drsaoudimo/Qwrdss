package com.example

import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.sqrt

object MatrixMath {

    /**
     * Calculates the trace of a 2D matrix.
     */
    fun trace(matrix: Array<DoubleArray>): Double {
        val n = minOf(matrix.size, if (matrix.isNotEmpty()) matrix[0].size else 0)
        var sum = 0.0
        for (i in 0 until n) {
            sum += matrix[i][i]
        }
        return sum
    }

    /**
     * Computes the Frobenius inner product of A and B.
     */
    fun frobeniusInnerProduct(A: Array<DoubleArray>, B: Array<DoubleArray>): Double {
        var sum = 0.0
        val rows = minOf(A.size, B.size)
        for (i in 0 until rows) {
            val cols = minOf(A[i].size, B[i].size)
            for (j in 0 until cols) {
                sum += A[i][j] * B[i][j]
            }
        }
        return sum
    }

    /**
     * Computes the Frobenius norm of A.
     */
    fun frobeniusNorm(A: Array<DoubleArray>): Double {
        return sqrt(frobeniusInnerProduct(A, A))
    }

    /**
     * Calculates sparsity percentage.
     */
    fun sparsity(matrix: Array<DoubleArray>): Double {
        if (matrix.isEmpty()) return 1.0
        var zeroCount = 0
        val total = matrix.size * matrix[0].size
        for (row in matrix) {
            for (valEntry in row) {
                if (abs(valEntry) < 1e-9) {
                    zeroCount++
                }
            }
        }
        return zeroCount.toDouble() / total.toDouble()
    }

    /**
     * Performs Gaussian Elimination with partial pivoting to calculate Rank and Determinant.
     * Returns a Pair of Pair(Rank, Determinant).
     */
    fun gaussianMetrics(matrix: Array<DoubleArray>): Pair<Int, Double> {
        val n = matrix.size
        if (n == 0) return Pair(0, 0.0)
        val m = matrix[0].size
        
        // Deep copy matrix to mutable double grid
        val a = Array(n) { i -> DoubleArray(m) { j -> matrix[i][j] } }
        
        var det = 1.0
        var rank = 0
        var pivotRow = 0
        
        for (col in 0 until m) {
            if (pivotRow >= n) break
            
            // Find pivot
            var maxRow = pivotRow
            var maxVal = abs(a[pivotRow][col])
            for (r in pivotRow + 1 until n) {
                val v = abs(a[r][col])
                if (v > maxVal) {
                    maxVal = v
                    maxRow = r
                }
            }
            
            // Skip column if too small
            if (maxVal < 1e-9) {
                det = 0.0
                continue
            }
            
            // Swap rows
            if (maxRow != pivotRow) {
                val temp = a[pivotRow]
                a[pivotRow] = a[maxRow]
                a[maxRow] = temp
                det *= -1.0
            }
            
            val pivot = a[pivotRow][col]
            det *= pivot
            
            // Eliminate values down
            for (r in pivotRow + 1 until n) {
                val factor = a[r][col] / pivot
                for (c in col until m) {
                    a[r][c] -= factor * a[pivotRow][c]
                }
            }
            
            rank++
            pivotRow++
        }
        
        if (rank < n) det = 0.0
        return Pair(rank, det)
    }

    /**
     * Calculates the symmetric eigenvalues of 0.5 * (A + A^T) using Jacobi rotation method.
     * Returns a list of real eigenvalues.
     */
    fun symmetricEigenvalues(matrix: Array<DoubleArray>, maxSweeps: Int = 12): List<Double> {
        val n = matrix.size
        if (n == 0) return emptyList()
        
        // Define S = 0.5 * (A + A^T)
        val a = Array(n) { i ->
            DoubleArray(n) { j ->
                0.5 * (matrix[i][j] + matrix[j][i])
            }
        }
        
        val d = DoubleArray(n) { i -> a[i][i] }
        val v = Array(n) { i -> DoubleArray(n) { j -> if (i == j) 1.0 else 0.0 } }
        
        for (sweep in 0 until maxSweeps) {
            var sumOfOffDiagonals = 0.0
            for (i in 0 until n - 1) {
                for (j in i + 1 until n) {
                    sumOfOffDiagonals += abs(a[i][j])
                }
            }
            
            if (sumOfOffDiagonals < 1e-9) break
            
            for (p in 0 until n - 1) {
                for (q in p + 1 until n) {
                    val gap = 100.0 * abs(a[p][q])
                    if (sweep > 3 && gap < 1e-15) {
                        a[p][q] = 0.0
                    } else if (abs(a[p][q]) > 1e-20) {
                        val h = d[q] - d[p]
                        val t: Double
                        if (gap < 1e-15) {
                            t = a[p][q] / (h + 1e-30)
                        } else {
                            val theta = 0.5 * h / a[p][q]
                            var tempT = 1.0 / (abs(theta) + sqrt(1.0 + theta * theta))
                            if (theta < 0) tempT = -tempT
                            t = tempT
                        }
                        
                        val c = 1.0 / sqrt(1.0 + t * t)
                        val s = t * c
                        val tau = s / (1.0 + c)
                        
                        val hVal = t * a[p][q]
                        d[p] -= hVal
                        d[q] += hVal
                        a[p][q] = 0.0
                        
                        for (j in 0 until p) {
                            val g = a[j][p]
                            val hTemp = a[j][q]
                            a[j][p] = g - s * (hTemp + g * tau)
                            a[j][q] = hTemp + s * (g - hTemp * tau)
                        }
                        for (j in p + 1 until q) {
                            val g = a[p][j]
                            val hTemp = a[j][q]
                            a[p][j] = g - s * (hTemp + g * tau)
                            a[j][q] = hTemp + s * (g - hTemp * tau)
                        }
                        for (j in q + 1 until n) {
                            val g = a[p][j]
                            val hTemp = a[q][j]
                            a[p][j] = g - s * (hTemp + g * tau)
                            a[q][j] = hTemp + s * (g - hTemp * tau)
                        }
                        for (r in 0 until n) {
                            val g = v[r][p]
                            val hTemp = v[r][q]
                            v[r][p] = g - s * (hTemp + g * tau)
                            v[r][q] = hTemp + s * (g - hTemp * tau)
                        }
                    }
                }
            }
            
            for (i in 0 until n) {
                a[i][i] = d[i]
            }
        }
        
        return d.toList().sortedDescending()
    }

    /**
     * Calculates Shannon Spectral Entropy and Condition Number.
     * Returns a Pair of Pair(SpectralEntropy, ConditionNumber).
     */
    fun spectralMetrics(matrix: Array<DoubleArray>): Pair<Double, Double> {
        val eigen = symmetricEigenvalues(matrix).map { abs(it) }
        if (eigen.isEmpty()) return Pair(0.0, 1.0)
        
        // Spectral Entropy
        val normSum = eigen.sum()
        var entropy = 0.0
        if (normSum > 1e-9) {
            for (e in eigen) {
                val p = e / normSum
                if (p > 1e-9) {
                    entropy -= p * ln(p)
                }
            }
        }
        
        // Condition Number
        val maxE = eigen.first()
        val minE = eigen.last()
        val conditionNo = if (abs(minE) < 1e-9) 1e9 else maxE / minE
        
        return Pair(entropy, conditionNo)
    }

    /**
     * Generates a 2D Projection (PCA-like) of all 114 Surahs on the similarity matrix.
     * Returns a List of Pairs specifying (x, y) coordinates scaled within [-1, 1].
     */
    fun pcaProjection(similarityMatrix: Array<DoubleArray>): List<Pair<Float, Float>> {
        val n = similarityMatrix.size
        if (n == 0) return emptyList()
        
        // Center the similarity matrix: K_centered = I_c * S * I_c
        val rowSums = DoubleArray(n) { i -> similarityMatrix[i].sum() }
        val grandSum = rowSums.sum()
        
        val a = Array(n) { i ->
            DoubleArray(n) { j ->
                similarityMatrix[i][j] - rowSums[i]/n - rowSums[j]/n + grandSum/(n*n)
            }
        }
        
        // Power Iteration for Eigenvector 1
        val v1 = DoubleArray(n) { 1.0 / sqrt(n.toDouble()) }
        for (iter in 0 until 40) {
            val nextV = DoubleArray(n)
            for (i in 0 until n) {
                for (j in 0 until n) {
                    nextV[i] += a[i][j] * v1[j]
                }
            }
            val norm = sqrt(nextV.map { it * it }.sum())
            if (norm < 1e-9) break
            for (i in 0 until n) v1[i] = nextV[i] / norm
        }
        
        // Deflate key dimension 1: a_new = a - lambda1 * v1 * v1^T
        val lambda1Vec = DoubleArray(n)
        for (i in 0 until n) {
            for (j in 0 until n) {
                lambda1Vec[i] += a[i][j] * v1[j]
            }
        }
        val lambda1 = lambda1Vec.zip(v1).map { it.first * it.second }.sum()
        
        val aDeflated = Array(n) { i ->
            DoubleArray(n) { j ->
                a[i][j] - lambda1 * v1[i] * v1[j]
            }
        }
        
        // Power Iteration for Eigenvector 2
        val v2 = DoubleArray(n) { if (it % 2 == 0) 1.0 else -1.0 }
        // Orthogonalize v2 with respect to v1
        val dot = v1.zip(v2).map { it.first * it.second }.sum()
        for (i in 0 until n) v2[i] -= dot * v1[i]
        var initNorm = sqrt(v2.map { it * it }.sum())
        if (initNorm > 1e-9) {
            for (i in 0 until n) v2[i] /= initNorm
        }
        
        for (iter in 0 until 40) {
            val nextV = DoubleArray(n)
            for (i in 0 until n) {
                for (j in 0 until n) {
                    nextV[i] += aDeflated[i][j] * v2[j]
                }
            }
            // Orthogonalize
            val orthoDot = v1.zip(nextV).map { it.first * it.second }.sum()
            for (i in 0 until n) nextV[i] -= orthoDot * v1[i]
            
            val norm = sqrt(nextV.map { it * it }.sum())
            if (norm < 1e-9) break
            for (i in 0 until n) v2[i] = nextV[i] / norm
        }
        
        // Projection coordinates
        val coords = ArrayList<Pair<Float, Float>>()
        for (i in 0 until n) {
            var x = 0.0
            var y = 0.0
            for (j in 0 until n) {
                x += similarityMatrix[i][j] * v1[j]
                y += similarityMatrix[i][j] * v2[j]
            }
            coords.add(Pair(x.toFloat(), y.toFloat()))
        }
        
        // Normalize coordinates to [-1, 1] range safely
        val minX = coords.minOf { it.first }
        val maxX = coords.maxOf { it.first }
        val minY = coords.minOf { it.second }
        val maxY = coords.maxOf { it.second }
        
        val rangeX = if (maxX - minX < 1e-5f) 1.0f else maxX - minX
        val rangeY = if (maxY - minY < 1e-5f) 1.0f else maxY - minY
        
        return coords.map {
            Pair(
                -1f + 2f * (it.first - minX) / rangeX,
                -1f + 2f * (it.second - minY) / rangeY
            )
        }
    }
}

import java.util.*

fun createRandomField(height: Int, width: Int): Field<Int> {
    val num = height * width
    val field = FieldImpl(height, width, 0)
    val list = (0 until num).toMutableList()
    var i = 0
    while (list.isNotEmpty()) {
        val value = Random().nextInt(num)
        if (value in list) {
            field[i / width, i % width] = value
            list.remove(value)
            i++
        }
    }
    return field
}

fun fifteenGameMoves(field: Field<Int>, moves: List<Int>): Field<Int> {
    val M = FieldImpl(field.height, field.width, 0)
    for (i in 0 until M.height) for (j in 0 until M.width) { M[i, j] = field[i, j]; M[field[i, j]] = Cell(i, j) }
    val num = M.height * M.width
    for (i in 0 until moves.size) {
        if (moves[i] !in 1 until num) throw IllegalStateException()
        else {
            val deltaColumn = Math.abs(M[moves[i]].column - M[0].column)
            val deltaRow = Math.abs(M[moves[i]].row - M[0].row)
            if (deltaColumn == 1 && deltaRow == 0 || deltaRow == 1 && deltaColumn == 0) {
                M[M[0]] = moves[i]
                M[M[moves[i]]] = 0
                val x = M[0]
                M[0] = M[moves[i]]
                M[moves[i]] = x
            }
            else throw IllegalStateException()
        }
        printlnField(M)
    }
    return M
}

fun fifteenGameSolution(field: Field<Int>): List<Int> {
    val stepsLeftDown = listOf(1, 2, 2, 3, 0)
    val stepsLeftUp = listOf(3, 2, 2, 1, 0)
    val stepsUpRight = listOf(0, 3, 3, 2, 1)
    val stepRightDown = listOf(1, 0, 0, 3, 2)
    val stepRightUp = listOf(3, 0, 0, 1, 2)
    var trajectory = listOf<Int>()
    var tempSteps: List<Int>
    val M = FieldImpl(field.height, field.width, 0)
    for (i in 0 until M.height)
        for (j in 0 until M.width) {
            M[i, j] = field[i, j]
            M[field[i, j]] = Cell(i, j)
        }
    for (l in 0 until field.height - 2) {
        println(l)
        trajectory += M.change(List(M[0].row - l) { 3 } + List(M[0].column) { 2 })
        for (k in l * M.width + 1..(l + 1) * M.width) {
            if (M[k].column < (k - 1) % M.width) {
                trajectory += M.change(List(M[k].row - M[0].row) { 1 } + List(M[0].column - M[k].column) { 2 })
                tempSteps = listOf()
                if (M[k].row == l + 1) {
                    for (i in 1..(k - 1) % M.width - M[k].column) tempSteps += stepRightDown
                    trajectory += M.change(tempSteps + listOf(1, 0))
                } else {
                    for (i in 1..(k - 1) % M.width - M[k].column) tempSteps += stepRightUp
                    trajectory += M.change(tempSteps + listOf(3, 0))
                }
                trajectory += M.change(List(M[0].row - l) { 3 })
            }
            if (M[k].column != (k - 1) % M.width) {
                trajectory += M.change(List(M[k].row - M[0].row) { 1 } +
                        List(M[k].column - M[0].column) { 0 })
                val plus = if (M[k].row == l) stepsLeftDown else stepsLeftUp
                tempSteps = listOf()
                for (i in k - l * M.width..M[k].column) tempSteps += plus
                trajectory += M.change(tempSteps)
            }
            if (M[k].row != l && k % M.width != 0) {
                trajectory += M.change(if (M[0].column > M[k].column) listOf(3, 2, 1)
                else List(M[k].row - l) { 1 })
                tempSteps = listOf()
                for (i in 0 until M[k].row - l) tempSteps += stepsUpRight
                trajectory += M.change(tempSteps + listOf(0, 3))
            }
        }
        trajectory += M.change(listOf(1, 2, 2))
        if (M[(l + 1) * M.width].row == l) continue
        val deltaRow = M[(l + 1) * M.width].row - M[0].row
        val deltaColumn = M[(l + 1) * M.width].column - M[0].column
        val plus = List(deltaRow) { 1 } + List(deltaColumn) { 0 } + List(deltaRow) { 3 } + List(deltaColumn) { 2 }
        tempSteps = listOf()
        for (k in 1..deltaRow + deltaColumn) tempSteps += plus
        trajectory += M.change(tempSteps + listOf(3, 0, 1, 0, 3, 2, 2, 1))
    }
    trajectory += M.change(List(M[0].column) { 2 })
    for (l in 0 until M.width - 2) {
        var k = M.width * (M.height - 1) + 1 + l
        for (j in 0..1) {
            if (M[k].column < j + l) trajectory += M.change(listOf(1, 2, 3, 0, 1, 0, 3, 2, 2, 1, 0, 3))
            if (M[k].column != j + l) {
                trajectory += M.change(List(M[k].row - M[0].row) { 1 } +
                        List(M[k].column - M[0].column) { 0 })
                val plus = if (M[k].row == M.height - 2) stepsLeftDown else stepsLeftUp
                tempSteps = listOf<Int>()
                for (i in j + l until M[k].column) tempSteps += plus
                trajectory += M.change(tempSteps)
            }
            if (M[k].row == M.height - 1) {
                trajectory += M.change(if (M[0].column > M[k].column) listOf(3, 2, 1, 0, 3)
                else listOf(1, 0, 3))
            }
            k -= M.width
        }
        trajectory += M.change(stepsLeftDown)
    }
    while (M[M.width * M.height - 1] != Cell(M.height - 1, M.width - 1))
        trajectory += M.change(listOf(0, 1, 2, 3))
    trajectory += M.change(if (M[M.height - 1, M.width - 2] > M[M.height - 2, M.width - 1])
        listOf(1, 2, 3, 0, 1, 0, 3, 2, 2, 1, 0, 0, 3, 2, 1, 0) else listOf(1, 0))
    return trajectory
}

fun FieldImpl<Int>.change(moves: List<Int>): MutableList<Int> {
    val trajectory = mutableListOf<Int>()
    for (i in 0 until moves.size) {
        var dColumn = 0
        var dRow = 0
        when (moves[i]) {
            0 -> dColumn = 1  //right
            1 -> dRow = 1     //down
            2 -> dColumn = -1 //left
            3 -> dRow = -1    //up
            else -> throw IllegalStateException()
        }
        if (this[0].row + dRow !in 0 until this.height || this[0].column + dColumn !in 0 until this.width)
            throw IllegalStateException()
        val digit = this[this[0].row + dRow, this[0].column + dColumn]
        trajectory.add(digit)
        this[this[0]] = digit
        this[this[digit]] = 0
        this[0] = this[digit]
        this[digit] = Cell(this[0].row - dRow, this[0].column - dColumn)
    }
    return trajectory
}

fun main(args: Array<String>) {
    val m = createRandomField(4, 4)
    fifteenGameMoves(m, fifteenGameSolution(m))
}

fun twoStr(n: Int) = when (n) {
    0 -> "  ██"
    in 1..9 -> "   $n"
    in 10..99 -> "  $n"
    in 100..999 -> " $n"
    else -> throw IllegalArgumentException("num of digits is more then 1000")
}

fun printlnField(field: Field<Int>) {
    for (j in 0 until field.height) {
        for (k in 0 until field.width) print(twoStr(field[j, k]))
        println("")
        println("")
    }
    readLine()
}
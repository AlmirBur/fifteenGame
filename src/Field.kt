data class Cell(val row: Int, val column: Int)

interface Field<E> {
    val height: Int
    val width: Int

    operator fun get(row: Int, column: Int): E
    operator fun get(cell: Cell): E

    operator fun set(row: Int, column: Int, value: E)
    operator fun set(cell: Cell, value: E)
}

class FieldImpl<E>(override val height: Int, override val width: Int, e: E) : Field<E> {
    private val elements = mutableMapOf<Cell, E>()

    private val keys = MutableList(this.height * this.width) { Cell(0, 0) }

    init { for (i in 0 until height) for (j in 0 until width) elements[Cell(i, j)] = e }

    override fun get(row: Int, column: Int): E = get(Cell(row, column))

    override fun get(cell: Cell): E = elements[cell] ?: throw IllegalArgumentException()

    operator fun get(index: Int): Cell = keys[index]

    override fun set(row: Int, column: Int, value: E) {
        if (row !in 0 until height || column !in 0 until width) throw IllegalArgumentException()
        else elements[Cell(row, column)] = value
    }

    override fun set(cell: Cell, value: E) = set(cell.row, cell.column, value)

    operator fun set(index: Int, element: Cell) { keys[index] = element }

    override fun equals(other: Any?) = if (other is Field<*> && height == other.height && width == other.width) {
        var result = true
        for (i in 0 until height) for (j in 0 until width) if (other[i, j] != this[i, j]) {result = false; break}
        result
    }
    else false


    override fun hashCode(): Int {
        var result = height
        result = 31 * result + width
        result = 31 * result + elements.hashCode()
        return result
    }

    override fun toString(): String {
        val res = mutableListOf<String>()
        res.add("[ ")
        for (row in 0 until height) {
            res.add("[")
            for (column in 0 until width) {
                res.add(this[row, column].toString())
                res.add(",")
            }
            res.removeAt(res.size - 1)
            res.add("] ")
        }
        res.add("]")
        return res.joinToString("")
    }
}
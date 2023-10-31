package ru.netology.nmedia.entity.utills

class ConverterCountFromIntToString {
    companion object {
        fun convertCount(count: Int): String = when (count) {
            in (0 until 1000) -> count.toString()
            in (1000 until 1100) -> ((count / 1000)).toString() + "K"
            in (1100 until 10_000) -> ((count - count % 100).toFloat() / 1000).toString() + "K"
            in (10_000 until 1000_000) -> ((count) / 1000).toString() + "K"
            in (1000_000 until 1100_000) -> ((count / 1000_000)).toString() + "M"
            in (1100_000 until 10_000_000) -> ((count - count % 100_000).toFloat() / 1000_000).toString() + "M"
            in (10_000_000 until 1000_000_000) -> ((count / 1000_000)).toString() + "M"
            else -> ">1B"
        }
    }
}

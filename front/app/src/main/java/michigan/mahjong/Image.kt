package michigan.mahjong

fun getIcon(name: String) : Int {
    return when(name) {
        "1m" -> R.drawable.m1
        "2m" -> R.drawable.m2
        "3m" -> R.drawable.m3
        "4m" -> R.drawable.m4
        "5m" -> R.drawable.m5
        "6m" -> R.drawable.m6
        "7m" -> R.drawable.m7
        "8m" -> R.drawable.m8
        "9m" -> R.drawable.m9
        "1p" -> R.drawable.p1
        "2p" -> R.drawable.p2
        "3p" -> R.drawable.p3
        "4p" -> R.drawable.p4
        "5p" -> R.drawable.p5
        "6p" -> R.drawable.p6
        "7p" -> R.drawable.p7
        "8p" -> R.drawable.p8
        "9p" -> R.drawable.p9
        "1s" -> R.drawable.s1
        "2s" -> R.drawable.s2
        "3s" -> R.drawable.s3
        "4s" -> R.drawable.s4
        "5s" -> R.drawable.s5
        "6s" -> R.drawable.s6
        "7s" -> R.drawable.s7
        "8s" -> R.drawable.s8
        "9s" -> R.drawable.s9
        "1z" -> R.drawable.z1
        "2z" -> R.drawable.z2
        "3z" -> R.drawable.z3
        "4z" -> R.drawable.z4
        "5z" -> R.drawable.z5
        "6z" -> R.drawable.z6
        "7z" -> R.drawable.z7
        else -> {R.drawable.add}
    }
}

fun getIconName(name: String) : String {
    return when(name) {
        "1m" -> "1 Man"
        "2m" -> "2 Man"
        "3m" -> "3 Man"
        "4m" -> "4 Man"
        "5m" -> "5 Man"
        "6m" -> "6 Man"
        "7m" -> "7 Man"
        "8m" -> "8 Man"
        "9m" -> "9 Man"
        "1p" -> "1 Pin"
        "2p" -> "2 Pin"
        "3p" -> "3 Pin"
        "4p" -> "4 Pin"
        "5p" -> "5 Pin"
        "6p" -> "6 Pin"
        "7p" -> "7 Pin"
        "8p" -> "8 Pin"
        "9p" -> "9 Pin"
        "1s" -> "1 Sou"
        "2s" -> "2 Sou"
        "3s" -> "3 Sou"
        "4s" -> "4 Sou"
        "5s" -> "5 Sou"
        "6s" -> "6 Sou"
        "7s" -> "7 Sou"
        "8s" -> "8 Sou"
        "9s" -> "9 Sou"
        "1z" -> "East"
        "2z" -> "South"
        "3z" -> "West"
        "4z" -> "North"
        "5z" -> "White Dragon"
        "6z" -> "Green Dragon"
        "7z" -> "Red Dragon"
        else -> {""}
    }
}
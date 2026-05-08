package product.lifecycle.windenergy

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
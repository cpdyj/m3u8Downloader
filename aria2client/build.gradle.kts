
plugins{
    application
}

application{
    mainClassName="MainKt"
}

dependencies {
    implementation("org.jsoup:jsoup:1.13.1")

//    implementation(files("libs/kotlin-grammar-tools-0.1-43.jar"))
}

//sourceSets["main"].let { println(it.allSource.toList()) }

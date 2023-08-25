plugins {
    kotlin("jvm") version "1.8.21"
    java
}

group = "cn.gionrose.facered"
version = "1.0.1-04"

repositories {

    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://repo.tabooproject.org/repository/releases/")
        isAllowInsecureProtocol = true
    }
    maven {
        url = uri("https://repo.rosewooddev.io/repository/public/")

    }
}

dependencies {

    compileOnly(kotlin("stdlib"))
    implementation("com.google.code.gson:gson:2.10.1")
    compileOnly("ink.ptms.core:v12001:12001:mapped")
    compileOnly("ink.ptms.core:v12001:12001:universal")
    compileOnly ("org.black_ixx:playerpoints:3.2.6")
    compileOnly (fileTree("src/libs"))
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("com.zaxxer:HikariCP:5.0.1")//数据库连接池
    implementation("com.google.code.gson:gson:2.10.1")
}



tasks.withType<Jar> {
    destinationDirectory.set (File ("F:\\mc\\mcPaperServer\\plugins"))
}



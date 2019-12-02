install:
	gradle clean build jar
	cp out/artifacts/cdd_kotlin_main_jar/cdd-kotlin.main.jar ~/.cdd/bin/cdd-kotlin.jar
	echo '#!/bin/sh\njava -jar ~/.cdd/bin/cdd-kotlin.jar $$@' > ~/.cdd/bin/cdd-kotlin
	chmod 777 ~/.cdd/bin/cdd-kotlin

build:
	kotlinc src/main/kotlin/MainKotlin.kt -include-runtime -d cdd-kotlin.jar

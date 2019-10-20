build:
	cp out/artifacts/cdd_kotlin_main_jar/cdd-kotlin.main.jar jar/cdd-kotlin.jar

install:
	cp jar/cdd-kotlin.jar ~/.cdd/services/cdd-kotlin.jar
	echo '#!/bin/sh\njava -jar ~/.cdd/services/cdd-kotlin.jar $$1' > ~/.cdd/services/cdd-kotlin
	chmod 777 ~/.cdd/services/cdd-kotlin

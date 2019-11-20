install:
	gradle clean build jar
	cp build/libs/cdd-kotlin-1.0-SNAPSHOT.jar ~/.cdd/services/cdd-kotlin.jar
	echo '#!/bin/sh\njava -jar ~/.cdd/services/cdd-kotlin.jar $$@' > ~/.cdd/services/cdd-kotlin
	chmod 777 ~/.cdd/services/cdd-kotlin

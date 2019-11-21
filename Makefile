install:
	gradle clean build jar
	cp cdd-kotlin.jar ~/.cdd/bin/cdd-kotlin.jar
	echo '#!/bin/sh\njava -jar ~/.cdd/bin/cdd-kotlin.jar $$@' > ~/.cdd/bin/cdd-kotlin
	chmod 777 ~/.cdd/bin/cdd-kotlin

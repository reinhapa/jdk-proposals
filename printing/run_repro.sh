#!/bin/bash
for i in {1..1}
do
   echo "Run $i"
   java --add-exports java.desktop/sun.print=ALL-UNNAMED \
        -Dsun.print.ippdebug=true \
        -Dsun.java2d.print.polling=true \
        -Dsun.java2d.print.minRefreshTime=120 \
        src/main/java/Reproducer.java | grep "SUCCESS"
done

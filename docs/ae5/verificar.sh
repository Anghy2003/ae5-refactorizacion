#!/bin/sh
# Ciclo seguro de Ae5: ejecuta la suite y comprueba que la salida observable
# de los nueve escenarios sigue siendo identica a la linea base original.
#   uso: sh docs/ae5/verificar.sh <nombre-del-paso>
cd "$(dirname "$0")/../.." || exit 1
PASO="$1"
mvn clean test > "docs/ae5/tests-$PASO.txt" 2>&1
if ! grep -q "BUILD SUCCESS" "docs/ae5/tests-$PASO.txt"; then
    echo "PRUEBAS EN ROJO ($PASO):"; grep -E "Tests run|FAIL|ERROR" "docs/ae5/tests-$PASO.txt" | head -20; exit 1
fi
grep -E "^\[INFO\] Tests run: .*, Time" "docs/ae5/tests-$PASO.txt" | tail -1
mvn -q exec:java "-Dexec.mainClass=edu.uees.refactor.app.LineaBase" > "docs/ae5/salida-$PASO.txt" 2>&1
if diff -q docs/ae5/salida-linea-base-original.txt "docs/ae5/salida-$PASO.txt" > /dev/null; then
    echo "OK  pruebas verdes y salida identica a la linea base  ($PASO)"
else
    echo "DIFERENCIAS en la salida observable ($PASO):"
    diff docs/ae5/salida-linea-base-original.txt "docs/ae5/salida-$PASO.txt" | head -20; exit 1
fi

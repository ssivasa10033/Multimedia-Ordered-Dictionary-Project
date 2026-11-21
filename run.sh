#!/usr/bin/env bash
# Compiles the dictionary and starts the query CLI.
#   ./run.sh                  uses data/sample-input.txt
#   ./run.sh path/to/file.txt uses another input file
set -euo pipefail

cd "$(dirname "$0")"
input="${1:-data/sample-input.txt}"

if [ ! -f "$input" ]; then
  echo "Input file not found: $input" >&2
  exit 1
fi

mkdir -p out
javac -d out src/*.java support/*.java
java -cp out Query "$input"

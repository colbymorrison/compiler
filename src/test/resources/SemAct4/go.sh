#! /bin/bash
for FILE in "array-ref" "array" "array" "expression" "fib" "func-manyinputs" "func" "if" "many-reads-writes" "missing-semicolon" "noparm" "proc" "recursion" "simple" "testi" "ultimate" "uminus" "while" 
 do
   curl -O https://www.cs.vassar.edu/~cs331/semantic_actions/phase4_test_files/$FILE.pas
   curl -O  https://www.cs.vassar.edu/~cs331/semantic_actions/phase4_test_files/$FILE.tvi
 done

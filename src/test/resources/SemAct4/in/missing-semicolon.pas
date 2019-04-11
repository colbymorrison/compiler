program missingSemicolon (input, output);
 var a, b : integer;
        c : real;
 begin
   a := 3;
   b := a * 4;
   {the next line is missing a semi-colon and should throw an error}
   c := (b + a)/ 2

   write(a,b,c)

 end.
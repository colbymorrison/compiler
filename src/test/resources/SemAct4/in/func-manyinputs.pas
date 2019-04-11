program funcTest(input, output);
var
        i, j, k, l, m:integer;
        n, o, p, q:real;

function Sum(a,b,c,d:integer; e,f,g,h:real): result integer;
begin
        write(a,b,c,d);
        Sum := (((a * b) + c) * d)
end

begin
        read(i,j,l,m);
        write(i,j,l,m);
        k := Sum(i,j,l,m,n,o,p,q) * 2;
        write(k)
end.
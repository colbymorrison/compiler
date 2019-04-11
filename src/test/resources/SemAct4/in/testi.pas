program testitest(input, output);
var
    a,b,c:integer;

function testi(a,b:integer): result integer;
begin

	if a < b then
	testi := b
	else testi := testi(a,b+1)

end

begin
c := testi(3,1);
write(c)
end.
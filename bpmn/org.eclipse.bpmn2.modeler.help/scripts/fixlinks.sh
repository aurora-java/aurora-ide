mkdir ../html.tmp
cd ../html
for file in *.htm
do
	awk -F"a href=" '
	BEGIN {
		while (getline < "../scripts/links.txt") {
			split($0, a, "\t");
			link = a[1]
			fn = a[2]
			lookup[link] = fn
		}
	}
	{
		if (match($0, /"#[A-Za-z0-9_]*"/)) {
			
			for (i=1; i<=NF; ++i) {
				txt = $i
				split(txt, a, "\"");
				sub("#","",a[2])
				link = a[2]
				fn = lookup[link]
				if (sub(/#[A-Za-z0-9_]*/, fn "#" link,txt)==1) {
					printf("a href=\%s", txt)
				}
				else {
					printf("%s", $i)
				}
			}
			print ""
		}
		else {
			print
		}
	} ' "$file" >../html.tmp/"$file"
done

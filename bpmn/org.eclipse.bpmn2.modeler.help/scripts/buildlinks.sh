cd ../html
> ../scripts/links.txt
for file in *.htm
do
	awk -F"<a name=" '
	{
		for (i=1; i<=NF; ++i) {
			link = $i
			if (sub(/"/,"",link) == 1) {
				if (sub(/".*/,"",link) == 1) {
					if (link ~ "^_") {
						printf("%s\t%s\n", link, FILENAME);
					}
				}
			}

		}
	}
	' "$file" >>../scripts/links.txt
done

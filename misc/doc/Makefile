all: overview.pdf

overview.dvi: .pics

.pics: oview.fun
	java -cp ../obj geomlab.RunScript ../src/solns.txt $<
	echo datestamp >$@

oview.fun: overview.tex
	sed -n '/^%> /s///p' $< >$@

# all: talk1.pdf talk2.pdf

talk2.dvi: pics/talk2-1.eps

pics/talk2-1.eps: talk2.fun
	java -cp ../obj geomlab.RunScript ../src/solns.txt $<

talk2.fun: talk2.tex
	sed -n '/^%> /s///p' $< >$@

%.tex: %.ms
	../tex/obgrind -e $< >$@

%.dvi: %.tex
	tex $<

%.ps: %.dvi
	dvips -Ppdf $< -o $@

%.pdf: %.ps
	ps2pdf $<

clean: force
	rm -f .pics oview.fun *.dvi *.ps *.pdf *.log

force:

export TEXINPUTS = ../tex:

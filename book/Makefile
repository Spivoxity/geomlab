# book/Makefile

all: workbook.pdf

TRAN = intro.tex sheet1.tex sheet2.tex sheet3.tex sheet4.tex \
	sheet5.tex sheet6.tex sheet7.tex sheet8.tex

workbook.dvi: $(TRAN)

publish: .publish

.publish: workbook.pdf
	tclsh ../scripts/image-upload.tcl $?
	echo timestamp >$@

%.pdf: %.dvi
	dvips -Ppdf $<
	ps2pdf $*.ps

%.dvi: %.tex
	tex $<

$(TRAN): %.tex: ../wiki/%.wiki ../scripts/textran.tcl
	tclsh ../scripts/textran.tcl $< >$@

clean: force
	rm -f .publish workbook.pdf workbook.ps *.dvi *.bkm *.log *.bko $(TRAN) 

realclean: clean

.DELETE_ON_ERROR:

export TEXINPUTS = .:../figs:../img:

force:

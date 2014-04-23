# book/Makefile

all: workbook.pdf

TRAN = intro.tex sheet1.tex sheet2.tex sheet3.tex sheet4.tex \
	sheet5.tex sheet6.tex sheet7.tex sheet8.tex sheet9.tex

workbook.dvi: $(TRAN)

%.pdf: %.dvi
	dvips -Ppdf $<
	ps2pdf $*.ps

%.dvi: %.tex
	tex $<

$(TRAN): %.tex: ../wiki/%.wiki ../scripts/textran.tcl
	tclsh ../scripts/textran.tcl $< >$@

.DELETE_ON_ERROR:

export TEXINPUTS = .:../figs:

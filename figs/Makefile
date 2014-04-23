# figs/Makefile -- figures for worksheets, etc.

SOURCES = sheet1 sheet2 sheet3 sheet4 sheet5 sheet6 \
	sheet7 sheet8 sheet9 tiles

all: $(SOURCES:%=.%-fig) blank.png limit3-400.png invlimit-400.png

RUNSCRIPT = java -cp ../obj geomlab.RunScript ../progs/solutions.txt

blank.png: ../progs/solutions.txt
	$(RUNSCRIPT) -e 'makepic(solid(1, white), "blank")'

limit3-%.png: ../progs/solutions.txt
	$(RUNSCRIPT) -e 'makepic_r(colour(limit(3)), "limit3-$*", $*)'

invlimit-%.png: ../progs/solutions.txt
	$(RUNSCRIPT) -e 'makepic_r(colour(invlimit(10)), "invlimit-$*", $*)'

.%-fig: %.fun ../progs/solutions.txt
	$(RUNSCRIPT) $<
	echo timestamp >$@

# The scripts sheet1.fun, etc., are made in such a way that their
# timestamps are not updated if they haven't changed.

%.fun: .%-fun
	@test -e $@ || { rm $<; $(MAKE) $<; }

.%-fun: %.wiki ../scripts/mkscript.sed
	sed -n -f ../scripts/mkscript.sed $< >tmp
	cmp tmp $*.fun || cp tmp $*.fun
	@rm -f tmp
	echo timestamp >$@

vpath %.wiki ../wiki

.PRECIOUS: %.fun .%-fun

IMAGES := $(wildcard *.png) $(wildcard *.jpg)

publish: .publish

.publish: $(IMAGES)
	tclsh ../scripts/image-upload.tcl $?
	echo timestamp >$@

cleanup: force
	tclsh ../scripts/image-cleanup.tcl $(IMAGES)	

clean: force
	rm -f .*-fun .*-fig $(SOURCES:%=%.fun) *.png *.eps *.jpg

force:

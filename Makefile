# Top level Makefile for GeomLab

JAVAC = /usr/lib/jvm/java-7-openjdk-i386/bin/javac

PACKAGES = funbase funjit geomlab plugins
JAVA := $(patsubst src/%,%,$(foreach pkg,$(PACKAGES),$(wildcard src/$(pkg)/*)))
SOURCE = $(JAVA) boot.txt compiler.txt prelude.txt 
HELP = commands errors language library tips
RESOURCES = VeraMono.ttf mike.jpg mikelet.jpg contents.html style.css \
	$(HELP:%=%.html) properties
ICONS = icon16.png icon32.png icon64.png icon128.png
IMAGES = obj/geomlab.gls examples.gls

all: prep .compiled $(RESOURCES:%=obj/%) $(IMAGES) $(ICONS:%=obj/%)

prep: force
	@mkdir -p obj

JFILES = $(PACKAGES:%=src/%/*.java)
.compiled: $(wildcard $(JFILES))
	$(JAVAC) -d obj $(JFILES)
	echo timestamp >$@

obj/%: res/%; cp $< $@
obj/%: src/%; cp $< $@

obj/%.html: wiki/%.wiki wiki/htmlframe scripts/htmltran.tcl
	tclsh scripts/htmltran.tcl $< >$@

obj/icon128.png:
	runscript progs/solutions.txt -e 'savepic(colour(T), "$@", 128, 0.5, 0)'

obj/icon%.png: obj/icon128.png
	convert obj/icon128.png -scale $*x$* $@

RUNJAVA = java -cp obj -ea
RUNSCRIPT = $(RUNJAVA) geomlab.RunScript

stage1.boot: .compiled src/boot.txt src/compiler.txt
	$(RUNSCRIPT) -b src/boot.txt src/compiler.txt -e '_dump("$@")'

obj/geomlab.gls: .compiled stage1.boot src/prelude.txt
	$(RUNSCRIPT) -b stage1.boot src/compiler.txt \
		src/prelude.txt -e '_save("$@")'

examples.gls: obj/geomlab.gls progs/examples.txt
	$(RUNSCRIPT) progs/examples.txt -e '_save("$@")'

bootstrap: stage1.boot force
	$(RUNSCRIPT) -b stage1.boot src/compiler.txt -e '_dump("stage2.boot")'
	$(RUNSCRIPT) -b stage2.boot src/compiler.txt -e '_dump("stage3.boot")'
	cmp stage2.boot stage3.boot
	(sed '/^ *$$/q' src/boot.txt; cat stage2.boot) >boot.tmp
	mv boot.tmp src/boot.txt


# Web resources

PREFIX = https://spivey.oriel.ox.ac.uk/wiki/files/gl

web: web-dirs update .signed

web-dirs: force
	@mkdir -p web

update: web/.htaccess web/geomlab.jar web/examples.jar \
		web/geomlab.jnlp web/geomdemo.jnlp \
		web/arrow.png web/icon32.png web/icon64.png

web/geomlab.jar: .compiled obj/geomlab.gls $(RESOURCES:%=obj/%)
	cd obj; jar cfm ../$@ ../scripts/manifest \
		$(PACKAGES) $(RESOURCES) geomlab.gls

web/examples.jar: examples.gls
	jar cf $@ $<

.signed: web/geomlab.jar web/examples.jar
	for f in $?; do jarsigner -storepass `cat ~/.keypass` $$f mykey; done
	echo timestamp >$@

web/.htaccess: scripts/htaccess;		cp $< $@
web/%: res/%;					cp $< $@
web/%: obj/%;					cp $< $@

web/%.jnlp: scripts/%.jnlp.in
	sed 's=@CODEBASE@=$(PREFIX)=' $< >$@

publish: web force
	rsync -rvt --delete web/ spivey:wiki/files/gl

clean: force
	rm -rf obj examples.gls
	rm -f .compiled .signed

force:

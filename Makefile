# Top level Makefile for GeomLab

JAVAC = /usr/lib/jvm/java-6-openjdk-i386/bin/javac

PACKAGES = funbase funjit geomlab plugins
JAVA := $(patsubst src/%,%,$(foreach pkg,$(PACKAGES),$(wildcard src/$(pkg)/*)))
SOURCE = $(JAVA) boot.txt compiler.txt prelude.txt 
HELP = commands errors language library tips
RESOURCES = VeraMono.ttf mike.jpg mikelet.jpg contents.html style.css \
	$(HELP:%=%.html) properties sunflowers.jpg
ICONS = icon16.png icon32.png icon64.png icon128.png
SESSIONS = obj/geomlab.gls examples.gls

all: build figs book

figs book: force
	$(MAKE) -C $@ all

build: prep .compiled $(RESOURCES:%=obj/%) $(SESSIONS) $(ICONS:%=obj/%)

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

# Testing

test: force
	tclsh test/language
	tclsh test/library
	tclsh test/graphics
	tclsh test/examples


# Web resources

PREFIX = https://spivey.oriel.ox.ac.uk/gwiki/files

web: web-dirs update .signed

FILES = .htaccess geomlab.jar examples.jar geomlab.jnlp \
	deployJava.js arrow.png icon32.png icon64.png

SKIN = web/skins/GeomSkin

web-dirs: force
	@mkdir -p web/files $(SKIN)/images web/extensions

update: $(FILES:%=web/files/%) \
	$(SKIN)/GeomSkin.php $(SKIN)/screen.css \
	$(SKIN)/images/cycletpale.png $(SKIN)/images/quad96.png \
	$(SKIN)/images/document.png \
	web/LocalSettings.php \
	web/extensions/GeomGrind.php

web/files/geomlab.jar: .compiled obj/geomlab.gls $(RESOURCES:%=obj/%)
	cd obj; jar cfm ../$@ ../scripts/manifest \
		$(PACKAGES) $(RESOURCES) $(ICONS) geomlab.gls

web/files/examples.jar: examples.gls
	jar cf $@ $<

web/files/.htaccess: res/htaccess;		cp $< $@
web/files/%: obj/%;				cp $< $@
web/% web/files/% web/extensions/% \
	$(SKIN)/% $(SKIN)/images/%: res/%; 	cp $< $@

web/files/%.jnlp: res/%.jnlp.in
	sed 's=@CODEBASE@=$(PREFIX)=' $< >$@

TSA = http://timestamp.comodoca.com/rfc3161

.signed: web/files/geomlab.jar web/files/examples.jar
	for f in $?; do \
	    jarsigner -storepass `cat ~/.keypass` -tsa $(TSA) $$f mykey; \
	done
	echo timestamp >$@

publish:: web force
	rsync -rvt web/ spivey:/var/www/gwiki

publish:: force
	$(MAKE) -C wiki $@
	$(MAKE) -C figs $@
	$(MAKE) -C img $@

clean: force
	rm -rf obj examples.gls
	rm -f .compiled .signed

force:

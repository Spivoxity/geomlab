# Top level Makefile for GeomLab

JAVADIR = /usr/local/jdk1.7/bin
JAVA = $(JAVADIR)/java
JAVAC = $(JAVADIR)/javac
JAR = $(JAVADIR)/jar
JARSIGNER = $(JAVADIR)/jarsigner

PACKAGES = funbase funjit geomlab plugins
JSRC := $(patsubst src/%,%,$(foreach pkg,$(PACKAGES),$(wildcard src/$(pkg)/*)))
SOURCE = $(JSRC) boot.txt compiler.txt prelude.txt 
HELP = commands errors language library tips
RESOURCES = VeraMono.ttf mike.jpg mikelet.jpg contents.html style.css \
	$(HELP:%=%.html) properties sunflowers.jpg
ICONS = icon16.png icon32.png icon64.png icon128.png
SESSIONS = obj/geomlab.gls examples.gls

default: build figs package static

all: build figs book static

figs book: force
	$(MAKE) -C $@ all


### Build application

build: prep .compiled $(RESOURCES:%=obj/%) $(SESSIONS) $(ICONS:%=obj/%)

prep: force
	@mkdir -p obj

JFILES = $(PACKAGES:%=src/%/*.java)
.compiled: $(wildcard $(JFILES))
	$(JAVAC) -d obj $(JFILES)
	echo timestamp >$@

obj/%: res/%; cp $< $@
obj/%: src/%; cp $< $@

obj/%.html: wiki/%.wiki wiki/htmlframe.html scripts/htmltran.tcl
	tclsh scripts/htmltran.tcl $< >$@

obj/icon128.png:
	runscript progs/solutions.txt -e 'savepic(colour(T), "$@", 128, 0.5, 0)'

obj/icon%.png: obj/icon128.png
	convert obj/icon128.png -scale $*x$* $@

RUNJAVA = $(JAVA) -cp obj -ea
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

### Testing

test: force
	tclsh test/language
	tclsh test/library
	tclsh test/graphics
	tclsh test/examples

### Static website

static: force
	@mkdir -p static
	$(MAKE) -C static -f ../scripts/Makefile.static 

push: force
	rsync static/ spivey:/var/www/geomlab


### Web resources for wiki

PREFIX = https://spivey.oriel.ox.ac.uk/gwiki/files

web: web-dirs update

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

web/files/.htaccess: res/htaccess;		cp $< $@
web/files/%: obj/%;				cp $< $@
web/% web/files/% web/extensions/% \
	$(SKIN)/% $(SKIN)/images/%: res/%; 	cp $< $@

web/files/%.jnlp: res/%.jnlp.in
	sed 's=@CODEBASE@=$(PREFIX)=' $< >$@

### JAR packaging

package: geomlab.jar examples.jar

TSA = http://timestamp.globalsign.com/scripts/timestamp.dll 

SIGN = $(JARSIGNER) -keystore javakey/keystore \
	-storepass `cat javakey/storepass` -tsa $(TSA)

geomlab.jar: .compiled obj/geomlab.gls $(RESOURCES:%=obj/%)
	cd obj; $(JAR) cfm ../$@ ../scripts/manifest \
		$(PACKAGES) $(RESOURCES) $(ICONS) geomlab.gls
	$(SIGN) $@ geomlab

examples.jar: examples.gls
	$(JAR) cf $@ $<
	$(SIGN) $@ geomlab


### Publishing to wiki

HOST = spivey.oriel.ox.ac.uk
WIKI = /var/www/gwiki

publish: web force
	rsync -rvt web/ $(HOST):$(WIKI)
	$(MAKE) -C wiki $@
	$(MAKE) -C figs $@
	$(MAKE) -C img $@

purge: force
	ssh $(HOST) php $(WIKI)/maintenance/deleteOldRevisions.php --delete

clean: force
	rm -rf obj examples.gls
	rm -f .compiled .signed

realclean: clean

force:

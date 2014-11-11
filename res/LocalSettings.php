<?php
# This file was automatically generated by the MediaWiki 1.23.0
# installer. If you make manual changes, please keep track in case you
# need to recreate them later.
#
# See includes/DefaultSettings.php for all configurable settings
# and their default values, but don't forget to make changes in _this_
# file, not there.
#
# Further documentation for configuration settings may be found at:
# https://www.mediawiki.org/wiki/Manual:Configuration_settings

# Protect against web entry
if ( !defined( 'MEDIAWIKI' ) ) {
	exit;
}

include "Secrets.php";

## Uncomment this to disable output compression
# $wgDisableOutputCompression = true;

$wgSitename = "GeomLab";
$wgMetaNamespace = "Project";

## The URL base path to the directory containing the wiki;
## defaults for all runtime URL paths are based off of this.
## For more information on customizing the URLs
## (like /w/index.php/Page_title to /wiki/Page_title) please see:
## https://www.mediawiki.org/wiki/Manual:Short_URL
$wgScriptPath = "/gwiki";
$wgArticlePath = "/geomlab/$1";
$wgScriptExtension = ".php";

## The protocol and server name to use in fully-qualified URLs
$wgServer = "http://spivey.oriel.ox.ac.uk";

## The relative URL path to the skins directory
$wgStylePath = "$wgScriptPath/skins";

## The relative URL path to the logo.  Make sure you change this from the default,
## or else you'll overwrite your logo when you upgrade!
$wgLogo = "$wgStylePath/common/images/escher.png";

## UPO means: this is also a user preference option

$wgEnableEmail = false;
$wgEnableUserEmail = true; # UPO

$wgEmergencyContact = "apache@spivey";
$wgPasswordSender = "apache@spivey";

$wgEnotifUserTalk = false; # UPO
$wgEnotifWatchlist = false; # UPO
$wgEmailAuthentication = true;

## Database settings
$wgDBtype = "mysql";
$wgDBserver = "localhost";
$wgDBname = "geomlab";
# $wgDBuser = SECRET;
# $wgDBpassword = SECRET;

# MySQL specific settings
$wgDBprefix = "";

# MySQL table options to use during installation or update
$wgDBTableOptions = "ENGINE=InnoDB, DEFAULT CHARSET=binary";

# Experimental charset support for MySQL 5.0.
$wgDBmysql5 = false;

## Shared memory settings
$wgMainCacheType = CACHE_ACCEL;
$wgMemCachedServers = array();

## To enable image uploads, make sure the 'images' directory
## is writable, then set this to true:
$wgEnableUploads = true;
$wgUseImageMagick = true;
$wgImageMagickConvertCommand = "/usr/bin/convert";

$wgFileExtensions[] = 'pdf';
$wgFileExtensions[] = 'ppt';

# InstantCommons allows wiki to use images from http://commons.wikimedia.org
$wgUseInstantCommons = false;

## If you use ImageMagick (or any other shell command) on a
## Linux server, this will need to be set to the name of an
## available UTF-8 locale
$wgShellLocale = "en_US.utf8";

## If you want to use image uploads under safe mode,
## create the directories images/archive, images/thumb and
## images/temp, and make them all writable. Then uncomment
## this, if it's not already uncommented:
#$wgHashedUploadDirectory = false;

## Set $wgCacheDirectory to a writable directory on the web server
## to make your wiki go slightly faster. The directory should not
## be publically accessible from the web.
#$wgCacheDirectory = "$IP/cache";

# Site language code, should be one of the list in ./languages/Names.php
$wgLanguageCode = "en-gb";

# $wgSecretKey = SECRET;

# Site upgrade key. Must be set to a string (default provided) to turn on the
# web installer while LocalSettings.php is in place
# $wgUpgradeKey = SECRET;

## Default skin: you can change the default skin. Use the internal symbolic
## names, ie 'cologneblue', 'monobook', 'vector':
$wgDefaultSkin = "geomskin";

## For attaching licensing metadata to pages, and displaying an
## appropriate copyright notice / icon. GNU Free Documentation
## License and Creative Commons licenses are supported so far.
$wgRightsPage = "none"; # Set to the title of a wiki page that describes your license/copyright
$wgRightsUrl = "";
$wgRightsText = "";
$wgRightsIcon = "";

# Path to the GNU diff3 utility. Used for conflict resolution.
$wgDiff3 = "/usr/bin/diff3";

# The following permissions were set based on your choice in the installer
$wgGroupPermissions['*']['createaccount'] = false;
$wgGroupPermissions['*']['edit'] = false;

/** Don't use RC Patrolling to check for vandalism */
$wgUseRCPatrol = false;

/** Don't use new page patrolling to check new pages on Special:Newpages */
$wgUseNPPatrol = false;

# Set $wgNoFollowLinks to true if you open up your wiki to editing by
# the general public and wish to apply nofollow to external links as a
# deterrent to spammers. Nofollow is not a comprehensive anti-spam solution
# and open wikis will generally require other anti-spam measures; for more
# information, see https://www.mediawiki.org/wiki/Manual:Combating_spam
$wgNoFollowLinks = false;

# Enabled Extensions. Most extensions are enabled by including the base extension file here
# but check specific extension documentation for more details
# The following extensions were automatically enabled:
require_once "$IP/extensions/Cite/Cite.php";
require_once "$IP/extensions/ParserFunctions/ParserFunctions.php";
require_once "$IP/extensions/GeomGrind.php";
require_once "$IP/extensions/HtmlTemplate.php";

require_once "$IP/skins/GeomSkin/GeomSkin.php";

# Customize footer
$wgHooks['SkinTemplateOutputPageBeforeExec'][] = 'myFooter';
function myFooter( $sk, &$tpl ) {
	unset($tpl->data['footerlinks']['places']);
	return true;
}

# When you make changes to this configuration file, this will make
# sure that cached pages are cleared.
$wgCacheEpoch = max($wgCacheEpoch, gmdate('YmdHis', @filemtime(__FILE__)));

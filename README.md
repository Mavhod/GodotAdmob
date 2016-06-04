AdMob
=====
This is the AdMob module for Godot Engine (https://github.com/okamstudio/godot)
- Android only
- Banner only (hasn't Interstitial)
 
How to use
----------
Drop the "admob" directory inside the "modules" directory on the Godot source.

~~Move file GodotAdMob.java from "admob/android/" to "platform/android/java/src/org/godotengine/godot/".~~

Recompile.


In Example project goto Export->Target->Android:

	Options:
		Custom Package:
			- place your apk from build
		Permissions on:
			- Access Network State
			- Internet

Configuring your game
---------------------

To enable the module on Android, add the path to the module to the "modules" property on the [android] section of your engine.cfg file. It should look like this:

	[android]
	modules="org/godotengine/godot/GodotAdMob"

API Reference
-------------

The following methods are available:

	void init(boolean isReal, boolean isTop, String id)
		isReal: show real ad or test ad
		isTop: banner is top of screen or buttom
		id: banner unit id
	
	void showBanner(boolean isShow)
		isShow: show or hide banner
	
	int getAdWidth()
	int getAdHeight()
	
	void resize(boolean isTop)
		isTop: banner is top of screen or buttom

License
-------------
MIT license
	

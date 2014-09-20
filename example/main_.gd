
extends Node

var adMob

func _ready():
	set_process(true)
	if(Globals.has_singleton("AdMob")):
		adMob		= Globals.get_singleton("AdMob")
		adMob.init(true, true, "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX")	[Place your Ad Unit ID here, and delete this message.]
		#var adWidth		= adMob.getAdWidth()
		#var adHeight	= adMob.getAdHeight()
	else:
		adMob	= null
	
func _process(delta):
	if(null != adMob):
		adMob.showBanner(true)

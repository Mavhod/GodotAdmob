
extends Node

var admob
var sizeOld
var isTop

func _ready():
	set_process(true)
	sizeOld		= OS.get_video_mode_size()
	isTop		= true
	if(Globals.has_singleton("AdMob")):
		admob		= Globals.get_singleton("AdMob")
		admob.init(true, isTop, "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX")	[Place your Ad Unit ID and delete this message.]
		admob.showBanner(true)
	else:
		admob	= null
	
func _process(delta):
	var size	= OS.get_video_mode_size()
	
	if( ((sizeOld.x - size.x) != 0) || ((sizeOld.y - size.y) != 0) ):
		sizeOld	= size
		if(null != admob):
			print(admob.getAdWidth(), ", ", admob.getAdHeight())
			admob.resize(isTop)

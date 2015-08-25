extends Node

var admob = null
var isTop = true
var ad_id = "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX" [Replace with your Ad Unit ID and delete this message.]

func _ready():
	get_tree().connect("screen_resized",self,"update_size")
	if(Globals.has_singleton("AdMob")):
		admob = Globals.get_singleton("AdMob")
		admob.init(true, isTop, ad_id)
		admob.showBanner(true)
	
func update_size():
	if(admob != null):
		print(admob.getAdWidth(), ", ", admob.getAdHeight())
		admob.resize(isTop)

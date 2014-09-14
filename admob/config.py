def can_build(plat):
	return plat=="android"

def configure(env):
	if (env['platform'] == 'android'):
		env.android_module_file("android/GodotAdMob.java")
		env.android_module_manifest("android/AndroidManifestChunk.xml")
		env.disable_module()


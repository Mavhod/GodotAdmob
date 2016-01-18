def can_build(plat):
	return plat=="android"

def configure(env):
	if (env['platform'] == 'android'):
		env.android_add_dependency("compile 'com.google.android.gms:play-services-ads:+'")
		#env.android_add_java_dir("android/java/src")
		env.android_add_to_manifest("android/AndroidManifestChunk.xml")
		env.disable_module()


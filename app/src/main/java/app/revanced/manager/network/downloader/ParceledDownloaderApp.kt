package app.revanced.manager.network.downloader

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import app.revanced.manager.plugin.downloader.DownloaderPlugin
import kotlinx.parcelize.Parcelize

@Parcelize
/**
 * A parceled [DownloaderPlugin.App]. Instances of this class can be safely stored in a bundle without needing to set the [ClassLoader].
 */
class ParceledDownloaderApp private constructor(
    val pluginPackageName: String,
    private val bundle: Bundle
) : Parcelable {
    constructor(plugin: LoadedDownloaderPlugin, app: DownloaderPlugin.App) : this(
        plugin.packageName,
        createBundle(app)
    )

    fun unwrapWith(plugin: LoadedDownloaderPlugin): DownloaderPlugin.App {
        bundle.classLoader = plugin.classLoader

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val className = bundle.getString(CLASS_NAME_KEY)!!
            val clazz = plugin.classLoader.loadClass(className)

            bundle.getParcelable(APP_KEY, clazz)!! as DownloaderPlugin.App
        } else @Suppress("DEPRECATION") bundle.getParcelable(APP_KEY)!!
    }

    private companion object {
        const val CLASS_NAME_KEY = "class"
        const val APP_KEY = "app"

        fun createBundle(app: DownloaderPlugin.App) = Bundle().apply {
            putParcelable(APP_KEY, app)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) putString(
                CLASS_NAME_KEY,
                app::class.java.canonicalName
            )
        }
    }
}
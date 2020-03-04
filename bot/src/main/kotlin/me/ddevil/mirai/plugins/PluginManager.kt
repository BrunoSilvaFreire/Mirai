package me.ddevil.mirai.plugins

import me.ddevil.mirai.Mirai
import me.ddevil.mirai.command.DebugCommand
import me.ddevil.mirai.command.Prefixed
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile
import java.util.logging.Logger


class PluginManager(
    val mirai: Mirai,
    val directory: File
) : Prefixed {
    private val loadedPlugins = ArrayList<Plugin>()

    val plugins: Set<Plugin>
        get() = loadedPlugins.toSet()

    companion object {
        val logger = Logger.getLogger(PluginManager::class.java.name)
    }

    init {
        mirai.whenCommandManagerAvailable {
            withCommandOf<DebugCommand> {
                register(this@PluginManager) {
                    raw("Total of ${plugins.size} plugins loaded.")
                    for (plugin in plugins) {
                        markdown(plugin.pluginDescriptor.identifier, italic = true)
                    }
                }
            }
        }
        logger.info("Searching for plugins in ${directory.absolutePath}")
        val files = directory.listFiles { file, name ->
            name.endsWith("jar")
        }
        if (files != null) {
            logger.info("Found ${files.size} files.")
            for (listFile in files) {
                try {
                    val file = JarFile(listFile)
                    var entry = file.getJarEntry("plugin.json")
                    if (entry == null) {
                        logger.warning("Jar file $file is not a plugin!")
                        continue
                    }
                    val descriptor = PluginDescriptor.from(file.getInputStream(entry))
                    val child = URLClassLoader(
                        arrayOf<URL>(listFile.toURI().toURL()),
                        this::class.java.classLoader
                    )
                    val classToLoad = Class.forName(descriptor.main, true, child)
                    if (classToLoad == null) {
                        logger.warning("Plugin ${descriptor.name}'s main class ${descriptor.main} could not be found.")
                        continue
                    }
                    if (classToLoad.isAssignableFrom(Plugin::class.java)) {
                        logger.warning("Plugin ${descriptor.name}'s main class ${descriptor.main} does not extend Plugin.")
                        continue
                    }
                    val ctor = classToLoad.getConstructor()
                    if (ctor == null) {
                        logger.warning("Plugin ${descriptor.name}'s main class ${descriptor.main} does not have a no args constructor.")
                        continue
                    }
                    val instance = ctor.newInstance()
                    if (instance !is Plugin) {
                        logger.severe("Plugin ${descriptor.name}'s instantiation of ${descriptor.main} does not extend Plugin.")
                        continue
                    }
                    logger.info("Initialzing $instance")
                    instance.initialize(descriptor, mirai)
                    logger.info("Plugin ${descriptor.identifier} (${descriptor.name}) loaded.")
                    loadedPlugins += instance
                } catch (exception: Throwable) {
                    logger.warning("Error while loading plugin ${listFile}, skipping.")
                }
            }
        }

    }

    override val prefix: String
        get() = "mirai.plugin_manager"
}
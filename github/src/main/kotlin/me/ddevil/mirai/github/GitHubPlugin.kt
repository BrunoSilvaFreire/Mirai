package me.ddevil.mirai.github

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import me.ddevil.mirai.persistence.DataScope
import me.ddevil.mirai.plugins.Plugin
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub

class GitHubPlugin : Plugin() {
    private lateinit var persistence: DataScope
    private lateinit var github: GitHub
    private val repositories = ArrayList<GHRepository>()
    override fun bootstrap() {
        val pluginConfig = mirai.configurationManager.pluginScope(this)
        logger.info("Hooked into config scope $pluginConfig")
        persistence = mirai.persistenceManager.request("github")
        logger.info("Hooked into persistence scope ($persistence)")
        runBlocking {
            coroutineScope {
                val token = pluginConfig.get("credentials")
                if (token == null) {
                    logger.warning("GitHub plugin found no credentials in config. Plugin won't do anything.")
                    return@coroutineScope
                }
                for (map in persistence.all()) {

                }
            }
        }
    }


}


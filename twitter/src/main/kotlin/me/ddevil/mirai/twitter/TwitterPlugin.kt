package me.ddevil.mirai.twitter

import jp.nephy.penicillin.PenicillinClient
import jp.nephy.penicillin.core.session.config.account
import jp.nephy.penicillin.core.session.config.application
import jp.nephy.penicillin.core.session.config.token
import jp.nephy.penicillin.endpoints.accountActivity
import jp.nephy.penicillin.endpoints.timeline
import jp.nephy.penicillin.endpoints.timeline.userTimeline
import jp.nephy.penicillin.endpoints.timeline.userTimelineByScreenName
import jp.nephy.penicillin.extensions.await
import jp.nephy.penicillin.extensions.models.text
import jp.nephy.penicillin.extensions.queue
import jp.nephy.penicillin.models.Status
import kotlinx.coroutines.runBlocking
import me.ddevil.mirai.plugins.Plugin

class TwitterPlugin : Plugin() {
    override fun bootstrap() {
        // Creates new ApiClient
        val client = PenicillinClient {
            account {
                application("ConsumerKey", "ConsumerSecret")
                token("AccessToken", "AccessToken Secret")
            }
        }
        runBlocking {

            client.timeline.userTimelineByScreenName(screenName = "realdonaldtrump", count = 100).queue {
                for (status in it) {
                    TweetObserver(status)
                }
            }
            // Disposes ApiClient
            client.close()
        }
    }
}

class TweetObserver(
    val status: Status
) {

}
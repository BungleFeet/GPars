//  GPars (formerly GParallelizer)
//
//  Copyright © 2008-9  The original author or authors
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License. 

package groovyx.gpars.actor.blocking

import java.util.concurrent.CountDownLatch
import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.Actors

public class ImmutableMessageTest extends GroovyTestCase {
    public void testSend() {
        volatile String result
        final CountDownLatch latch = new CountDownLatch(1)

        final Actor bouncer = Actors.actor {
            receive {
                it.reply new TestMessage(it.value)
            }
        }.start()

        Actors.actor {
            bouncer << new TestMessage('Value')
            receive {
                result = it.value
                latch.countDown()
            }
        }.start()

        latch.await()
        assertEquals 'Value', result
    }
}

@Immutable final class TestMessage {
    String value
}

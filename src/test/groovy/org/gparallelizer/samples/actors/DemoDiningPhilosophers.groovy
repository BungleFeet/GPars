import org.gparallelizer.actors.pooledActors.AbstractPooledActor
import org.gparallelizer.actors.pooledActors.PooledActors

/**
 * Shows actor solution to The Dining Philosophers problem
 */

PooledActors.defaultPooledActorGroup.resize 5

final class Philosopher extends AbstractPooledActor {
    private Random random = new Random()

    String name
    def forks = []

    void act() {
        assert 2 == forks.size()
        loop {
            think()
            forks*.send new Take()
            react {a, b ->
                if ([a, b].any {Rejected.isCase it}) {
                    println "$name: \tOops, can't get my forks! Giving up."
                    [a, b].find {Accepted.isCase it}?.reply new Finished()
                } else {
                    eat()
                    reply new Finished()
                }
            }
        }
    }

    void think() {
        println "$name: \tI'm thinking"
        Thread.sleep random.nextInt(5000)
        println "$name: \tI'm done thinking"
    }

    void eat() {
        println "$name: \tI'm EATING"
        Thread.sleep random.nextInt(2000)
        println "$name: \tI'm done EATING"
    }
}

final class Fork extends AbstractPooledActor {

    String name
    boolean available = true

    void act() {
        loop {
            react {message ->
                switch (message) {
                    case Take:
                        if (available) {
                            available = false
                            reply new Accepted()
                        } else reply new Rejected()
                        break
                    case Finished:
                        assert !available
                        available = true
                        break
                    default: throw new IllegalStateException("Cannot process the message: $message")
                }
            }
        }
    }
}

final class Take {}
final class Accepted {}
final class Rejected {}
final class Finished {}

def forks = [
        new Fork(name:'Fork 1'),
        new Fork(name:'Fork 2'),
        new Fork(name:'Fork 3'),
        new Fork(name:'Fork 4'),
        new Fork(name:'Fork 5')
]

def philosophers = [
        new Philosopher(name:'Joe', forks:[forks[0], forks[1]]),
        new Philosopher(name:'Dave', forks:[forks[1], forks[2]]),
        new Philosopher(name:'Alice', forks:[forks[2], forks[3]]),
        new Philosopher(name:'James', forks:[forks[3], forks[4]]),
        new Philosopher(name:'Phil', forks:[forks[4], forks[0]]),
]

forks*.start()
philosophers*.start()

System.in.read()
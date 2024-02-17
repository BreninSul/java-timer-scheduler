This is small lib for easy run scheduled jobs in thread per job (SchedulerType.TIMER_PER_TASK) or virtual thread per job mode (SchedulerType.SINGLETON_VIRTUAL_THREADS)

Configure with properties

Beans in Spring Boot will be automatically registered in ConfigurableTransactionAutoConfiguration with defined properties ConfigurableTransactionTemplateProperties (prefix synchronisation).

add the following dependency:

````kotlin
dependencies {
//Other dependencies
    implementation("io.github.breninsul:configurable-transaction-template-starter:${version}")
//Other dependencies
}

````
# Example of usage

````kotlin
  TaskSchedulerRegistry.registerTypeTask(type, "testTask", jobDelay) {
    //do smth
}
````

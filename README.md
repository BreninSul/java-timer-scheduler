This is small lib for easy run scheduled jobs in thread per job 
    - SchedulerType.THREAD_PER_TASK - Creates timer(and thread) for every task. Task will not be executed when previous is not completed
    - SchedulerType.VIRTUAL_THREAD_PER_TASK_NO_WAIT - One common timer is created, virtual thread for any task. Task will be executed when previous is completed
    - SchedulerType.VIRTUAL_THREAD_PER_TASK_WAIT - One common timer is created, virtual thread for any task. Task will not be executed when previous is completed

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
